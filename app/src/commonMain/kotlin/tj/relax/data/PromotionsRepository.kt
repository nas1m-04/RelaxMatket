package tj.relax.data

import tj.relax.core.api.RelaxApiService
import tj.relax.core.api.dataOrThrow
import tj.relax.ui.screens.promotions.data.toDomain

class PromotionsRepository(
    private val api: RelaxApiService,
) {
    suspend fun getPromotions(): List<Promotion> =
        api.getPromotions().dataOrThrow().map { it.toDomain() }
}
