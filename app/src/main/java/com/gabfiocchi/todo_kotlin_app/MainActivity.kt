package com.gabfiocchi.todo_kotlin_app

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.ListView
import android.widget.ProgressBar
import com.google.firebase.FirebaseApp
import com.google.firebase.database.*

import kotlinx.android.synthetic.main.activity_main.*
import com.google.firebase.database.FirebaseDatabase


class MainActivity : AppCompatActivity(), ItemRowListener {

    object Constants {
        @JvmStatic
        val FIREBASE_ITEM: String = "todo_item"
    }

    lateinit var mDatabase: DatabaseReference
    var toDoItemList: MutableList<ToDoItem>? = null
    lateinit var adapter: ToDoItemAdapter
    private var listViewItems: ListView? = null
    private var mProgressBar: ProgressBar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        title = "Recordatorios"

        fab.setOnClickListener { view ->
            //Show Activity here to add new Item
            val intent = Intent(this, ItemActivity::class.java)
            startActivityForResult(intent, 200)
        }

        listViewItems = findViewById<View>(R.id.items_list) as ListView
        mProgressBar = findViewById<View>(R.id.progress_bar) as ProgressBar

        toDoItemList = mutableListOf()
        adapter = ToDoItemAdapter(this, toDoItemList!!)

        getData()
    }

    private fun getData() {
        mProgressBar!!.visibility = View.VISIBLE

        mDatabase = FirebaseDatabase.getInstance().reference
        mDatabase.keepSynced(true)

        mDatabase.orderByKey().addListenerForSingleValueEvent(itemListener)
    }

    //update item
    override fun modifyItemState(itemObjectId: String, isDone: Boolean) {
        val itemReference = mDatabase.child(Constants.FIREBASE_ITEM).child(itemObjectId)
        itemReference.child("done").setValue(isDone)

        getData()
    }

    //delete an item
    override fun onItemDelete(itemObjectId: String) {

        val dialog = AlertDialog.Builder(this)
        dialog.setTitle("Confirmar acción")
        dialog.setMessage("¿Estás seguro que deseas borrar el item?")

        dialog.setPositiveButton("Borrar", DialogInterface.OnClickListener { dialog, whichButton ->
            mProgressBar!!.visibility = View.VISIBLE
            //get child reference in database via the ObjectID
            val itemReference = mDatabase.child(Constants.FIREBASE_ITEM).child(itemObjectId)
            //deletion can be done via removeValue() method
            itemReference.removeValue()

            getData()
        })

        dialog.setNegativeButton("Cancelar", DialogInterface.OnClickListener { dialog, whichButton ->
            //pass
        })
        dialog.show()
    }


    private var itemListener: ValueEventListener = object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            // Get Post object and use the values to update the UI
            mProgressBar!!.visibility = View.GONE
            toDoItemList?.clear()
            adapter.notifyDataSetChanged()
            listViewItems!!.adapter = adapter
            addDataToList(dataSnapshot)
        }

        override fun onCancelled(databaseError: DatabaseError) {
            // Getting Item failed, log a message
            Log.w("MainActivity", "loadItem:onCancelled", databaseError.toException())
        }
    }

    private fun addDataToList(dataSnapshot: DataSnapshot) {
        val items = dataSnapshot.children.iterator()
        //Check if current database contains any collection
        if (items.hasNext()) {
            val toDoListindex = items.next()
            val itemsIterator = toDoListindex.children.iterator()

            //check if the collection has any to do items or not
            while (itemsIterator.hasNext()) {
                //get current item
                val currentItem = itemsIterator.next()
                val todoItem = ToDoItem.create()
                //get current data in a map
                val map = currentItem.getValue() as HashMap<String, Any>
                //key will return Firebase ID
                todoItem.objectId = currentItem.key
                todoItem.done = map.get("done") as Boolean?
                todoItem.itemText = map.get("itemText") as String?
                toDoItemList!!.add(todoItem);
            }
        }
        //alert adapter that has changed
        adapter.notifyDataSetChanged()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        if (requestCode == 200) {
            if (resultCode == Activity.RESULT_OK) {
                getData()
            }
        }
    }
}
