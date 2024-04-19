package koziol.mooo.com.mkb2.ui

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
import koziol.mooo.com.mkb2.R
import koziol.mooo.com.mkb2.data.RestClient

@Composable
fun LoginDialog(onDismissRequest: () -> Unit) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var revealPassword by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismissRequest) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
        ) {

            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                modifier = Modifier.padding(16.dp),
                label = { Text("Benutzername") },
                singleLine = true,
            )
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                modifier = Modifier.padding(16.dp),
                label = { Text("Passwort") },
                singleLine = true,
                trailingIcon = {
                    if (revealPassword) {
                        IconButton(
                            onClick = {
                                revealPassword = false
                            },
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.outline_visibility_off_24),
                                contentDescription = null
                            )
                        }
                    } else {
                        IconButton(
                            onClick = {
                                revealPassword = true
                            },
                        ) {

                            Icon(
                                painter = painterResource(id = R.drawable.outline_visibility_24),
                                contentDescription = null
                            )
                        }
                    }
                },
                visualTransformation = if (revealPassword) {
                    VisualTransformation.None
                } else {
                    PasswordVisualTransformation()
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
            )
            Row {
                Spacer(modifier = Modifier.weight(1f))
                FilledIconButton(modifier = Modifier.padding(16.dp), onClick = onDismissRequest) {
                    Icon(
                        painter = painterResource(id = R.drawable.outline_cancel_24),
                        contentDescription = "cancel"
                    )
                }
                FilledIconButton(modifier = Modifier.padding(16.dp), onClick = {
                    CoroutineScope(Dispatchers.IO).launch {
                        RestClient.login(username, password)
                        onDismissRequest.invoke()
                    }
                }) {
                    Icon(
                        painter = painterResource(id = R.drawable.outline_login_24),
                        contentDescription = "login"
                    )
                }

            }

        }
    }
}