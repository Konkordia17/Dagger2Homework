package ru.otus.daggerhomework

import android.app.Application
import android.content.Context
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import java.lang.ref.WeakReference
import javax.inject.Inject
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class ViewModelReceiver(context: Context, private val stateObserver: ReceiverStateObserver) :
    ViewModel() {
  private val context: WeakReference<Context> = WeakReference(context)

  private val _colorLiveData = MutableLiveData<Int>()
  val colorLiveData: LiveData<Int> = _colorLiveData

  init {
    viewModelScope.launch {
      stateObserver.getStateObserver().collect {
        it?.let { color ->
          observeColors()
          _colorLiveData.value = color
        }
      }
    }
  }

  private fun observeColors() {
    if (context.get() !is Application) throw RuntimeException("Здесь нужен контекст апликейшена")
    Toast.makeText(context.get(), "Color received", Toast.LENGTH_LONG).show()
  }
}

class ViewModelReceiverFactory
@Inject
constructor(
    @ApplicationContext private val context: Context,
    private val stateObserver: ReceiverStateObserver
) : ViewModelProvider.Factory {

  override fun <T : ViewModel> create(modelClass: Class<T>): T {
    return when (modelClass) {
      ViewModelReceiver::class.java -> {
        ViewModelReceiver(context, stateObserver)
      }
      else -> throw IllegalArgumentException("Unknown ViewModel class")
    }
        as T
  }
}
