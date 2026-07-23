package tj.relax.data

data class SupportTicket(
    val id: String,
    val message: String,
    val status: String,
    val createdAt: String,
    val resolvedAt: String?,
) {
    val isResolved: Boolean get() = status.equals("Resolved", ignoreCase = true)
}

data class CreateSupportTicketRequest(
    val message: String,
)
