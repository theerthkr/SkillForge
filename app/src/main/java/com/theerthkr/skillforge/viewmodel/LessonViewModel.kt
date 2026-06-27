package com.theerthkr.skillforge.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.theerthkr.skillforge.data.model.Course
import com.theerthkr.skillforge.data.model.Lesson
import com.theerthkr.skillforge.data.repository.SkillforgeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.IOException

data class LessonScreenData(
    val course: Course,
    val currentLesson: Lesson
)

class LessonViewModel(
    private val courseId: String,
    initialLessonId: String,
    private val repository: SkillforgeRepository = SkillforgeRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<LessonScreenData>>(UiState.Loading)
    val uiState: StateFlow<UiState<LessonScreenData>> = _uiState.asStateFlow()

    init {
        loadLesson(initialLessonId)
    }

    /** Lets the user tap another lesson in the list without leaving the screen. */
    fun selectLesson(lessonId: String) {
        loadLesson(lessonId)
    }

    private fun loadLesson(lessonId: String) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            _uiState.value = try {
                val course = repository.getCourseById(courseId)
                val lesson = course?.lessons?.firstOrNull { it.id == lessonId }
                if (course != null && lesson != null) {
                    UiState.Success(LessonScreenData(course, lesson))
                } else {
                    UiState.Error("Couldn't find that lesson.")
                }
            } catch (e: IOException) {
                UiState.Error("Couldn't connect. Check your internet and try again.")
            } catch (e: Exception) {
                UiState.Error("Something went wrong while loading this lesson.")
            }
        }
    }
}
