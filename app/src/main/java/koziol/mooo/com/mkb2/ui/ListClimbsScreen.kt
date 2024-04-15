package koziol.mooo.com.mkb2.ui

import android.util.Log
import androidx.activity.ComponentActivity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Face
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import koziol.mooo.com.mkb2.R
import koziol.mooo.com.mkb2.data.ClimbsRepository

@Composable
fun ListClimbsScreen(
    destinations: Map<String, () -> Unit>, listClimbsViewModel: ListClimbsViewModel = viewModel(
        LocalContext.current as ComponentActivity
    )
) {

    Scaffold(topBar = { ListClimbsTopBar(destinations) }, bottomBar = {
        ClimbsBottomBar(destinations)
    }, content = { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding)

        ) {
            val searchText by listClimbsViewModel.searchText.collectAsState()
            val isSearching by listClimbsViewModel.isSearching.collectAsState()
            val climbs by listClimbsViewModel.climbList.collectAsState()
            TextField(
                value = searchText,
                placeholder = { Text("Search") },
                onValueChange = listClimbsViewModel::onSearchTextChange,
                modifier = Modifier.fillMaxWidth(),
                //leadingIcon = {Icon(imageVector = Icons.Outlined.Search, null)},
                trailingIcon = {
                    if (searchText.isNotEmpty()) {
                        Icon(painterResource(id = R.drawable.outline_cancel_24),
                            null,
                            modifier = Modifier.clickable { listClimbsViewModel.onSearchTextChange("") })
                    }
                },
                singleLine = true,
            )
            LazyColumn {
                if (climbs.isEmpty()) {
                    item { Text("Nothing found") }
                } else {
                    items(climbs) { climb ->
                        ListItem(modifier = Modifier.clickable(onClick = {
                            Log.d("Mkb2", "Climb in list tapped")
                            ClimbsRepository.currentClimb = climb
                            destinations["displayBoard"]?.invoke()
                        }),
                            overlineContent = { Text("${climb.ascents}") },
                            headlineContent = { Text(climb.name) },
                            supportingContent = {
                                Text(
                                    String.format(
                                        "${climb.grade} %.1f set by ${climb.setter}",
                                        climb.deviation
                                    )
                                )
                            },

                            trailingContent = {
                                Text(
                                    String.format(
                                        "%.2f", climb.rating
                                    )
                                )
                            })
                        HorizontalDivider()
                    }

                }
            }
        }
    })
}

@Composable
fun ClimbsBottomBar(
    destinations: Map<String, () -> Unit>
) {
    BottomAppBar(
        actions = {
            NavigationBarItem(icon = {
                Icon(
                    painter = painterResource(id = R.drawable.outline_new_window_24px),
                    contentDescription = "Set Boulder"
                )
            },
                //label = { Text("Neuer Boulder") },
                selected = false, onClick = { })
            NavigationBarItem(icon = {
                Icon(
                    Icons.Outlined.Face, contentDescription = "about you"
                )
            },
                //label = { Text("Mein Profil") },
                selected = false, onClick = { })

        },
        /*        floatingActionButton = {
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
                }*/
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListClimbsTopBar(
    destinations: Map<String, () -> Unit>,
) {
    TopAppBar(title = {
        Text("MKB2")
    },

        navigationIcon = {
            OutlinedIconButton(onClick = {}, content = {
                Icon(
                    painter = painterResource(id = R.drawable.outline_bookmarks_24),
                    contentDescription = "Filter bookmarks"
                )
            })
        }, actions = {
            OutlinedButton(onClick = { /* do something */ }) {
                Text("40°")
                Spacer(modifier = Modifier.size(10.dp))
                Icon(
                    painter = painterResource(id = R.drawable.outline_screen_rotation_24),
                    contentDescription = "Localized description"
                )
            }
            OutlinedIconButton(onClick = destinations["climbsFilter"] ?: {}, content = {
                Icon(
                    painter = painterResource(id = R.drawable.outline_filter_list_24),
                    contentDescription = "Filter bookmarks"
                )
            })

        })
}