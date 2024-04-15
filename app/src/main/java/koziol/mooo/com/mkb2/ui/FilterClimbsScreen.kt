package koziol.mooo.com.mkb2.ui

import android.util.Log
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import koziol.mooo.com.mkb2.R
import kotlin.math.roundToInt

@Composable
fun FilterClimbsScreen(
    destinations: Map<String, () -> Unit>,
    filterViewModel: FilterViewModel = viewModel(LocalContext.current as ComponentActivity)
) {
    Scaffold(topBar = {
        FilterClimbsTopBar(
            destinations, filterViewModel::applyAllFilters, filterViewModel::clearAllFilters
        )
    }, content = { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {

            val min by filterViewModel.minGrade.collectAsStateWithLifecycle()
            val max by filterViewModel.maxGrade.collectAsStateWithLifecycle()
            val minDev by filterViewModel.minDeviation.collectAsStateWithLifecycle()
            val maxDev by filterViewModel.maxDeviation.collectAsStateWithLifecycle()
            GradeRangeSelector(
                selectedMin = min,
                selectedMax = max,
                selectedMinDev = minDev,
                selectedMaxDev = maxDev,
                grades = filterViewModel.gradeNames,
                onRangeChanged = filterViewModel::updateGradeRange,
                onDeviationChanged = filterViewModel::updateDeviationRange
            )

            val minRating by filterViewModel.minRating.collectAsStateWithLifecycle()
            val maxRating by filterViewModel.maxRating.collectAsStateWithLifecycle()
            RatingRangeSelector(
                selectedMin = minRating,
                selectedMax = maxRating,
                onRangeChanged = filterViewModel::updateRatingRange
            )

            val minAscents by filterViewModel.minNumOfAscents.collectAsStateWithLifecycle()
            MinAscentsSelector(
                minAscents,
                options = filterViewModel.numOfAscentsOptions,
                onValueChanged = filterViewModel::updateMinNumOfAscents
            )

            val myAscents by filterViewModel.myAscents.collectAsStateWithLifecycle()
            val myTries by filterViewModel.myTries.collectAsStateWithLifecycle()
            val myBoulders by filterViewModel.myBoulders.collectAsStateWithLifecycle()
            MyClimbsFilter(
                ascentsOptions = myAscents,
                triesOptions = myTries,
                boulderFilter = myBoulders,
                onAscentsChanged = filterViewModel::updateMyAscents,
                onTriesChanged = filterViewModel::updateMyTries,
                onBouldersChanged = filterViewModel::updateMyBoulders
            )

            val theirAscents by filterViewModel.theirAscents.collectAsStateWithLifecycle()
            val theirTries by filterViewModel.theirTries.collectAsStateWithLifecycle()
            val theirBoulders by filterViewModel.theirBoulders.collectAsStateWithLifecycle()
            TheirClimbsFilter(
                ascentsOptions = theirAscents,
                triesOptions = theirTries,
                boulderFilter = theirBoulders,
                onAscentsChanged = filterViewModel::updateTheirAscents,
                onTriesChanged = filterViewModel::updateTheirTries,
                onBouldersChanged = filterViewModel::updateTheirBoulders
            )

            HoldsFilter(
                destinations["holdsFilter"],
                isSelected = filterViewModel.selectedHoldsList.isNotEmpty()
            )
        }
    })
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterClimbsTopBar(
    destinations: Map<String, () -> Unit>, onApplyFilter: () -> Unit, onClearFilter: () -> Unit
) {
    TopAppBar(colors = TopAppBarDefaults.topAppBarColors(
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        titleContentColor = MaterialTheme.colorScheme.primary,
    ), title = {
        Text("Filterkonfiguration")
    }, navigationIcon = {
        OutlinedIconButton(onClick = {
            Log.d("Mkb2", "leaving filterscreen")
            onApplyFilter()
            destinations["climbs"]?.invoke()
        }, content = {
            Icon(
                painter = painterResource(id = R.drawable.outline_filter_alt_24),
                contentDescription = "Apply filter",
            )
        })

    }, actions = {
        IconButton(onClick = { onClearFilter() }, content = {
            Icon(
                painter = painterResource(id = R.drawable.outline_filter_alt_off_24),
                contentDescription = "Clear filter"
            )
        })

    })
}

@Composable
fun GradeRangeSelector(
    selectedMin: Int,
    selectedMax: Int,
    selectedMinDev: Float,
    selectedMaxDev: Float,
    grades: Array<String>,
    onRangeChanged: (Int, Int) -> Unit,
    onDeviationChanged: (Float, Float) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(5.dp)
    ) {

        val minGrade = grades[selectedMin]
        val maxGrade = grades[selectedMax]

        Text("Schwierigkeit: $minGrade - $maxGrade")
        RangeSlider(
            modifier = Modifier.padding(20.dp),
            value = selectedMin.toFloat()..selectedMax.toFloat(),
            steps = grades.size - 2,
            onValueChange = { range ->
                onRangeChanged(
                    range.start.roundToInt(), range.endInclusive.roundToInt()
                )
            },
            valueRange = 0f..(grades.size - 1).toFloat(),
            onValueChangeFinished = { },
        )

        Text(
            String.format(
                "Grade deviation: %.1f - %.1f", selectedMinDev, selectedMaxDev
            )
        )
        RangeSlider(
            modifier = Modifier.padding(20.dp),
            value = selectedMinDev..selectedMaxDev,
            //steps = 9,
            onValueChange = { range -> onDeviationChanged(range.start, range.endInclusive) },
            valueRange = -0.5f..0.5f,
            onValueChangeFinished = {},
        )
    }
}

@Composable
fun RatingRangeSelector(
    selectedMin: Float, selectedMax: Float, onRangeChanged: (Float, Float) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(5.dp)
    ) {


        Text(
            String.format(
                "Rating: %.1f - %.1f", selectedMin, selectedMax
            )
        )
        RangeSlider(
            modifier = Modifier.padding(20.dp),
            value = selectedMin..selectedMax,
            //steps = 29,
            onValueChange = { range ->
                onRangeChanged(
                    range.start, range.endInclusive
                )
            },
            valueRange = 1f..3f,
            onValueChangeFinished = {},
        )
    }
}

@Composable
fun MinAscentsSelector(
    min: Int, options: Array<Int>, onValueChanged: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(5.dp)
    ) {
        Text(
            "Min number of ascents: ${options[min]}"
        )
        Slider(
            modifier = Modifier.padding(20.dp),
            value = min.toFloat(),
            steps = options.size - 2,
            onValueChange = { value ->
                onValueChanged(
                    value.roundToInt(),
                )
            },
            valueRange = 0f..(options.size - 1).toFloat(),
            onValueChangeFinished = { },
        )
    }
}

@Composable
fun MyClimbsFilter(
    ascentsOptions: FilterOptions,
    triesOptions: FilterOptions,
    boulderFilter: FilterOptions,
    onAscentsChanged: (FilterOptions) -> Unit,
    onTriesChanged: (FilterOptions) -> Unit,
    onBouldersChanged: (FilterOptions) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(5.dp)
    ) {
        FilterChip(
            modifier = Modifier.padding(5.dp),
            onClick = {
                onAscentsChanged(
                    when (ascentsOptions) {
                        FilterOptions.INCLUDE -> FilterOptions.EXCLUDE
                        FilterOptions.EXCLUDE -> FilterOptions.EXCLUSIVE
                        FilterOptions.EXCLUSIVE -> FilterOptions.INCLUDE
                    }
                )
            },

            label = {
                Text("My Ascents")
            },
            selected = ascentsOptions != FilterOptions.INCLUDE,
            leadingIcon = {
                when (ascentsOptions) {
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
            modifier = Modifier.padding(5.dp),
            onClick = {
                onTriesChanged(
                    when (triesOptions) {
                        FilterOptions.INCLUDE -> FilterOptions.EXCLUDE
                        FilterOptions.EXCLUDE -> FilterOptions.EXCLUSIVE
                        FilterOptions.EXCLUSIVE -> FilterOptions.INCLUDE
                    }
                )
            },

            label = {
                Text("My Tries")
            },
            selected = triesOptions != FilterOptions.INCLUDE,
            leadingIcon = {
                when (triesOptions) {
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
    }
}

@Composable
fun TheirClimbsFilter(
    ascentsOptions: FilterOptions,
    triesOptions: FilterOptions,
    boulderFilter: FilterOptions,
    onAscentsChanged: (FilterOptions) -> Unit,
    onTriesChanged: (FilterOptions) -> Unit,
    onBouldersChanged: (FilterOptions) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(5.dp)
    ) {
        FilterChip(
            modifier = Modifier.padding(5.dp),
            onClick = {
                onAscentsChanged(
                    when (ascentsOptions) {
                        FilterOptions.INCLUDE -> FilterOptions.EXCLUSIVE
                        FilterOptions.EXCLUDE -> FilterOptions.EXCLUSIVE
                        FilterOptions.EXCLUSIVE -> FilterOptions.INCLUDE
                    }
                )
            },

            label = {
                Text("Followees Ascents")
            },
            selected = ascentsOptions != FilterOptions.INCLUDE,
            leadingIcon = {
                when (ascentsOptions) {
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
            modifier = Modifier.padding(5.dp),
            onClick = {
                onTriesChanged(
                    when (triesOptions) {
                        FilterOptions.INCLUDE -> FilterOptions.EXCLUSIVE
                        FilterOptions.EXCLUDE -> FilterOptions.EXCLUSIVE
                        FilterOptions.EXCLUSIVE -> FilterOptions.INCLUDE
                    }
                )
            },

            label = {
                Text("Followees Tries")
            },
            selected = triesOptions != FilterOptions.INCLUDE,
            leadingIcon = {
                when (triesOptions) {
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
    }
}

@Composable
fun HoldsFilter(onClick: (() -> Unit)?, isSelected: Boolean) {
    FilterChip(
        modifier = Modifier.padding(5.dp),
        onClick = {
            if (onClick != null) {
                onClick()
            }
        },

        label = {
            Text("Filter holds")
        },
        selected = isSelected,
        leadingIcon = {
            Icon(
                painter = painterResource(id = R.drawable.background_dot_small_24px),
                contentDescription = "holds",
                modifier = Modifier.size(FilterChipDefaults.IconSize)
            )
        },
    )
}

enum class FilterOptions {
    INCLUDE, EXCLUDE, EXCLUSIVE
}