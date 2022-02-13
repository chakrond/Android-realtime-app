package com.example.dataarduino.ref//package com.example.dataarduino
//
//import android.content.Context
//import android.content.SharedPreferences
//import android.graphics.Color
//import android.os.Bundle
//import android.util.Log
//import android.view.View
//import com.github.mikephil.charting.charts.LineChart
//import com.github.mikephil.charting.components.XAxis
//import com.github.mikephil.charting.data.Entry
//import com.github.mikephil.charting.data.LineData
//import com.github.mikephil.charting.data.LineDataSet
//import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
//import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.delay
//import kotlinx.coroutines.launch
//import java.util.ArrayList
//
//override fun onCreate(savedInstanceState: Bundle?) {
//    super.onCreate(savedInstanceState)
//    setContentView(R.layout.activity_main)
//
//    // create Chart
//    val chart = findViewById<View>(R.id.samplechart) as LineChart
//
//    // enable scaling and dragging
//    chart.setScaleEnabled(false)
//    chart.isDragEnabled = true
//    chart.setPinchZoom(true)
//
//    // no description text
//    chart.description.isEnabled = false
//
//    // enable touch gestures
//    chart.setTouchEnabled(true);
//    chart.dragDecelerationFrictionCoef = 0.9f;
//
//    // data List
//    val HValue = ArrayList<Entry>()
//    val TValue = ArrayList<Entry>()
//
//    // axis Setting
//    chart.xAxis.position = XAxis.XAxisPosition.BOTTOM
//    chart.axisRight.isEnabled = false
//
//    // set number of label
//    chart.xAxis.labelCount = 4
//
//
//
////        chart.setViewPortOffsets(150F, 50F, 50F, 150F);
////        chart.moveViewToX(5F)
////        chart.xAxis.setDrawLabels(true)   //to hide all xaxis values
////        chart.xAxis.setDrawGridLines(false)
////        chart.xAxis.setDrawAxisLine(true)
////        chart.xAxis.granularity = 1F
//
//    coroutineScope.launch(Dispatchers.IO) {
//
//        var i = 0
//        var d = 0F
//
//        while (i < 100) {
//
//            // Connect to Database
//            val hostserver = "dech-task-manager.herokuapp.com";
//            val sharedPreference: SharedPreferences =  getSharedPreferences("PREFERENCE_NAME",
//                Context.MODE_PRIVATE)
//            var useURL = sharedPreference.getString("user_url", "$hostserver/data/real")
//
////                val url: String = "http://192.168.1.6"
//            var url: String = useURL.toString()
//
//
//            try {
//                Data.FectData(url)
//            } catch (e: Throwable ) {
//                Log.d("coroutineScope", "Error: Cannot Connect Webserver")
//                isTimeout = true
//                break
//            }
//
//
//            // Date
//            var DD: Long = Data.mills
//            var DDsec: Float = (DD / (10E3).toLong()).toFloat()
//            Log.d("coroutineScope", "mills: $DD")
//            Log.d("coroutineScope", "X Data: Second $DDsec")
//
//            // Humidity
//            val HH: Float = Data.tH
//            Log.d("coroutineScope", "Y Data: Humidity $HH")
//            HValue.add(Entry(d, HH))
//            val set1 = LineDataSet(HValue, "Humidity")
//            set1.fillAlpha = 110
//
//            // Temperature
//            val TT: Float = Data.tT
//            Log.d("coroutineScope", "Y Data: Temperature $TT")
//            TValue.add(Entry(d, TT))
//            val set2 = LineDataSet(TValue, "Temperature")
//            set2.fillAlpha = 110
//            // black lines and points
//            set2.color = Color.RED;
//            set2.setCircleColor(Color.RED);
//
//            // data Set
//            val dataSets = ArrayList<ILineDataSet>()
//            dataSets.add(set1)
//            dataSets.add(set2)
//            val data = LineData(dataSets)
//
//            // adding data
//            chart.data = data
//
//            // increment
//            i++
//            d++
//
//            // let the chart know it's data has changed
//            chart.notifyDataSetChanged();
//            chart.invalidate();
//
//            // limit the number of visible entries
//            chart.setVisibleXRangeMaximum(5F)
//            // chart.setVisibleYRange(30, AxisDependency.LEFT);
//
//            // move to the latest entry
//            chart.moveViewToX(data.entryCount.toFloat())
//
//            // Axis Formatter
//            chart.xAxis.valueFormatter = IndexAxisValueFormatter(Data.xAxisLabel)
//
//            delay(1000L)
//        }
//
//        if (isTimeout) {
//            this@MainActivity.runOnUiThread{
//                NetworkErrorPrompt()
//
//
//            }
//        }
//    }
//}