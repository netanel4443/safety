package com.e.security.ui.fragments

import android.content.Context
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.e.security.di.viewmodelfactory.ViewModelProviderFactory
import javax.inject.Inject

open class VmDialogFragment:DialogFragment() {

    @Inject lateinit var factory:ViewModelProviderFactory

    protected inline fun <reified VM:ViewModel> getViewModel(): VM {
        return ViewModelProvider(requireActivity(),factory)[VM::class.java]
    }


}