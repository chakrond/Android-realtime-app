package com.example.dataarduino.livePlot

import android.graphics.Color
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.dagger.storage.Storage
import com.example.dataarduino.SECRET
import com.example.dataarduino.data.DataManager
import com.example.dataarduino.utils.getData
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.google.gson.Gson
import com.google.gson.JsonObject
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.features.json.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.*
import java.util.ArrayList
import javax.inject.Inject

private const val TAG = "LivePlotViewModel"

class LivePlotViewModel @Inject constructor(private val dataManager: DataManager): ViewModel()  {

    // Coroutine
//    val job = Job()
//    val coroutineScope = CoroutineScope(job + Dispatchers.Main)
    var isTimeout: Boolean = false

    // Line Data
    var data = LineData()

    // data List
    val HValue = ArrayList<Entry>()
    val TValue = ArrayList<Entry>()

//    val HValue = mutableListOf<Entry>()
//    val HValueEntries: MutableList<Entry>
//        get() = HValue
//    val TValue = mutableListOf<Entry>()
//    val TValueEntries: MutableList<Entry>
//        get() = TValue

    // Connect to Database
    val hostserver = SECRET.HOST_SERVER_ADDRESS

//    val saveUrl = sharedPreference.saveString("user_url", "")
//    val useURL = sharedPreference.getString("user_url", "$hostserver/data/real")
//    val url: String = useURL.toString()
    val setUrl = dataManager.setHostServerUrl(hostserver)
    val getUrl = dataManager.hostServerUrl
    val url = "$getUrl/data/real"

    init {
        viewModelScope.launch(Dispatchers.IO) {
                Log.d(TAG, "coroutineScope is called")
                var i = 0
                var d = 0F

                // Get token
                val client1 = HttpClient(CIO) {
                    install(JsonFeature) {
                        serializer = GsonSerializer() {
                            setPrettyPrinting()
                            disableHtmlEscaping()
                        }
                    }
                }

                data class User(val email: String, val password: String)
                val getToken: HttpResponse = client1.post(SECRET.HOST_SERVER_ADDRESS_LOGIN) {
                    headers {
                        append(HttpHeaders.UserAgent, "kotlin/ktor")
                    }
                    contentType(ContentType.Application.Json)
                    body = User(SECRET.USER, SECRET.PASSWORD)
                }
                client1.close()

                val jsongetToken = Gson().fromJson(getToken.readText(), JsonObject::class.java)
                val stringtoken = jsongetToken.get("token").toString()
                val token = stringtoken.substring(1, stringtoken.length - 1)
                dataManager.setStorageData("user_token",token)
                Log.d(TAG, "[coroutineScope] token: $token")


                while (true) {

                    try {
                        getData.FetchDatabase(url, token)
                    } catch (e: Throwable ) {
                        Log.d(TAG, "[coroutineScope] Cannot Connect Webserver")
                        isTimeout = true
                        break
                    }


                    // Date
//                    var DD: Long = getData.mills
//                    var DDsec: Float = (DD / (10E3).toLong()).toFloat()
//                    Log.d(TAG, "[coroutineScope] mills: $DD")
//                    Log.d(TAG, "[coroutineScope] X Data: Second $DDsec")

                    // data Set
                    val dataSets = ArrayList<ILineDataSet>()



                    // Humidity
                    val HH: Float = getData.RThumidity
                    Log.d(TAG, "[coroutineScope] Y Data: Humidity $HH")
                    HValue.add(Entry(d, HH))
                    val set1 = LineDataSet(HValue, "Humidity")
                    set1.fillAlpha = 110

                    // Temperature
                    val TT: Float = getData.RTtemperature
                    Log.d(TAG, "[coroutineScope] Y Data: Temperature $TT")
                    TValue.add(Entry(d, TT))
                    val set2 = LineDataSet(TValue, "Temperature")
                    set2.fillAlpha = 110
                    // black lines and points
                    set2.color = Color.RED;
                    set2.setCircleColor(Color.RED);

                    // data Set
                    dataSets.add(set1)
                    dataSets.add(set2)
                    data = LineData(dataSets)

                    // increment
                    i++
                    d++

                    delay(5000)
                }

        }
    }


}