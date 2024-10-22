package com.aidarn.gitcook.data

import com.aidarn.gitcook.Constants


class IngredientList(private val ingredients: MutableList<IngredientInfo>) :
    List<IngredientInfo> by ingredients {
    fun replace(index: Int, newValue: IngredientInfo): IngredientList {
        ingredients[index] = newValue
        return IngredientList(ingredients)
    }

    fun remove(index: Int): IngredientList {
        ingredients.removeAt(index)
        return IngredientList(ingredients)
    }

    fun add(value: IngredientInfo): IngredientList {
        ingredients.add(value)
        return IngredientList(ingredients)
    }

    override fun toString(): String {
        val stringBuilder = StringBuilder()
        ingredients.forEach {
            stringBuilder.append(
                it.name,
                Constants.INGREDIENT_INFO_DELIMITER,
                it.quantity,
                Constants.SEPARATOR
            )
        }
        if (stringBuilder.isNotBlank()) {
            stringBuilder.deleteCharAt(stringBuilder.lastIndex)
        }

        return stringBuilder.toString()
    }

    fun toBulletList() = buildString {
        ingredients.forEach {
            append("\u2022 ${it.name}")
            if (it.quantity.isNotBlank()) {
                append(" \u2013 ${it.quantity}")
            }
            appendLine()
        }
        if (isNotEmpty()) {
            deleteCharAt(lastIndex)
        }
    }
}

fun getIngredientInfos(rawItems: String) = IngredientList(
    rawItems.split(Constants.SEPARATOR).fold(mutableListOf()) { list, string ->
        if (string.isNotBlank()) {
            val rawInfo = string.split(Constants.INGREDIENT_INFO_DELIMITER, limit = 2)
            val info = if (rawInfo.size == 2) {
                IngredientInfo(rawInfo[0], rawInfo[1])
            } else {
                IngredientInfo(rawInfo[0], "")
            }
            list.add(info)
        }
        list
    }
)

data class IngredientInfo(val name: String, val quantity: String)