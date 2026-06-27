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
    val currentLesson: Lesson,
    val isUnlocked: Boolean
)

class LessonViewModel(
    private val courseId: String,
    initialLessonId: String,
    private val repository: SkillforgeRepository = SkillforgeRepository.instance
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<LessonScreenData>>(UiState.Loading)
    val uiState: StateFlow<UiState<LessonScreenData>> = _uiState.asStateFlow()

    init {
        loadLesson(initialLessonId)
    }

    /** Lets the user tap another lesson in the list without leaving the screen. */
    fun selectLesson(lessonId: String) {
        val current = (_uiState.value as? UiState.Success)?.data
        val newLesson = current?.course?.lessons?.firstOrNull { it.id == lessonId }
        if (current != null && newLesson != null) {
            _uiState.value = UiState.Success(current.copy(currentLesson = newLesson))
        } else {
            loadLesson(lessonId)
        }
    }

    private fun loadLesson(lessonId: String) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            _uiState.value = try {
                val course = repository.getCourseById(courseId)
                val lesson = course?.lessons?.firstOrNull { it.id == lessonId }
                if (course != null && lesson != null) {
                    val isUnlocked = repository.isCourseUnlocked(courseId)
                    UiState.Success(LessonScreenData(course, lesson, isUnlocked))
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
