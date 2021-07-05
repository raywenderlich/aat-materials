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
package com.raywenderlich.cinematic.login

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import com.raywenderlich.cinematic.MainActivity
import com.raywenderlich.cinematic.R
import com.raywenderlich.cinematic.databinding.ActivityAuthBinding

class AuthActivity : AppCompatActivity() {
  private val viewModel by viewModels<AuthViewModel>()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    val binding = ActivityAuthBinding.inflate(layoutInflater)
    setContentView(binding.root)
    supportFragmentManager.commit {
      replace(R.id.fragmentContainer, AuthFragment.newInstance())
    }

    viewModel.showMain.observe(this) {
      startActivity(Intent(this, MainActivity::class.java))
    }
    viewModel.showLogin.observe(this) {
      showLogin(it)
    }
    viewModel.showSignUp.observe(this) {
      showSignup(it)
    }
  }

  private fun showLogin(sharedView: View) {
    supportFragmentManager.commit {
      replace(R.id.fragmentContainer, LoginFragment.newInstance())
      addSharedElement(sharedView, "logo_transition_name")
      addToBackStack(null)
    }
  }

  private fun showSignup(sharedView: View) {
    supportFragmentManager.commit {
      replace(R.id.fragmentContainer, SignupFragment.newInstance())
      addSharedElement(sharedView, "logo_transition_name")
      addToBackStack(null)
    }
  }
}