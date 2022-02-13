//package com.example.dataarduino
//
//import android.app.AlertDialog
//import android.view.View
//import android.widget.EditText
//import android.widget.Toast
//
//fun withEditText(view: View) {
//    val builder = AlertDialog.Builder(this)
//    val inflater = layoutInflater
//    builder.setTitle("With EditText")
//    val dialogLayout = inflater.inflate(R.layout.activity_prompt, null)
//    val editText  = dialogLayout.findViewById<EditText>(R.id.editText)
//    builder.setView(dialogLayout)
//    builder.setPositiveButton("OK") { dialogInterface, i -> Toast.makeText(applicationContext, "EditText is " + editText.text.toString(), Toast.LENGTH_SHORT).show() }
//    builder.show()
//}