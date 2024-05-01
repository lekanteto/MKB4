package com.mooo.koziol.mkb2.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun MainSurface() {
    Surface(
        modifier = Modifier.fillMaxSize(),
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
            composable("displayBoard") {
                BoardScreen(
                    navController = navController, destinations = destinations
                )
            }
            composable("climbsFilter") {
                FilterClimbsScreen(
                    navController = navController, destinations = destinations
                )
            }
            composable("holdsFilter") { FilterHoldsScreen(navController = navController) }
        }
    }
}