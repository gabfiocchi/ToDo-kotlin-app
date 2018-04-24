package com.gabfiocchi.todo_kotlin_app

interface ItemRowListener {
    fun modifyItemState(itemObjectId: String, isDone: Boolean)
    fun onItemDelete(itemObjectId: String)
}