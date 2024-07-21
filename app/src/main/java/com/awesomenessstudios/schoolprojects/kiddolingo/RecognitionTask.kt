package com.awesomenessstudios.schoolprojects.kiddolingo

import android.util.Log
import com.google.android.gms.tasks.SuccessContinuation
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.mlkit.vision.digitalink.DigitalInkRecognizer
import com.google.mlkit.vision.digitalink.Ink
import com.google.mlkit.vision.digitalink.RecognitionResult
import com.awesomenessstudios.schoolprojects.kiddolingo.ui.game.Game
import com.awesomenessstudios.schoolprojects.kiddolingo.utils.Common
import java.util.concurrent.atomic.AtomicBoolean

/** Task to run asynchronously to obtain recognition results.  */
class RecognitionTask(private val recognizer: DigitalInkRecognizer?, private val ink: Ink) {
  private var currentResult: RecognizedInk? = null
  private val cancelled: AtomicBoolean
  private val done: AtomicBoolean

  val theActivity = Game()

  fun cancel() {
    cancelled.set(true)
  }

  fun done(): Boolean {
    return done.get()
  }

  fun result(): RecognizedInk? {
    return currentResult
  }

  /** Helper class that stores an ink along with the corresponding recognized text.  */
  class RecognizedInk internal constructor(val ink: Ink, val text: String?)

  fun run(): Task<String?> {
    Log.i(TAG, "RecoTask.run")
    return recognizer!!
      .recognize(ink)
      .onSuccessTask(
        SuccessContinuation { result: RecognitionResult? ->
          if (cancelled.get() || result == null || result.candidates.isEmpty()
          ) {
            return@SuccessContinuation Tasks.forResult<String?>(null)
          }
          currentResult =
            RecognizedInk(
              ink,
              result.candidates[0]
                .text
            )
          Common.recognizedText = currentResult!!.text.toString()
          Log.i(
            TAG,
            "result: " + currentResult!!.text
          )
          Log.i(TAG, "run: ${Common.recognizedText}")

          theActivity.plateNumber = currentResult!!.text.toString()

          Log.d(TAG, "runActivity: ${theActivity.plateNumber}")
          done.set(
            true
          )
          return@SuccessContinuation Tasks.forResult<String?>(currentResult!!.text)
        }
      )
  }



  companion object {
    private const val TAG = "MLKD.RecognitionTask"
  }

  init {
    cancelled = AtomicBoolean(false)
    done = AtomicBoolean(false)
  }
}
