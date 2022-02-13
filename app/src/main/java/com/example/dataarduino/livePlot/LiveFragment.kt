package com.example.dataarduino.livePlot

import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.lifecycle.*
import com.example.dataarduino.MyApplication
import com.example.dataarduino.R
import com.example.dataarduino.main.MainActivity
import com.example.dataarduino.utils.getData
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import kotlinx.coroutines.*
import javax.inject.Inject

private const val TAG = "LiveFragment"

class LiveFragment : Fragment() {

    // Line Chart
    lateinit var chart: LineChart
    var accessN = 0

//    // Coroutine Scope - OnResume
//    val job1 = Job()
//    val RefreshCoroutineScope = CoroutineScope(job1 + Dispatchers.Main)


    // @Inject annotated fields will be provided by Dagger
    @Inject
    lateinit var livePlotViewModel: LivePlotViewModel

    override fun onAttach(context: Context) {
        super.onAttach(context)

        // Grabs the registrationComponent from the Activity and injects this Fragment
        (activity as MainActivity).mainComponent.inject(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate is called")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d(TAG, "onCreateView is called")

        // View
        val viewLayout = inflater.inflate(R.layout.fragment_live, container, false)
        chart = viewLayout.findViewById(R.id.samplechart) as LineChart

        // ViewModel
//        val livePlotViewModel = ViewModelProvider(requireActivity()).get(LivePlotViewModel::class.java)

        // enable scaling and dragging
        chart.setScaleEnabled(false)
        chart.isDragEnabled = true
        chart.setPinchZoom(true)

        // no description text
        chart.description.isEnabled = false

        // enable touch gestures
        chart.setTouchEnabled(true);
        chart.dragDecelerationFrictionCoef = 0.9f

        // axis Setting
        chart.xAxis.position = XAxis.XAxisPosition.BOTTOM
        chart.axisRight.isEnabled = false

        // set number of label
        chart.xAxis.labelCount = 4

        // Create a new coroutine in the lifecycleScope
        viewLifecycleOwner.lifecycleScope.launch {
            if (accessN == 0) {  //  First time get data
                delay(5000)
            }

            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                accessN = 1 // get rid of delay for the first time access
                Log.d(TAG, "[onCreateView] repeatOnLifecycle is called")
                while (true) {
                    //==========================================================================================

                    // adding data
                    chart.data = livePlotViewModel.data

                    // move to the latest entry
                    chart.moveViewToX(livePlotViewModel.data.entryCount.toFloat())

                    // Axis Formatter
                    chart.xAxis.valueFormatter = IndexAxisValueFormatter(getData.xAxisLabel)

                    // let the chart know it's data has changed
                    chart.notifyDataSetChanged()
                    chart.invalidate()

                    // limit the number of visible entries
                    chart.setVisibleXRangeMaximum(5F)
                    // chart.setVisibleYRange(30, AxisDependency.LEFT)
                    delay(1000)
                    //==========================================================================================
//                viewModel.someDataFlow.collect {
//                    // Process item
//                }
                }
            }
        }

        // Inflate the layout for this fragment
        return viewLayout
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart is called")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume is called")

    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause is called")
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop is called")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d(TAG, "onDestroyView is called")

        // Cancell coroutine
//        job1.cancel()
    }

    override fun onDetach() {
        super.onDetach()
        Log.d(TAG, "onDetach is called")
    }

    override fun onDestroy(){
        super.onDestroy()
        Log.d(TAG, "onDestroy is called")
    }

    fun networkErrorPrompt() {
        Log.d(TAG, "networkErrorPrompt is called")
        Log.d(TAG, "[networkErrorPrompt] Job cancelled, connection timeout")

        val builder = AlertDialog.Builder(activity)
        val inflater = layoutInflater
        builder.setTitle("Network Error")
        val dialogLayout = inflater.inflate(R.layout.activity_prompt, null)
        val editText = dialogLayout.findViewById<EditText>(R.id.editText)
        builder.setView(dialogLayout)

        // SharedPreferences
        val btnSave = dialogLayout.findViewById<Button>(R.id.savebutton)
        val sharedPreference = requireActivity().getSharedPreferences("PREFERENCE_NAME", Context.MODE_PRIVATE)
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
            editText.setText(sharedNameValue).toString()
        }

        // re-Launch onCreate
        val btnLaunch = dialogLayout.findViewById<Button>(R.id.launbtn)
        btnLaunch.setOnClickListener {
            requireActivity().recreate()
        }

        builder.show()
    }
}