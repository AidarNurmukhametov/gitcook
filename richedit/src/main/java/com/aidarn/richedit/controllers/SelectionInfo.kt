package com.aidarn.richedit.controllers

import androidx.compose.runtime.Immutable

@Immutable
data class SelectionInfo(
    val isH1Selected: Boolean = false,
    val isH2Selected: Boolean = false,
    val isBold: Boolean = false,
    val isItalic: Boolean = false,
    val isOrderedList: Boolean = false,
    val isUnorderedList: Boolean = false
)
