package tj.relax.core.api

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

/**
 * Single entry point for surfacing API errors to the user.
 *
 * Repositories and ViewModels call [report] from their catch blocks when a request fails;
 * [tj.relax.ui.components.ErrorAlertDialogHost] observes [current] and renders the
 * shared [tj.relax.ui.components.ErrorAlertDialog] — so the dialog UI is never duplicated
 * across screens.
 */
object ErrorPresenter {
    var current: ApiException? by mutableStateOf(null)
        private set

    /** Reports an API error to be shown in the global [tj.relax.ui.components.ErrorAlertDialog]. */
    fun report(error: ApiException) {
        current = error
    }

    /** Reports any throwable, wrapping non-API errors (e.g. network/IO) into a generic [ApiException]. */
    fun report(throwable: Throwable) {
        if (throwable is kotlinx.coroutines.CancellationException) return
        current = throwable as? ApiException
            ?: ApiException(apiCode = null, message = friendlyErrorMessage(throwable))
    }

    fun dismiss() {
        current = null
    }
}