package sk.uniza.fri.boorova2.randomproductivity.ui.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import sk.uniza.fri.boorova2.randomproductivity.R
import sk.uniza.fri.boorova2.randomproductivity.database.entities.TaskEntity

@Composable
fun TaskCreationDialog(showDialog: Boolean, onDismiss: () -> Unit, onSave: (TaskEntity) -> Unit) {
    if (showDialog) {

        var title by rememberSaveable { mutableStateOf("") }

        AlertDialog(

            onDismissRequest = onDismiss,
            title = { Text(stringResource(R.string.add_new_task)) },
            text = {
                Column {
                    TextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text(stringResource(R.string.name)) }
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    onSave(TaskEntity(name = title))
                    onDismiss()
                }) {
                    Text(stringResource(R.string.save))
                }
            },
            dismissButton = {
                Button(onClick = onDismiss) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )

    }
}

