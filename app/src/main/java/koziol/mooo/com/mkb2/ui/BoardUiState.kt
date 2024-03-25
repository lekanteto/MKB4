package koziol.mooo.com.mkb2.ui

import koziol.mooo.com.mkb2.data.KBHold

data class BoardUiState(
    val selectedHoldsList: List<KBHold> = mutableListOf()
)
