package com.example.dataarduino

import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import java.text.SimpleDateFormat
import java.util.*

class LineChartXAxisValueFormatter : IndexAxisValueFormatter() {
    override fun getFormattedValue(value: Float): String? {
        return SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date( (value*1000F).toLong()) )
    }
}