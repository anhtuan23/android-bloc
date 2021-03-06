# android-bloc
A Bloc implementation for Android, inspired by [programadorthi/android-bloc](https://github.com/programadorthi/android-bloc)

The state is exposed via a Kotlin's [StateFlow](https://developer.android.com/kotlin/flow/stateflow-and-sharedflow).

Designed to work nicely with [Jetpack Compose](https://developer.android.com/jetpack/compose).

[![](https://jitpack.io/v/anhtuan23/android-bloc.svg)](https://jitpack.io/#anhtuan23/android-bloc)

## Install
Add jitpack.io in your root build.gradle at the end of repositories:
```
	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```

Add the dependency
```
dependencies {
	        implementation 'com.github.anhtuan23:android-bloc:v0.1.6'
	}
```

## Usage
CounterBloc.kt
```
class CounterBloc(  
    blocScope: CoroutineScope,  
    initialState: CounterState = CounterState.Initial,  
) : Bloc<CounterEvent, CounterState>(blocScope, initialState) {  
  
    override suspend fun FlowCollector<CounterState>.mapEventToState(event: CounterEvent) {  
        when (event) {  
            is CounterEvent.Increase -> emit(onIncrease())  
            is CounterEvent.Decrease -> emit(onDecrease())  
        }  
    }  
  
    private fun onIncrease(): CounterState = when (val state = currentState) {  
        is CounterState.Initial -> CounterState.Counting(0)  
        is CounterState.Counting -> state.increase()  
    }  
  
    private fun onDecrease(): CounterState = when (val state = currentState) {  
        is CounterState.Initial -> CounterState.Counting(0)  
        is CounterState.Counting -> state.decrease()  
    }  
}  
  
sealed class CounterState {  
    object Initial : CounterState()  
    data class Counting(val count: Int) : CounterState()  
}  
  
fun CounterState.Counting.increase() = CounterState.Counting(this.count + 1)  
fun CounterState.Counting.decrease() = CounterState.Counting(this.count - 1)  
  
sealed class CounterEvent {  
    object Increase : CounterEvent()  
    object Decrease : CounterEvent()  
}
```

Include in ViewModel
```
class MainViewModel @ViewModelInject constructor() : ViewModel() {  
  
    val counterBloc = CounterBloc(viewModelScope)  
  
    override fun onCleared() {  
        super.onCleared()  
        counterBloc.onClose()  
    }  
}
```

Collect State and add Event in a Composable function:

```
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
            FloatingActionButton(onClick = {counterBloc.add(CounterEvent.Increase)}) 
                {Icon(Icons.Outlined.Add)}

            Spacer(modifier = Modifier.height(8.dp))

            FloatingActionButton(onClick = {counterBloc.add(CounterEvent.Decrease)}) 
                {Icon(Icons.Outlined.Remove)}
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
 ```