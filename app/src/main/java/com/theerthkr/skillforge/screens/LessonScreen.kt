package com.theerthkr.skillforge.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.theerthkr.skillforge.data.model.Course
import com.theerthkr.skillforge.data.model.Lesson
import com.theerthkr.skillforge.screens.coursedetail.LessonListItem
import com.theerthkr.skillforge.ui.theme.CreamBackground
import com.theerthkr.skillforge.ui.theme.DarkTealPrimary
import com.theerthkr.skillforge.viewmodel.LessonScreenData
import com.theerthkr.skillforge.viewmodel.LessonViewModel
import com.theerthkr.skillforge.viewmodel.UiState

@Composable
fun LessonScreen(
    courseId: String,
    lessonId: String,
    onBackClick: () -> Unit = {}
) {
    val viewModel: LessonViewModel = viewModel(
        key = "lesson_${courseId}_$lessonId",
        factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                return LessonViewModel(courseId, lessonId) as T
            }
        }
    )
    val state by viewModel.uiState.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(CreamBackground)
    ) {
        when (val s = state) {
            is UiState.Loading -> LoadingState()
            is UiState.Error -> ErrorState(message = s.message, onRetry = { /* selection-driven, no single retry target */ })
            is UiState.Success -> LessonContent(
                data = s.data,
                onBackClick = onBackClick,
                onLessonSelected = viewModel::selectLesson
            )
        }
    }
}

private enum class LessonTab { LESSONS, NOTES, RESOURCES }

@Composable
private fun LessonContent(
    data: LessonScreenData,
    onBackClick: () -> Unit,
    onLessonSelected: (String) -> Unit
) {
    var selectedTab by remember { mutableStateOf(LessonTab.LESSONS) }

    Column(modifier = Modifier.fillMaxSize()) {
        VideoPlayerHeader(course = data.course, lesson = data.currentLesson, onBackClick = onBackClick)

        Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)) {
            Text(
                text = "LESSON ${data.course.lessons.indexOfFirst { it.id == data.currentLesson.id } + 1} · ${data.course.title.uppercase()}",
                style = MaterialTheme.typography.labelMedium,
                color = DarkTealPrimary,
                fontWeight = FontWeight.Bold
            )
            androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = data.currentLesson.title,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1A1A1A)
            )
            androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = data.currentLesson.content,
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF8A8A8A)
            )
        }

        TabRow(selectedTab = selectedTab, onTabSelected = { selectedTab = it })

        when (selectedTab) {
            LessonTab.LESSONS -> LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp)
            ) {
                items(data.course.lessons) { lesson ->
                    val isCurrent = lesson.id == data.currentLesson.id
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                if (isCurrent) DarkTealPrimary.copy(alpha = 0.08f)
                                else Color.Transparent
                            )
                    ) {
                        if (isCurrent) {
                            NowPlayingRow(lesson = lesson)
                        } else {
                            LessonListItem(
                                lesson = lesson,
                                index = data.course.lessons.indexOf(lesson),
                                onClick = {
                                    if (lesson.isFree || isAlreadyUnlockedByOrder(data.course, lesson)) {
                                        onLessonSelected(lesson.id)
                                    }
                                }
                            )
                        }
                    }
                }
            }
            LessonTab.NOTES -> EmptyTabContent("Your notes for this lesson will show up here.")
            LessonTab.RESOURCES -> EmptyTabContent("Downloadable resources for this lesson will show up here.")
        }
    }
}

// Mirrors the mock's behavior: free lessons are always tappable; locked ones
// are visually locked. We don't gate on "already watched" since there's no
// progress-tracking field in the API — only isFree distinguishes access.
private fun isAlreadyUnlockedByOrder(course: Course, lesson: Lesson): Boolean = lesson.isFree

@Composable
private fun NowPlayingRow(lesson: Lesson) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp, horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .background(DarkTealPrimary, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.Pause,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(18.dp)
            )
        }
        androidx.compose.foundation.layout.Spacer(modifier = Modifier.size(width = 12.dp, height = 0.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = lesson.title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1A1A1A)
            )
            Text(
                text = "Now playing · ${lesson.durationMinutes} min",
                style = MaterialTheme.typography.bodySmall,
                color = DarkTealPrimary
            )
        }
    }
}

@Composable
private fun EmptyTabContent(message: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = Color(0xFF8A8A8A),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}

@Composable
private fun TabRow(selectedTab: LessonTab, onTabSelected: (LessonTab) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        TabItem("Lessons", selectedTab == LessonTab.LESSONS) { onTabSelected(LessonTab.LESSONS) }
        TabItem("Notes", selectedTab == LessonTab.NOTES) { onTabSelected(LessonTab.NOTES) }
        TabItem("Resources", selectedTab == LessonTab.RESOURCES) { onTabSelected(LessonTab.RESOURCES) }
    }
}

@Composable
private fun TabItem(label: String, isSelected: Boolean, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = if (isSelected) Color(0xFF1A1A1A) else Color(0xFF8A8A8A)
        )
        androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(6.dp))
        Box(
            modifier = Modifier
                .height(2.dp)
                .background(if (isSelected) DarkTealPrimary else Color.Transparent)
                .fillMaxWidth()
        )
    }
}

@Composable
private fun VideoPlayerHeader(course: Course, lesson: Lesson, onBackClick: () -> Unit) {
    // No real video stream exists for this fake API, so we render a faithful
    // player chrome (gradient, play/pause, scrubber) without wiring ExoPlayer
    // to a dead URL. Scrubbing is simulated locally for the UI demo.
    var isPlaying by remember(lesson.id) { mutableStateOf(true) }
    var progress by remember(lesson.id) { mutableFloatStateOf(0.35f) }
    val totalSeconds = lesson.durationMinutes * 60
    val currentSeconds = (progress * totalSeconds).toInt()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(260.dp)
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF0E3B3C), DarkTealPrimary)
                )
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            CircleIconButton(icon = Icons.Filled.ArrowBack, onClick = onBackClick)
            CircleIconButton(icon = Icons.Filled.Fullscreen, onClick = { /* fullscreen not in scope */ })
        }

        Box(
            modifier = Modifier
                .size(64.dp)
                .align(Alignment.Center)
                .background(Color.White, CircleShape)
                .clickable { isPlaying = !isPlaying },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                contentDescription = if (isPlaying) "Pause" else "Play",
                tint = DarkTealPrimary,
                modifier = Modifier.size(32.dp)
            )
        }

        Column(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = 16.dp, top = 70.dp)
        ) {
            Box(
                modifier = Modifier
                    .background(Color.White.copy(alpha = 0.15f))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "// kotlin",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White
                )
            }
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Slider(
                value = progress,
                onValueChange = { progress = it },
                colors = SliderDefaults.colors(
                    thumbColor = Color.White,
                    activeTrackColor = Color.White,
                    inactiveTrackColor = Color.White.copy(alpha = 0.3f)
                ),
                modifier = Modifier.fillMaxWidth()
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(formatTime(currentSeconds), style = MaterialTheme.typography.labelSmall, color = Color.White)
                Text(formatTime(totalSeconds), style = MaterialTheme.typography.labelSmall, color = Color.White)
            }
        }
    }
}

@Composable
private fun CircleIconButton(icon: androidx.compose.ui.graphics.vector.ImageVector, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .background(Color.White.copy(alpha = 0.2f), CircleShape),
        contentAlignment = Alignment.Center
    ) {
        IconButton(onClick = onClick) {
            Icon(imageVector = icon, contentDescription = null, tint = Color.White)
        }
    }
}

private fun formatTime(totalSeconds: Int): String {
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "%02d:%02d".format(minutes, seconds)
}
