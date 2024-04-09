package koziol.mooo.com.mkb2.ui

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Face
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TextFieldDefaults.indicatorLine
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import koziol.mooo.com.mkb2.R
import koziol.mooo.com.mkb2.data.Climb
import koziol.mooo.com.mkb2.data.ClimbRepository

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListClimbsScreen(destinations: Map<String, () -> Unit>) {

    Scaffold(topBar = { ListClimbsTopBar(destinations) }, bottomBar = {
        ClimbsBottomBar(destinations)
    }, content = { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            ClimbsList(destinations)
        }
    })
}

@Composable
fun ClimbsBottomBar(
    destinations: Map<String, () -> Unit>
) {
    BottomAppBar(actions = {
        NavigationBarItem(icon = {
            Icon(
                painter = painterResource(id = R.drawable.outline_new_window_24px),
                contentDescription = "Set Boulder"
            )
        },
            //label = { Text("Neuer Boulder") },
            selected = false, onClick = { })
        NavigationBarItem(icon = { Icon(Icons.Outlined.Face, contentDescription = "about you") },
            //label = { Text("Mein Profil") },
            selected = false, onClick = { })

    }, floatingActionButton = {
        FloatingActionButton(
            onClick = destinations["climbsFilter"] ?: {},
            containerColor = BottomAppBarDefaults.bottomAppBarFabColor,
            elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation(),
        ) {
            Icon(
                painter = painterResource(id = R.drawable.outline_filter_list_24),
                contentDescription = "Filter climbs"
            )
        }
    })
}

@Composable
fun ClimbsList(
    destinations: Map<String, () -> Unit>, climbsViewModel: ClimbsViewModel = viewModel()
) {

    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
        climbsViewModel.climbsList.forEach { climb: Climb ->
            ListItem(modifier = Modifier.clickable(onClick = {
                Log.d("Mkb2", "Climb in list tapped")
                ClimbRepository.currentClimb = climb
                destinations["displayBoard"]?.invoke()
            }),
                headlineContent = { Text(climb.name) },
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
    LargeTopAppBar(colors = TopAppBarDefaults.topAppBarColors(
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        titleContentColor = MaterialTheme.colorScheme.primary,

        ), title = {
        var text by remember { mutableStateOf("") }

        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            value = text,
            onValueChange = { text = it },
            label = { Text("Search") },
            trailingIcon = {
                Icon(

                    painter = painterResource(id = R.drawable.outline_cancel_24),
                    contentDescription = null,
                    modifier = Modifier.clickable(onClick = {text = ""}),
                )
            }
        )

    }, navigationIcon = {
        IconButton(onClick = { /*TODO*/ }, content = {
            Icon(
                painter = painterResource(id = R.drawable.outline_bookmarks_24),
                contentDescription = "Filter bookmarks"
            )
        })
    }, actions = {})
}