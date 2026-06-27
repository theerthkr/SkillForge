package com.theerthkr.skillforge.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.theerthkr.skillforge.data.model.Category
import com.theerthkr.skillforge.data.repository.SkillforgeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.IOException

class HomeViewModel(
    private val repository: SkillforgeRepository = SkillforgeRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<List<Category>>>(UiState.Loading)
    val uiState: StateFlow<UiState<List<Category>>> = _uiState.asStateFlow()

    init {
        loadCategories()
    }

    fun loadCategories() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            _uiState.value = try {
                UiState.Success(repository.getCategories(forceRefresh = true))
            } catch (e: IOException) {
                UiState.Error("Couldn't connect. Check your internet and try again.")
            } catch (e: Exception) {
                UiState.Error("Something went wrong while loading courses.")
            }
        }
    }
}
