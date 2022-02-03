package com.example.dataarduino

import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
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

class Live_Fragment : Fragment() {

    val job = Job()
    val coroutineScope = CoroutineScope(job + Dispatchers.Main)
    var isTimeout: Boolean = false
//    val sharedPreference: SharedPreferences =  getSharedPreferences("PREFERENCE_NAME",Context.MODE_PRIVATE)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val viewLayout = inflater.inflate(R.layout.fragment_live, container, false)
        val chart = viewLayout.findViewById(R.id.samplechart) as LineChart

        // enable scaling and dragging
        chart.setScaleEnabled(false)
        chart.isDragEnabled = true
        chart.setPinchZoom(true)

        // no description text
        chart.description.isEnabled = false

        // enable touch gestures
        chart.setTouchEnabled(true);
        chart.dragDecelerationFrictionCoef = 0.9f

        // data List
        val HValue = ArrayList<Entry>()
        val TValue = ArrayList<Entry>()

        // axis Setting
        chart.xAxis.position = XAxis.XAxisPosition.BOTTOM
        chart.axisRight.isEnabled = false

        // set number of label
        chart.xAxis.labelCount = 4



//        chart.setViewPortOffsets(150F, 50F, 50F, 150F);
//        chart.moveViewToX(5F)
//        chart.xAxis.setDrawLabels(true)   //to hide all xaxis values
//        chart.xAxis.setDrawGridLines(false)
//        chart.xAxis.setDrawAxisLine(true)
//        chart.xAxis.granularity = 1F

        coroutineScope.launch(Dispatchers.IO) {

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
            Log.d("coroutineScope", "token: $token")

            while (i < 100) {

                // Connect to Database
                val hostserver = SECRET.HOST_SERVER_ADDRESS
                val sharedPreference: SharedPreferences =  activity!!.getSharedPreferences("PREFERENCE_NAME", Context.MODE_PRIVATE)
                val useURL = sharedPreference.getString("user_url", "$hostserver/data/real")
                val url: String = useURL.toString()
                Log.d("coroutineScope", "url: $url")

                try {
                    getData.FetchDatabase(url, token)
                } catch (e: Throwable ) {
                    Log.d("coroutineScope", "Error: Cannot Connect Webserver")
                    isTimeout = true
                    break
                }


                // Date
                var DD: Long = getData.mills
                var DDsec: Float = (DD / (10E3).toLong()).toFloat()
                Log.d("coroutineScope", "mills: $DD")
                Log.d("coroutineScope", "X Data: Second $DDsec")

                // Humidity
                val HH: Float = getData.RThumidity
                Log.d("coroutineScope", "Y Data: Humidity $HH")
                HValue.add(Entry(d, HH))
                val set1 = LineDataSet(HValue, "Humidity")
                set1.fillAlpha = 110

                // Temperature
                val TT: Float = getData.RTtemperature
                Log.d("coroutineScope", "Y Data: Temperature $TT")
                TValue.add(Entry(d, TT))
                val set2 = LineDataSet(TValue, "Temperature")
                set2.fillAlpha = 110
                // black lines and points
                set2.color = Color.RED;
                set2.setCircleColor(Color.RED);

                // data Set
                val dataSets = ArrayList<ILineDataSet>()
                dataSets.add(set1)
                dataSets.add(set2)
                val data = LineData(dataSets)

                // adding data
                chart.data = data

                // increment
                i++
                d++

                // let the chart know it's data has changed
                chart.notifyDataSetChanged();
                chart.invalidate();

                // limit the number of visible entries
                chart.setVisibleXRangeMaximum(5F)
                // chart.setVisibleYRange(30, AxisDependency.LEFT);

                // move to the latest entry
                chart.moveViewToX(data.entryCount.toFloat())

                // Axis Formatter
                chart.xAxis.valueFormatter = IndexAxisValueFormatter(getData.xAxisLabel)

                delay(1000L)
            }

            if (isTimeout) {
                activity!!.runOnUiThread{
                    networkErrorPrompt()

                }

            }
        }

        // Inflate the layout for this fragment
        return viewLayout
    }

    override fun onDestroy(){
        job.cancel()
        super.onDestroy()

    }


    fun networkErrorPrompt() {
        Log.d("coroutineScope", "Job cancelled, connection timeout")

        val builder = AlertDialog.Builder(activity)
        val inflater = layoutInflater
        builder.setTitle("Network Error")
        val dialogLayout = inflater.inflate(R.layout.activity_prompt, null)
        val editText = dialogLayout.findViewById<EditText>(R.id.editText)
        builder.setView(dialogLayout)

        // SharedPreferences
        val btnSave = dialogLayout.findViewById<Button>(R.id.savebutton)
        val sharedPreference = activity!!.getSharedPreferences("PREFERENCE_NAME", Context.MODE_PRIVATE)
        btnSave.setOnClickListener{
            val editor: SharedPreferences.Editor =  sharedPreference.edit()
            val userURL:String = editText.text.toString()
            editor.putString("user_url",userURL)
            editor.apply()
            editor.commit()
        }

        val btnView = dialogLayout.findViewById<Button>(R.id.viewbtn)
        btnView.setOnClickListener {
            val sharedNameValue = sharedPreference.getString("user_url","")
//            if(sharedIdValue.equals(0) && sharedNameValue.equals("defaultname")){
//                outputName.setText("default name: ${sharedNameValue}").toString()
//                outputId.setText("default id: ${sharedIdValue.toString()}")
//            }else{
            editText.setText(sharedNameValue).toString()
        }

        // re-Launch onCreate
        val btnLaunch = dialogLayout.findViewById<Button>(R.id.launbtn)
        btnLaunch.setOnClickListener {
            activity!!.recreate()
        }

        builder.show()
    }

}