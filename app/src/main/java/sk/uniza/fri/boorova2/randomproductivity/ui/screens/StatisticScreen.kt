package sk.uniza.fri.boorova2.randomproductivity.ui.screens


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import sk.uniza.fri.boorova2.randomproductivity.R
import sk.uniza.fri.boorova2.randomproductivity.ui.viewmodels.StatisticViewModel
import sk.uniza.fri.boorova2.randomproductivity.ui.composables.StatisticChart
import sk.uniza.fri.boorova2.randomproductivity.ui.composables.PieChart
import sk.uniza.fri.boorova2.randomproductivity.ui.viewmodels.TaskViewModel
import java.util.Calendar

@Composable
fun StatisticScreen(
    statisticViewModel: StatisticViewModel = hiltViewModel(),
    taskViewModel: TaskViewModel = hiltViewModel(),
    onBack: () -> Unit
) {

    val weeklyStats = statisticViewModel.getCompletionByTask(
        Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, -7)
        }.time
    ).observeAsState(emptyMap())

    val monthlyStats = statisticViewModel.getCompletionByTask(
        Calendar.getInstance().apply {
            add(Calendar.MONTH, -1)
        }.time
    ).observeAsState(emptyMap())

    val monthlyTimeStats = statisticViewModel.getMonthlyTimeSpentByTask().observeAsState(emptyMap())

    val tasks = taskViewModel.allTasks.observeAsState(emptyList())

    val taskNames = tasks.value.associate { it.id to it.name }

    val weeklyData = weeklyStats.value.mapKeys { taskNames[it.key] ?: stringResource(R.string.no_task) }
    val monthlyData = monthlyStats.value.mapKeys { taskNames[it.key] ?: stringResource(R.string.no_task) }
    val monthlyTimeData = monthlyTimeStats.value.mapKeys { taskNames[it.key] ?: stringResource(R.string.no_task) }

    val taskCompletionPercentages = statisticViewModel.getMonthlyTaskCompletionPercentage().observeAsState(emptyMap())
    val bestTaskAndDay = statisticViewModel.getBestTaskAndDay(taskNames).observeAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        item {
            Text(stringResource(R.string.weekly), style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.padding(top = 20.dp, start = 30.dp, end = 30.dp))
            Box(modifier = Modifier.padding(30.dp)) {
                StatisticChart(data = weeklyData)
            }
            Text(stringResource(R.string.monthly), style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.padding(start = 30.dp, end = 30.dp))
            Box(modifier = Modifier.padding(30.dp)) {
                StatisticChart(data = monthlyData)
            }
            Row(modifier = Modifier
                .fillMaxWidth()
                .padding(start = 30.dp, end = 30.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .weight(2f)
                        .padding(end = 10.dp)
                ) {
                    PieChart(taskCompletionPercentages.value.mapKeys { taskNames[it.key] ?: stringResource(R.string.no_task) }, LocalContext.current)
                }

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(stringResource(R.string.best_task), style = MaterialTheme.typography.bodyLarge)
                    Text(bestTaskAndDay.value?.first ?: stringResource(R.string.no_task), style = MaterialTheme.typography.bodyMedium)

                    HorizontalDivider(
                        color = Color.DarkGray, thickness = 1.dp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 20.dp)
                    )

                    Text(stringResource(R.string.best_day), style = MaterialTheme.typography.bodyLarge)
                    Text(bestTaskAndDay.value?.second ?: stringResource(R.string.no_task), style = MaterialTheme.typography.bodyMedium)
                }
            }

            Text(stringResource(R.string.monthly_time), style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(top = 20.dp, start = 30.dp, end = 30.dp))
            Box(modifier = Modifier.padding(30.dp)) {
                StatisticChart(data = monthlyTimeData)
            }

            Button(
                onClick = { onBack() },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 30.dp, end = 30.dp)
            ) {
                Text(stringResource(R.string.back_to_task))
            }
        }
    }
}
