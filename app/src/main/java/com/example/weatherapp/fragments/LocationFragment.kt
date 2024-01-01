package com.example.weatherapp.fragments

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.weatherapp.R
import com.example.weatherapp.databinding.FragmentLocationBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.search.SearchView
import com.google.android.material.search.SearchView.TransitionState


class LocationFragment : Fragment() {

    private lateinit var _binding: FragmentLocationBinding
    private val binding get() = _binding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentLocationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bottomNavView =
            requireActivity().findViewById<BottomNavigationView>(R.id.bottom_nav_view)

        binding.locationSearchView.setupWithSearchBar(binding.sbLocationSearch)

        binding.locationSearchView.addTransitionListener(object : SearchView.TransitionListener {
            override fun onStateChanged(
                searchView: SearchView,
                previousState: SearchView.TransitionState,
                newState: SearchView.TransitionState
            ) {

                if(newState == TransitionState.SHOWING){
                    bottomNavView.visibility = View.GONE
                }
                if(newState == TransitionState.HIDING){
                    bottomNavView.visibility = View.VISIBLE
                }

            }
        })

    }

    companion object {

        fun newInstance() = LocationFragment()

    }
}