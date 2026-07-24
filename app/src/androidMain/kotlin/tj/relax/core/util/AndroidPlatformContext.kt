package tj.relax.core.util

import android.content.Context

/** Set once from [tj.relax.RelaxApp.onCreate], before anything else runs — lets Android-only
 * expect/actual implementations (opening a URL, reading the app's files dir, ...) reach the
 * Application Context without needing it threaded through every call site. */
object AndroidPlatformContext {
    lateinit var applicationContext: Context
        internal set

    fun init(context: Context) {
        applicationContext = context.applicationContext
    }
}
