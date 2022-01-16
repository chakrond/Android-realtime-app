package com.example.dataarduino

import android.annotation.SuppressLint
import android.util.Log
import kotlinx.coroutines.*
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.io.IOException
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

object Data {

    var tH: Float = 0F
    var tT: Float = 0F
    val calendar = Calendar.getInstance()
    var tD: Date = calendar.time
    var mills :Long = 0
    var xAxisLabel: ArrayList<String> = ArrayList()

    suspend fun FectData(url: String) {

        val doc: Document = Jsoup.connect(url).userAgent("Chrome").get()
        var b: String
//    var tempdate: Date
        val tempSensorData = ArrayList<ArrayList<String>>()
//        val tH: Float

//        try {
//            doc = Jsoup.connect(url).get()
//        } catch (e: IOException) {
//            e.printStackTrace()
//        }


        val paragraphs = doc.getElementsByTag("p")
        for (a in paragraphs) {
            b = a.text()
            val row = ArrayList(Arrays.asList(*b.split(",").toTypedArray()))
            tempSensorData.add(row)
        }

        val originalPattern = "yyyy-mm-dd'T'HH:mm:ss"
        val formatter1 = SimpleDateFormat(originalPattern)
        tD = formatter1.parse(tempSensorData[1][0])
        mills = tD.time
        Log.d("FectData", "Date: $tD")
        Log.d("FectData", "mills: $mills")

        // X axis label
        xAxisLabel.add(SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(tD))
        Log.d("FectData", "xAxisLabel: $xAxisLabel")


//    try {
//        tD = formatter1.parse(tempSensorData[1][0])
//    } catch (e: ParseException) {
//        e.printStackTrace();
//    }

        tH = tempSensorData[1][1].toFloat()
        tT = tempSensorData[1][2].toFloat()
//        delay(1000)
//    Log.d(" tempH", "" + temph)

    }
}
