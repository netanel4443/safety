package com.e.security.ui.dialogfragments

import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.e.fakerestapi.ui.recyclerviews.helpers.GenericVhItem
import com.e.security.ui.recyclerviews.generics.GenericRecyclerviewAdapter2
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import javax.inject.Inject

open class BaseRecyclerViewDialogFragment : DialogFragment() {


    @Inject
    lateinit var factory: ViewModelProvider.Factory

    protected inline fun <reified VM: ViewModel> getViewModel(): VM =
        ViewModelProvider(requireActivity(),factory)[VM::class.java]

    private var compositeDisposable= CompositeDisposable()


    fun Disposable.addDisposable() {
        compositeDisposable.add(this)
    }

    protected var recyclerviewAdapter: GenericRecyclerviewAdapter2<GenericVhItem>? = null


    fun submitList(items: List<GenericVhItem>) {
        recyclerviewAdapter!!.submitList(items)
    }

    fun setRecyclerViewAdapter(recyclerView: RecyclerView,adapter: GenericRecyclerviewAdapter2<GenericVhItem>): GenericRecyclerviewAdapter2<GenericVhItem> {
        recyclerviewAdapter = adapter
        recyclerView.adapter = adapter
        return adapter
    }

    fun setLayoutManager(recyclerView: RecyclerView,layoutManager: RecyclerView.LayoutManager): RecyclerView {
        recyclerView.layoutManager = layoutManager
        return recyclerView
    }


    fun setVerticalLinearLayoutManager(recyclerView: RecyclerView) {
        setLinearLayoutManager(recyclerView,LinearLayoutManager.VERTICAL)
    }

    fun setHorizontalLinearLayoutManager(recyclerView: RecyclerView) {
        setLinearLayoutManager(recyclerView,LinearLayoutManager.HORIZONTAL)
    }

    private fun setLinearLayoutManager(recyclerView: RecyclerView,orientation: Int) {
        recyclerView.layoutManager = LinearLayoutManager(
            context,
            orientation,
            false
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
    }

}