package tj.relax

import android.app.Application
import coil3.ImageLoader
import coil3.SingletonImageLoader
import coil3.network.ktor3.KtorNetworkFetcherFactory
import coil3.request.crossfade
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import tj.relax.core.api.RelaxApiService
import tj.relax.core.crash.CrashReporter
import tj.relax.core.di.appModule
import tj.relax.core.di.networkModule
import tj.relax.core.di.platformModule
import tj.relax.core.di.viewModelModule
import tj.relax.core.firebase.NotificationChannels
import tj.relax.core.util.AndroidPlatformContext
import tj.relax.data.LocalUserStore

class RelaxApp : Application() {

    private val apiService: RelaxApiService by inject()
    private val localUserStore: LocalUserStore by inject()

    private val appScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()
        AndroidPlatformContext.init(this)
        if (BuildConfig.DEBUG) {
            Napier.base(DebugAntilog())
        }
        startKoin {
            androidContext(this@RelaxApp)
            modules(platformModule(), networkModule, appModule, viewModelModule)
        }
        NotificationChannels.ensureCreated(this)
        CrashReporter.install()
        appScope.launch {
            CrashReporter.sendPendingCrashIfAny(apiService, localUserStore.get()?.uid)
        }

        SingletonImageLoader.setSafe { context ->
            ImageLoader.Builder(context)
                .crossfade(true)
                .components { add(KtorNetworkFetcherFactory()) }
                .build()
        }
    }
}
