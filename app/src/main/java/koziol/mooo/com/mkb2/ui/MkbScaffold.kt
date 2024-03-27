package koziol.mooo.com.mkb2.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Face
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Face
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MkbScaffold() {
    var presses by remember { mutableIntStateOf(0) }

    Scaffold(
        topBar = {
            ClimbsTopBar()
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(icon = {
                    Icon(
                        Icons.Outlined.Search, contentDescription = "Search Climbs"
                    )
                }, label = { Text("Search Climbs") }, selected = false, onClick = { })
                NavigationBarItem(icon = {
                    Icon(
                        Icons.Filled.Add, contentDescription = "Set Boulder"
                    )
                }, label = { Text("Set Boulder") }, selected = true, onClick = { })
                NavigationBarItem(icon = {
                    Icon(
                        Icons.Filled.Face, contentDescription = "About you"
                    )
                }, label = { Text("About you") }, selected = true, onClick = { })
            }
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            //KilterBard()
            Climbs()
        }
    }
}