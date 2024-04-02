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
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
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
    filterClimbsViewModel: FilterClimbsViewModel = viewModel(LocalContext.current as ComponentActivity)
) {
    Scaffold(topBar = { FilterClimbsTopBar(destinations, filterClimbsViewModel::clearAllFilters) },
        content = { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
            ) {

                val min by filterClimbsViewModel.minGrade.collectAsStateWithLifecycle()
                val max by filterClimbsViewModel.maxGrade.collectAsStateWithLifecycle()
                val minDev by filterClimbsViewModel.minDeviation.collectAsStateWithLifecycle()
                val maxDev by filterClimbsViewModel.maxDeviation.collectAsStateWithLifecycle()
                GradeRangeSelector(
                    initialMin = min,
                    initialMax = max,
                    initialMinDeviation = minDev,
                    initialMaxDeviation = maxDev,
                    grades = filterClimbsViewModel.gradeNames,
                    onRangeChanged = filterClimbsViewModel::updateGradeRange,
                    onDeviationChanged = filterClimbsViewModel::updateDeviationRange
                )

                val minRating by filterClimbsViewModel.minRating.collectAsStateWithLifecycle()
                val maxRating by filterClimbsViewModel.maxRating.collectAsStateWithLifecycle()
                RatingRangeSelector(
                    initialMin = minRating,
                    initialMax = maxRating,
                    onRangeChanged = filterClimbsViewModel::updateRatingRange
                )

                val minAscents by filterClimbsViewModel.minNumOfAscents.collectAsStateWithLifecycle()
                MinAscentsSelector(
                    minAscents,
                    options = filterClimbsViewModel.numOfAscentsOptions,
                    onValueChanged = filterClimbsViewModel::updateMinNumOfAscents
                )

                val myAscents by filterClimbsViewModel.myAscents.collectAsStateWithLifecycle()
                val myTries by filterClimbsViewModel.myTries.collectAsStateWithLifecycle()
                val myBoulders by filterClimbsViewModel.myBoulders.collectAsStateWithLifecycle()
                MyClimbsFilter(
                    ascentsFilter = myAscents,
                    triesFilter = myTries,
                    boulderFilter = myBoulders,
                    onAscentsChanged = filterClimbsViewModel::updateMyAscents,
                    onTriesChanged = filterClimbsViewModel::updateMyTries,
                    onBouldersChanged = filterClimbsViewModel::updateMyBoulders
                )

                val theirAscents by filterClimbsViewModel.theirAscents.collectAsStateWithLifecycle()
                val theirTries by filterClimbsViewModel.theirTries.collectAsStateWithLifecycle()
                val theirBoulders by filterClimbsViewModel.theirBoulders.collectAsStateWithLifecycle()
                TheirClimbsFilter(
                    ascentsFilter = theirAscents,
                    triesFilter = theirTries,
                    boulderFilter = theirBoulders,
                    onAscentsChanged = filterClimbsViewModel::updateTheirAscents,
                    onTriesChanged = filterClimbsViewModel::updateTheirTries,
                    onBouldersChanged = filterClimbsViewModel::updateTheirBoulders
                )
            }
        })
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterClimbsTopBar(
    destinations: Map<String, () -> Unit>, onClearFilter: () -> Unit
) {
    TopAppBar(colors = TopAppBarDefaults.topAppBarColors(
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        titleContentColor = MaterialTheme.colorScheme.primary,
    ), title = {
        Text("Filterkonfiguration")
    }, navigationIcon = {
        FilledTonalIconButton(onClick = {
            Log.d("Mkb2", "leaving filterscreen")
            destinations["climbs"]?.invoke()
        }, content = {
            Icon(
                painter = painterResource(id = R.drawable.outline_filter_alt_24),
                contentDescription = "Apply filter"
            )
        })
    }, actions = {
        OutlinedIconButton(onClick = { onClearFilter() }, content = {
            Icon(
                painter = painterResource(id = R.drawable.outline_filter_alt_off_24),
                contentDescription = "Clear filter"
            )
        })

    })
}

@Composable
fun GradeRangeSelector(
    initialMin: Int,
    initialMax: Int,
    initialMinDeviation: Float,
    initialMaxDeviation: Float,
    grades: Array<String>,
    onRangeChanged: (Int, Int) -> Unit,
    onDeviationChanged: (Float, Float) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(5.dp)
    ) {

        val minGrade = grades[initialMin]
        val maxGrade = grades[initialMax]

        Text("Schwierigkeit: $minGrade - $maxGrade")
        RangeSlider(
            modifier = Modifier.padding(20.dp),
            value = initialMin.toFloat()..initialMax.toFloat(),
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
                "Grade deviation: %.1f - %.1f", initialMinDeviation, initialMaxDeviation
            )
        )
        RangeSlider(
            modifier = Modifier.padding(20.dp),
            value = initialMinDeviation..initialMaxDeviation,
            steps = 9,
            onValueChange = { range -> onDeviationChanged(range.start, range.endInclusive) },
            valueRange = -0.5f..0.5f,
            onValueChangeFinished = {},
        )
    }
}

@Composable
fun RatingRangeSelector(
    initialMin: Float, initialMax: Float, onRangeChanged: (Float, Float) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(5.dp)
    ) {


        Text(
            String.format(
                "Rating: %.1f - %.1f", initialMin, initialMax
            )
        )
        RangeSlider(
            modifier = Modifier.padding(20.dp),
            value = initialMin..initialMax,
            steps = 29,
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
    initialMin: Int, options: Array<Int>, onValueChanged: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(5.dp)
    ) {
        Text(
            "Min number of ascents: ${options[initialMin]}"
        )
        Slider(
            modifier = Modifier.padding(20.dp),
            value = initialMin.toFloat(),
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
    ascentsFilter: FilterOptions,
    triesFilter: FilterOptions,
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
                    when (ascentsFilter) {
                        FilterOptions.INCLUDE -> FilterOptions.EXCLUDE
                        FilterOptions.EXCLUDE -> FilterOptions.EXCLUSIVE
                        FilterOptions.EXCLUSIVE -> FilterOptions.INCLUDE
                    }
                )
            },

            label = {
                Text("My Ascents")
            },
            selected = ascentsFilter != FilterOptions.INCLUDE,
            leadingIcon = {
                when (ascentsFilter) {
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
                    when (triesFilter) {
                        FilterOptions.INCLUDE -> FilterOptions.EXCLUDE
                        FilterOptions.EXCLUDE -> FilterOptions.EXCLUSIVE
                        FilterOptions.EXCLUSIVE -> FilterOptions.INCLUDE
                    }
                )
            },

            label = {
                Text("My Tries")
            },
            selected = triesFilter != FilterOptions.INCLUDE,
            leadingIcon = {
                when (triesFilter) {
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
    ascentsFilter: FilterOptions,
    triesFilter: FilterOptions,
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
                    when (ascentsFilter) {
                        FilterOptions.INCLUDE -> FilterOptions.EXCLUSIVE
                        FilterOptions.EXCLUDE -> FilterOptions.EXCLUSIVE
                        FilterOptions.EXCLUSIVE -> FilterOptions.INCLUDE
                    }
                )
            },

            label = {
                Text("Followees Ascents")
            },
            selected = ascentsFilter != FilterOptions.INCLUDE,
            leadingIcon = {
                when (ascentsFilter) {
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
                    when (triesFilter) {
                        FilterOptions.INCLUDE -> FilterOptions.EXCLUSIVE
                        FilterOptions.EXCLUDE -> FilterOptions.EXCLUSIVE
                        FilterOptions.EXCLUSIVE -> FilterOptions.INCLUDE
                    }
                )
            },

            label = {
                Text("Followees Tries")
            },
            selected = triesFilter != FilterOptions.INCLUDE,
            leadingIcon = {
                when (triesFilter) {
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

enum class FilterOptions {
    INCLUDE, EXCLUDE, EXCLUSIVE
}