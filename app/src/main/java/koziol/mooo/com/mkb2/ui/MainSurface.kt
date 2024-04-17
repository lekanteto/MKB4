package koziol.mooo.com.mkb2.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import koziol.mooo.com.mkb2.data.ClimbsRepository

@Composable
fun MainSurface() {
    Surface(
        modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
    ) {
        val navController = rememberNavController()


        NavHost(navController = navController, startDestination = "climbs") {

            val destinations = mapOf(
                "climbs" to { navController.navigate("climbs") },
                "climbsFilter" to { navController.navigate("climbsFilter") },
                "holdsFilter" to { navController.navigate("holdsFilter") },
                "displayBoard" to { navController.navigate("displayBoard") },
            )

            //val filterViewModel = FilterViewModel()

            composable("climbs") { ListClimbsScreen(destinations = destinations) }
            composable("displayBoard") { BoardScreen(destinations = destinations) }
            composable("climbsFilter") { FilterClimbsScreen(destinations = destinations) }
            composable("holdsFilter") { FilterHoldsScreen(destinations = destinations) }
        }
    }
}