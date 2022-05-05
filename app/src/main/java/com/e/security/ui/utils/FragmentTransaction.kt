package com.e.security.ui.utils

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity

fun AppCompatActivity.addFragment(fragment: Fragment, container: Int, tag:String) {
    val currentFragment = supportFragmentManager.findFragmentByTag(tag)
    if (currentFragment == null) {
        supportFragmentManager.beginTransaction()
            .setReorderingAllowed(true)
            .add(container, fragment, tag)
            .addToBackStack(tag)
            .commit()
    }
}
fun FragmentActivity.addFragment(fragment: Fragment, container: Int, tag:String) {
    val currentFragment = supportFragmentManager.findFragmentByTag(tag)
    if (currentFragment == null) {
        supportFragmentManager.beginTransaction()
            .setReorderingAllowed(true)
            .add(container, fragment, tag)
            .addToBackStack(tag)
            .commit()
    }
}

////todo check if childfragmentmanager is correct
//fun Fragment.addFragment(fragment: Fragment, container: Int, tag:String) {
//    val currentFragment = childFragmentManager.findFragmentByTag(tag)
//    if (currentFragment == null) {
//        childFragmentManager.beginTransaction()
//            .setReorderingAllowed(true)
//            .add(container, fragment, tag)
//            .addToBackStack(tag)
//            .commit()
//    }
//}