package com.mooo.koziol.mkb2.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mooo.koziol.mkb2.R
import com.mooo.koziol.mkb2.data.ClimbsRepository

@Composable
fun ListClimbsScreen(
    destinations: Map<String, () -> Unit>, listClimbsViewModel: ListClimbsViewModel = viewModel(
        //LocalContext.current as ComponentActivity
    )
) {
    val isDownloading by listClimbsViewModel.isDownloading.collectAsState()

    Scaffold(topBar = { ListClimbsTopBar(destinations) }, bottomBar = {
        ClimbsBottomBar(
            destinations, listClimbsViewModel::downloadSyncTables, isDownloading, false
        )
    }, content = { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding)

        ) {
            val searchText by listClimbsViewModel.searchText.collectAsState()
            val isSearching by listClimbsViewModel.isSearching.collectAsState()
            val climbs by listClimbsViewModel.climbList.collectAsState(emptyList())
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

            val listState = rememberLazyListState()
            LaunchedEffect(key1 = ClimbsRepository.currentClimb.collectAsState()) {
                val index =
                    ClimbsRepository.climbs.value.indexOf(ClimbsRepository.currentClimb.value)
                if(index != -1) {
                    listState.scrollToItem(index)
                }
            }



            if (!isSearching) {
                LazyColumn(state = listState) {
                    if (climbs.isEmpty()) {
                        item { Text("Nothing found") }
                    } else {
                        items(climbs) { climb ->
                            ListItem(modifier = Modifier.clickable(onClick = {

                                ClimbsRepository.currentClimb.value = climb
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
            } else {
                Text("Searching...")
            }

        }
    })
}

@Composable
fun ClimbsBottomBar(
    destinations: Map<String, () -> Unit>,
    download: () -> Unit,
    isDownloading: Boolean,
    isLoggingIn: Boolean
) {
    val showLoginDialog = remember { mutableStateOf(false) }

    BottomAppBar(actions = {
        NavigationBarItem(icon = {
            Icon(
                painter = painterResource(id = R.drawable.outline_new_window_24px),
                contentDescription = "Set Boulder"
            )
        }, selected = false, onClick = { }, enabled = false)
        NavigationBarItem(icon = {
            Icon(
                painter = painterResource(id = R.drawable.outline_login_24),
                contentDescription = "Log in on server"
            )
        },
            selected = isLoggingIn,
            onClick = { showLoginDialog.value = true },
            enabled = !isLoggingIn
        )
        NavigationBarItem(
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.outline_cloud_download_24),
                    contentDescription = "Download data from server"
                )
            }, selected = isDownloading, onClick = download, enabled = !isDownloading
        )


    }, floatingActionButton = {
        FloatingActionButton(
            onClick = destinations["climbsFilter"] ?: {},
            elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation(),
        ) {
            Icon(
                painter = painterResource(id = R.drawable.outline_filter_list_24),
                contentDescription = "Filter climbs"
            )
        }
    })

    if (showLoginDialog.value) {
        LoginDialog {
            showLoginDialog.value = false
        }
    }
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