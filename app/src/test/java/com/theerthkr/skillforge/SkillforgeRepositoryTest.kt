package com.theerthkr.skillforge

import com.theerthkr.skillforge.data.model.Category
import com.theerthkr.skillforge.data.model.Course
import com.theerthkr.skillforge.data.model.Instructor
import com.theerthkr.skillforge.data.model.Lesson
import com.theerthkr.skillforge.data.model.SkillforgeResponse
import com.theerthkr.skillforge.data.remote.SkillforgeApi
import com.theerthkr.skillforge.data.repository.SkillforgeRepository
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

/**
 * Fake API that returns a fixed, small catalog so we don't hit the network in tests.
 */
private class FakeSkillforgeApi(private val response: SkillforgeResponse) : SkillforgeApi {
    var callCount = 0
    override suspend fun getCatalog(): SkillforgeResponse {
        callCount++
        return response
    }
}

private fun fakeCourse(id: String, lessonIds: List<String>) = Course(
    id = id,
    title = "Course $id",
    instructor = Instructor(id = "inst_$id", name = "Instructor $id"),
    lessons = lessonIds.map { lessonId ->
        Lesson(id = lessonId, title = "Lesson $lessonId", isFree = lessonId.endsWith("1"))
    }
)

class SkillforgeRepositoryTest {

    private val fakeResponse = SkillforgeResponse(
        categories = listOf(
            Category(
                id = "cat_1",
                name = "Category One",
                courses = listOf(
                    fakeCourse("course_a", listOf("les_a1", "les_a2")),
                    fakeCourse("course_b", listOf("les_b1"))
                )
            )
        )
    )

    @Test
    fun `getCourseById finds a course nested under a category`() = runBlocking {
        val repository = SkillforgeRepository(FakeSkillforgeApi(fakeResponse))

        val course = repository.getCourseById("course_b")

        assertEquals("Course course_b", course?.title)
    }

    @Test
    fun `getCourseById returns null for an unknown id`() = runBlocking {
        val repository = SkillforgeRepository(FakeSkillforgeApi(fakeResponse))

        val course = repository.getCourseById("does_not_exist")

        assertNull(course)
    }

    @Test
    fun `getLessonById finds a lesson nested under the correct course`() = runBlocking {
        val repository = SkillforgeRepository(FakeSkillforgeApi(fakeResponse))

        val lesson = repository.getLessonById("course_a", "les_a2")

        assertEquals("Lesson les_a2", lesson?.title)
    }

    @Test
    fun `getCategories caches the result so the API is only called once`() = runBlocking {
        val fakeApi = FakeSkillforgeApi(fakeResponse)
        val repository = SkillforgeRepository(fakeApi)

        repository.getCategories()
        repository.getCategories()
        repository.getCategories()

        assertEquals(1, fakeApi.callCount)
    }

    @Test
    fun `getCategories with forceRefresh bypasses the cache`() = runBlocking {
        val fakeApi = FakeSkillforgeApi(fakeResponse)
        val repository = SkillforgeRepository(fakeApi)

        repository.getCategories()
        repository.getCategories(forceRefresh = true)

        assertEquals(2, fakeApi.callCount)
    }
}
