package contacts.async.util

import contacts.async.ASYNC_DISPATCHER
import contacts.core.Contacts
import contacts.core.entities.Contact
import contacts.core.entities.ExistingRawContactEntityWithContactId
import contacts.core.util.contact
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

/**
 * Suspends the current coroutine, performs the operation in the given [coroutineContext], then
 * returns the result.
 *
 * Computations automatically stops if the parent coroutine scope / job is cancelled.
 *
 * See [ExistingRawContactEntityWithContactId.contact].
 */
suspend fun ExistingRawContactEntityWithContactId.contactWithContext(
    contacts: Contacts,
    coroutineContext: CoroutineContext = ASYNC_DISPATCHER
): Contact? = withContext(coroutineContext) {
    contact(contacts) { !isActive }
}

/**
 * Creates a [CoroutineScope] with the given [coroutineContext], performs the operation in that
 * scope, then returns the [Deferred] result.
 *
 * Computations automatically stops if the parent coroutine scope / job is cancelled.
 *
 * See [ExistingRawContactEntityWithContactId.contact].
 */
fun ExistingRawContactEntityWithContactId.contactAsync(
    contacts: Contacts,
    coroutineContext: CoroutineContext = ASYNC_DISPATCHER
): Deferred<Contact?> = CoroutineScope(coroutineContext).async {
    contact(contacts) { !isActive }
}