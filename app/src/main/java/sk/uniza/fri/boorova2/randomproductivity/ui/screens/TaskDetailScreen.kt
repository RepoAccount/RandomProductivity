package sk.uniza.fri.boorova2.randomproductivity.ui.screens

import android.app.DatePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import sk.uniza.fri.boorova2.randomproductivity.R
import sk.uniza.fri.boorova2.randomproductivity.database.entities.TaskEntity
import sk.uniza.fri.boorova2.randomproductivity.ui.composables.StatisticChart
import sk.uniza.fri.boorova2.randomproductivity.ui.viewmodels.StatisticViewModel
import sk.uniza.fri.boorova2.randomproductivity.ui.viewmodels.TaskDetailViewModel
import sk.uniza.fri.boorova2.randomproductivity.ui.viewmodels.TaskViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


@Composable
fun TaskDetailScreen(viewModel: TaskDetailViewModel = hiltViewModel(), taskId: Long,
                     onTaskUpdated: (TaskEntity) -> Unit, taskViewModel: TaskViewModel,
                     statisticViewModel: StatisticViewModel, navController: NavController
) {
    val task by viewModel.selectedTask.observeAsState()
    var showGoalReachedPopup by remember { mutableStateOf(false) }
    val calendar = remember { Calendar.getInstance() }
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val context = LocalContext.current

    LaunchedEffect(taskId) {
        viewModel.loadTask(taskId)
    }

    task?.let { thisTask ->
        var priority by remember { mutableStateOf(thisTask.priority.toString()) }
        var hideFromShuffler by remember { mutableStateOf(thisTask.hideFromShuffler) }
        var notify by remember { mutableStateOf(thisTask.notify) }
        val currentTask by taskViewModel.currentTask.observeAsState()
        var dueDate by remember { mutableStateOf(thisTask.dueDate?.let { dateFormat.format(it) } ?: "") }
        var showDatePickerDialog by remember { mutableStateOf(false) }
        var goalAmount by remember { mutableStateOf(thisTask.goalAmount?.toString() ?: "") }
        var trackTime by remember { mutableStateOf(task?.trackTime ?: false) }
        var showTimeInputDialog by remember { mutableStateOf(false) }
        var timeSpent by remember { mutableStateOf("") }
        var newTaskName by remember { mutableStateOf(thisTask.name) }
        var showDeleteConfirm by remember { mutableStateOf(false) }

        val stats = if (trackTime) {
            statisticViewModel.getWeekTimeSpent(thisTask.id).observeAsState(emptyMap())
        } else {
            statisticViewModel.getWeekCompletionCount(thisTask.id).observeAsState(emptyMap())
        }

        val statsData = remember(stats.value) { stats.value }

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

        if (showGoalReachedPopup) {
            AlertDialog(
                onDismissRequest = {  },
                confirmButton = {
                    Button(onClick = {
                        task?.let { viewModel.resetGoal(it.id) }
                        goalAmount = ""
                        showGoalReachedPopup = false
                        onTaskUpdated(thisTask)
                    }) {
                        Text("OK")
                    }
                },
                text = { Text(stringResource(R.string.goal_done)) }
            )
        }

        if (showDeleteConfirm) {
            AlertDialog(
                onDismissRequest = { showDeleteConfirm = false },
                title = { Text(stringResource(R.string.confirm_delete)) },
                text = { Text(stringResource(R.string.confirm_delete_message)) },
                confirmButton = {
                    Button(
                        onClick = {
                            taskViewModel.removeTask(thisTask.id)
                            navController.popBackStack()
                        }
                    ) {
                        Text(stringResource(R.string.delete))
                    }
                },
                dismissButton = {
                    Button(
                        onClick = { showDeleteConfirm = false }
                    ) {
                        Text(stringResource(R.string.cancel))
                    }
                }
            )
        }


        LaunchedEffect(task?.progress) {
            if (task?.goalAmount != null && (task?.progress ?: 0) >= (task!!.goalAmount
                    ?: Int.MAX_VALUE)
            ) {
                showGoalReachedPopup = true
            }
        }

        LazyColumn(modifier = Modifier.padding(16.dp)) {
            item {

                Box(modifier = Modifier.fillMaxWidth().padding(20.dp)) {
                    StatisticChart(statsData)
                }

                Button(
                    onClick = {
                        navController.navigate("statistics")
                    },
                    modifier = Modifier.fillMaxWidth()
                        .padding(start = 30.dp, end = 30.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray)
                ) {
                    Text(stringResource(R.string.see_more_stats))
                }

                if (currentTask == null) {
                    Button(
                        onClick = { taskViewModel.selectTask(thisTask) },
                        modifier = Modifier.fillMaxWidth()
                            .padding(start = 30.dp, end = 30.dp, top = 10.dp)
                    ) {
                        Text(stringResource(R.string.select_task))
                    }
                } else if (currentTask!!.id == thisTask.id) {
                    Button(
                        onClick = {
                            if (trackTime) {
                                showTimeInputDialog = true
                            } else {
                                viewModel.completeTask(thisTask.id)
                                taskViewModel.clearCurrentTask()
                            }
                        }, modifier = Modifier.fillMaxWidth()
                            .padding(start = 30.dp, end = 30.dp, top = 10.dp)
                    ) {
                        Text(stringResource(R.string.complete_task))
                    }
                    Button(
                        onClick = {
                            taskViewModel.clearCurrentTask()
                        }, modifier = Modifier.fillMaxWidth()
                            .padding(start = 30.dp, end = 30.dp, top = 10.dp)
                    ) {
                        Text(stringResource(R.string.drop_task))
                    }
                }

                HorizontalDivider(
                    color = Color.DarkGray, thickness = 1.dp,
                    modifier = Modifier.fillMaxWidth()
                        .padding(30.dp)
                )

                TextField(
                    value = newTaskName,
                    onValueChange = { newTaskName = it },
                    label = { Text(stringResource(R.string.rename_task)) },
                    modifier = Modifier.padding(horizontal = 30.dp).fillMaxWidth()
                )

                Button(
                    onClick = {
                        showDeleteConfirm = true
                    },
                    modifier = Modifier.fillMaxWidth().padding(start = 30.dp, end = 30.dp, top = 10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text(stringResource(R.string.delete_task))
                }

                HorizontalDivider(
                    color = Color.DarkGray, thickness = 1.dp,
                    modifier = Modifier.fillMaxWidth()
                        .padding(30.dp)
                )

                if (showTimeInputDialog) {
                    AlertDialog(
                        onDismissRequest = { showTimeInputDialog = false },
                        title = { Text(stringResource(R.string.time_spent)) },
                        text = {
                            TextField(
                                value = timeSpent,
                                onValueChange = { timeSpent = it },
                                label = { Text(stringResource(R.string.enter_time_spent)) },
                                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
                            )
                        },
                        confirmButton = {
                            Button(
                                onClick = {
                                    val timeSpentLong = timeSpent.toLongOrNull() ?: 0L
                                    viewModel.completeTask(thisTask.id, timeSpentLong)
                                    taskViewModel.clearCurrentTask()
                                    showTimeInputDialog = false
                                    timeSpent = ""
                                },
                                modifier = Modifier.fillMaxWidth()
                                    .padding(start = 30.dp, end = 30.dp, top = 30.dp)
                            ) {
                                Text(stringResource(R.string.complete_task))
                            }
                        },
                        dismissButton = {
                            Button(
                                onClick = { showTimeInputDialog = false },
                                modifier = Modifier.fillMaxWidth()
                                    .padding(start = 30.dp, end = 30.dp, top = 30.dp)
                            ) {
                                Text(stringResource(R.string.cancel))
                            }
                        }
                    )
                }

                TextField(
                    value = priority,
                    onValueChange = { priority = it },
                    label = { Text(stringResource(R.string.priority)) },
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                    modifier = Modifier.padding(start = 30.dp, end = 30.dp)
                        .fillMaxWidth()
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(start = 30.dp, end = 30.dp, top = 10.dp)
                ) {
                    Text(stringResource(R.string.hide))
                    Checkbox(
                        checked = hideFromShuffler,
                        onCheckedChange = { hideFromShuffler = it })
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(start = 30.dp, end = 30.dp)
                ) {
                    Text(stringResource(R.string.time_track))
                    Checkbox(checked = trackTime, onCheckedChange = { trackTime = it })
                }

                HorizontalDivider(
                    color = Color.DarkGray, thickness = 1.dp,
                    modifier = Modifier.fillMaxWidth()
                        .padding(30.dp)
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(start = 30.dp, end = 30.dp)
                ) {
                    Text(
                        text = dueDate.ifEmpty { stringResource(R.string.deadline) },
                        modifier = Modifier
                            .clickable { showDatePickerDialog = true }
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .padding(20.dp)
                            .fillMaxWidth(),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(start = 30.dp, end = 30.dp, top = 10.dp)
                ) {
                    Text(stringResource(R.string.notify))
                    Checkbox(checked = notify, onCheckedChange = { notify = it })
                }

                HorizontalDivider(
                    color = Color.DarkGray, thickness = 1.dp,
                    modifier = Modifier.fillMaxWidth()
                        .padding(30.dp)
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth().padding(start = 30.dp, end = 30.dp)
                ) {
                    TextField(
                        value = goalAmount,
                        onValueChange = { goalAmount = it },
                        label = { Text(stringResource(R.string.goal)) },
                        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Column(
                    modifier = Modifier.fillMaxWidth().padding(start = 30.dp, end = 30.dp, top = 20.dp)
                ) {
                    LinearProgressIndicator(
                        progress = {
                            thisTask.progress / (thisTask.goalAmount?.toFloat() ?: 1f)
                        },
                        modifier = Modifier.fillMaxWidth().height(20.dp)
                    )
                    Text("${thisTask.progress} / ${thisTask.goalAmount ?: stringResource(R.string.no_task)}")
                }

                Button(
                    onClick = {
                        val goal = goalAmount.toIntOrNull()
                        val validatedGoalAmount = if (goal != null && goal > 0) {
                            goal
                        } else {
                            null
                        }

                        val updatedTask = thisTask.copy(
                            name = newTaskName,
                            priority = priority.toIntOrNull() ?: thisTask.priority,
                            hideFromShuffler = hideFromShuffler,
                            dueDate = dueDate.takeIf { it.isNotBlank() }
                                ?.let { dateFormat.parse(it) } ?: thisTask.dueDate,
                            notify = notify,
                            goalAmount = validatedGoalAmount,
                            progress = if (validatedGoalAmount == null) 0 else thisTask.progress,
                            trackTime = trackTime
                        )

                        if (validatedGoalAmount != null && thisTask.progress >= validatedGoalAmount) {
                            showGoalReachedPopup = true
                        }

                        viewModel.updateTaskDetails(updatedTask)
                        taskViewModel.cancelTaskNotifications(context, thisTask.id)
                        taskViewModel.scheduleTaskNotification(context, updatedTask)

                        if (!showGoalReachedPopup) {
                            onTaskUpdated(updatedTask)
                        }

                    },
                    modifier = Modifier.fillMaxWidth()
                        .padding(start = 30.dp, end = 30.dp, top = 30.dp)
                ) {
                    Text(stringResource(R.string.save))
                }
            }
        }
    }
}
