package com.e.safety.ui.dialogfragments.basedialogfragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.e.safety.databinding.EditTextDialogBinding
import com.e.safety.ui.activities.mainactivity.MainActivity
import javax.inject.Inject

abstract class BaseEditTextDialogFragment : DialogFragment() {

    @Inject
    lateinit var factory: ViewModelProvider.Factory

    protected inline fun <reified VM : ViewModel> getViewModel(): VM =
        ViewModelProvider(requireActivity(), factory)[VM::class.java]

    protected var binding: EditTextDialogBinding? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (requireActivity() as MainActivity).mainActivityComponent.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = EditTextDialogBinding.inflate(inflater)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding!!.cancelButton.setOnClickListener {
            onCancelBtnClick()
        }
        binding!!.confirmButton.setOnClickListener {
            onAcceptBtnClick()
        }
        observeForChanges()
    }

    abstract fun observeForChanges()

    protected fun setData(text: String) {
        binding!!.editText.setText(text)
    }

    protected open fun onCancelBtnClick() {
        dismiss()
    }

    protected open fun onAcceptBtnClick() {
        dismiss()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}