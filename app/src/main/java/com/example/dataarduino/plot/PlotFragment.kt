package com.example.dataarduino.plot

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.lifecycle.Observer
import com.example.dataarduino.R
import com.example.dataarduino.main.MainActivity
import com.example.dataarduino.plot.addPlot.AddPlotFragment
import com.example.dataarduino.plot.addPlot.AddPlotViewModel
import com.example.dataarduino.plot.addPlot.SubmitState
import com.example.dataarduino.plot.addPlot.SubmitSuccess
import com.example.dataarduino.utils.getData
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import javax.inject.Inject

private const val TAG = "PlotFragment"

class PlotFragment : Fragment() {

    // Line Chart
    lateinit var chart: LineChart

    @Inject
    lateinit var addPlotViewModel: AddPlotViewModel

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.d(TAG, "onAttach is called")
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

        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_plot, container, false)

        setupViews(view)
        return view
    }

    private fun setupViews(view: View) {

        // Button
        view.findViewById<Button>(R.id.button_addPlot).setOnClickListener{
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_host, AddPlotFragment())
                .addToBackStack(AddPlotFragment::class.java.simpleName)
                .setReorderingAllowed(true)
                .commit()
        }

        addPlotViewModel.submitState.observe(viewLifecycleOwner, Observer<SubmitState> { state ->
            when (state) {
                is SubmitSuccess -> {
                    // Chart
                    chart = view.findViewById(R.id.plot) as LineChart

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
                    chart.xAxis.labelCount = 6

                    // limit the number of visible entries
//                    chart.setVisibleXRangeMaximum(1000F)
                }
            }
        })

    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume is called")

        addPlotViewModel.submitState.observe(viewLifecycleOwner, Observer<SubmitState> { state ->
            when (state) {
                is SubmitSuccess -> {
                    onPlotSubmitted()
                }
            }
        })
    }

    private fun onPlotSubmitted() {
        Log.d(TAG, "onPlotSubmitted is called")

        // adding data
        chart.data = addPlotViewModel.data

        // Axis Formatter
        chart.xAxis.valueFormatter = IndexAxisValueFormatter(addPlotViewModel.xAxisLabel)

        chart.notifyDataSetChanged()
        chart.invalidate()
    }


}

