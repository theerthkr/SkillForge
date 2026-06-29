package com.theerthkr.skillforge.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.theerthkr.skillforge.data.model.Category
import com.theerthkr.skillforge.data.model.Course
import com.theerthkr.skillforge.screens.home.CategoryCard
import com.theerthkr.skillforge.screens.home.CourseCard
import com.theerthkr.skillforge.ui.theme.CreamBackground
import com.theerthkr.skillforge.ui.theme.DarkTealPrimary
import com.theerthkr.skillforge.viewmodel.HomeViewModel
import com.theerthkr.skillforge.viewmodel.UiState

@Composable
fun HomeScreen(
    onCourseClick: (String) -> Unit,
    viewModel: HomeViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(CreamBackground)
            .statusBarsPadding()
    ) {
        when (val s = state) {
            is UiState.Loading -> LoadingState()
            is UiState.Error -> ErrorState(message = s.message, onRetry = viewModel::loadCategories)
            is UiState.Success -> HomeContent(
                categories = s.data,
                onCourseClick = onCourseClick
            )
        }
    }
}

@Composable
private fun HomeContent(
    categories: List<Category>,
    onCourseClick: (String) -> Unit
) {
    var query by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<String?>(null) }

    val filteredCourses: List<Course> = remember(categories, query, selectedCategory) {
        val baseCourses = if (selectedCategory == null) {
            categories.flatMap { it.courses }
        } else {
            categories.find { it.name == selectedCategory }?.courses ?: emptyList()
        }

        if (query.isBlank()) baseCourses
        else baseCourses.filter {
            it.title.contains(query, ignoreCase = true) ||
                    it.instructor?.name?.contains(query, ignoreCase = true) == true ||
                    it.level.contains(query, ignoreCase = true)
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        item {
            HomeHeader()
        }

        // ADDED: Gap between Welcome Header and Search Bar
        item {
            Spacer(modifier = Modifier.height(16.dp))
        }

        item {
            SearchBar(query = query, onQueryChange = { query = it })
        }

        // ADDED: Gap between Search Bar and Categories Section
        item {
            Spacer(modifier = Modifier.height(24.dp))
        }

        item {
            SectionHeader(title = "Categories")
        }
        item {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(categories, key = { it.id }) { category ->
                    CategoryCard(
                        category = category,
                        isSelected = category.name == selectedCategory,
                        onClick = {
                            selectedCategory = if (selectedCategory == category.name) null else category.name
                        }
                    )
                }
            }
        }

        // MODIFIED: Increased gap between Categories and Popular Courses (was 8.dp)
        item {
            Spacer(modifier = Modifier.height(32.dp))
        }

        item {
            SectionHeader(title = "Popular courses")
        }
        items(filteredCourses, key = { it.id }) { course ->
            Box(modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)) {
                CourseCard(
                    course = course,
                    onClick = { onCourseClick(course.id) }
                )
            }
        }
    }
}

@Composable
private fun HomeHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "Welcome back",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF8A8A8A)
            )
            // Added Spacer to increase the gap
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Find your next skill",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF1A1A1A)
            )
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            // ... Notifications IconButton box remains the same ...
            Spacer(modifier = Modifier.width(10.dp))

            // Replaced the Icon with an AsyncImage for your URL avatar
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE0E0E0)),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = "https://ui-avatars.com/api/?name=Aarav+Sharma&size=150&background=2dd4bf&color=ffffff&bold=true&format=png", // Drop your image URL here
                    contentDescription = "Profile",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

@Composable
private fun SearchBar(query: String, onQueryChange: (String) -> Unit) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        placeholder = { Text("Search courses, topics…", color = Color(0xFFB0B0B0)) },
        leadingIcon = {
            Icon(
                imageVector = Icons.Filled.Search,
                contentDescription = null,
                tint = Color(0xFFB0B0B0)
            )
        },
        singleLine = true,
        shape = RoundedCornerShape(16.dp),
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedContainerColor = Color.White,
            focusedContainerColor = Color.White,
            unfocusedBorderColor = Color.Transparent,
            focusedBorderColor = DarkTealPrimary
        )
    )
}

@Composable
private fun SectionHeader(title: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1A1A1A)
        )
        Text(
            text = "See all",
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            color = DarkTealPrimary
        )
    }
}
