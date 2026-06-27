package com.theerthkr.skillforge

import com.theerthkr.skillforge.data.model.Category
import com.theerthkr.skillforge.data.model.SkillforgeResponse
import com.theerthkr.skillforge.data.remote.SkillforgeApi
import com.theerthkr.skillforge.data.repository.SkillforgeRepository
import com.theerthkr.skillforge.viewmodel.HomeViewModel
import com.theerthkr.skillforge.viewmodel.UiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestWatcher
import org.junit.runner.Description
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
class MainDispatcherRule(
    val testDispatcher: TestDispatcher = UnconfinedTestDispatcher(),
) : TestWatcher() {
    override fun starting(description: Description) {
        Dispatchers.setMain(testDispatcher)
    }

    override fun finished(description: Description) {
        Dispatchers.resetMain()
    }
}

private class FakeHomeApi(private val response: SkillforgeResponse?, private val shouldThrow: Boolean = false) : SkillforgeApi {
    override suspend fun getCatalog(): SkillforgeResponse {
        if (shouldThrow) {
            throw IOException("Network error")
        }
        return response ?: SkillforgeResponse()
    }
}

class HomeViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val fakeResponse = SkillforgeResponse(
        categories = listOf(
            Category(id = "cat_1", name = "Category One", courseCount = 0)
        )
    )

    @Test
    fun `loadCategories emits Success when repository returns data`() = runTest {
        val api = FakeHomeApi(fakeResponse)
        val repository = SkillforgeRepository(api)
        val viewModel = HomeViewModel(repository)

        // Wait/assert that the final state is Success
        val state = viewModel.uiState.value
        assertTrue(state is UiState.Success)
        assertEquals(1, (state as UiState.Success).data.size)
        assertEquals("Category One", state.data[0].name)
    }

    @Test
    fun `loadCategories emits Error when repository throws IOException`() = runTest {
        val api = FakeHomeApi(fakeResponse, shouldThrow = true)
        val repository = SkillforgeRepository(api)
        val viewModel = HomeViewModel(repository)

        val state = viewModel.uiState.value
        assertTrue(state is UiState.Error)
        assertEquals("Couldn't connect. Check your internet and try again.", (state as UiState.Error).message)
    }
}
