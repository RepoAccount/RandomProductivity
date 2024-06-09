package sk.uniza.fri.boorova2.randomproductivity.ui.screens

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.delay
import sk.uniza.fri.boorova2.randomproductivity.R
import sk.uniza.fri.boorova2.randomproductivity.database.entities.TaskEntity
import sk.uniza.fri.boorova2.randomproductivity.ui.composables.TaskCreationDialog
import sk.uniza.fri.boorova2.randomproductivity.ui.composables.TaskList
import sk.uniza.fri.boorova2.randomproductivity.ui.viewmodels.TaskViewModel

@Composable
fun HomeScreen(viewModel: TaskViewModel = hiltViewModel()) {

    val tasks by viewModel.allTasks.observeAsState(emptyList())
    var showDialog by remember { mutableStateOf(false) }
    var shuffledTask by remember { mutableStateOf<TaskEntity?>(null) }
    var isShuffling by remember { mutableStateOf(false) }
    var currentTaskIndex by remember { mutableIntStateOf(0) }

    Scaffold { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {

            LaunchedEffect(isShuffling) {
                if (isShuffling) {
                    val totalTasks = tasks.size
                    repeat(totalTasks * 2) {
                        currentTaskIndex = (currentTaskIndex + 1) % totalTasks
                        delay(100L)
                    }
                    shuffledTask = tasks[currentTaskIndex]
                    isShuffling = false
                }
            }

            Button(
                onClick = { showDialog = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 30.dp, end = 30.dp, top = 30.dp)
            ) {
                Text(stringResource(R.string.new_task), fontSize = 30.sp)
            }

            Button(
                onClick = { isShuffling = true },
                enabled = !isShuffling && shuffledTask == null,
                colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 30.dp, end = 30.dp, top = 30.dp)
            ) {
                Text(stringResource(R.string.shuffle_tasks), fontSize = 30.sp)
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 30.dp, end = 30.dp, top = 30.dp)
                    .border(1.dp, Color.Gray, RoundedCornerShape(8.dp))
                    .padding(16.dp)
            ) {
                Text(
                    text = stringResource(R.string.shuffling) +
                            " ${
                                tasks.getOrNull(currentTaskIndex)?.name
                                    ?: stringResource(R.string.no_task)
                            }",
                    style = MaterialTheme.typography.headlineSmall
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 30.dp, end = 30.dp, top = 30.dp)
                    .border(1.dp, Color.Gray, RoundedCornerShape(8.dp))
                    .padding(16.dp)
            ) {
                Text(
                    text = stringResource(R.string.current_task) +
                            " ${shuffledTask?.name ?: stringResource(R.string.no_task)}",
                    style = MaterialTheme.typography.headlineSmall
                )
            }


            Text(
                text = stringResource(R.string.task_list),
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(start = 30.dp, end = 30.dp, top = 30.dp)
            )

            TaskList(tasks = tasks, onTaskClicked = { task ->
                // TODO: Show task details
            }, modifier = Modifier.padding(30.dp))
        }

        TaskCreationDialog(
            showDialog = showDialog,
            onDismiss = { showDialog = false },
            onSave = { taskEntity -> viewModel.addTask(taskEntity) }
        )
    }
}
