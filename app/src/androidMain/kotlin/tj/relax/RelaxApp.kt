package tj.relax

import android.app.Application
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.disk.DiskCache
import coil.memory.MemoryCache
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import tj.relax.core.api.RelaxApiService
import tj.relax.core.crash.CrashReporter
import tj.relax.core.firebase.NotificationChannels
import tj.relax.data.LocalUserStore
import javax.inject.Inject

@HiltAndroidApp
class RelaxApp : Application(), ImageLoaderFactory {

    @Inject lateinit var apiService: RelaxApiService
    @Inject lateinit var localUserStore: LocalUserStore

    private val appScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()
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
