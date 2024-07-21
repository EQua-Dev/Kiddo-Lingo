package com.awesomenessstudios.schoolprojects.kiddolingo

import android.content.Context
import androidx.appcompat.widget.AppCompatTextView
import android.util.AttributeSet
import com.awesomenessstudios.schoolprojects.kiddolingo.StrokeManager.StatusChangedListener
import com.awesomenessstudios.schoolprojects.kiddolingo.utils.Common

/**
 * Status bar for the test app.
 *
 *
 * It is updated upon status changes announced by the StrokeManager.
 */
class StatusTextView : AppCompatTextView, StatusChangedListener {
  private var strokeManager: StrokeManager? = null

  constructor(context: Context) : super(context) {}
  constructor(context: Context?, attributeSet: AttributeSet?) : super(
    context!!,
    attributeSet
  ) {
  }

  override fun onStatusChanged() {
    this.text = strokeManager!!.status
    Common.recognizedText = strokeManager!!.textRetrieved.toString()

  }

  fun setStrokeManager(strokeManager: StrokeManager?) {
    this.strokeManager = strokeManager
  }
}
