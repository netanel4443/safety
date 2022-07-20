package com.e.safety.ui.compose

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.e.safety.R
import com.e.safety.ui.activities.mainactivity.MainActivity
import com.e.safety.ui.fragments.BaseSharedVmFragment
import com.e.safety.ui.recyclerviews.celldata.ImageViewVhCell
import com.e.safety.ui.viewmodels.MainViewModel

class CreateFindingScreen : BaseSharedVmFragment() {

    private val viewModel: MainViewModel by lazy(this::getViewModel)

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (requireActivity() as MainActivity).mainActivityComponent.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply{
            setContent {
                var state = viewModel.viewState.observeAsState()
                var currState = state.value?.createFindingFragmentState?.let { currState->

                val expandSpinner = remember { mutableStateOf(false) }

                val setExpandedSpinner: (Boolean) -> Unit = {
                    expandSpinner.value = it
                }
                Surface(modifier = Modifier
                    .fillMaxWidth(),
                    color = Color.White
             ) {

                Column(modifier = Modifier.fillMaxSize()) {

                    HorizontalList(
                        Modifier
                            .fillMaxWidth()
                            .height(100.dp),
                        items = state.value!!.createFindingFragmentState.problemImage
                    )

                    Row(modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)) {
                        val modifier = Modifier.weight(1f)
                        Text(modifier = modifier, text = "קדימות")
                        TextSpinner(
                            modifier = modifier,
                            listOf("1", "2", "3"),
                            expandSpinner.value,
                            setExpandedSpinner
                        )
                    }

                    TextHint(
                        text = currState.finding.requirement,
                        placeHolder = getString(R.string.requirement), onClick = ::println
                    )


                    val problemDescription = customTextField(
                        initialValue = state.value!!.createFindingFragmentState.finding.problem,
                        modifier = Modifier.fillMaxWidth()
                    )
                    val problemLocation = customTextField(
                        initialValue = state.value!!.createFindingFragmentState.finding.problemLocation,
                        modifier = Modifier.fillMaxWidth()
                    )

                    TextButton(modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(vertical = 8.dp),
                        onClick = { }) {
                        Text(text = getString(R.string.confirm))
                    }

                }
            }
            }
            }
        }
    }

    @Composable
    fun TextHint(
        modifier: Modifier = Modifier,
        text: String,
        placeHolder: String,
        onClick: () -> Unit
    ) {
        val clickModifier = modifier.clickable(onClick = onClick)
        return if (text.isEmpty()) {
            Text(modifier = clickModifier, text = placeHolder, color = Color.LightGray)
        } else {
            Text(modifier = clickModifier, text = text)
        }
    }

    @Composable
    fun HorizontalList(modifier: Modifier = Modifier, items: List<ImageViewVhCell>) {
        Box(modifier = modifier) {
            Icon(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(8.dp),
                painter = painterResource(id = R.drawable.ic_baseline_add_24),
                contentDescription = null,
//                tint = Color.White
            )
            LazyRow(
                modifier = Modifier.fillMaxSize(),
            ) {
                items(
                    key = { index -> items[index].id },
                    count = items.size
                ) { index ->
                    AsyncImage(
                        model = items[index].image,
                        contentDescription = null
                    )
                }
            }
        }
    }

    @Composable
    fun customTextField(
        initialValue: String,
        modifier: Modifier = Modifier,
        placeHolder: String = ""
    ): State<TextFieldValue> {
        val textState = remember { mutableStateOf(TextFieldValue(initialValue)) }
        TextField(
            modifier = modifier,
            value = textState.value,
            placeholder = { Text(text = placeHolder) },
            onValueChange = { textState.value = it }
        )
        return textState
    }

    @Composable
    fun <T> TextSpinner(
        modifier: Modifier = Modifier,
        items: List<T>, expanded: Boolean, setExpanded: (Boolean) -> Unit
    ) {
        DropdownMenu(
            modifier = modifier,
            expanded = expanded,
            onDismissRequest = { setExpanded(false) },
        ) {
            items.forEach { item ->
                DropdownMenuItem(onClick = {
                    setExpanded(false)
                }) {
                    Text(text = item.toString())
                }
            }
        }
    }
}




