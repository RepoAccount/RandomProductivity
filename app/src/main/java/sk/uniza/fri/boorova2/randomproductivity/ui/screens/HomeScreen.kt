package sk.uniza.fri.boorova2.randomproductivity.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import sk.uniza.fri.boorova2.randomproductivity.R
import sk.uniza.fri.boorova2.randomproductivity.ui.composables.TaskCreationDialog
import sk.uniza.fri.boorova2.randomproductivity.ui.composables.TaskList
import sk.uniza.fri.boorova2.randomproductivity.ui.viewmodels.TaskViewModel

@Composable
fun HomeScreen(viewModel: TaskViewModel = hiltViewModel()) {

    val tasks by viewModel.allTasks.observeAsState(emptyList())
    var showDialog by remember { mutableStateOf(false) }

    Scaffold { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            Button(
                onClick = { showDialog = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 30.dp, end = 30.dp, top = 30.dp)
            ) {
                Text(stringResource(R.string.new_task), fontSize = 30.sp)
            }

            // TODO: Shuffle
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 30.dp, end = 30.dp, top = 30.dp)
                    .height(200.dp)
                    .background(MaterialTheme.colorScheme.primary)
            ) {
                Text("Shuffler Placeholder", modifier = Modifier.align(Alignment.Center))
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
