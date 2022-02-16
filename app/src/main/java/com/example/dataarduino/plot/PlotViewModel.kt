package com.example.dataarduino.plot

import android.graphics.Color
import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.dataarduino.data.DataManager
import com.example.dataarduino.plot.addPlot.AddPlotViewModel
import com.example.dataarduino.utils.getData.calendar
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

private const val TAG = "PlotViewModel"

class PlotViewModel @Inject constructor(
    private val dataManager: DataManager): ViewModel()  {

}

