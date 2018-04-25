package com.gabfiocchi.todo_kotlin_app

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MenuItem
import android.widget.Toast
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_item.*
import android.view.inputmethod.InputMethodManager


class ItemActivity : AppCompatActivity() {

    lateinit var mDatabase: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item)

        title = "Agregar recordatorio"

        saveButton.isEnabled = false
        showKeyboard()

        mDatabase = FirebaseDatabase.getInstance().reference


        muliTextView.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                saveButton.isEnabled = muliTextView.text.toString().trim().isNotEmpty()
            }
        })
        saveButton.setOnClickListener {
            add()
        }
    }

    private fun add() {
        val todoItem = ToDoItem.create()
        todoItem.itemText = muliTextView.text.toString()
        todoItem.done = false
        //We first make a push so that a new item is made with a unique ID
        val newItem = mDatabase.child(MainActivity.Constants.FIREBASE_ITEM).push()
        todoItem.objectId = newItem.key
        //then, we used the reference to set the value on that ID
        newItem.setValue(todoItem)

        Toast.makeText(this, "Se creó correctamente el recordatorio.", Toast.LENGTH_SHORT).show()

        val data = Intent()
        data.putExtra("status","save")
        setResult(android.app.Activity.RESULT_OK, data)
        hideKeyboard()
        finish()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                hideKeyboard()
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun hideKeyboard(){
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(muliTextView.windowToken, 0)
    }
    private fun showKeyboard(){
        muliTextView.requestFocus()
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)
    }
}
