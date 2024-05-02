package com.mooo.koziol.mkb2.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.mooo.koziol.mkb2.R
import com.mooo.koziol.mkb2.data.ClimbsRepository
import com.mooo.koziol.mkb2.data.ConfigRepository
import com.mooo.koziol.mkb2.data.RestClient

@Composable
fun SessionDialog(onDismissRequest: () -> Unit) {
    var username by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismissRequest) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
        ) {
            LaunchedEffect(null) {
                username = ConfigRepository.getCurrentUsername() ?: ""
            }
            Text(text = username, modifier = Modifier.padding(10.dp))


            Row {
                Spacer(modifier = Modifier.weight(1f))
                FilledIconButton(modifier = Modifier.padding(16.dp), onClick = onDismissRequest) {
                    Icon(
                        painter = painterResource(id = R.drawable.outline_cancel_24),
                        contentDescription = null
                    )
                }
                FilledIconButton(modifier = Modifier.padding(16.dp), onClick = {
                    CoroutineScope(Dispatchers.IO).launch {
                        RestClient.logout(username)
                        onDismissRequest.invoke()
                    }
                }) {
                    Icon(
                        painter = painterResource(id = R.drawable.outline_logout_24),
                        contentDescription = null
                    )
                }

            }

        }
    }
}