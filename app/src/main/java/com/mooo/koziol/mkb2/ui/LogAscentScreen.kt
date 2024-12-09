package com.mooo.koziol.mkb2.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.room.util.TableInfo
import java.util.Locale
import kotlin.math.roundToInt

@Composable
fun LogAscentScreen() {
    Scaffold() {paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {

        }

    }
}

@Composable
fun GradeSelector() {
    Column {
        Text( "Grade")
        
    }
}