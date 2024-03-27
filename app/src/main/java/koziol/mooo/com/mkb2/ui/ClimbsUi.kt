package koziol.mooo.com.mkb2.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.InputChip
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import koziol.mooo.com.mkb2.R
import koziol.mooo.com.mkb2.data.Climb

@Composable
fun Climbs(climbsViewModel: ClimbsViewModel = viewModel()) {
    val climbsUiState by climbsViewModel.uiState.collectAsState()

    Column {

        climbsViewModel.climbsList.forEach { climb: Climb ->
            ListItem(headlineContent = { Text(climb.name) },
                supportingContent = { Text(climb.grade) })
            HorizontalDivider()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClimbsTopBar() {
    val showFilterDialog = remember {
        mutableStateOf(false)
    }
    TopAppBar(colors = TopAppBarDefaults.topAppBarColors(
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        titleContentColor = MaterialTheme.colorScheme.primary,
    ), title = {
        Text("Top app bar")
    }, navigationIcon = {
        IconButton(onClick = { /*TODO*/ }, content = {
            Icon(
                painter = painterResource(id = R.drawable.outline_bookmarks_24),
                contentDescription = "Filter bookmarks"
            )
        })
    }, actions = {
        IconButton(onClick = { showFilterDialog.value = true }, content = {
            Icon(
                painter = painterResource(id = R.drawable.outline_filter_list_24),
                contentDescription = "Filter climbs"
            )
        })

    })

    if (showFilterDialog.value) {
        FilterDialog()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterDialog() {
    Dialog(properties = DialogProperties(usePlatformDefaultWidth = false),
        onDismissRequest = { /*TODO*/ }) {
        Surface {
            var sliderPosition by remember { mutableStateOf(0f..100f) }
            var myClimbsFilter by remember {
                mutableStateOf(FilterOptions.INCLUDE)
            }
            var myTriesFilter by remember {
                mutableStateOf(FilterOptions.INCLUDE)
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(5.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text("Grade")
                RangeSlider(
                    modifier = Modifier.padding(20.dp),
                    value = sliderPosition,
                    steps = 23,
                    onValueChange = { range -> sliderPosition = range },
                    valueRange = 10f..33f,
                    onValueChangeFinished = {
                        // launch some business logic update with the state you hold
                        // viewModel.updateSelectedSliderValue(sliderPosition)
                    },
                )
                Text("Grade deviation")
                RangeSlider(
                    modifier = Modifier.padding(20.dp),
                    value = sliderPosition,
                    steps = 9,
                    onValueChange = { range -> sliderPosition = range },
                    valueRange = -0.5f..0.5f,
                    onValueChangeFinished = {
                        // launch some business logic update with the state you hold
                        // viewModel.updateSelectedSliderValue(sliderPosition)
                    },
                )
                Text("Rating 1 - 3")
                RangeSlider(
                    modifier = Modifier.padding(20.dp),
                    value = sliderPosition,
                    steps = 30,
                    onValueChange = { range -> sliderPosition = range },
                    valueRange = 1f..3f,
                    onValueChangeFinished = {
                        // launch some business logic update with the state you hold
                        // viewModel.updateSelectedSliderValue(sliderPosition)
                    },
                )

                Text("Min number of ascents")
                Slider(
                    modifier = Modifier.padding(20.dp),
                    value = 10f,
                    steps = 5,
                    onValueChange = { },
                    valueRange = 1f..100f,
                    onValueChangeFinished = {
                        // launch some business logic update with the state you hold
                        // viewModel.updateSelectedSliderValue(sliderPosition)
                    },
                )

                FilterChip(
                    onClick = {
                        myClimbsFilter = when (myClimbsFilter) {
                            FilterOptions.INCLUDE -> FilterOptions.EXCLUDE
                            FilterOptions.EXCLUDE -> FilterOptions.EXCLUSIVE
                            FilterOptions.EXCLUSIVE -> FilterOptions.INCLUDE
                        }
                    },

                    label = {
                        Text("My Ascents")
                    },
                    selected = myClimbsFilter != FilterOptions.INCLUDE,
                    leadingIcon = {
                        when (myClimbsFilter) {
                            FilterOptions.INCLUDE -> Icon(
                                painter = painterResource(id = R.drawable.outline_visibility_24),
                                contentDescription = "Include",
                                modifier = Modifier.size(FilterChipDefaults.IconSize)
                            )

                            FilterOptions.EXCLUDE -> Icon(
                                painter = painterResource(id = R.drawable.outline_visibility_off_24),
                                contentDescription = "Include",
                                modifier = Modifier.size(FilterChipDefaults.IconSize)
                            )

                            FilterOptions.EXCLUSIVE -> Icon(
                                painter = painterResource(id = R.drawable.outline_filter_alt_24),
                                contentDescription = "Include",
                                modifier = Modifier.size(FilterChipDefaults.IconSize)
                            )

                        }
                    },
                )

                FilterChip(
                    onClick = {
                        myTriesFilter = when (myTriesFilter) {
                            FilterOptions.INCLUDE -> FilterOptions.EXCLUDE
                            FilterOptions.EXCLUDE -> FilterOptions.EXCLUSIVE
                            FilterOptions.EXCLUSIVE -> FilterOptions.INCLUDE
                        }
                    },

                    label = {
                        Text("My Tries")
                    },
                    selected = myTriesFilter != FilterOptions.INCLUDE,
                    leadingIcon = {
                        when (myTriesFilter) {
                            FilterOptions.INCLUDE -> Icon(
                                painter = painterResource(id = R.drawable.outline_visibility_24),
                                contentDescription = "Include",
                                modifier = Modifier.size(FilterChipDefaults.IconSize)
                            )

                            FilterOptions.EXCLUDE -> Icon(
                                painter = painterResource(id = R.drawable.outline_visibility_off_24),
                                contentDescription = "Include",
                                modifier = Modifier.size(FilterChipDefaults.IconSize)
                            )

                            FilterOptions.EXCLUSIVE -> Icon(
                                painter = painterResource(id = R.drawable.outline_filter_alt_24),
                                contentDescription = "Include",
                                modifier = Modifier.size(FilterChipDefaults.IconSize)
                            )

                        }
                    },
                )

                FilterChip(
                    onClick = {
                        myTriesFilter = when (myTriesFilter) {
                            FilterOptions.INCLUDE -> FilterOptions.EXCLUSIVE
                            FilterOptions.EXCLUDE -> FilterOptions.INCLUDE
                            FilterOptions.EXCLUSIVE -> FilterOptions.INCLUDE
                        }
                    },

                    label = {
                        Text("Climbed by my followees")
                    },
                    selected = myTriesFilter != FilterOptions.INCLUDE,
                    leadingIcon = {
                        when (myTriesFilter) {
                            FilterOptions.INCLUDE -> Icon(
                                painter = painterResource(id = R.drawable.outline_visibility_24),
                                contentDescription = "Include",
                                modifier = Modifier.size(FilterChipDefaults.IconSize)
                            )

                            FilterOptions.EXCLUDE -> Icon(
                                painter = painterResource(id = R.drawable.outline_visibility_off_24),
                                contentDescription = "Include",
                                modifier = Modifier.size(FilterChipDefaults.IconSize)
                            )

                            FilterOptions.EXCLUSIVE -> Icon(
                                painter = painterResource(id = R.drawable.outline_filter_alt_24),
                                contentDescription = "Include",
                                modifier = Modifier.size(FilterChipDefaults.IconSize)
                            )

                        }
                    },
                )

                FilterChip(
                    onClick = {
                        myTriesFilter = when (myTriesFilter) {
                            FilterOptions.INCLUDE -> FilterOptions.EXCLUSIVE
                            FilterOptions.EXCLUDE -> FilterOptions.INCLUDE
                            FilterOptions.EXCLUSIVE -> FilterOptions.INCLUDE
                        }
                    },

                    label = {
                        Text("Set by my followees")
                    },
                    selected = myTriesFilter != FilterOptions.INCLUDE,
                    leadingIcon = {
                        when (myTriesFilter) {
                            FilterOptions.INCLUDE -> Icon(
                                painter = painterResource(id = R.drawable.outline_visibility_24),
                                contentDescription = "Include",
                                modifier = Modifier.size(FilterChipDefaults.IconSize)
                            )

                            FilterOptions.EXCLUDE -> Icon(
                                painter = painterResource(id = R.drawable.outline_visibility_off_24),
                                contentDescription = "Include",
                                modifier = Modifier.size(FilterChipDefaults.IconSize)
                            )

                            FilterOptions.EXCLUSIVE -> Icon(
                                painter = painterResource(id = R.drawable.outline_filter_alt_24),
                                contentDescription = "Include",
                                modifier = Modifier.size(FilterChipDefaults.IconSize)
                            )

                        }
                    },
                )

                InputChip(
                    onClick = {
                        myTriesFilter = when (myTriesFilter) {
                            FilterOptions.INCLUDE -> FilterOptions.EXCLUSIVE
                            FilterOptions.EXCLUDE -> FilterOptions.INCLUDE
                            FilterOptions.EXCLUSIVE -> FilterOptions.INCLUDE
                        }
                    },

                    label = {
                        Text("Setter")
                    },
                    selected = myTriesFilter != FilterOptions.INCLUDE,
                    leadingIcon = {
                        when (myTriesFilter) {
                            FilterOptions.INCLUDE -> Icon(
                                painter = painterResource(id = R.drawable.outline_search_24),
                                contentDescription = "Include",
                                modifier = Modifier.size(FilterChipDefaults.IconSize)
                            )

                            FilterOptions.EXCLUDE -> Icon(
                                painter = painterResource(id = R.drawable.outline_visibility_off_24),
                                contentDescription = "Include",
                                modifier = Modifier.size(FilterChipDefaults.IconSize)
                            )

                            FilterOptions.EXCLUSIVE -> Icon(
                                painter = painterResource(id = R.drawable.outline_filter_alt_24),
                                contentDescription = "Include",
                                modifier = Modifier.size(FilterChipDefaults.IconSize)
                            )

                        }
                    },
                )

                Text("circuit")
                Text("only selected holds")
                Text("Sort by")
                Slider(
                    modifier = Modifier.padding(20.dp),
                    value = 2f,
                    steps = 4,
                    onValueChange = { },
                    valueRange = 1f..3f,
                    onValueChangeFinished = {
                        // launch some business logic update with the state you hold
                        // viewModel.updateSelectedSliderValue(sliderPosition)
                    },
                )
            }
        }
    }
}

enum class FilterOptions {
    INCLUDE, EXCLUDE, EXCLUSIVE
}