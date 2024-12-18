package com.mooo.koziol.mkb2.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mooo.koziol.mkb2.R
import com.mooo.koziol.mkb2.data.ClimbsRepository
import com.mooo.koziol.mkb2.data.RestClient
import java.util.Locale

@Composable
fun ListClimbsScreen(
    destinations: Map<String, () -> Unit>, listClimbsViewModel: ListClimbsViewModel = viewModel(
        //LocalContext.current as ComponentActivity
    )
) {
    val isDownloading by listClimbsViewModel.isDownloading.collectAsState()
    val isLoggedIn by listClimbsViewModel.isLoggedIn.collectAsState()
    val currentAngle by listClimbsViewModel.currentAngle.collectAsState(initial = 3)

    Scaffold(topBar = {
        ListClimbsTopBar(
            destinations, listClimbsViewModel::onSelectAngle, currentAngle
        )
    }, bottomBar = {
        ClimbsBottomBar(
            destinations,
            listClimbsViewModel::downloadSyncTables,
            isDownloading,
            false,
            isLoggedIn = isLoggedIn
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
                var index =
                    ClimbsRepository.climbs.value.indexOf(ClimbsRepository.currentClimb.value)
                if (index == -1) {
                    index = 0
                }
                listState.scrollToItem(index)
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
                                            "${climb.grade} %+.1f by ${climb.setter}",
                                            climb.deviation
                                        )
                                    )
                                },
                                trailingContent = {
                                    Text(
                                        String.format(
                                            Locale.GERMANY, "%.2f", climb.rating
                                        )
                                    )
                                })
                            HorizontalDivider()
                        }
                    }
                }
            } else {
                Box(modifier = Modifier.fillMaxSize()) {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        }
    })
}

@Composable
fun ClimbsBottomBar(
    destinations: Map<String, () -> Unit>,
    download: () -> Unit,
    isDownloading: Boolean,
    isLoggingIn: Boolean,
    isLoggedIn: Boolean,
) {
    var showLoginDialog by remember { mutableStateOf(false) }
    var showSessionDialog by remember { mutableStateOf(false) }

    BottomAppBar(actions = {
        NavigationBarItem(icon = {
            Icon(
                painter = painterResource(id = R.drawable.outline_new_window_24px),
                contentDescription = null
            )
        }, selected = false, onClick = { }, enabled = false)
        val painter = if (isLoggedIn) {
            painterResource(id = R.drawable.outline_badge_24)
        } else {
            painterResource(id = R.drawable.outline_login_24)
        }

        NavigationBarItem(icon = {
            Icon(
                painter = painter, contentDescription = null
            )
        }, selected = isLoggingIn, onClick = {
            if (isLoggedIn) {
                showSessionDialog = true
            } else {
                showLoginDialog = true
            }
        }, enabled = !isLoggingIn
        )

        val counter = RestClient.downLoadCount.collectAsStateWithLifecycle()
        NavigationBarItem(
            icon = {
                BadgedBox(badge = {
                    if (isDownloading) {
                        Badge {
                            if (counter.value > 0) {
                                Text("${counter.value}")
                            } else {
                                Icon(
                                    painter = painterResource(id = R.drawable.outline_downloading_24),
                                    contentDescription = null
                                )
                            }
                        }
                    }
                }) {
                    Icon(
                        painter = painterResource(id = R.drawable.outline_cloud_download_24),
                        contentDescription = null
                    )
                }
            }, selected = isDownloading, onClick = download, enabled = !isDownloading
        )


    }, floatingActionButton = {
        FloatingActionButton(
            onClick = destinations["climbsFilter"] ?: {},
            elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation(),
        ) {
            Icon(
                painter = painterResource(id = R.drawable.outline_filter_list_24),
                contentDescription = null
            )
        }
    })

    if (showLoginDialog) {
        LoginDialog {
            showLoginDialog = false
        }
    }
    if (showSessionDialog) {
        SessionDialog {
            showSessionDialog = false
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListClimbsTopBar(
    destinations: Map<String, () -> Unit>,
    onSelectAngle: (Int) -> Unit,
    currentAngle: Int,
) {
    var expanded by remember { mutableStateOf(false) }


    TopAppBar(title = {
        Text("MKB2")
    },

        navigationIcon = {
            OutlinedIconButton(onClick = {}, content = {
                Icon(
                    painter = painterResource(id = R.drawable.outline_bookmarks_24),
                    contentDescription = null
                )
            }, enabled = false
            )
        }, actions = {
            OutlinedButton(onClick = { expanded = true }) {
                Text("$currentAngle°")
                Spacer(modifier = Modifier.size(10.dp))
                Icon(
                    painter = painterResource(id = R.drawable.outline_screen_rotation_24),
                    contentDescription = null
                )

                DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    for (angle in 20..70 step 5) {
                        DropdownMenuItem(
                            text = { Text("$angle°") },
                            onClick = {
                                onSelectAngle(angle)
                                expanded = false
                            },
                        )
                    }

                }
            }
            OutlinedIconButton(onClick = destinations["climbsFilter"] ?: {}, content = {
                Icon(
                    painter = painterResource(id = R.drawable.outline_filter_list_24),
                    contentDescription = null
                )
            })
        })
}