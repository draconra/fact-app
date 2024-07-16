package jp.speakbuddy.edisonandroidexercise.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import jp.speakbuddy.edisonandroidexercise.model.FactResponse
import jp.speakbuddy.edisonandroidexercise.network.FactService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val factService: FactService
) : ViewModel() {

    private val _uiState = MutableStateFlow<SearchUiState>(SearchUiState.Idle)
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    fun searchFacts(query: String, limit: Int = 50, maxLength: Int = 500) {
        viewModelScope.launch {
            _uiState.value = SearchUiState.Loading
            try {
                val factsResponse = factService.getFacts(limit, maxLength)
                val filteredFacts = factsResponse.data.filter { it.fact.contains(query, ignoreCase = true) }
                if (filteredFacts.isNotEmpty()) {
                    _uiState.value = SearchUiState.Success(filteredFacts)
                } else {
                    _uiState.value = SearchUiState.NoData("No facts found for query: $query")
                }
            } catch (e: Throwable) {
                _uiState.value = SearchUiState.Error(e.message ?: "An unknown error occurred")
            }
        }
    }
}
