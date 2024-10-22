package com.aidarn.richedit

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.buildAnnotatedString

abstract class DecoratedTextTest : BaseUnitTest() {
    abstract fun transformString(input: String): AnnotatedString
    final override fun addCharAtStartTestData(): Pair<String, AnnotatedString> {
        return Pair("atest", transformString("atest"))
    }

    final override fun addCharInMiddleTestData(): Pair<String, AnnotatedString> {
        return Pair("teast", transformString("teast"))
    }

    final override fun addCharAtEndTestData(): Pair<String, AnnotatedString> {
        return Pair("testa", transformString("testa"))
    }

    final override fun addStringAtStartTestData(): Pair<String, AnnotatedString> {
        return Pair("aatest", transformString("aatest"))
    }

    final override fun addStringInMiddleTestData(): Pair<String, AnnotatedString> {
        return Pair("teaast", transformString("teaast"))
    }

    final override fun addStringAtEndTestData(): Pair<String, AnnotatedString> {
        return Pair("testaa", transformString("testaa"))
    }

    final override fun removeCharAtStartTestData(): Pair<String, AnnotatedString> {
        return Pair("est", transformString("est"))
    }

    final override fun removeCharInMiddleTestData(): Pair<String, AnnotatedString> {
        return Pair("tet", transformString("tet"))
    }

    final override fun removeCharAtEndTestData(): Pair<String, AnnotatedString> {
        return Pair("tes", transformString("tes"))
    }

    final override fun removeStringAtStartTestData(): Pair<String, AnnotatedString> {
        return Pair("st", transformString("st"))
    }

    final override fun removeStringInMiddleTestData(): Pair<String, AnnotatedString> {
        return Pair("tt", transformString("tt"))
    }

    final override fun removeStringAtEndTestData(): Pair<String, AnnotatedString> {
        return Pair("te", transformString("te"))
    }

    final override fun replaceCharAtStartTestData(): Pair<String, AnnotatedString> {
        return Pair("aest", transformString("aest"))
    }

    final override fun replaceCharInMiddleTestData(): Pair<String, AnnotatedString> {
        return Pair("teat", transformString("teat"))
    }

    final override fun replaceCharAtEndTestData(): Pair<String, AnnotatedString> {
        return Pair("tesa", transformString("tesa"))
    }

    final override fun replaceStringAtStartTestData(): Pair<String, AnnotatedString> {
        return Pair("aast", transformString("aast"))
    }

    final override fun replaceStringInMiddleTestData(): Pair<String, AnnotatedString> {
        return Pair("taat", transformString("taat"))
    }

    final override fun replaceStringAtEndTestData(): Pair<String, AnnotatedString> {
        return Pair("teaa", transformString("teaa"))
    }

    final override fun addNewlineAtStartTestData(): Pair<String, AnnotatedString> {
        return Pair("\ntest", buildAnnotatedString {
            append("\n")
            append(transformString("test"))
        })
    }

    final override fun addNewlineInMiddleTestData(): Pair<String, AnnotatedString> {
        return Pair("te\nst", buildAnnotatedString {
            append(transformString("te"))
            append("\n")
            append(transformString("st"))
        })
    }

    final override fun addNewlineAtEndTestData(): Pair<String, AnnotatedString> {
        return Pair("test\n", buildAnnotatedString {
            append(transformString("test\n"))
        })
    }
}