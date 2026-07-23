package tj.relax.core.Interceptor

import io.ktor.client.plugins.api.createClientPlugin
import io.ktor.client.request.header
import tj.relax.core.util.LocaleManager

/** Attaches the currently selected app language to every request so the backend can localize responses. */
val LanguageInterceptor = createClientPlugin("LanguageInterceptor") {
    onRequest { request, _ ->
        request.header("Accept-Language", LocaleManager.getCurrentLanguage())
    }
}
