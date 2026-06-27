package com.theerthkr.skillforge.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.theerthkr.skillforge.data.model.Course
import com.theerthkr.skillforge.ui.theme.DarkTealPrimary

@Composable
fun CourseCard(
    course: Course,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val levelColor = when (course.level.lowercase()) {
        "beginner" -> DarkTealPrimary
        "intermediate" -> Color(0xFFD97706)
        else -> Color(0xFF6366F1)
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(DarkTealPrimary.copy(alpha = 0.15f))
            ) {
                AsyncImage(
                    model = course.thumbnailUrl,
                    contentDescription = course.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxWidth().height(64.dp)
                )
            }

            androidx.compose.foundation.layout.Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = course.level.uppercase(),
                    style = MaterialTheme.typography.labelSmall,
                    color = levelColor,
                    fontWeight = FontWeight.Bold
                )
                androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = course.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color(0xFF1A1A1A),
                    maxLines = 1
                )
                androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = course.instructor?.name ?: "",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF8A8A8A)
                )
                androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.Star,
                        contentDescription = null,
                        tint = Color(0xFFF59E0B),
                        modifier = Modifier.size(14.dp)
                    )
                    androidx.compose.foundation.layout.Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${course.rating}",
                        style = MaterialTheme.typography.labelMedium,
                        color = Color(0xFF1A1A1A)
                    )
                    androidx.compose.foundation.layout.Spacer(modifier = Modifier.width(12.dp))
                    Icon(
                        imageVector = Icons.Filled.Schedule,
                        contentDescription = null,
                        tint = Color(0xFF8A8A8A),
                        modifier = Modifier.size(14.dp)
                    )
                    androidx.compose.foundation.layout.Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = formatDuration(course.durationHours),
                        style = MaterialTheme.typography.labelMedium,
                        color = Color(0xFF8A8A8A)
                    )
                }
            }
        }
    }
}

fun formatDuration(hours: Double): String {
    return if (hours == hours.toLong().toDouble()) {
        "${hours.toLong()}h"
    } else {
        "${hours}h"
    }
}
