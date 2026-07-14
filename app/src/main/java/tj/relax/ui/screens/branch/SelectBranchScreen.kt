package tj.relax.ui.screens.branch

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Store
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import tj.relax.R
import tj.relax.data.Branch
import tj.relax.ui.components.RelaxTopBar
import tj.relax.ui.theme.*

@Composable
fun SelectBranchScreen(
    isOnboarding: Boolean,
    onDone: () -> Unit,
    onBack: () -> Unit = {},
    viewModel: SelectBranchViewModel = hiltViewModel(),
) {
    val state = viewModel.uiState

    LaunchedEffect(state.saved) {
        if (state.saved) onDone()
    }

    Column(modifier = Modifier.fillMaxSize().background(RelaxBackground)) {
        if (isOnboarding) {
            Box(modifier = Modifier.fillMaxWidth().background(RelaxWhite)) {
                Column(modifier = Modifier.statusBarsPadding().padding(20.dp)) {
                    Text(
                        stringResource(R.string.select_branch_title),
                        style = MaterialTheme.typography.headlineSmall,
                        color = RelaxTextPrimary,
                        fontWeight = FontWeight.Bold,
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        stringResource(R.string.select_branch_subtitle),
                        style = MaterialTheme.typography.bodyMedium,
                        color = RelaxTextSecondary,
                    )
                }
            }
        } else {
            Box(modifier = Modifier.background(RelaxWhite)) {
                RelaxTopBar(title = stringResource(R.string.select_branch_title), onBack = onBack)
            }
        }

        if (state.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = RelaxDark)
            }
            return@Column
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            state.branches.forEach { branch ->
                BranchRow(
                    branch = branch,
                    selected = branch.id == state.selectedBranchId,
                    onClick = { viewModel.selectBranch(branch.id) },
                )
            }
            if (state.branches.isEmpty()) {
                Text(
                    stringResource(R.string.select_branch_empty),
                    style = MaterialTheme.typography.bodyMedium,
                    color = RelaxTextSecondary,
                )
            }
            state.error?.let {
                Text(it, color = RelaxRed, style = MaterialTheme.typography.bodySmall)
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(RelaxWhite)
                .navigationBarsPadding()
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = { viewModel.confirm() },
                    enabled = !state.isSaving && state.selectedBranchId != null,
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = RelaxRed),
                ) {
                    if (state.isSaving) {
                        CircularProgressIndicator(modifier = Modifier.size(22.dp), color = RelaxWhite, strokeWidth = 2.dp)
                    } else {
                        Text(stringResource(R.string.select_branch_confirm), fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }
                if (isOnboarding) {
                    TextButton(onClick = onDone, modifier = Modifier.fillMaxWidth()) {
                        Text(stringResource(R.string.select_branch_skip), color = RelaxTextSecondary)
                    }
                }
            }
        }
    }
}

@Composable
private fun BranchRow(branch: Branch, selected: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(if (selected) RelaxDark.copy(alpha = 0.06f) else RelaxWhite)
            .border(width = 1.5.dp, color = if (selected) RelaxDark else RelaxDivider, shape = RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier.size(44.dp).clip(RoundedCornerShape(12.dp)).background(if (selected) RelaxDark else RelaxSurfaceAlt),
            contentAlignment = Alignment.Center,
        ) {
            Icon(Icons.Rounded.Store, null, tint = if (selected) RelaxWhite else RelaxTextSecondary, modifier = Modifier.size(22.dp))
        }
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(branch.name, style = MaterialTheme.typography.titleSmall, color = RelaxTextPrimary, fontWeight = FontWeight.Bold)
            Text(branch.address, style = MaterialTheme.typography.bodySmall, color = RelaxTextSecondary)
        }
        if (selected) {
            Icon(Icons.Rounded.CheckCircle, null, tint = RelaxDark, modifier = Modifier.size(22.dp))
        }
    }
}
