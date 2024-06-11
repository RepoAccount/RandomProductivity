package sk.uniza.fri.boorova2.randomproductivity.ui.composables

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter

@Composable
fun WeeklyStatisticsChart(data: Map<String, Long>) {

    val barEntries = data.entries.mapIndexed { index, entry ->
        BarEntry(index.toFloat(), entry.value.toFloat())
    }

    val dataSet = BarDataSet(barEntries, "Weekly Stats").apply {
        color = Color.DarkGray.toArgb()
    }

    val barData = BarData(dataSet)

    AndroidView(
        factory = {
            BarChart(it).apply {
                this.data = barData
                xAxis.valueFormatter = IndexAxisValueFormatter(listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"))
                description.isEnabled = false
                axisRight.isEnabled = false
                animateY(1000)
                setFitBars(true)
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        update = { chart ->
            chart.data = barData
            chart.invalidate()
        }
    )

}


