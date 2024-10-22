package com.aidarn.richedit

import androidx.compose.ui.test.junit4.createComposeRule
import org.junit.Rule

abstract class BaseTest {
    companion object {
        const val THE_SOUP = "The Soup"
        const val THE_CAKE = "The Cake"
        const val ITALIC_NODE_TEXT = "Italic"
        const val BOLD_NODE_TEXT = "Bold"
        const val HEADING1_NODE_TEXT = "H1"
        const val HEADING2_NODE_TEXT = "H2"
    }

    @get:Rule
    val composeTestRule = createComposeRule()
}