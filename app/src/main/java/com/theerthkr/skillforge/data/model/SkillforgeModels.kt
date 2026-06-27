package com.theerthkr.skillforge.data.model

import kotlinx.serialization.Serializable

@Serializable
data class SkillforgeResponse(
    val categories: List<Category> = emptyList()
)

@Serializable
data class Category(
    val id: String,
    val name: String,
    val description: String = "",
    val iconColor: String = "#2dd4bf",
    val courseCount: Int = 0,
    val courses: List<Course> = emptyList()
)

@Serializable
data class Course(
    val id: String,
    val title: String,
    val subtitle: String = "",
    val thumbnailUrl: String = "",
    val level: String = "",
    val durationHours: Double = 0.0,
    val rating: Double = 0.0,
    val studentsEnrolled: Int = 0,
    val language: String = "",
    val lastUpdated: String = "",
    val tags: List<String> = emptyList(),
    val instructor: Instructor? = null,
    val description: String = "",
    val lessons: List<Lesson> = emptyList()
)

@Serializable
data class Instructor(
    val id: String,
    val name: String,
    val title: String = "",
    val avatarUrl: String = "",
    val bio: String = ""
)

@Serializable
data class Lesson(
    val id: String,
    val title: String,
    val durationMinutes: Int = 0,
    val isFree: Boolean = false,
    val videoUrl: String = "",
    val content: String = ""
)
