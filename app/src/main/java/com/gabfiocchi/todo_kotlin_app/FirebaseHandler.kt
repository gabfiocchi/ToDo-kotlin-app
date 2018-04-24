package com.gabfiocchi.todo_kotlin_app

import android.app.Application
import com.google.firebase.database.FirebaseDatabase

// Sin esto crashea la app al iniciar luego de apretar el back button.
internal class FirebaseHandler : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseDatabase.getInstance().setPersistenceEnabled(true)
    }
}