package tj.relax.ui.screens.branch

import tj.relax.data.Branch

data class SelectBranchUiState(
    val isLoading: Boolean = true,
    val branches: List<Branch> = emptyList(),
    val selectedBranchId: Int? = null,
    val isSaving: Boolean = false,
    val error: String? = null,
    val saved: Boolean = false,
)
