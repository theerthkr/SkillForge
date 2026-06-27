package com.theerthkr.skillforge.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.theerthkr.skillforge.data.model.Course
import com.theerthkr.skillforge.data.repository.SkillforgeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.IOException

class CourseDetailViewModel(
    private val courseId: String,
    private val repository: SkillforgeRepository = SkillforgeRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<Course>>(UiState.Loading)
    val uiState: StateFlow<UiState<Course>> = _uiState.asStateFlow()

    init {
        loadCourse()
    }

    fun loadCourse() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            _uiState.value = try {
                val course = repository.getCourseById(courseId)
                if (course != null) {
                    UiState.Success(course)
                } else {
                    UiState.Error("Couldn't find that course.")
                }
            } catch (e: IOException) {
                UiState.Error("Couldn't connect. Check your internet and try again.")
            } catch (e: Exception) {
                UiState.Error("Something went wrong while loading this course.")
            }
        }
    }
}
