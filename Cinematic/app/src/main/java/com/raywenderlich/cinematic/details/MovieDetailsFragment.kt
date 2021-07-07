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
package com.raywenderlich.cinematic.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewAnimationUtils
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import coil.imageLoader
import coil.load
import coil.request.ImageRequest
import coil.transform.BlurTransformation
import com.raywenderlich.cinematic.R
import com.raywenderlich.cinematic.databinding.FragmentDetailsBinding
import com.raywenderlich.cinematic.model.Movie
import com.raywenderlich.cinematic.util.Constants.IMAGE_BASE
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel
import kotlin.math.hypot

class MovieDetailsFragment : Fragment(R.layout.fragment_details) {

  private var _binding: FragmentDetailsBinding? = null
  private val binding get() = _binding!!

  private val args: MovieDetailsFragmentArgs by navArgs()
  private val viewModel: MovieDetailsViewModel by viewModel()

  private val castAdapter: CastAdapter by inject()

  override fun onCreateView(
      inflater: LayoutInflater,
      container: ViewGroup?,
      savedInstanceState: Bundle?,
  ): View {
    _binding = FragmentDetailsBinding.inflate(inflater, container, false)
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    binding.castList.apply {
      adapter = castAdapter
      layoutManager =
          LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
    }

    args.movieId.also {
      viewModel.getMovieDetails(it)
      viewModel.getCast(it)
    }
    attachObservers()
  }

  private fun attachObservers() {
    viewModel.movie.observe(viewLifecycleOwner, { movie ->
      renderUi(movie)
    })

    viewModel.cast.observe(viewLifecycleOwner, { cast ->
      castAdapter.submitList(cast)
    })
  }

  override fun onCreateAnimation(transit: Int, enter: Boolean, nextAnim: Int): Animation? {
    return AnimationUtils.loadAnimation(requireContext(), nextAnim).apply {
      setAnimationListener(object: Animation.AnimationListener {
        override fun onAnimationStart(animation: Animation?){}
        override fun onAnimationRepeat(animation: Animation?) {}
        override fun onAnimationEnd(animation: Animation?) {
          if (enter) {
            viewModel.movie.observe(viewLifecycleOwner) {
              animateMoviePosterIn(it)
            }
          }
        }
      })
    }
  }

  private fun animateMoviePosterIn(movie: Movie) {
    val request = ImageRequest.Builder(requireContext())
      .data(IMAGE_BASE + movie.backdropPath)
      .target(binding.poster)
      .listener(onSuccess = { _, _ ->
        val view = binding.posterContainer
        view.doOnPreDraw {
          val cx = view.width / 2
          val cy = view.height / 2

          val finalRadius = hypot(cx.toDouble(), cy.toDouble()).toFloat()
          val anim = ViewAnimationUtils.createCircularReveal(view, cx, cy, 0f, finalRadius)
          view.visibility = View.VISIBLE
          anim.start()
        }
      })
      .build()

    requireContext().imageLoader.enqueue(request)
  }

  private fun renderUi(movie: Movie) {
    binding.backdrop.load(IMAGE_BASE + movie.backdropPath) {
      crossfade(true)
      transformations(BlurTransformation(requireContext()))
    }

    binding.title.text = movie.title
    binding.summary.text = movie.overview
    binding.ratingValue.text = movie.rating.toString()
    binding.movieRating.rating = movie.rating

    binding.addToFavourites.apply {
      icon = if (movie.isFavourite) {
        getDrawable(requireContext(), R.drawable.ic_baseline_favorite_24)
      } else {
        getDrawable(requireContext(), R.drawable.ic_baseline_favorite_border_24)
      }
      text = if (movie.isFavourite) {
        getString(R.string.remove_from_favourites)
      } else {
        getString(R.string.add_to_favourites)
      }
      setOnClickListener {
        if (movie.isFavourite) {
          viewModel.unsetMovieAsFavourite(movie.id)
        } else {
          viewModel.setMovieAsFavourite(movie.id)
        }
      }
    }
  }
}