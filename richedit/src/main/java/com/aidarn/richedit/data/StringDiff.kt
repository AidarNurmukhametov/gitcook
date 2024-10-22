package com.aidarn.richedit.data

sealed class StringDiff {
    abstract val start: Int
    abstract val end: Int

    data class Add(override val start: Int, val string: String) : StringDiff() {
        override val end: Int = start + string.length
    }

    data class Remove(override val start: Int, override val end: Int) : StringDiff()
    data class Replace(override val start: Int, override val end: Int, val string: String) :
        StringDiff()
}

fun compare(first: String, second: String): StringDiff? {
    if (first == second) {
        return null
    }
    val minLength = minOf(first.length, second.length)

    var index = 0
    while (index < minLength && first[index] == second[index]) {
        index++
    }

    if (index == first.length) {
        return StringDiff.Add(index, second.substring(index))
    }

    if (index == second.length) {
        return StringDiff.Remove(index, first.length)
    }

    var endIndexFirst = first.length
    var endIndexSecond = second.length

    while (endIndexFirst > index && endIndexSecond > index && first[endIndexFirst - 1] == second[endIndexSecond - 1]) {
        endIndexFirst--
        endIndexSecond--
    }

    return if (index < endIndexFirst && index < endIndexSecond) {
        StringDiff.Replace(index, endIndexFirst, second.substring(index, endIndexSecond))
    } else if (index < endIndexFirst) {
        StringDiff.Remove(index, endIndexFirst)
    } else {
        val step = endIndexSecond - index
        var shift = step
        while ((index - shift) > 0) {
            if (second.substring(index - shift, endIndexSecond - shift) == second.substring(
                    index,
                    endIndexSecond
                )
            ) {
                shift += step
            } else {
                break
            }
        }
        shift -= step

        StringDiff.Add(index - shift, second.substring(index - shift, endIndexSecond - shift))
    }
}
