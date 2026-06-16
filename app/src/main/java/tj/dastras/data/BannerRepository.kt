package tj.dastras.data

import tj.dastras.data.remote.RelaxApiService
import tj.dastras.data.remote.dataOrThrow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BannerRepository @Inject constructor(
    private val api: RelaxApiService,
) {
    suspend fun getAll(): List<Banner> =
        api.getBanners().dataOrThrow()
}
