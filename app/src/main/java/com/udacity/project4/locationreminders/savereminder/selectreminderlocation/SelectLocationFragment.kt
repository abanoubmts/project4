package com.udacity.project4.locationreminders.savereminder.selectreminderlocation


import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.content.res.Resources
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.material.snackbar.Snackbar
import com.udacity.project4.Constants
import com.udacity.project4.Constants.PERMISSION_CODE_LOCATION_REQUEST
import com.udacity.project4.R
import com.udacity.project4.base.BaseFragment
import com.udacity.project4.base.NavigationCommand
import com.udacity.project4.databinding.FragmentSelectLocationBinding
import com.udacity.project4.locationreminders.savereminder.SaveReminderViewModel
import com.udacity.project4.utils.setDisplayHomeAsUpEnabled
import org.koin.android.ext.android.inject
import java.util.*

class SelectLocationFragment : BaseFragment(), OnMapReadyCallback {

    //Use Koin to get the view model of the SaveReminder
    private val TAG_location = SelectLocationFragment::class.java.simpleName
    override val _viewModel: SaveReminderViewModel by inject()
    private lateinit var binding: FragmentSelectLocationBinding
    private lateinit var map: GoogleMap
    private var marker: Marker? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_select_location, container, false)

        binding.viewModel = _viewModel
        binding.lifecycleOwner = this

        setHasOptionsMenu(true)
        setDisplayHomeAsUpEnabled(true)

//        TODO: add the map setup implementation
//        TODO: zoom to the user location after taking his permission
//        TODO: add style to the map
//        TODO: put a marker to location that the user selected

        val mapFragment = childFragmentManager
            .findFragmentById(R.id.mapFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)



//        TODO: call this function after the user confirms on the selected location
        onLocationSelected()

        return binding.root
    }

    private fun onLocationSelected() {
        //        TODO: When the user confirms on the selected location,
        //         send back the selected location details to the view model
        //         and navigate back to the previous fragment to save the reminder and add the geofence


        marker?.let {marker ->
            _viewModel.latitude.value = marker.position.latitude
            _viewModel.longitude.value = marker.position.longitude
            _viewModel.reminderSelectedLocationStr.value = marker.title
            _viewModel.navigationCommand.value = NavigationCommand.Back
        }
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.map_options, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        // TODO: Change the map type based on the user's selection.
        R.id.normal_map -> {
            true
        }
        R.id.hybrid_map -> {
            true
        }
        R.id.satellite_map -> {
            true
        }
        R.id.terrain_map -> {
            true
        }
        else -> super.onOptionsItemSelected(item)
    }



    private fun PrepareMapStyle(map: GoogleMap) {
        try {

            val success = map.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                    requireContext(),
                    R.raw.map_style
                )
            )
            if (!success) {
                Log.e(TAG_location, "Style parsing failed.")
            }
        } catch (e: Resources.NotFoundException) {
            Log.e(TAG_location, "Can't find style. Error: ", e)
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        PrepareMapStyle(map)
        PreparePoiClick(map)
        PrepareMapLongClick(map)


        if (isPermissionGranted()) {
            getUserLocation()
        } else {
            requestLocationPermission()
        }
    }


    @SuppressLint("MissingPermission")
    private fun getUserLocation() {
        map.isMyLocationEnabled = true
        Log.d("MapsActivity", "getLastLocation Called")
        fusedLocationProviderClient.lastLocation
            .addOnSuccessListener { location : Location? ->
                location?.let {
                    val userLocation = LatLng(location.latitude, location.longitude)
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, Constants.DEFAULT_ZOOM_LEVEL))
                    marker=  map.addMarker(
                        MarkerOptions().position(userLocation)
                            .title(getString(R.string.mylocation))
                    )
                    marker?.showInfoWindow()
                }
            }
    }


    private fun isPermissionGranted() : Boolean {
        return ContextCompat.checkSelfPermission(
            requireActivity(),
            Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestLocationPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) ==
            PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) ==
            PackageManager.PERMISSION_GRANTED) {
            map.isMyLocationEnabled = true

        }

        else {
            this.requestPermissions(
                arrayOf<String>(Manifest.permission.ACCESS_FINE_LOCATION),
                PERMISSION_CODE_LOCATION_REQUEST
            )
        }
    }



    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.isNotEmpty() && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
            getUserLocation()
        } else {
            showRationale()
        }
    }

    private fun showRationale() {
        if (
            ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
        ) {
            AlertDialog.Builder(requireActivity())
                .setTitle(R.string.locationpermission)
                .setMessage(R.string.permission_denied_explanation)
                .setPositiveButton("OK"){ _, _, ->
                    requestLocationPermission()
                }
                .create()
                .show()

        } else {
            requestLocationPermission()
        }
    }

    private fun PreparePoiClick(map: GoogleMap) {
        map.setOnPoiClickListener { poi ->
            map.clear()
            marker = map.addMarker(MarkerOptions()
                .position(poi.latLng)
                .title(poi.name)
            )
            marker?.showInfoWindow()


            map.animateCamera(CameraUpdateFactory.newLatLng(poi.latLng))
        }
    }

    private fun PrepareMapLongClick(map: GoogleMap) {
        map.setOnMapLongClickListener { latLng ->


            val snippet = String.format(
                Locale.getDefault(),
                "Lat: %1$.7f, Long: %2$.7f",
                latLng.latitude,
                latLng.longitude
            )
            map.clear()
            marker = map.addMarker(
                MarkerOptions()
                    .position(latLng)
                    .title(getString(R.string.dropped_pin))
                    .snippet(snippet)
            )

            marker?.showInfoWindow()
            map.animateCamera(CameraUpdateFactory.newLatLng(latLng))
        }
    }


    private val fusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(requireActivity())
    }
}
