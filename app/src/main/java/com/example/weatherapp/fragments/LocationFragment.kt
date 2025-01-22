package com.example.weatherapp.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weatherapp.Keys
import com.example.weatherapp.MainActivity
import com.example.weatherapp.R
import com.example.weatherapp.activities.SettingsActivity
import com.example.weatherapp.adapter.HeaderAdapter
import com.example.weatherapp.adapter.LocationAdapter
import com.example.weatherapp.api.GeoCodingApiInstance
import com.example.weatherapp.databinding.FragmentLocationBinding
import com.example.weatherapp.models.GeoCoding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.search.SearchView
import com.google.android.material.search.SearchView.TransitionState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException
import java.net.SocketException


class LocationFragment : Fragment() {

    private lateinit var _binding: FragmentLocationBinding
    private val binding get() = _binding
    private var locations: MutableList<GeoCoding>? = mutableListOf()
    private lateinit var locationAdapter: LocationAdapter
    private lateinit var adapter: ConcatAdapter
    private lateinit var headerAdapter: HeaderAdapter
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


         headerAdapter = HeaderAdapter("Top Results")

        binding.rvLocations.layoutManager = LinearLayoutManager(requireContext())


        locationAdapter = LocationAdapter(
            locations!!,
            requireContext(),
            object : LocationAdapter.LocationItemClickListener {
                override fun onclick(location: GeoCoding) {

                    Log.d(TAG, "clicked")


                    val args = Bundle()
                    val weatherFragmentInstance = WeatherFragment.newInstance()
                    args.putSerializable(LOCATION_DATA, location)
                    weatherFragmentInstance.arguments = args
//

//
//                    requireActivity().supportFragmentManager.beginTransaction()
//                        .replace(R.id.fragment_container, weatherFragmentInstance)
//                        .commit()

                    binding.locationSearchView.hide()
                    parentFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, weatherFragmentInstance)
                        .addToBackStack(WeatherFragment.toString())
                        .commit()


                    (activity as MainActivity).binding.bottomNavView.isVisible = false


                }
            })

//        adapter = ConcatAdapter(ConcatAdapter.Config.Builder()
//            .setIsolateViewTypes(false)
//            .build(),headerAdapter,locationAdapter)
        adapter = ConcatAdapter(headerAdapter,locationAdapter)
        binding.rvLocations.adapter = adapter

        binding.sbLocationSearch.apply {
            inflateMenu(R.menu.settings_menu)
            setOnMenuItemClickListener { item ->
                if (item.itemId == R.id.settings_screen) {

                    val settingsIntent = Intent(requireContext(), SettingsActivity::class.java)
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
                if (newState == TransitionState.SHOWING) {
                    Log.d(TAG, "searchview showing")
                    bottomNavView.visibility = View.GONE
                }
                if (newState == TransitionState.HIDING) {
                    Log.d(TAG, "searchview hiding")
                    bottomNavView.visibility = View.VISIBLE

                }

            }
        })



        binding.locationSearchView.editText.setOnEditorActionListener(object :
            TextView.OnEditorActionListener {
            override fun onEditorAction(
                textView: TextView?,
                actionId: Int,
                event: KeyEvent?
            ): Boolean {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {

                    val location = (if (textView != null) textView.text else "").trim().toString()


                    if (location.isEmpty() || location.isBlank()) {
                        Toast.makeText(
                            requireContext(),
                            "Text cannot be empty or blank",
                            Toast.LENGTH_SHORT
                        ).show()
                        return true
                    }
                    toggleSwipeToRefresh(true)
                    lifecycleScope.launch(Dispatchers.Main) {

                        val defered = async(Dispatchers.IO) {
                            fetchCoordinates(location)
                        }

                        val geoCoding = defered.await()

                        if (geoCoding == null) {
                            Log.d(TAG, "null")
                            return@launch
                        }

                        if (geoCoding!!.isNotEmpty()) {
                            Log.d(TAG, "lon : ${geoCoding!![0].lon} lat : ${geoCoding!![0].lat}")
                            locations!!.clear()
                            locations!!.addAll(geoCoding)
                            binding.rvLocations.visibility = View.VISIBLE
                            locationAdapter.notifyDataSetChanged()
                            binding.tvSearchForLocation.isVisible = false

                        } else {
                            Log.d(TAG, "Please check your input")
                            Toast.makeText(requireContext(),"Please check your input",Toast.LENGTH_SHORT).show()
                        }
                    }



                }

                return false

            }
        })

        binding.appCompatButton.setOnClickListener {
            Log.d(TAG, "clicked")
        }
    }

    private suspend fun fetchCoordinates(cityName: String): List<GeoCoding>? {
        var geoCoding: List<GeoCoding>? = null

        val response: Response<List<GeoCoding>> = try {
            GeoCodingApiInstance.geoCodingApi.getCoordinates(
                cityName = cityName,
                apiKey = Keys.weatherApiKey
            )
        } catch (e: IOException) {
            Log.e(TAG, "No Internet Connection ")
            withContext(Dispatchers.Main) {
                Toast.makeText(requireContext(), "No Internet Connection", Toast.LENGTH_SHORT)
                    .show()
            }
            return null
        } catch (e: SocketException) {
            Log.e(TAG, "Internet not working")
            withContext(Dispatchers.Main) {
                Toast.makeText(requireContext(), "Internet Not Working", Toast.LENGTH_SHORT).show()
            }
            return null
        } catch (e: HttpException) {
            Log.e(TAG, "Exception for an unexpected, non-2xx HTTP response : ${e.code()}")
            return null
        }

        if (response.isSuccessful && response.body() != null) {
            geoCoding = response.body()
            Log.i(TAG, "${response.body()}")
            Log.i(TAG, "${response.code()}")
            Log.i(TAG, "${response.raw().request.url}")
        }

        return geoCoding


    }

    private fun toggleSwipeToRefresh(start: Boolean) {
//        binding.swipeRefreshCoordinates.isRefreshing = start

    }

//    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
//        inflater.inflate(R.menu.settings_menu,menu)
//        super.onCreateOptionsMenu(menu, inflater)
//    }


    companion object {

        const val TAG = "LocationFragment"
        const val LOCATION_DATA = "LocationData"

        fun newInstance() = LocationFragment()

    }

    override fun toString(): String {
        return "LocationFragment"
    }
}