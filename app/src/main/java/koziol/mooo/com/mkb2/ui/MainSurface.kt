package koziol.mooo.com.mkb2.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun MainSurface() {
    Surface(
        modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
    ) {
        val navController = rememberNavController()


        NavHost(navController = navController, startDestination = "climbs") {

            val destinations = mapOf(
                "climbs" to { navController.navigate("climbs") },
                "climbsFilter" to { navController.navigate("climbs") },
                "climbsFilter" to { navController.navigate("climbsFilter") },
            )

            composable("climbs") { ListClimbsScreen(destinations = destinations) }
            composable("test") { TestDestination() }
            composable("climbsFilter") { FilterClimbsScreen(destinations = destinations) }
            // Add more destinations similarly.

        }
    }
}

@Composable
fun MainNavigationBar(
    destinations: Map<String, () -> Unit>
) {
    NavigationBar {
        NavigationBarItem(icon = {
            Icon(
                Icons.Outlined.Search, contentDescription = "Search Climbs"
            )
        },
            label = { Text("Suche Boulder") },
            selected = true,
            onClick = destinations["climbs"] ?: {})
        NavigationBarItem(icon = {
            Icon(
                Icons.Filled.Add, contentDescription = "Set Boulder"
            )
        },
            label = { Text("Erstelle Boulder") },
            selected = false,
            onClick = destinations["climbs"] ?: {})
    }

}