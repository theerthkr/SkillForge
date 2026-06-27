package com.theerthkr.skillforge.screens.coursedetail

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
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
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp, horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .background(
                    color = if (lesson.isFree) DarkTealPrimary.copy(alpha = 0.12f) else Color(0xFFEDEDED),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (lesson.isFree) Icons.Filled.PlayArrow else Icons.Filled.Lock,
                contentDescription = null,
                tint = if (lesson.isFree) DarkTealPrimary else Color(0xFF9A9A9A),
                modifier = Modifier.size(18.dp)
            )
        }

        androidx.compose.foundation.layout.Spacer(modifier = Modifier.size(width = 12.dp, height = 0.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = lesson.title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF1A1A1A)
            )
            Text(
                text = "${lesson.durationMinutes} min",
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF8A8A8A)
            )
        }

        if (lesson.isFree) {
            Surface(
                color = DarkTealPrimary.copy(alpha = 0.12f),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "FREE",
                    style = MaterialTheme.typography.labelSmall,
                    color = DarkTealPrimary,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                )
            }
        }
    }
}
