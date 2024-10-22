package com.aidarn.gitcook.views.edit

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.aidarn.gitcook.Constants
import com.aidarn.gitcook.R
import com.aidarn.gitcook.data.IngredientInfo
import com.aidarn.gitcook.data.getIngredientInfos


@Composable
internal fun IngredientEditor(rawItems: String, onItemsChanged: (String) -> Unit) {
    val (items, setItems) = remember { mutableStateOf(getIngredientInfos(rawItems)) }
    Column(verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.column_spacing))) {
        NewItemEntryField(
            placeholder = stringResource(R.string.ingredients),
            modifier = Modifier.fillMaxWidth()
        ) { newItem ->
            val newItems = items.add(IngredientInfo(newItem, ""))
            setItems(newItems)
            onItemsChanged(newItems.toString())
        }
        items.forEachIndexed { index, item ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.small_padding)),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = item.name, modifier = Modifier.weight(3f))
                OutlinedTextField(
                    value = item.quantity,
                    onValueChange = {
                        val newItems = items.replace(index, IngredientInfo(item.name, it))
                        setItems(newItems)
                        onItemsChanged(newItems.toString())
                    },
                    placeholder = { Text(text = stringResource(R.string.enter_quantity)) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    modifier = Modifier.weight(8f)
                )
                IconButton(onClick = {
                    val newItems = items.remove(index)
                    setItems(newItems)
                    onItemsChanged(newItems.toString())
                }) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = stringResource(R.string.remove_ingredient)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun IngredientEditorPreview() {
    var ingredients by remember { mutableStateOf("Salt_1 tea spoon" + Constants.SEPARATOR + "Pepper") }
    Column(modifier = Modifier.padding(16.dp)) {
        IngredientEditor(ingredients) {
            ingredients = it
        }
    }
}
