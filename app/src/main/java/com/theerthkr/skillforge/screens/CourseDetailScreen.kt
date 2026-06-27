package com.theerthkr.skillforge.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.theerthkr.skillforge.data.model.Course
import com.theerthkr.skillforge.screens.coursedetail.LessonListItem
import com.theerthkr.skillforge.screens.home.formatDuration
import com.theerthkr.skillforge.ui.theme.CreamBackground
import com.theerthkr.skillforge.ui.theme.DarkTealPrimary
import com.theerthkr.skillforge.viewmodel.CourseDetailScreenData
import com.theerthkr.skillforge.viewmodel.CourseDetailViewModel
import com.theerthkr.skillforge.viewmodel.UiState

@Composable
fun CourseDetailScreen(
    courseId: String,
    onLessonClick: (String) -> Unit,
    onEnrollClick: (String) -> Unit = {},
    onBackClick: () -> Unit = {}
) {
    val viewModel: CourseDetailViewModel = viewModel(
        key = "course_detail_$courseId",
        factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                return CourseDetailViewModel(courseId) as T
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
            is UiState.Error -> ErrorState(message = s.message, onRetry = viewModel::loadCourse)
            is UiState.Success -> CourseDetailContent(
                data = s.data,
                onLessonClick = onLessonClick,
                onEnrollClick = onEnrollClick,
                onBackClick = onBackClick
            )
        }
    }
}

@Composable
private fun CourseDetailContent(
    data: CourseDetailScreenData,
    onLessonClick: (String) -> Unit,
    onEnrollClick: (String) -> Unit,
    onBackClick: () -> Unit
) {
    val course = data.course
    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 96.dp)
        ) {
            item { HeroSection(course = course, onBackClick = onBackClick) }
            item {
                Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = course.title,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1A1A1A)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = course.subtitle,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF8A8A8A)
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.Star,
                            contentDescription = null,
                            tint = Color(0xFFF59E0B),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("${course.rating}", style = MaterialTheme.typography.labelLarge, color = Color(0xFF1A1A1A))
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = formatStudentCount(course.studentsEnrolled),
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFF8A8A8A)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Icon(
                            imageVector = Icons.Filled.Schedule,
                            contentDescription = null,
                            tint = Color(0xFF8A8A8A),
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = formatDuration(course.durationHours),
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFF8A8A8A)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = course.level,
                            style = MaterialTheme.typography.labelLarge,
                            color = DarkTealPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    InstructorCard(course = course)

                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = course.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF4A4A4A)
                    )

                    Spacer(modifier = Modifier.height(20.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Text(
                            text = "Course content",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1A1A1A)
                        )
                        Text(
                            text = "${course.lessons.size} lessons · ${course.lessons.sumOf { it.durationMinutes }} min",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF8A8A8A)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
            itemsIndexed(course.lessons) { index, lesson ->
                Box(modifier = Modifier.padding(horizontal = 20.dp)) {
                    LessonListItem(
                        lesson = lesson,
                        index = index,
                        onClick = { 
                            if (lesson.isFree || data.isUnlocked) {
                                onLessonClick(lesson.id) 
                            } else {
                                onEnrollClick(course.id)
                            }
                        }
                    )
                }
            }
        }

        val isCourseFree = course.lessons.all { it.isFree } || course.price() == 0.0
        if (!data.isUnlocked) {
            PriceFooter(
                modifier = Modifier.align(Alignment.BottomCenter),
                isFree = isCourseFree,
                onEnrollClick = {
                    if (isCourseFree) {
                        course.lessons.firstOrNull()?.id?.let { onLessonClick(it) }
                    } else {
                        onEnrollClick(course.id)
                    }
                }
            )
        }
    }
}

// The API doesn't return a price field — the mock shows "Free" for this course,
// so we treat courses as free unless/until a real price field exists.
private fun Course.price(): Double = 0.0

private fun formatStudentCount(count: Int): String {
    return if (count >= 1000) {
        val thousands = count / 1000.0
        "${if (thousands == thousands.toLong().toDouble()) thousands.toLong().toString() else String.format("%.1f", thousands)}k"
    } else {
        count.toString()
    }
}

@Composable
private fun HeroSection(course: Course, onBackClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp)
    ) {
        AsyncImage(
            model = course.thumbnailUrl,
            contentDescription = course.title,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, CreamBackground)
                    )
                )
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            CircleIconButton(icon = Icons.Filled.ArrowBack, onClick = onBackClick)
            CircleIconButton(icon = Icons.Filled.BookmarkBorder, onClick = { /* bookmarking not in scope */ })
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 16.dp, end = 16.dp, bottom = 48.dp)
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                course.tags.forEach { tag ->
                    Surface(
                        color = Color.White.copy(alpha = 0.85f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = tag,
                            style = MaterialTheme.typography.labelSmall,
                            color = DarkTealPrimary,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun InstructorCard(course: Course) {
    val instructor = course.instructor
    Surface(
        color = Color.White,
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(DarkTealPrimary),
                contentAlignment = Alignment.Center
            ) {
                if (!instructor?.avatarUrl.isNullOrBlank()) {
                    AsyncImage(
                        model = instructor!!.avatarUrl,
                        contentDescription = instructor.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize().clip(CircleShape)
                    )
                } else {
                    Text(
                        text = instructor?.name?.take(2)?.uppercase() ?: "",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = instructor?.name ?: "",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A1A1A)
                )
                Text(
                    text = instructor?.title ?: "",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF8A8A8A)
                )
            }
            Text(
                text = "Follow",
                style = MaterialTheme.typography.labelLarge,
                color = DarkTealPrimary,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable { /* follow not in scope */ }
            )
        }
    }
}

@Composable
private fun PriceFooter(isFree: Boolean, onEnrollClick: () -> Unit, modifier: Modifier = Modifier) {
    Surface(
        color = CreamBackground,
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "PRICE",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFF8A8A8A)
                )
                Text(
                    text = if (isFree) "Free" else "Paid",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = DarkTealPrimary
                )
            }
            Button(
                onClick = onEnrollClick,
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = DarkTealPrimary),
                modifier = Modifier.height(52.dp)
            ) {
                Text(
                    text = "Enroll now",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 24.dp)
                )
            }
        }
    }
}
