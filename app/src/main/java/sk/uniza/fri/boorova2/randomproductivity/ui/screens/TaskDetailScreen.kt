package sk.uniza.fri.boorova2.randomproductivity.ui.screens

import android.app.DatePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import sk.uniza.fri.boorova2.randomproductivity.R
import sk.uniza.fri.boorova2.randomproductivity.database.entities.TaskEntity
import sk.uniza.fri.boorova2.randomproductivity.ui.viewmodels.TaskDetailViewModel
import sk.uniza.fri.boorova2.randomproductivity.ui.viewmodels.TaskViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskDetailScreen(viewModel: TaskDetailViewModel = hiltViewModel(), taskId: Long,
                     onTaskUpdated: (TaskEntity) -> Unit, sharedViewModel: TaskViewModel
) {
    val task by viewModel.selectedTask.observeAsState()
    // var showGoalReachedPopup by remember { mutableStateOf(false) }

    LaunchedEffect(taskId) {
        viewModel.loadTask(taskId)
    }

    /*if (showGoalReachedPopup) {
        AlertDialog(
            onDismissRequest = { showGoalReachedPopup = false },
            title = { Text("Goal Reached!") },
            text = { Text("Congratulations! You've reached your goal.") },
            confirmButton = {
                Button(onClick = { showGoalReachedPopup = false }) {
                    Text("OK")
                }
            }
        )
    }*/

    task?.let { thisTask ->
        val title by remember { mutableStateOf(thisTask.name) }
        var priority by remember { mutableStateOf(thisTask.priority.toString()) }
        var hideFromShuffler by remember { mutableStateOf(thisTask.hideFromShuffler) }
        var notify by remember { mutableStateOf(thisTask.notify) }
        var goalAmount by remember { mutableStateOf(thisTask.goalAmount?.toString() ?: "") }
        val currentTask by sharedViewModel.currentTask.observeAsState()
        val calendar = remember { Calendar.getInstance() }
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        var dueDate by remember { mutableStateOf(thisTask.dueDate?.let { dateFormat.format(it) } ?: "") }
        var showDatePickerDialog by remember { mutableStateOf(false) }

        if (showDatePickerDialog) {
            DatePickerDialog(
                LocalContext.current,
                { _, year, month, dayOfMonth ->
                    calendar.set(year, month, dayOfMonth)
                    dueDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.time)
                    showDatePickerDialog = false
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        Column(modifier = Modifier.padding(16.dp)) {

            Box(modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)) {
                Text("STATS PLACEHOLDER")
            }

            if (currentTask == null)
            {
                Button(onClick = { sharedViewModel.selectTask(thisTask) }) {
                    Text(stringResource(R.string.select_task))
                }
            } else if (currentTask!!.id == thisTask.id) {
                Button(onClick = { viewModel.completeTask(thisTask.id)
                    sharedViewModel.clearCurrentTask()}) {
                    Text(stringResource(R.string.complete_task))
                }
                Button(onClick = { sharedViewModel.clearCurrentTask()
                }) {
                    Text(stringResource(R.string.drop_task))
                }
            }

            TextField(
                value = priority,
                onValueChange = { priority = it },
                label = { Text(stringResource(R.string.priority)) },
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(stringResource(R.string.hide))
                Checkbox(checked = hideFromShuffler, onCheckedChange = { hideFromShuffler = it })
            }

            Text(
                text = dueDate.ifEmpty { stringResource(R.string.deadline) },
                modifier = Modifier
                    .clickable { showDatePickerDialog = true }
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(30.dp)
                    .fillMaxWidth(),
                style = MaterialTheme.typography.bodyLarge
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(stringResource(R.string.notify))
                Checkbox(checked = notify, onCheckedChange = { notify = it })
            }

            TextField(
                value = goalAmount,
                onValueChange = { goalAmount = it },
                label = { Text(stringResource(R.string.goal)) },
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
            )
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = {
                    thisTask.progress / (thisTask.goalAmount?.toFloat() ?: 1f)
                },
            )
            Text("${thisTask.progress} / ${thisTask.goalAmount ?: stringResource(R.string.no_task)}")

            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = {
                val updatedTask = thisTask.copy(
                    name = title,
                    priority = priority.toIntOrNull() ?: thisTask.priority,
                    hideFromShuffler = hideFromShuffler,
                    dueDate = dueDate.takeIf { it.isNotBlank() }?.let { dateFormat.parse(it) } ?: thisTask.dueDate,
                    notify = notify,
                    goalAmount = goalAmount.toIntOrNull(),
                    progress = thisTask.progress
                )
                viewModel.updateTaskDetails(updatedTask)
                onTaskUpdated(updatedTask)
            }) {
                Text(stringResource(R.string.save))
            }
        }
    }
}
