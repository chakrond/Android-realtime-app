package com.example.dataarduino.plot.addPlot

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
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

        // CheckBox
        val humidityCheckbox1 = view.findViewById<CheckBox>(R.id.checkBox_humidity)
        val temperatureCheckbox1 = view.findViewById<CheckBox>(R.id.checkBox_temperature)

        // Switch
        val addModeSwitch = view.findViewById<Switch>(R.id.switch_addmode)

        // Button
        view.findViewById<Button>(R.id.button_submitSingle).setOnClickListener{
            val dateQuery = editTextSingleDate.text.toString()
            addPlotViewModel.setDateQuery(dateQuery)
            addPlotViewModel.getSingleQueryData("date")
            addPlotViewModel.setChartName(dateQuery)
            addPlotViewModel.checkSubmitStat()

            addPlotViewModel.submitState.observe(viewLifecycleOwner, Observer<SubmitState> { state ->
                when (state) {
                    is SubmitSuccess -> {
                        parentFragmentManager.beginTransaction()
                            .replace(R.id.fragment_host, PlotFragment())
                            .addToBackStack(PlotFragment::class.java.simpleName)
                            .setReorderingAllowed(true)
                            .commit()

                        onAddModeChecked(addModeSwitch)
                        onCheckboxClicked(humidityCheckbox1)
                        onCheckboxClicked(temperatureCheckbox1)
                        addPlotViewModel.formatData()
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

        // Check Switch State
        addPlotViewModel.addModeSwitch.observe(viewLifecycleOwner, Observer<SwitchState> {state ->
            when (state) {
                is AddModeChecked -> {
                    addModeSwitch.isChecked = true
                }
                is AddModeUnchecked -> {
                    addModeSwitch.isChecked = false
                }
            }
        })


    }

    fun onCheckboxClicked(view: View) {
        if (view is CheckBox) {
            val checked: Boolean = view.isChecked

            when (view.id) {
                R.id.checkBox_humidity -> {
                    if (checked) {
                        addPlotViewModel.humidityChecked()
//                        Log.d("onCheckboxClicked", "humidity: Checked")
                    }
                }
                R.id.checkBox_temperature -> {
                    if (checked) {
                        addPlotViewModel.temperatureChecked()
//                        Log.d("onCheckboxClicked", "temperature: Checked")
                    }
                }

            }
        }
    }

    fun onAddModeChecked(view: View) {

        if (view is Switch) {
            val checked: Boolean = view.isChecked

            when (view.id) {
                R.id.switch_addmode -> {
                    if (checked) {
                        addPlotViewModel.onAddMode()
//                        Log.d("onAddModeChecked", "Switch_Addmode: Checked")
                    } else {
                        addPlotViewModel.offAddMode()
//                        Log.d("onAddModeChecked", "Switch_Addmode: Unchecked")
                    }
                }
            }
        }

//        val toggle: ToggleButton = findViewById(R.id.togglebutton)
//        toggle.setOnCheckedChangeListener { _, isChecked ->
//            if (isChecked) {
//                // The toggle is enabled
//            } else {
//                // The toggle is disabled
//            }
//        }

    }



}


sealed class SubmitState
object SubmitSuccess : SubmitState()
//object SubmitError : SubmitState()
object SubmitNull: SubmitState()

sealed class CheckboxState
object HumidityChecked : CheckboxState()
object TemperatureChecked : CheckboxState()
object Unchecked: CheckboxState()

sealed class SwitchState
object AddModeChecked : SwitchState()
object AddModeUnchecked: SwitchState()

