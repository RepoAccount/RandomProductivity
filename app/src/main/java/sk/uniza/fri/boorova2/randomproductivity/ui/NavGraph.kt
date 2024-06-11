package sk.uniza.fri.boorova2.randomproductivity.ui

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import sk.uniza.fri.boorova2.randomproductivity.ui.screens.HomeScreen
import sk.uniza.fri.boorova2.randomproductivity.ui.screens.TaskDetailScreen
import sk.uniza.fri.boorova2.randomproductivity.ui.viewmodels.StatisticViewModel
import sk.uniza.fri.boorova2.randomproductivity.ui.viewmodels.TaskViewModel

@Composable
fun NavGraph(startDestination: String = "home") {
    val navController = rememberNavController()
    val taskViewModel: TaskViewModel = hiltViewModel()
    val statisticViewModel: StatisticViewModel = hiltViewModel()

    NavHost(navController = navController, startDestination = startDestination) {
        composable("home") {
            HomeScreen(navController = navController, viewModel = taskViewModel,
                /*statisticViewModel = statisticViewModel*/)
        }
        composable(
            "taskDetail/{taskId}",
            arguments = listOf(
                navArgument("taskId") { type = NavType.LongType },
            )
        ) { backStackEntry ->
            val taskId = backStackEntry.arguments?.getLong("taskId") ?: 0L
            TaskDetailScreen(taskId = taskId, onTaskUpdated = {
                navController.popBackStack()
            }, taskViewModel = taskViewModel, statisticViewModel = statisticViewModel)
        }
    }
}
