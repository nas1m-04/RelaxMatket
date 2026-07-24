package tj.relax.ui.screens.profile

import tj.relax.data.UserProfile

data class ProfileUiState(
    val profile: UserProfile? = null,
    val isLoading: Boolean = false,
    val isUploadingAvatar: Boolean = false,
    val branchName: String? = null,
    val error: String? = null,
    val loggedOut: Boolean = false,
)
