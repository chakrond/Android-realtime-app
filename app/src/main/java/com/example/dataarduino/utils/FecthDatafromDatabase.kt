package com.example.dataarduino.utils

import android.util.Log
import com.google.gson.Gson
import com.google.gson.JsonObject
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.features.json.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import java.text.SimpleDateFormat
import java.util.*

object getData {

    val calendar = Calendar.getInstance()
    var RTrecTime: Date = calendar.time
    var RThumidity: Float = 0.0f
    var RTtemperature: Float = 0.0f
    var mills :Long = 0
    var xAxisLabel: ArrayList<String> = ArrayList()

    suspend fun FetchDatabase(url: String, token: String) {

//        Log.d("FetchDatabase", "url: $url")
//        Log.d("FetchDatabase", "token: $token")


        // Get real time data
        val client2 = HttpClient(CIO) {
            install(JsonFeature) {
                serializer = GsonSerializer() {
                    setPrettyPrinting()
                    disableHtmlEscaping()
                }
            }
        }

        val getData: HttpResponse = client2.get(url) {
            headers {
                append(HttpHeaders.Authorization, "Bearer $token")
                append(HttpHeaders.UserAgent, "kotlin/ktor")
                append(HttpHeaders.Accept, "*/*")
                append(HttpHeaders.Connection, "close")
            }
        }
        client2.close()

        val jsongetData = Gson().fromJson(getData.readText(), JsonObject::class.java)
        val recTimeString = jsongetData.get("recTime").toString()
        val recTime = recTimeString.substring(1, recTimeString.length - 1)
        RThumidity = jsongetData.get("Humidity").toString().toFloat()
        RTtemperature = jsongetData.get("Temperature").toString().toFloat()


        // Date Formatter
        val originalPattern = "yyyy-mm-dd'T'HH:mm:ss"
        val formatter1 = SimpleDateFormat(originalPattern)
        RTrecTime = formatter1.parse(recTime)
        mills = RTrecTime.time

        // X axis label
        xAxisLabel.add(SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(RTrecTime))

        Log.d("FetchDatabase", "Date: $RTrecTime")
//        Log.d("FetchDatabase", "mills: $mills")
        Log.d("FetchDatabase", "xAxisLabel: $xAxisLabel")

    }
}