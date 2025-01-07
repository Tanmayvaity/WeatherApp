package com.example.weatherapp.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.commit
import com.example.weatherapp.R
import com.example.weatherapp.activities.SettingsActivity
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

//        (activity as AppCompatActivity).setSupportActionBar(binding.sbLocationSearch)



        val bottomNavView =
            requireActivity().findViewById<BottomNavigationView>(R.id.bottom_nav_view)

        binding.sbLocationSearch.apply {
            inflateMenu(R.menu.settings_menu)
            setOnMenuItemClickListener { item ->
                if(item.itemId == R.id.settings_screen){

                    val settingsIntent = Intent(requireContext(),SettingsActivity::class.java)
                    startActivity(settingsIntent)
//                    val fm = requireActivity().supportFragmentManager
//
//                    fm.commit {
//                        setCustomAnimations(R.anim.slide_left,R.anim.slide_out,R.anim.fade_in,R.anim.slide_out)
//                        replace(R.id.fragment_container,SettingsScreen())
//                        addToBackStack(SettingsScreen().javaClass.name)
//                        bottomNavView.visibility = View.GONE


//                    }
                }


                true
            }
        }
        binding.locationSearchView.setupWithSearchBar(binding.sbLocationSearch)

        binding.locationSearchView.addTransitionListener(object : SearchView.TransitionListener {
            override fun onStateChanged(
                searchView: SearchView,
                previousState: SearchView.TransitionState,
                newState: SearchView.TransitionState
            ) {
                if(newState == TransitionState.SHOWING){
                    Log.d(TAG,"searchview showing")
                    bottomNavView.visibility = View.GONE
                }
                if(newState == TransitionState.HIDING){
                    Log.d(TAG,"searchview hiding")
                    bottomNavView.visibility = View.VISIBLE
                }

            }
        })

        binding.appCompatButton.setOnClickListener{
            Log.d(TAG,"clicked")
        }

    }

//    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
//        inflater.inflate(R.menu.settings_menu,menu)
//        super.onCreateOptionsMenu(menu, inflater)
//    }


    companion object {

        const val TAG = "LocationFragment"

        fun newInstance() = LocationFragment()

    }

    override fun toString(): String {
        return "LocationFragment"
    }
}