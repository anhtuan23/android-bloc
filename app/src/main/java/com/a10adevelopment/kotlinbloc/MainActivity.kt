package com.a10adevelopment.kotlinbloc

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Remove
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.setContent
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.a10adevelopment.kotlinbloc.bloc.CounterEvent
import com.a10adevelopment.kotlinbloc.bloc.CounterState
import com.a10adevelopment.kotlinbloc.ui.KotlinBlocTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModels()

    @ExperimentalCoroutinesApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            KotlinBlocTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    HomeScreen(viewModel)
                }
            }
        }
    }
}

@ExperimentalCoroutinesApi
@Composable
fun HomeScreen(viewModel: MainViewModel) {
    val counterBloc = viewModel.counterBloc
    val counterState = counterBloc.stateFlow.collectAsState().value
    Scaffold(floatingActionButton = {
        Column {
            FloatingActionButton(
                onClick = {
                    counterBloc.add(CounterEvent.Increase)
                }
            ) {
                Icon(Icons.Outlined.Add)
            }
            Spacer(modifier = Modifier.height(8.dp))
            FloatingActionButton(
                onClick = {
                    counterBloc.add(CounterEvent.Decrease)
                }
            ) {
                Icon(Icons.Outlined.Remove)
            }
        }
    }) {
        Column(
            Modifier.fillMaxWidth().fillMaxHeight(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            when (counterState) {
                is CounterState.Initial -> Text("Initial")
                is CounterState.Counting -> Text(counterState.count.toString())
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
//    KotlinBlocTheme {
//        HomeScreen("Android")
//    }
}