package tj.dastras.ui.screens.home.data.repository

import tj.dastras.core.api.RelaxApiService
import tj.dastras.core.api.dataOrThrow
import tj.dastras.data.Banner
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BannerRepository @Inject constructor(
    private val api: RelaxApiService,
) {
    @Volatile private var cached: List<Banner>? = null

    suspend fun getAll(): List<Banner> {
        cached?.let { return it }
        return api.getBanners().dataOrThrow().also { cached = it }
    }
}