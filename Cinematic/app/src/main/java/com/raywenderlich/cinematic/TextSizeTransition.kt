/*
 * Copyright (c) 2021 Razeware LLC
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * Notwithstanding the foregoing, you may not use, copy, modify, merge, publish,
 * distribute, sublicense, create a derivative work, and/or sell copies of the
 * Software in any work that is designed, intended, or marketed for pedagogical or
 * instructional purposes related to programming, coding, application development,
 * or information technology.  Permission for such use, copying, modification,
 * merger, publication, distribution, sublicensing, creation of derivative works,
 * or sale is expressly withheld.
 *
 * This project and source code may use libraries or frameworks that are
 * released under various Open-Source licenses. Use of those libraries and
 * frameworks are governed by their own individual licenses.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.raywenderlich.cinematic

import android.animation.Animator
import android.animation.ObjectAnimator
import android.content.Context
import android.util.AttributeSet
import android.util.Property
import android.util.TypedValue
import android.view.ViewGroup
import android.widget.TextView
import androidx.transition.Transition
import androidx.transition.TransitionValues

class TextSizeTransition : Transition {
  constructor()
  constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)

  override fun getTransitionProperties() = properties

  override fun captureStartValues(transitionValues: TransitionValues) {
    captureTextSize(transitionValues)
  }

  override fun captureEndValues(transitionValues: TransitionValues) {
    captureTextSize(transitionValues)
  }

  private fun captureTextSize(transitionValues: TransitionValues) {
    (transitionValues.view as? TextView)?.let { textView ->
      transitionValues.values[textSizeProp] = textView.textSize
    }
  }

  override fun createAnimator(
    sceneRoot: ViewGroup,
    startValues: TransitionValues?,
    endValues: TransitionValues?
  ): Animator? {
    if (startValues == null || endValues == null) {
      return null
    }
    val startSize = startValues.values[textSizeProp] as Float
    val endSize = endValues.values[textSizeProp] as Float
    val view = endValues.view as TextView
    view.setTextSize(TypedValue.COMPLEX_UNIT_PX, startSize)
    return ObjectAnimator.ofFloat(view, textSizeProperty, startSize, endSize)
  }

  companion object {
    private const val textSizeProp = "transition:textsize"
    private val properties = arrayOf(textSizeProp)
    private val textSizeProperty = TextSizeProperty()
  }

  class TextSizeProperty : Property<TextView, Float>(Float::class.java, "textSize") {
    override fun get(textView: TextView): Float = textView.textSize
    override fun set(textView: TextView, textSizePixels: Float) =
      textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSizePixels)
  }
}