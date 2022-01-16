package com.example.dataarduino

import android.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast

class Activity_Prompt : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_prompt)

        val builder = AlertDialog.Builder(this@Activity_Prompt)
        val inflater = layoutInflater
        builder.setTitle("Network Error")
        val dialogLayout = inflater.inflate(R.layout.activity_prompt, null)
        val editText = dialogLayout.findViewById<EditText>(R.id.editText)
        builder.setView(dialogLayout)
        builder.setPositiveButton("OK") { dialogInterface, i ->
            Toast.makeText(
                applicationContext,
                "EditText is " + editText.text.toString(),
                Toast.LENGTH_SHORT
            ).show()
        }
        builder.show()
    }



}