package com.theerthkr.skillforge.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.theerthkr.skillforge.screens.CourseDetailScreen
import com.theerthkr.skillforge.screens.HomeScreen
import com.theerthkr.skillforge.screens.LessonScreen

@Composable
fun SkillforgeNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "home") {

        composable("home") {
            HomeScreen(
                onCourseClick = { courseId ->
                    navController.navigate("course_detail/$courseId")
                }
            )
        }

        composable(
            route = "course_detail/{courseId}",
            arguments = listOf(navArgument("courseId") { type = NavType.StringType })
        ) { backStackEntry ->
            val courseId = backStackEntry.arguments?.getString("courseId") ?: ""
            CourseDetailScreen(
                courseId = courseId,
                onLessonClick = { lessonId ->
                    navController.navigate("lesson/$courseId/$lessonId")
                },
                onBackClick = { navController.popBackStack() }
            )
        }

        // Lessons are nested under a course in the API (categories -> courses -> lessons),
        // so the Lesson screen needs both ids to look the lesson up via the repository.
        composable(
            route = "lesson/{courseId}/{lessonId}",
            arguments = listOf(
                navArgument("courseId") { type = NavType.StringType },
                navArgument("lessonId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val courseId = backStackEntry.arguments?.getString("courseId") ?: ""
            val lessonId = backStackEntry.arguments?.getString("lessonId") ?: ""
            LessonScreen(
                courseId = courseId,
                lessonId = lessonId,
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}
