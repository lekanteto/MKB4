package com.mooo.koziol.mkb2.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mooo.koziol.mkb2.R

@Composable
fun SetterListDialog(
    onSelect: (String) -> Unit,
    onDismissRequest: () -> Unit,
    setterListViewModel: SetterListViewModel = viewModel()
) {
    Dialog(onDismissRequest = onDismissRequest) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
        ) {

            val searchText by setterListViewModel.searchText.collectAsState()
            val setters by setterListViewModel.setters.collectAsState(initial = emptyList())

            Row() {
                IconButton(onClick = {
                    onSelect("")
                    (onDismissRequest)()
                }) {
                    Icon(painterResource(id = R.drawable.outline_filter_alt_off_24), null)
                }
                TextField(
                    value = searchText,
                    placeholder = { Text("Setter") },
                    onValueChange = setterListViewModel::onSearchTextChange,
                    modifier = Modifier.weight(1f, false),
                    //leadingIcon = {Icon(imageVector = Icons.Outlined.Search, null)},
                    trailingIcon = {
                        if (searchText.isNotEmpty()) {
                            Icon(painterResource(id = R.drawable.outline_cancel_24),
                                null,
                                modifier = Modifier.clickable {
                                    setterListViewModel.onSearchTextChange(
                                        ""
                                    )
                                })
                        }
                    },
                    singleLine = true,
                )
            }

            LazyColumn(
                modifier = Modifier.fillMaxHeight(0.7f)
            ) {
                if (setters.isEmpty()) {
                    item { Text("Nothing found") }
                } else {
                    items(setters) { setter ->
                        ListItem(
                            modifier = Modifier.clickable(onClick = {
                                onSelect(setter)
                                (onDismissRequest)()
                            }),
                            headlineContent = { Text(setter) },
                        )
                        HorizontalDivider()
                    }

                }
            }
        }
    }
}