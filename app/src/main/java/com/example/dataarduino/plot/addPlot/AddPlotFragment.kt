package com.example.dataarduino.plot.addPlot

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.example.dataarduino.R
import com.example.dataarduino.main.MainActivity
import com.example.dataarduino.plot.PlotFragment
import com.example.dataarduino.plot.PlotViewModel
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import java.util.ArrayList
import javax.inject.Inject

private const val TAG = "addPlotFragment"

class AddPlotFragment : Fragment() {

//     @Inject annotated fields will be provided by Dagger
        @Inject
        lateinit var addPlotViewModel: AddPlotViewModel

        @Inject
        lateinit var plotViewModel: PlotViewModel


    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.d(TAG, "onAttach is called")
        // Grabs the registrationComponent from the Activity and injects this Fragment
        (activity as MainActivity).mainComponent.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d(TAG, "onCreateView is called")

        // View
        val view = inflater.inflate(R.layout.fragment_add_plot, container, false)
        setupViews(view)


        // Inflate the layout for this fragment
        return view
    }

    private fun setupViews(view: View) {

        // EditText
        val editTextSingleDate = view.findViewById<EditText>(R.id.editText_singleDate)
        val editTextDateStart = view.findViewById<EditText>(R.id.editTextDate_dateStart)
        val editTextDateEnd = view.findViewById<EditText>(R.id.editTextDate_dateEnd)

        // Button
        view.findViewById<Button>(R.id.button_submitSingle).setOnClickListener{
            val dateQuery = editTextSingleDate.text.toString()
            addPlotViewModel.setDateQuery(dateQuery)
            addPlotViewModel.getSingleQueryData("date")

            addPlotViewModel.submitState.observe(viewLifecycleOwner, Observer<SubmitState> { state ->
                when (state) {
                    is SubmitSuccess -> {
                        parentFragmentManager.beginTransaction()
                            .replace(R.id.fragment_host, PlotFragment())
                            .addToBackStack(PlotFragment::class.java.simpleName)
                            .setReorderingAllowed(true)
                            .commit()

                        addPlotViewModel.createLineData()
                    }
//                    is SubmitError -> errorTextView.visibility = View.VISIBLE
                }
            })
        }

        // Button
        view.findViewById<Button>(R.id.button_submitRangQ).setOnClickListener{
            val dateStartQuery = editTextDateStart.text.toString()
            val dateEndQuery = editTextDateEnd.text.toString()
            addPlotViewModel.setRangeDateQuery(dateStartQuery, dateEndQuery)
        }

    }

}

sealed class SubmitState
object SubmitSuccess : SubmitState()
//object SubmitError : SubmitState()