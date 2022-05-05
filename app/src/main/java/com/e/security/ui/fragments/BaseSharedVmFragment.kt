package com.e.security.ui.fragments

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import javax.inject.Inject

open class BaseSharedVmFragment: Fragment() {

    @Inject lateinit var factory:ViewModelProvider.Factory

    protected inline fun <reified VM:ViewModel> getViewModel(): VM =
        ViewModelProvider(requireActivity(),factory)[VM::class.java]
}