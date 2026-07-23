package tj.relax.data

import tj.relax.core.api.RelaxApiService
import tj.relax.core.api.dataOrThrow
import tj.relax.ui.screens.promotions.data.toDomain
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PromotionsRepository @Inject constructor(
    private val api: RelaxApiService,
) {
    suspend fun getPromotions(): List<Promotion> =
        api.getPromotions().dataOrThrow().map { it.toDomain() }
}
