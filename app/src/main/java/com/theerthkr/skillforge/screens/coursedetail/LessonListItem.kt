package com.theerthkr.skillforge.screens.coursedetail

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.theerthkr.skillforge.data.model.Lesson
import com.theerthkr.skillforge.ui.theme.DarkTealPrimary

@Composable
fun LessonListItem(
    lesson: Lesson,
    index: Int,
    isCurrent: Boolean = false, // New parameter to handle state
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(), // Removed internal vertical padding so spacedBy controls gaps
        shape = RoundedCornerShape(12.dp),
        // Tint the background lightly if it's the current lesson
        color = if (isCurrent) DarkTealPrimary.copy(alpha = 0.05f) else Color.White,
        // Tint the border if it's the current lesson
        border = BorderStroke(1.dp, if (isCurrent) DarkTealPrimary.copy(alpha = 0.3f) else Color(0xFFE0E0E0))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .padding(horizontal = 20.dp, vertical = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        color = if (isCurrent) DarkTealPrimary
                        else if (lesson.isFree) DarkTealPrimary.copy(alpha = 0.12f)
                        else Color(0xFFEDEDED),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isCurrent) Icons.Filled.Pause
                    else if (lesson.isFree) Icons.Filled.PlayArrow
                    else Icons.Filled.Lock,
                    contentDescription = null,
                    tint = if (isCurrent) Color.White
                    else if (lesson.isFree) DarkTealPrimary
                    else Color(0xFF9A9A9A),
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = lesson.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    // Text goes Teal when playing
                    color = if (isCurrent) DarkTealPrimary else Color(0xFF1A1A1A)
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    // Subtitle updates when playing
                    text = if (isCurrent) "Now playing · ${lesson.durationMinutes} min" else "${lesson.durationMinutes} min",
                    style = MaterialTheme.typography.bodyMedium,
                    // Subtitle goes Teal when playing
                    color = if (isCurrent) DarkTealPrimary else Color(0xFF8A8A8A)
                )
            }

            // Hide the FREE tag if this is the currently playing video
            if (lesson.isFree && !isCurrent) {
                Surface(
                    color = DarkTealPrimary.copy(alpha = 0.12f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "FREE",
                        style = MaterialTheme.typography.labelMedium,
                        color = DarkTealPrimary,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                    )
                }
            }
        }
    }
}