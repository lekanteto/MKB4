package koziol.mooo.com.mkb2.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.viewmodel.compose.viewModel
import koziol.mooo.com.mkb2.R
import koziol.mooo.com.mkb2.data.Climb

@Composable
fun ListClimbs(
    destinations: Map<String, () -> Unit>, climbsViewModel: ClimbsViewModel = viewModel()
) {
    val climbsUiState by climbsViewModel.uiState.collectAsState()

    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
        climbsViewModel.climbsList.forEach { climb: Climb ->
            ListItem(headlineContent = { Text(climb.name) },
                supportingContent = { Text(climb.grade) })
            HorizontalDivider()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListClimbsTopBar(
    destinations: Map<String, () -> Unit>,
) {
    TopAppBar(colors = TopAppBarDefaults.topAppBarColors(
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        titleContentColor = MaterialTheme.colorScheme.primary,
    ), title = {
        Text("Top app bar")
    }, navigationIcon = {
        OutlinedIconButton(onClick = { /*TODO*/ }, content = {
            Icon(
                painter = painterResource(id = R.drawable.outline_bookmarks_24),
                contentDescription = "Filter bookmarks"
            )
        })
    }, actions = {
        OutlinedIconButton(onClick = destinations["climbsFilter"] ?: {}, content = {
            Icon(
                painter = painterResource(id = R.drawable.outline_filter_list_24),
                contentDescription = "Filter climbs"
            )
        })

    })
}
