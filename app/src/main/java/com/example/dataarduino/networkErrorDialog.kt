package com.example.dataarduino

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.DialogFragment

class networkErrorDialog: DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var rootView: View = inflater.inflate(R.layout.activity_prompt, container, false)

        val editText = rootView.findViewById<EditText>(R.id.editText)

        // SharedPreferences
        val btnSave = rootView.findViewById<Button>(R.id.savebutton)
        val sharedPreference = activity!!.getPreferences(Context.MODE_PRIVATE)
        btnSave.setOnClickListener{
            val editor: SharedPreferences.Editor =  sharedPreference.edit()
            val userURL:String = editText.text.toString()
            editor.putString("user_url",userURL)
            editor.apply()
//            editor.commit()
        }

        val btnView = rootView.findViewById<Button>(R.id.viewbtn)
        btnView.setOnClickListener {
            val sharedNameValue = sharedPreference.getString("user_url","")
//            if(sharedIdValue.equals(0) && sharedNameValue.equals("defaultname")){
//                outputName.setText("default name: ${sharedNameValue}").toString()
//                outputId.setText("default id: ${sharedIdValue.toString()}")
//            }else{
            editText.setText(sharedNameValue).toString()
        }

        // re-Launch onCreate
        val btnLaunch = rootView.findViewById<Button>(R.id.launbtn)
        btnLaunch.setOnClickListener {
            activity!!.supportFragmentManager.beginTransaction().detach(this).attach(this).commit()
        }


        return rootView
    }
}