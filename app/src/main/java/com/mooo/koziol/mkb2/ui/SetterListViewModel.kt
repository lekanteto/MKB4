package com.mooo.koziol.mkb2.ui

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import com.mooo.koziol.mkb2.data.SetterRepository

class SetterListViewModel : ViewModel() {

    private val _searchText = MutableStateFlow("")
    val searchText = _searchText.asStateFlow()

    private val _setters = MutableStateFlow(listOf<String>())
    @OptIn(FlowPreview::class)
    val setters = searchText.debounce(700).combine(_setters) { searchText, _ ->
        SetterRepository.getSetters(searchText)
    }

    fun onSearchTextChange(text: String) {
        _searchText.value = text
    }
}