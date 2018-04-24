package com.gabfiocchi.todo_kotlin_app

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_item.*

class ItemActivity : AppCompatActivity() {

    lateinit var mDatabase: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item)

        title = "Agregar recordatorio"

        mDatabase = FirebaseDatabase.getInstance().reference

        saveButton.setOnClickListener {
            add()
        }
    }

    fun add() {
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
        finish()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
