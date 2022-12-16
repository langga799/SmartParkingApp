package com.example.bookingparking.helper

import android.content.Context
import android.view.View
import android.widget.Toast
import es.dmoral.toasty.Toasty

fun View.gone(){
    visibility = View.GONE
}

fun View.visible(){
    visibility = View.VISIBLE
}

fun View.inVisible(){
    visibility = View.INVISIBLE
}

fun Context.onSuccess(message:String){
    Toasty.success(this, message, Toasty.LENGTH_SHORT).show()
}

fun Context.onError(message:String){
    Toasty.error(this, message, Toasty.LENGTH_SHORT).show()
}

fun Context.onInfo(message:String){
    Toasty.info(this, message, Toasty.LENGTH_SHORT).show()
}