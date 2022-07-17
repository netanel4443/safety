package com.e.security.ui.dialogfragments.basedialogfragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.e.security.databinding.TextviewDeleteDialogBinding
import io.reactivex.rxjava3.disposables.CompositeDisposable
import javax.inject.Inject

abstract class BaseTextDialogFragment<T> : DialogFragment() {

    @Inject
    lateinit var factory: ViewModelProvider.Factory

    protected inline fun <reified VM:ViewModel > getViewModel(): VM =
        ViewModelProvider(requireActivity(), factory)[VM::class.java]


    protected var binding: TextviewDeleteDialogBinding? = null
    private val compositeDisposable = CompositeDisposable()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = TextviewDeleteDialogBinding.inflate(inflater)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding!!.cancelButton.setOnClickListener {
            onCancelBtnClick()
        }
        binding!!.confirmButton.setOnClickListener {
            onAcceptBtnClick()
        }
        setData()
    }


    private fun setData() {
        val data = setMessage()
        binding!!.message.text = data
    }

    protected abstract fun setMessage():String

    protected open fun onCancelBtnClick() {
        dismiss()
    }

    protected open fun onAcceptBtnClick() {
        dismiss()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
        compositeDisposable.clear()
    }
}