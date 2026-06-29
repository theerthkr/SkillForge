package com.theerthkr.skillforge.screens

import androidx.compose.foundation.BorderStroke
import com.theerthkr.skillforge.R
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.offset
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
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.res.painterResource
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
import java.text.NumberFormat
import java.util.Locale

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
            contentPadding = PaddingValues(bottom = 96.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { HeroSection(course = course, onBackClick = onBackClick) }
            item {
                Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                    Spacer(modifier = Modifier.height(32.dp))
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
                        // Student Count
                        Text(
                            text = formatStudentCount(course.studentsEnrolled),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold, // Added this line
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

// Duration
                        Text(
                            text = formatDuration(course.durationHours),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold, // Added this line
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
                        style = MaterialTheme.typography.bodyLarge, // Upgraded from bodyMedium
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

        val isCourseFree = course.lessons.any { it.isFree }
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



private fun formatStudentCount(count: Int): String {
    val formatter = NumberFormat.getNumberInstance(Locale.getDefault())
    return formatter.format(count)
}
@Composable
private fun HeroSection(course: Course, onBackClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(260.dp)
    ) {
        // 1. Your Background Image
        Image(
            painter = painterResource(id = R.drawable.hero),
            contentDescription = course.title,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // 2. The Gradient Merge Overlay
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp) // Height of the fade effect
                .align(Alignment.BottomCenter)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, CreamBackground) // Fades into the background color
                    )
                )
        )
        // Top Bar (Back, Bookmark, and small tag)
        Column(modifier = Modifier.padding(16.dp).statusBarsPadding()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                CircleIconButton(icon = Icons.Filled.ArrowBack, onClick = onBackClick)
                CircleIconButton(icon = Icons.Filled.BookmarkBorder, onClick = {})
            }
            Spacer(modifier = Modifier.height(16.dp))
            // The top left '// kotlin' tag
            Surface(
                color = MaterialTheme.colorScheme.secondary, // Theme color!
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "//  kotlin",
                    style = MaterialTheme.typography.labelSmall, // Smaller font
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }

        // Large White Title inside the Hero
        Text(
            text = "Kotlin\nFundamentals",
            style = MaterialTheme.typography.displaySmall,
            color = Color.White,
            fontWeight = FontWeight.ExtraBold,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 16.dp, top = 16.dp, end = 0.dp, bottom = 40.dp )
        )

        // Pill Tags shifted down over the edge
        Row(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 16.dp)
                .offset(y = 16.dp), // Pushes tags over the boundary edge
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("Kotlin", "Basics", "JVM").forEach { tag ->
                Surface(
                    color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, Color(0xFFE0E0E0))
                ) {
                    Text(
                        text = tag,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
        }

        // Add your SVG Vector image of the wave here, aligned to BottomCenter!
    }
}
@Composable
private fun InstructorCard(course: Course) {
    val instructor = course.instructor
    Surface(
        color = Color.White,
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, Color(0xFFE0E0E0)), // Subtle gray outline
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp), // Good internal padding
            verticalAlignment = Alignment.CenterVertically
        ){
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
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = instructor?.name ?: "",
                    style = MaterialTheme.typography.titleMedium, // Upgraded from titleSmall
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A1A1A)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = instructor?.title ?: "",
                    style = MaterialTheme.typography.bodyMedium, // Upgraded from bodySmall
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
        // 1. We wrap EVERYTHING in this outer Column
        Column(modifier = Modifier.fillMaxWidth()) {

            // 2. The divider sits right at the top, spanning the full width
            HorizontalDivider(thickness = 1.dp, color = Color(0xFFE0E0E0))

            // 3. The Row contains the text and button, placed below the divider
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // The inner text column (Notice the divider is gone from here!)
                Column(modifier = Modifier.padding(end = 24.dp)) {
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

                // Your massive Enroll Button
                Button(
                    onClick = onEnrollClick,
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = DarkTealPrimary),
                    modifier = Modifier
                        .weight(1f) // Now this works perfectly again!
                        .height(56.dp)
                ) {
                    Text(
                        text = "Enroll now",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}