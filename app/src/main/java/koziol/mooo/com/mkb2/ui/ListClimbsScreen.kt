package koziol.mooo.com.mkb2.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListClimbsScreen(destinations: Map<String, () -> Unit>) {

    Scaffold(topBar = { ListClimbsTopBar(destinations) }, bottomBar = {
        MainNavigationBar(destinations)
    }, content = { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            ListClimbs(destinations)
        }
    })
}

@Composable
fun TestDestination() {
    Text("testdest")
}