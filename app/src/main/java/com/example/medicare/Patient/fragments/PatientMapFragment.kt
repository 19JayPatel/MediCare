package com.example.medicare.Patient.fragments

import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.medicare.Patient.adapters.Hospital
import com.example.medicare.R
import com.example.medicare.databinding.CustomMarkerLayoutBinding
import com.example.medicare.databinding.FragmentPatientMapBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class PatientMapFragment : Fragment(), OnMapReadyCallback {

    private var _binding: FragmentPatientMapBinding? = null
    private val binding get() = _binding!!
    private lateinit var mMap: GoogleMap

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPatientMapBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mapFragment = childFragmentManager.findFragmentById(R.id.mapFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)

        setupHospitalRecycler()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Set initial location (e.g., Seattle)
        val seattle = LatLng(47.6062, -122.3321)
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(seattle, 14f))

        addCustomMarkers()
    }

    private fun addCustomMarkers() {
        val locations = listOf(
            LatLng(47.6082, -122.3351),
            LatLng(47.6042, -122.3301),
            LatLng(47.6102, -122.3401),
            LatLng(47.6022, -122.3251)
        )

        val markerBitmap = createCustomMarkerBitmap()

        locations.forEach { latLng ->
            mMap.addMarker(
                MarkerOptions()
                    .position(latLng)
                    .icon(BitmapDescriptorFactory.fromBitmap(markerBitmap))
            )
        }
    }

    private fun createCustomMarkerBitmap(): Bitmap {
        val markerBinding = CustomMarkerLayoutBinding.inflate(layoutInflater)
        markerBinding.root.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
        markerBinding.root.layout(0, 0, markerBinding.root.measuredWidth, markerBinding.root.measuredHeight)

        val bitmap = Bitmap.createBitmap(
            markerBinding.root.measuredWidth,
            markerBinding.root.measuredHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        markerBinding.root.draw(canvas)
        return bitmap
    }

    private fun setupHospitalRecycler() {
        val hospitals = arrayListOf(
            Hospital(
                "Golden Cardiology Center",
                "555 Bridge Street, Golden Gate",
                4.9,
                108,
                "2.5 km/40min",
                "Clinic",
                R.drawable.ic_hospital1
            ),
            Hospital(
                "Sunrise Health Clinic",
                "123 Oak Street, CA",
                5.0,
                58,
                "1.2 km/30min",
                "Hospital",
                R.drawable.ic_hospital2
            )
        )

        val homeFragment = PatientHomeFragment()
        val adapter = homeFragment.HospitalAdapter(hospitals) { hospital ->
            val location = when(hospital.name) {
                "Golden Cardiology Center" -> LatLng(47.6082, -122.3351)
                else -> LatLng(47.6042, -122.3301)
            }
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 15f))
        }

        binding.hospitalRecycler.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding.hospitalRecycler.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}