package com.jackapps.batteryalarm.presentation.settings_screen.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.jackapps.batteryalarm.R
import com.jackapps.batteryalarm.presentation.theme.layoutPadding

@Composable
fun DialogWithOptions(
    title: String,
    options: List<String>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedOption by remember { mutableStateOf(options[selectedIndex]) }

    val onSelected = { index: Int, text: String ->
        onSelect(index)
        selectedOption = text
        onDismiss()
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(modifier = Modifier.fillMaxWidth()) {
            Column {
                Text(
                    text = title,
                    fontSize = 20.sp,
                    modifier = Modifier.padding(horizontal = 32.dp, vertical = 20.dp)
                )

                options.forEachIndexed { index, text ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .selectable((text == selectedOption)) { onSelected(index, text) }
                            .padding(horizontal = layoutPadding)
                    ) {
                        RadioButton(
                            selected = (text == selectedOption),
                            onClick = { onSelected(index, text) }
                        )

                        Text(
                            text = text,
                            style = MaterialTheme.typography.bodyLarge.copy(fontSize = 18.sp),
                            modifier = Modifier.padding(start = 16.dp)
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    TextButton(
                        onClick = onDismiss,
                        modifier = Modifier.align(Alignment.CenterEnd)
                    ) {
                        Text(stringResource(R.string.dialog_action_cancel))
                    }
                }
            }
        }
    }
}
