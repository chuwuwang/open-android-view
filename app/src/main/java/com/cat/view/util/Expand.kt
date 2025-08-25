package com.cat.view.util

import android.app.Activity
import android.content.Intent

inline fun <reified T : Activity> Activity.openActivity() {
    val intent = Intent(this, T::class.java)
    startActivity(intent)
}