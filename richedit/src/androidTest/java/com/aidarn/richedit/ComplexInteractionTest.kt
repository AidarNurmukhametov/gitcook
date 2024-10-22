package com.aidarn.richedit

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import junit.framework.TestCase.assertEquals
import org.junit.Before
import org.junit.Test


class ComplexInteractionTest : BaseTest() {
    private var actualString = ""

    @Before
    fun setUp() {
        actualString = """
            # Recipe
            
            Step by step guide 
            1. Turn on the heat. Let oven preheat 10 minutes
            >!TIMER PT10M
            ![first image](IMAGE_PLACEHOLDER)
            2. Cut a fish
            3. Add cut fish to the cake
            ![second image](IMAGE_PLACEHOLDER)
            4. Bake 40 minutes
            >!TIMER PT40M
            
        """.trimIndent().replace("IMAGE_PLACEHOLDER", R.drawable.image.toString())
        composeTestRule.setContent {
            var markupString by remember {
                mutableStateOf(actualString)
            }
            val scrollState = rememberScrollState()
            Column(modifier = Modifier.verticalScroll(scrollState)) {
                RichEdit(
                    markupString = markupString,
                    onMarkupChanged = {
                        markupString = it
                        actualString = it
                    }
                )
            }
        }
    }

    @Test
    fun removeFirstTimer() {
        composeTestRule.onAllNodesWithContentDescription("Remove timer").onFirst().performClick()
        val expected = """
            # Recipe
            
            Step by step guide 
            1. Turn on the heat. Let oven preheat 10 minutes
            ![first image](IMAGE_PLACEHOLDER)
            2. Cut a fish
            3. Add cut fish to the cake
            ![second image](IMAGE_PLACEHOLDER)
            4. Bake 40 minutes
            >!TIMER PT40M
            
        """.trimIndent().replace("IMAGE_PLACEHOLDER", R.drawable.image.toString())
        assertEquals(expected, actualString)
    }

    @Test
    fun removeSecondTimer() {
        composeTestRule.onNodeWithText("40m timer").performScrollTo()
        composeTestRule.onAllNodesWithContentDescription("Remove timer")[1].performClick()
        val expected = """
            # Recipe
            
            Step by step guide 
            1. Turn on the heat. Let oven preheat 10 minutes
            >!TIMER PT10M
            ![first image](IMAGE_PLACEHOLDER)
            2. Cut a fish
            3. Add cut fish to the cake
            ![second image](IMAGE_PLACEHOLDER)
            4. Bake 40 minutes
            
        """.trimIndent().replace("IMAGE_PLACEHOLDER", R.drawable.image.toString())
        assertEquals(expected, actualString)
    }

    @Test
    fun removeFirstImage() {
        composeTestRule.onAllNodesWithContentDescription("Remove image").onFirst().performClick()
        val expected = """
            # Recipe
            
            Step by step guide 
            1. Turn on the heat. Let oven preheat 10 minutes
            >!TIMER PT10M
            2. Cut a fish
            3. Add cut fish to the cake
            ![second image](IMAGE_PLACEHOLDER)
            4. Bake 40 minutes
            >!TIMER PT40M
            
        """.trimIndent().replace("IMAGE_PLACEHOLDER", R.drawable.image.toString())
        assertEquals(expected, actualString)
    }

    @Test
    fun removeSecondImage() {
        composeTestRule.onNodeWithText("40m timer").performScrollTo()
        composeTestRule.onAllNodesWithContentDescription("Remove image")[1].performClick()
        val expected = """
            # Recipe
            
            Step by step guide 
            1. Turn on the heat. Let oven preheat 10 minutes
            >!TIMER PT10M
            ![first image](IMAGE_PLACEHOLDER)
            2. Cut a fish
            3. Add cut fish to the cake
            4. Bake 40 minutes
            >!TIMER PT40M
            
        """.trimIndent().replace("IMAGE_PLACEHOLDER", R.drawable.image.toString())
        assertEquals(expected, actualString)
    }
}