package com.example.dataarduino.ref

import com.example.dataarduino.Data
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.formatter.ValueFormatter

class XAxisTimeFormatter: ValueFormatter() {
    override fun getAxisLabel(value: Float, axis: AxisBase?): String {

        return Data.xAxisLabel[0]
//        return SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date( (value.toLong()) * (10E3).toLong() ) )
//        return (value*10F).toString()
//        var gg:Long = Data.mills
//        return (gg/1000000L).toString()

    }
}