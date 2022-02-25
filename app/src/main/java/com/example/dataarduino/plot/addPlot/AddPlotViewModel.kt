package com.example.dataarduino.plot.addPlot


import android.graphics.Color
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dataarduino.data.DataManager
import com.example.dataarduino.di.ActivityScope
import com.example.dataarduino.utils.getData
import com.example.dataarduino.utils.getData.calendar
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.features.json.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

private const val TAG = "AddPlotViewModel"
private const val SINGLE_QUERY = "single_q"
private const val RANGE_DATE_A = "dateA"
private const val RANGE_DATE_B = "dateB"

@ActivityScope
class AddPlotViewModel @Inject constructor(private val dataManager: DataManager): ViewModel()  {

    // data List
    val hEntry = ArrayList<Entry>()
    val tEntry = ArrayList<Entry>()

    // Button Status
    private val _submitState = MutableLiveData<SubmitState>()
    val submitState: LiveData<SubmitState>
        get() = _submitState

    fun checkSubmitStat() {
//        if (data!=null) {
//            _submitState.value = SubmitSuccess
//        } else {
//            _submitState.value = SubmitError
//        }
        _submitState.value = SubmitSuccess
    }

    // CheckBox Status
    private val _humidityCheckBoxState = MutableLiveData<CheckboxState>()
    val humidityCheckBoxState: LiveData<CheckboxState>
        get() = _humidityCheckBoxState

    private val _temperatureCheckBoxState = MutableLiveData<CheckboxState>()
    val temperatureCheckBoxState: LiveData<CheckboxState>
        get() = _temperatureCheckBoxState

    fun humidityChecked() {
        _humidityCheckBoxState.value = HumidityChecked
    }

    fun temperatureChecked() {
        _temperatureCheckBoxState.value = TemperatureChecked
    }

    // Chart Name
    private val _chartName = MutableLiveData<String>()
    val chartName: LiveData<String>
    get() = _chartName

    fun setChartName(name: String) {
        _chartName.value = name
    }

    // AddMode Switch
    private val _addModeSwitch = MutableLiveData<SwitchState>()
    val addModeSwitch: LiveData<SwitchState>
    get() = _addModeSwitch

    fun onAddMode() {
        _addModeSwitch.value = AddModeChecked
//        Log.d("AddMode", "AddModeChecked")
    }

    fun offAddMode() {
        _addModeSwitch.value = AddModeUnchecked
//        Log.d("AddMode", "AddModeUnchecked")
    }

    // Connect to Database
    private val getUrl = dataManager.hostServerUrl
    private val url = "$getUrl/data/date"
    private val token = dataManager.getStorageData("user_token")

    // X axis label
    val xAxisLabel = ArrayList<String>()

    // JsonArray
    var recTimeArray: JsonArray = JsonArray()
    var humidityArray: JsonArray = JsonArray()
    var temperatureArray: JsonArray = JsonArray()

    // Entry List
    var recTimeArrayList = ArrayList<String>()
    var humidArrayList = ArrayList<Float>()
    var tempArrayList = ArrayList<Float>()

    fun setDateQuery(value: String) {
        dataManager.setStorageData(SINGLE_QUERY ,value)
    }

    private val singleQuery: String
        get() = dataManager.getStorageData(SINGLE_QUERY)

    fun setRangeDateQuery(dateA: String, dateB: String) {
        dataManager.setStorageData(RANGE_DATE_A ,dateA)
        dataManager.setStorageData(RANGE_DATE_B ,dateB)
    }

    val rangeDateA: String
        get() = dataManager.getStorageData(RANGE_DATE_A)

    val rangeDateB: String
        get() = dataManager.getStorageData(RANGE_DATE_B)

    fun getSingleQueryData (type: String) {
        // Type: date, month ,year

        // Clear Array

        runBlocking {

            // Get record data
            val client3 = HttpClient(CIO) {
                install(JsonFeature) {
                    serializer = GsonSerializer() {
                        setPrettyPrinting()
                        disableHtmlEscaping()
                    }
                }
            }

            var queryRoute: String = type

            if (queryRoute == "date") {
                queryRoute = "by"
            }

            val getDataByDate: HttpResponse = client3.get(url) {
                headers {
                    append(HttpHeaders.Authorization, "Bearer $token")
                    append(HttpHeaders.UserAgent, "kotlin/ktor")
                    append(HttpHeaders.Accept, "*/*")
                    append(HttpHeaders.Connection, "close")
                }
                parameter(queryRoute, singleQuery)
            }
            client3.close()
            val response3 = getDataByDate.readText()
            val jsonObject: JsonObject = Gson().fromJson(response3, JsonObject::class.java)
            recTimeArray = jsonObject.getAsJsonArray("recTime")
            humidityArray = jsonObject.getAsJsonArray("Humidity")
            temperatureArray = jsonObject.getAsJsonArray("Temperature")
//            Log.d(TAG, "TemperatureArray: $temperatureArray")
        }

    }

    fun formatData() {

        // add value to Entry
        recTimeArrayList = Gson().fromJson(recTimeArray, ArrayList<String>()::class.java)
        humidArrayList = Gson().fromJson(humidityArray, ArrayList<Float>()::class.java)
        tempArrayList = Gson().fromJson(temperatureArray, ArrayList<Float>()::class.java)

        hEntry.clear()
        tEntry.clear()

        if (_addModeSwitch.value == AddModeUnchecked) {

            var y = 0F
            for ((i, a) in humidArrayList.withIndex()) {

                if (_humidityCheckBoxState.value == HumidityChecked) {
                    hEntry.add(Entry(y, humidArrayList[i]))
                }

                if (_temperatureCheckBoxState.value == TemperatureChecked) {
                    tEntry.add(Entry(y, tempArrayList[i]))
                }

                // Format time
                // Date Formatter
                val originalPattern = "yyyy-mm-dd'T'HH:mm:ss"
                val formatter1 = SimpleDateFormat(originalPattern)
                val RTrecTime = formatter1.parse(recTimeArrayList[i])
                xAxisLabel.add(SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(RTrecTime))
                y++
            }
            //        Log.d(TAG, "hEntry: $hEntry")
            //        Log.d(TAG, "tEntry: $tEntry")
            //        Log.d(TAG, "xAxisLabel: $xAxisLabel")
        }

        if (_addModeSwitch.value == AddModeChecked) {

            // Format time
            // Date Formatter
            val originalPattern = "yyyy-mm-dd'T'HH:mm:ss"
            val formatter1 = SimpleDateFormat(originalPattern)
            val reqRecTime: Date? = formatter1.parse(_chartName.value + "T00:00:00")
            val millReqRecTime: Long? = reqRecTime?.time

            for ((i, a) in humidArrayList.withIndex()) {

                val RTrecTime = formatter1.parse(recTimeArrayList[i])
                val mills: Long = RTrecTime.time - millReqRecTime!!


                if (_humidityCheckBoxState.value == HumidityChecked) {
                    hEntry.add(Entry(mills.toFloat(), humidArrayList[i]))
                }

                if (_temperatureCheckBoxState.value == TemperatureChecked) {
                    tEntry.add(Entry(mills.toFloat(), tempArrayList[i]))
                }
            }
        }

    }

    // Line Data
    var data = LineData()

    // Counter
    var iH = 0
    var iT = 0

    // Array Dataset
    val arraySetH = ArrayList<LineDataSet>()
    val arraySetT = ArrayList<LineDataSet>()

    // data Set
    var dataSetsAddMode = ArrayList<ILineDataSet>()

    fun createLineData() {

        if (_addModeSwitch.value == AddModeUnchecked) {

            Log.d("createLineData", "AddModeUnchecked")

            // data Set
            val dataSets = ArrayList<ILineDataSet>()

            // Humidity
            if (_humidityCheckBoxState.value == HumidityChecked) {
                val set1 = LineDataSet(hEntry, "Humidity")
                set1.fillAlpha = 110
//            Log.d(TAG, "set1: $set1")
                dataSets.add(set1)
            }

            // Temperature
            if (_temperatureCheckBoxState.value == TemperatureChecked) {
                val set2 = LineDataSet(tEntry, "Temperature")
                set2.fillAlpha = 110
                // black lines and points
                set2.color = Color.RED
                set2.setCircleColor(Color.RED)
//                Log.d(TAG, "set2: $set2")
                dataSets.add(set2)
            }

            data = LineData(dataSets)
        }

        if (_addModeSwitch.value == AddModeChecked) {

            if (_humidityCheckBoxState.value == HumidityChecked) {
                val setA = LineDataSet(hEntry, "Humidity")

                setA.fillAlpha = 110

                when (iH) {
                    0 -> {
                        setA.color = Color.RED
                        setA.setCircleColor(Color.RED)
                    }
                    1 -> {
                        setA.color = Color.BLUE
                        setA.setCircleColor(Color.BLUE)
                    }
                    2 -> {
                        setA.color = Color.GREEN
                        setA.setCircleColor(Color.GREEN)
                    }
                }

                arraySetH.add(setA)
                dataSetsAddMode.add(arraySetH[iH])
                iH++
            }

            if (_temperatureCheckBoxState.value == TemperatureChecked) {
                val setB = LineDataSet(tEntry, "Temperature")
//                Log.d("TestMode", "tEntry: $tEntry")
//                Log.d("TestMode", "setB: $setB")

                setB.fillAlpha = 110
                setB.setDrawCircles(false)

                when (iT) {
                    0 -> {
                        setB.color = Color.RED
//                        setB.setCircleColor(Color.RED)
                    }
                    1 -> {
                        setB.color = Color.BLUE
//                        setB.setCircleColor(Color.BLUE)
                    }
                    2 -> {
                        setB.color = Color.GREEN
//                        setB.setCircleColor(Color.GREEN)
                    }
                }

                dataSetsAddMode.add(setB) // **something wrong with this line

                for ((i,a) in dataSetsAddMode.withIndex()) {
                    Log.d("dataSetsAddMode", "dataSetsAddMode[$i]: $a}")
                }

                iT++
            }

            data = LineData(dataSetsAddMode)
        }

        resetCheckBoxStat()
    }

    fun resetCheckBoxStat() {

        // Reset status
        _humidityCheckBoxState.value = Unchecked
        _temperatureCheckBoxState.value = Unchecked

    }




}