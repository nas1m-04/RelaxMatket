package tj.relax

import android.app.Application
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.disk.DiskCache
import coil.memory.MemoryCache
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
import tj.relax.data.LocalUserStore

class RelaxApp : Application(), ImageLoaderFactory {

    private val apiService: RelaxApiService by inject()
    private val localUserStore: LocalUserStore by inject()

    private val appScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Napier.base(DebugAntilog())
        }
        startKoin {
            androidContext(this@RelaxApp)
            modules(platformModule(), networkModule, appModule, viewModelModule)
        }
        NotificationChannels.ensureCreated(this)
        CrashReporter.install(this)
        appScope.launch {
            CrashReporter.sendPendingCrashIfAny(this@RelaxApp, apiService, localUserStore.get()?.uid)
        }
    }

    override fun newImageLoader(): ImageLoader = ImageLoader.Builder(this)
        .crossfade(250)
        .memoryCache {
            MemoryCache.Builder(this)
                .maxSizePercent(0.20)
                .build()
        }
        .diskCache {
            DiskCache.Builder()
                .directory(cacheDir.resolve("image_cache"))
                .maxSizeBytes(50L * 1024 * 1024) // 50 MB
                .build()
        }
        .build()
}
