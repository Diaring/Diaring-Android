package com.oss.diaring.util

import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.NavDirections
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController

fun Fragment.popBackStack() {
    this.findNavController().popBackStack()
}

fun Fragment.navigate(action: Int) {
    this.findNavController().navigate(action)
}

fun Fragment.navigateWithData(navDirections: NavDirections) {
    this.findNavController().navigate(navDirections)
}

fun View.navigateWithData(navDirections: NavDirections) {
    Navigation.findNavController(this).navigate(navDirections)
}