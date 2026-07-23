package tj.relax.core.Interceptor

import okhttp3.Interceptor
import okhttp3.Response
import tj.relax.core.util.LocaleManager

/** Attaches the currently selected app language to every request so the backend can localize responses. */
class LanguageInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()
            .header("Accept-Language", LocaleManager.getCurrentLanguage())
            .build()
        return chain.proceed(request)
    }
}