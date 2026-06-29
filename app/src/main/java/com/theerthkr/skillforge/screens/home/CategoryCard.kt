package com.theerthkr.skillforge.screens.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.theerthkr.skillforge.data.model.Category

@Composable
fun CategoryCard(
    category: Category,
    isSelected: Boolean = false,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val accentColor = remember(category.iconColor) {
        runCatching { Color(android.graphics.Color.parseColor(category.iconColor)) }
            .getOrDefault(Color(0xFF2DD4BF))
    }

    Card(
        modifier = modifier
            .width(170.dp)
            .height(160.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        // Added the subtle border you asked for
        border = BorderStroke(1.dp, Color(0xFFE0E0E0)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // The Box-in-Box design
            Box(
                modifier = Modifier
                    .size(48.dp) // The outer semi-transparent box
                    .background(
                        color = accentColor.copy(alpha = 0.18f),
                        shape = RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                // The inner solid box
                Box(
                    modifier = Modifier
                        .size(22.dp) // Smaller inner box, completely opaque
                        .background(
                            color = accentColor,
                            shape = RoundedCornerShape(6.dp)
                        )
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // The text is back exactly as it was!
            Text(
                text = category.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1A1A1A),
                maxLines = 2
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "${category.courseCount} courses",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF8A8A8A)
            )
        }
    }
}