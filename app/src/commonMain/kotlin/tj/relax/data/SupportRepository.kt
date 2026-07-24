package tj.relax.data

import tj.relax.core.api.PagedResponse
import tj.relax.core.api.RelaxApiService
import tj.relax.core.api.dataOrThrow

class SupportRepository(
    private val api: RelaxApiService,
) {
    suspend fun createTicket(message: String): SupportTicket =
        api.createSupportTicket(CreateSupportTicketRequest(message)).dataOrThrow()

    suspend fun getTickets(page: Int = 1, pageSize: Int = 20): PagedResponse<SupportTicket> =
        api.getSupportTickets(page, pageSize).dataOrThrow()
}
