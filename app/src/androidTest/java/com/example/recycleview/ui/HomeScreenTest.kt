package com.example.recycleview.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.testing.TestNavHostController
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.recycleview.domain.models.Plant
import com.example.recycleview.presentation.home.HomeScreen
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class HomeScreenTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<HiltTestActivity>()

    val context = InstrumentationRegistry.getInstrumentation().targetContext


    private fun createNavController(): TestNavHostController {
        return TestNavHostController(context).apply {
            navigatorProvider.addNavigator(ComposeNavigator())
            // setGraph(R.navigation.nav_graph) ← якщо є граф
        }
    }

    @Test
    fun homeScreen_showsLoading_whenPlantsAreLoading() {
        composeTestRule.setContent {
            HomeScreen(
                navController = createNavController(),
            )
        }

        composeTestRule.onNodeWithTag("LoadingIndicator").assertIsDisplayed()
    }

    @Test
    fun homeScreen_showsEmptyState_whenNoPlants() {
        composeTestRule.setContent {
            HomeScreen(
                navController = createNavController(),
            )
        }

        composeTestRule.onNodeWithText("Your personal list is empty").assertIsDisplayed()
    }

    @Test
    fun homeScreen_opensSearchBar_whenSearchIconClicked() {
        composeTestRule.setContent {
            HomeScreen(
                navController = createNavController(),
            )
        }

        composeTestRule.onNodeWithText("Search").assertIsDisplayed()
    }

    @Test
    fun homeScreen_showsPlants_whenDataIsAvailable() {
        val plants = listOf(
            Plant(id = "1", name = "Rose", imagePath = null, description = "description 1"),
            Plant(id = "2", name = "Tulip", imagePath = null, description = "description 2")
        )

        composeTestRule.setContent {
            HomeScreen(
                navController = createNavController(),
            )
        }

        composeTestRule.onNodeWithText("Rose").assertIsDisplayed()
        composeTestRule.onNodeWithText("Tulip").assertIsDisplayed()
    }

    @Test
    fun homeScreen_navigatesToEditScreen_whenFabClicked() {
        val navController = createNavController()

        composeTestRule.setContent {
            HomeScreen(
                navController = navController,
            )
        }

        composeTestRule.onNodeWithContentDescription("Add").performClick()

        assert(navController.currentBackStackEntry?.destination?.route == "edit_screen")
    }
}
