package com.vestrel00.contacts.profile

import android.accounts.Account
import android.content.ContentResolver
import android.content.Context
import android.provider.ContactsContract
import com.vestrel00.contacts.*
import com.vestrel00.contacts.Fields.Required
import com.vestrel00.contacts.entities.Contact
import com.vestrel00.contacts.entities.cursor.getString
import com.vestrel00.contacts.entities.mapper.ContactsMapper

interface ProfileQuery {

    /**
     * Limits the RawContacts and associated data to those associated with the given accounts. The
     * Contact returned will not contain data that belongs to other accounts not specified in
     * [accounts].
     *
     * If no accounts are specified, then all RawContacts and associated data are included.
     */
    fun accounts(vararg accounts: Account): ProfileQuery

    /**
     * See [ProfileQuery.accounts]
     */
    fun accounts(accounts: Collection<Account>): ProfileQuery

    /**
     * See [ProfileQuery.accounts]
     */
    fun accounts(accounts: Sequence<Account>): ProfileQuery

    /**
     * Includes the given set of [fields] in the resulting contact object.
     *
     * If no fields are specified, then all fields are included. Otherwise, only the specified
     * fields will be included in addition to the required fields, which are always included.
     *
     * Fields that are included will not guarantee non-null attributes in the returned contact
     * object instances.
     *
     * It is recommended to only include fields that will be used to save CPU and memory.
     *
     * Note that the Android contacts **data table** uses generic column names (e.g. data1, data2,
     * ...) using the column 'mimetype' to distinguish the type of data in that generic column. For
     * example, the column name of name display name is the same as address formatted address, which
     * is 'data1'. This means that formatted address is also included when the name display name is
     * included. There is no workaround for this because the [ContentResolver.query] function only
     * takes in an array of column names.
     *
     * ## IMPORTANT!!!
     *
     * Do not perform updates on contacts returned by a query where all fields are not included as
     * it will result in data loss!!
     */
    fun include(vararg fields: Field): ProfileQuery

    /**
     * See [ProfileQuery.include].
     */
    fun include(fields: Collection<Field>): ProfileQuery

    /**
     * See [ProfileQuery.include].
     */
    fun include(fields: Sequence<Field>): ProfileQuery

    /**
     * Returns the profile [Contact], if any.
     *
     * ## Thread Safety
     *
     * This should be called in a background thread to avoid blocking the UI thread.
     */
    // [ANDROID X] @WorkerThread (not using annotation to avoid dependency on androidx.annotation)
    fun find(): Contact?

    /**
     * Returns the profile [Contact], if any.
     *
     * ## Cancellation
     *
     * The number of contacts and contact data found and processed may be large, which results
     * in this operation to take a while. Therefore, cancellation is supported while the contacts
     * list is being built. To cancel at any time, the [cancel] function should return true.
     *
     * This is useful when running this function in a background thread or coroutine.
     *
     * ## Thread Safety
     *
     * This should be called in a background thread to avoid blocking the UI thread.
     */
    // [ANDROID X] @WorkerThread (not using annotation to avoid dependency on androidx.annotation)
    // @JvmOverloads cannot be used in interface methods...
    // fun find(cancel: () -> Boolean = { false }): Contact?
    fun find(cancel: () -> Boolean): Contact?
}

@Suppress("FunctionName")
internal fun ProfileQuery(context: Context): ProfileQuery = ProfileQueryImpl(
    ContactsPermissions(context),
    QueryResolverFactory(context.contentResolver)
)

private class ProfileQueryImpl(
    private val permissions: ContactsPermissions,
    private val queryResolverFactory: QueryResolverFactory,

    private var rawContactsWhere: Where = DEFAULT_RAW_CONTACTS_WHERE,
    private var include: Include = DEFAULT_INCLUDE
) : ProfileQuery {

    override fun toString(): String {
        return """
            rawContactsWhere = $rawContactsWhere
            include = $include
        """.trimIndent()
    }

    override fun accounts(vararg accounts: Account): ProfileQuery = accounts(accounts.asSequence())

    override fun accounts(accounts: Collection<Account>): ProfileQuery =
        accounts(accounts.asSequence())

    override fun accounts(accounts: Sequence<Account>): ProfileQuery = apply {
        rawContactsWhere = if (accounts.count() == 0) {
            DEFAULT_RAW_CONTACTS_WHERE
        } else {
            accounts.whereOr { account ->
                (Fields.RawContacts.AccountName equalToIgnoreCase account.name)
                    .and(Fields.RawContacts.AccountType equalToIgnoreCase account.type)
            }
        }
    }

    override fun include(vararg fields: Field): ProfileQuery = include(fields.asSequence())

    override fun include(fields: Collection<Field>): ProfileQuery = include(fields.asSequence())

    override fun include(fields: Sequence<Field>): ProfileQuery = apply {
        include = if (fields.count() == 0) {
            DEFAULT_INCLUDE
        } else {
            Include(fields + Required.fields.asSequence())
        }
    }

    override fun find(): Contact? = find { false }

    override fun find(cancel: () -> Boolean): Contact? {
        if (!permissions.canQuery()) {
            return null
        }

        return queryResolverFactory
            .resolver(cancel)
            .resolve(rawContactsWhere, include)
    }

    private companion object {
        val DEFAULT_RAW_CONTACTS_WHERE = NoWhere
        val DEFAULT_INCLUDE = Include(Fields.All)
    }
}

private class QueryResolverFactory(private val contentResolver: ContentResolver) {
    fun resolver(cancel: () -> Boolean): QueryResolver = QueryResolver(contentResolver, cancel)
}

private class QueryResolver(
    private val contentResolver: ContentResolver,
    private val cancel: () -> Boolean
) {

    fun resolve(rawContactsWhere: Where, include: Include): Contact? {
        val rawContactIds = rawContactIds(rawContactsWhere)

        val contactsMapper = ContactsMapper(cancel)
        for (rawContactId in rawContactIds) {
            val cursor = dataCursorFor(rawContactId, include)

            if (cursor != null) {
                contactsMapper.fromCursor(cursor)
                cursor.close()
            }

            if (cancel()) {
                return null
            }
        }

        return contactsMapper.map().firstOrNull()
    }

    private fun dataCursorFor(rawContactId: String, include: Include) = contentResolver.query(
        ContactsContract.Profile.CONTENT_RAW_CONTACTS_URI.buildUpon()
            .appendEncodedPath(rawContactId)
            .appendEncodedPath(ContactsContract.RawContacts.Data.CONTENT_DIRECTORY)
            .build(),
        include.columnNames,
        null,
        null,
        null
    )

    private fun rawContactIds(rawContactsWhere: Where): Set<String> {
        val cursor = contentResolver.query(
            ContactsContract.Profile.CONTENT_RAW_CONTACTS_URI,
            Include(Fields.RawContacts.Id).columnNames,
            if (rawContactsWhere == NoWhere) null else "$rawContactsWhere",
            null,
            null
        )

        return mutableSetOf<String>().apply {
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    val rawContactId = cursor.getString(Fields.RawContacts.Id)
                    rawContactId?.let(::add)

                    if (cancel()) {
                        clear()
                        break
                    }
                }

                cursor.close()
            }
        }
    }
}