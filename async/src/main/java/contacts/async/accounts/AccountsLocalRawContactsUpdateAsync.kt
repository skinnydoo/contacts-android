package contacts.async.accounts

import contacts.async.ASYNC_DISPATCHER
import contacts.core.accounts.AccountsLocalRawContactsUpdate
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

/**
 * Suspends the current coroutine, performs the operation in the given [context], then returns the
 * result.
 *
 * Computations automatically stops if the parent coroutine scope / job is cancelled.
 *
 * See [AccountsLocalRawContactsUpdate.commit].
 */
suspend fun AccountsLocalRawContactsUpdate.commitWithContext(
    context: CoroutineContext = ASYNC_DISPATCHER
): AccountsLocalRawContactsUpdate.Result = withContext(context) { commit { !isActive } }

/**
 * Creates a [CoroutineScope] with the given [context], performs the operation in that scope, then
 * returns the [Deferred] result.
 *
 * Computations automatically stops if the parent coroutine scope / job is cancelled.
 *
 * See [AccountsLocalRawContactsUpdate.commit].
 */
fun AccountsLocalRawContactsUpdate.commitAsync(
    context: CoroutineContext = ASYNC_DISPATCHER
): Deferred<AccountsLocalRawContactsUpdate.Result> = CoroutineScope(context).async {
    commit { !isActive }
}