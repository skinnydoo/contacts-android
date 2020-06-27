package com.vestrel00.contacts

import android.database.DatabaseUtils
import com.vestrel00.contacts.entities.Entity
import com.vestrel00.contacts.entities.MimeType
import com.vestrel00.contacts.util.isEmpty
import java.util.*

// TODO TEST
// Java consumers would have to access these static functions via Wherekt instead of Where.
// Using @file:JvmName("Where") will not work because of the name clash with the Where class.
// In order for Java consumers to use these via OrderBy instead of Wherekt, we could redefine
// these functions in a companion object within the Where class. However, we won't do this just
// because it creates duplicate code. Java users just need to migrate to Kotlin already...

/**
 * Note that string comparison is case-sensitive.
 */
infix fun AbstractField.equalTo(value: Any): Where = EqualTo(this, value)

/**
 * Note that string comparison is case-sensitive.
 */
infix fun AbstractField.notEqualTo(value: Any): Where = NotEqualTo(this, value)

/**
 * Note that string comparison is case-insensitive when within ASCII range.
 */
infix fun AbstractField.equalToIgnoreCase(value: Any): Where = EqualToIgnoreCase(this, value)

/**
 * Note that string comparison is case-insensitive when within ASCII range.
 */
infix fun AbstractField.notEqualToIgnoreCase(value: Any): Where = NotEqualToIgnoreCase(this, value)

infix fun AbstractField.greaterThan(value: Any): Where = GreaterThan(this, value)
infix fun AbstractField.greaterThanOrEqual(value: Any): Where =
    GreaterThanOrEqual(this, value)

infix fun AbstractField.lessThan(value: Any): Where = LessThan(this, value)
infix fun AbstractField.lessThanOrEqual(value: Any): Where = LessThanOrEqual(this, value)

infix fun AbstractField.`in`(values: Collection<Any>): Where = In(this, values.asSequence())
infix fun AbstractField.`in`(values: Sequence<Any>): Where = In(this, values)
infix fun AbstractField.notIn(values: Collection<Any>): Where = NotIn(this, values.asSequence())
infix fun AbstractField.notIn(values: Sequence<Any>): Where = NotIn(this, values)

/**
 * Note that the value is case-insensitive when within ASCII range.
 */
infix fun AbstractField.startsWith(value: String): Where = StartsWith(this, value)

/**
 * Note that the value is case-insensitive when within ASCII range.
 */
infix fun AbstractField.endsWith(value: String): Where = EndsWith(this, value)

/**
 * Note that the value is case-insensitive when within ASCII range.
 */
infix fun AbstractField.contains(value: String): Where = Contains(this, value)

/**
 * Note that the value is case-insensitive when within ASCII range.
 */
infix fun AbstractField.doesNotStartWith(value: String): Where = DoesNotStartWith(this, value)

/**
 * Note that the value is case-insensitive when within ASCII range.
 */
infix fun AbstractField.doesNotEndWith(value: String): Where = DoesNotEndWith(this, value)

/**
 * Note that the value is case-insensitive when within ASCII range.
 */
infix fun AbstractField.doesNotContain(value: String): Where = DoesNotContain(this, value)

// Non-infix convenience functions

/**
 * Note that functions for "isNull" or "isNullOrEmpty" are not exposed to consumers to prevent
 * making misleading queries.
 *
 * Removing a piece of existing data results in the deletion of the row in the Data table if that
 * row no longer contains any meaningful data (no meaningful non-null "datax" columns left). This is
 * the behavior of the native Android Contacts app. Therefore, querying for null fields is not
 * possible. For example, there may be no Data rows that exist where the email address is null.
 * Thus, a query to search for all contacts with null email address may return 0 contacts even if
 * there are some contacts without email addresses.
 */
fun AbstractField.isNotNull(): Where = IsNotNull(this)

/**
 * Note that functions for "isNull" or "isNullOrEmpty" are not exposed to consumers to prevent
 * making misleading queries.
 *
 * Removing a piece of existing data results in the deletion of the row in the Data table if that
 * row no longer contains any meaningful data (no meaningful non-null "datax" columns left). This is
 * the behavior of the native Android Contacts app. Therefore, querying for null fields is not
 * possible. For example, there may be no Data rows that exist where the email address is null.
 * Thus, a query to search for all contacts with null email address may return 0 contacts even if
 * there are some contacts without email addresses.
 */
fun AbstractField.isNotNullOrEmpty(): Where = isNotNull() and notEqualTo("")

/**
 * Keep this function internal. Do not expose to consumers. Read the docs on [isNotNull] or
 * [isNotNullOrEmpty].
 */
internal fun AbstractField.isNull(): Where = IsNull(this)

// Collection convenience functions

/**
 * Transforms each item in this collection to a [Where] and combines them with the "OR" operator.
 *
 * For example;
 *
 * ```
 * val letters = listOf("a", "b", "c")
 * val whereStartsWithLetter = letters whereOr { Fields.Contact.DisplayName startsWith it }
 * ```
 *
 * Outputs
 *
 * ```
 * // (display_name LIKE 'a%%') OR (display_name LIKE 'b%%') OR (display_name LIKE 'c%%')
 * ```
 *
 * Another, more useful example is a where starting with a number;
 *
 * ```
 * val whereStartsWithNumber = (0..9).asSequence()
 *      .whereOr { Fields.Contact.DisplayName startsWith "$it" }
 * ```
 *
 * This may also be applied to a collection of [AbstractField]s. For example,
 *
 * ```
 * val fields = listOf(Fields.Contact.DisplayName, Fields.Email.Address)
 * val whereFieldsStartsWithLetter = fields whereOr { it startsWith "letter" }
 * ```
 *
 * Outputs
 *
 * ```
 * // (display_name LIKE 'letter%%') OR (data1 LIKE 'letter%%' <omitted for brevity>)
 * ```
 */
// Not inlined because of private functions and classes.
infix fun <T : Any?> Collection<T>.whereOr(where: (T) -> Where): Where? =
    asSequence().joinWhere(where, "OR")

/**
 * See [whereOr].
 */
// Not inlined because of private functions and classes.
infix fun <T : Any?> Sequence<T>.whereOr(where: (T) -> Where): Where? = joinWhere(where, "OR")

/**
 * Transforms each item in this collection to a [Where] and combines them with the "AND" operator.
 *
 * For example;
 *
 * ```
 * val letters = listOf("a", "b", "c")
 * val whereDoesNotStartWithLetter =
 *      letters whereAnd { Fields.Contact.DisplayName doesNotStartWith it }
 * ```
 *
 * Outputs
 *
 * ```
 * (display_name NOT LIKE 'a%%') AND (display_name NOT LIKE 'b%%') AND (display_name NOT LIKE 'c%%')
 * ```
 *
 * This may also be applied to a collection of [AbstractField]s. For example,
 *
 * ```
 * val fields = listOf(Fields.Contact.DisplayName, Fields.Email.Address)
 * val whereFieldsDoesNotStartWithLetter = fields whereAnd { it doesNotStartWith "letter" }
 * ```
 *
 * Outputs
 *
 * ```
 * // (display_name NOT LIKE 'letter%%') AND (data1 NOT LIKE 'letter%%' <omitted for brevity>)
 */
// Not inlined because of private functions and classes.
infix fun <T : Any?> Collection<T>.whereAnd(where: (T) -> Where): Where? =
    asSequence().joinWhere(where, "AND")

/**
 * See [whereAnd].
 */
// Not inlined because of private functions and classes.
infix fun <T : Any?> Sequence<T>.whereAnd(where: (T) -> Where): Where? = joinWhere(where, "AND")

/**
 * See [whereOr].
 */
infix fun FieldSet<*>.whereOr(where: (AbstractField) -> Where): Where? = all.whereOr(where)

/**
 * See [whereAnd].
 */
infix fun FieldSet<*>.whereAnd(where: (AbstractField) -> Where): Where? = all.whereAnd(where)

// Note that the above functions are not inlined because it requires this private fun to be public.
private fun <T : Any?> Sequence<T>.joinWhere(where: (T) -> Where, separator: String): Where? {
    if (isEmpty()) {
        return null
    }

    val whereString = joinToString(" $separator ") { "(${where(it)})" }
    return JoinedWhere(whereString)
}


// Conversion functions

/**
 * Converts [this] where clause to a where clause that is usable for the Contacts table.
 *
 * More specifically, this translates the following column names to work with the Contacts table;
 *
 * - RawContacts.CONTACT_ID -> Contacts._ID
 * - Data.CONTACT_ID -> Contacts._ID
 *
 * This does no translate anything else. So any fields used that does not exist in the Contacts
 * table will remain.
 */
internal fun Where.inContactsTable(): Where = ContactsTableWhere(
    toString()
        .replace(RawContactsFields.ContactId.columnName, ContactsFields.Id.columnName)
        // Technically, RawContactsFields.ContactId and Fields.Contact.Id have the same columnName.
        // For the sake of OCD, I'm performing this redundant replacement =) SUE ME!
        .replace(Fields.Contact.Id.columnName, ContactsFields.Id.columnName)
)

private class ContactsTableWhere(whereString: String) : Where(whereString)

/**
 * Converts [this] where clause to a where clause that is usable for the RawContacts table.
 *
 * More specifically, this translates the following column names to work with the RawContacts table;
 *
 * - Data.RAW_CONTACT_ID -> RawContacts._ID
 *
 * This does no translate anything else. So any fields used that does not exist in the RawContacts
 * table will remain.
 */
internal fun Where.inRawContactsTable(): Where = RawContactsTableWhere(
    toString().replace(Fields.RawContact.Id.columnName, RawContactsFields.Id.columnName)
)

private class RawContactsTableWhere(whereString: String) : Where(whereString)

/**
 * Each where expression is paired with its mimetype because the contacts Data table uses
 * generic column names (e.g. data1, data2, etc) using the column 'mimetype' to distinguish
 * the type of data in that generic column.
 *
 * For example, querying for contacts with name LIKE 'john' AND address LIKE 'colorado';
 *
 * ```
 * WHERE (data1 = 'john' AND mimetype = 'vnd.android.cursor.item/name')
 *   AND (data1 = 'colorado' AND mimetype = 'vnd.android.cursor.item/postal-address_v2')
 * ```
 *
 * This is important because if the mimetypes are not paired with the query;
 *
 * ```
 * WHERE (data1 = 'johnson' AND data1 = 'colorado')
 * ```
 *
 * The above will never match any row because 'johnson' = 'colorado' is never true.
 */
private fun where(field: AbstractField, operator: String, value: Any?): String {
    var where = "${field.columnName} $operator ${value.toSqlString()}"
    if (field is CommonDataFields && field.mimeType.value.isNotBlank()) {
        where += " AND ${Fields.MimeType.columnName} = '${field.mimeType.value}'"
    }
    return where
}

private fun where(lhs: Where, operator: String, rhs: Where): String = "($lhs) $operator ($rhs)"

sealed class Where(private val whereString: String) {

    override fun toString(): String = whereString

    infix fun and(where: Where): Where = And(this, where)

    infix fun or(where: Where): Where = Or(this, where)
}

private class And(lhs: Where, rhs: Where) : Where(where(lhs, "AND", rhs))
private class Or(lhs: Where, rhs: Where) : Where(where(lhs, "OR", rhs))

private class EqualTo(field: AbstractField, value: Any) : Where(where(field, "=", value))
private class NotEqualTo(field: AbstractField, value: Any) : Where(where(field, "!=", value))

private class GreaterThan(field: AbstractField, value: Any) : Where(where(field, ">", value))
private class GreaterThanOrEqual(field: AbstractField, value: Any) :
    Where(where(field, ">=", value))

private class LessThan(field: AbstractField, value: Any) : Where(where(field, "<", value))
private class LessThanOrEqual(field: AbstractField, value: Any) : Where(where(field, "<=", value))

private class IsNull(field: AbstractField) : Where(where(field, "IS", null))
private class IsNotNull(field: AbstractField) : Where(where(field, "IS NOT", null))

private class In(field: AbstractField, values: Sequence<Any>) : Where(where(field, "IN", values))
private class NotIn(field: AbstractField, values: Sequence<Any>) :
    Where(where(field, "NOT IN", values))

private open class Like(field: AbstractField, value: Any) : Where(where(field, "LIKE", value))
private open class NotLike(field: AbstractField, value: Any) :
    Where(where(field, "NOT LIKE", value))

private class EqualToIgnoreCase(field: AbstractField, value: Any) : Like(field, "$value")
private class StartsWith(field: AbstractField, value: String) : Like(field, "$value%%")
private class EndsWith(field: AbstractField, value: String) : Like(field, "%%$value")
private class Contains(field: AbstractField, value: String) : Like(field, "%%$value%%")

private class NotEqualToIgnoreCase(field: AbstractField, value: Any) : NotLike(field, "$value")
private class DoesNotStartWith(field: AbstractField, value: String) : NotLike(field, "$value%%")
private class DoesNotEndWith(field: AbstractField, value: String) : NotLike(field, "%%$value")
private class DoesNotContain(field: AbstractField, value: String) : NotLike(field, "%%$value%%")

private class JoinedWhere(whereString: String) : Where(whereString)

private fun Any?.toSqlString(): String = when (this) {
    null -> "NULL"
    is Boolean -> if (this) "1" else "0"
    is String -> DatabaseUtils.sqlEscapeString(this)
    is Array<*> -> this.asSequence().toSqlString()
    is Collection<*> -> this.asSequence().toSqlString()
    is Sequence<*> -> this.map { it?.toSqlString() }
        .joinToString(separator = ", ", prefix = "(", postfix = ")")
    is Entity.Type -> value.toSqlString()
    is Date -> time.toSqlString()
    is MimeType -> value.toSqlString()
    else -> this.toString().toSqlString()
}