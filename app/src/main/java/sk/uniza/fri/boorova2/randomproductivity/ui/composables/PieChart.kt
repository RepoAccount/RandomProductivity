package sk.uniza.fri.boorova2.randomproductivity.ui.composables

import android.content.Context
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry

@Composable
fun PieChart(data: Map<String, Float>, context: Context) {
    AndroidView(
        factory = {
            PieChart(context).apply {
                val entries = data.map { PieEntry(it.value, it.key) }
                val dataSet = PieDataSet(entries,"").apply {
                    sliceSpace = 2f
                    colors = listOf(
                        Color.Red,
                        Color.Yellow,
                        Color.Blue
                    ).map { it.toArgb()}
                }
                this.data = PieData(dataSet)
                invalidate()
                description.isEnabled = false
                setUsePercentValues(true)
                legend.isEnabled = false
                animateY(1000)
                isDrawHoleEnabled = false
            }
        },
        modifier = Modifier.fillMaxWidth().height(200.dp),
        update = { chart ->
            val entries = data.map { PieEntry(it.value, it.key) }
            val dataSet = PieDataSet(entries, "").apply {
                sliceSpace = 2f
                colors = listOf(
                    Color.Red,
                    Color.Yellow,
                    Color.Blue
                ).map { it.toArgb()}
            }
            chart.data = PieData(dataSet)
            chart.invalidate()
        }
    )
}
