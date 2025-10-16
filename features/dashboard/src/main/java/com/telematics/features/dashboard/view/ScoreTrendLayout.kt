package com.telematics.features.dashboard.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.appcompat.content.res.AppCompatResources
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.updatePadding
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.telematics.core.common.extension.dpToPx
import com.telematics.core.model.statistics.DailyScore
import com.telematics.features.dashboard.R
import com.telematics.features.dashboard.databinding.LayoutScoreTrendBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class ScoreTrendLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private val binding =
        LayoutScoreTrendBinding.inflate(LayoutInflater.from(context), this)

    init {
        init()
    }

    private fun init() {

        background = AppCompatResources.getDrawable(context, R.drawable.bg_card)

        with(binding) {
            lineChart.axisRight.isEnabled = false
            lineChart.legend.isEnabled = false
            lineChart.xAxis.isEnabled = false

            val yAxis = lineChart.axisLeft
            yAxis.axisMinimum = -5f
            yAxis.axisMaximum = 105f
            yAxis.setDrawGridLines(true)
            yAxis.gridColor = ContextCompat.getColor(context, R.color.design_dark_white)
            yAxis.gridLineWidth = 1.dpToPx.toFloat()
            yAxis.enableGridDashedLine(
                4.dpToPx.toFloat(),
                4.dpToPx.toFloat(),
                0f
            )
            yAxis.setDrawLabels(false)
            yAxis.setDrawAxisLine(false)
        }

        updatePadding(
            16.dpToPx,
            16.dpToPx,
            16.dpToPx,
            16.dpToPx,
        )
    }

    fun setData(last14DaysScores: List<DailyScore>) = with(binding) {

        val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.add(Calendar.DAY_OF_MONTH, -13)

        val score = last14DaysScores.associate { it.calcDate to it.score }

        val dataEntries: MutableList<Entry> = mutableListOf()
        for (index in 0..13) {
            val date = format.format(calendar.time)
            dataEntries.add(
                Entry(index.toFloat(), score[date]?.toFloat() ?: 0f)
            )
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }

        val dataSet = LineDataSet(dataEntries, "Safety Score")
        dataSet.color = ContextCompat.getColor(context, R.color.design_light_green)
        dataSet.setDrawCircles(true)
        dataSet.circleRadius = 3f
        dataSet.setCircleColor(ContextCompat.getColor(context, R.color.design_dark_green))
        dataSet.lineWidth = 2.5f

        dataSet.setDrawCircleHole(false)
        dataSet.circleHoleRadius = 0f

        dataSet.setDrawFilled(true)
        dataSet.fillColor = ContextCompat.getColor(context, R.color.design_green_10)
        dataSet.fillAlpha = 25
        dataSet.setDrawValues(false)
        dataSet.mode = LineDataSet.Mode.CUBIC_BEZIER
        dataSet.cubicIntensity = 0.1f
        dataSet.setDrawVerticalHighlightIndicator(false)
        dataSet.setDrawHorizontalHighlightIndicator(false)

        val data = LineData(dataSet)
        lineChart.data = data
        lineChart.description.isEnabled = false
        lineChart.invalidate()
    }
}
