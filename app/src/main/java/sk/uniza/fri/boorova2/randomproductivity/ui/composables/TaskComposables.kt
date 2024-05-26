package sk.uniza.fri.boorova2.randomproductivity.ui.composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import sk.uniza.fri.boorova2.randomproductivity.database.entities.TaskEntity

@Composable
fun TaskList(tasks: List<TaskEntity>, onTaskClicked: (TaskEntity) -> Unit,
             modifier: Modifier = Modifier) {
    LazyColumn(modifier = modifier) {
        items(tasks) { task ->
            TaskItem(task, onTaskClicked)
        }
    }
}

@Composable
fun TaskItem(task: TaskEntity, onTaskClicked: (TaskEntity) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onTaskClicked(task) },
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = task.name, style = MaterialTheme.typography.bodyMedium)
        }
    }
}
