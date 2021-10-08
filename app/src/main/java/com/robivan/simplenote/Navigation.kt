package com.robivan.simplenote

import android.text.TextUtils
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager

class Navigation(private val fragmentManager: FragmentManager) {
    fun addFragment(idView: Int, fragment: Fragment?, key: String?) {
        val fragmentTransaction = fragmentManager.beginTransaction()
        // Добавить фрагмент
        if (TextUtils.isEmpty(key)) {
            fragmentTransaction.replace(idView, fragment!!)
        } else {
            fragmentTransaction.replace(idView, fragment!!, key)
        }
        fragmentTransaction.addToBackStack(null).commit()
    }
}