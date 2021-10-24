package com.defines.bloomerbarber
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.util.*
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth


class HomeFragment : Fragment() {




    var cityname = ""
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val myfrag = inflater.inflate(R.layout.fragment_home, container, false)

        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(myfrag.context)


        fetchlocation(myfrag)
        fetchlocation(myfrag)
        val profile_pic_view: ImageView = myfrag.findViewById(R.id.fragment_home_profile_pic)

        val user = FirebaseAuth.getInstance().currentUser

        Glide.with(this).load(user!!.photoUrl.toString()).circleCrop().into(profile_pic_view)

        val search_button: SearchView = myfrag.findViewById(R.id.fragment_home_search_view)
        search_button.setOnClickListener {
            val intent = Intent(activity, SearchActivity::class.java)
            startActivity(intent)

        }
        // Inflate the layout for this fragment
        return myfrag
    }

    private fun fetchlocation(myfrag: View) {

        val task = fusedLocationProviderClient.lastLocation
        if (ActivityCompat.checkSelfPermission(
                myfrag.context,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            )
            != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                myfrag.context,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this.requireActivity(),
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                101
            )
            return
        }

        task.addOnSuccessListener {
            if (it != null) {
                var geocoder = Geocoder(myfrag.context, Locale.getDefault())
                var adress = geocoder.getFromLocation(it.latitude, it.longitude, 1)
                cityname = adress[0].getAddressLine(0)
                var hello = myfrag.findViewById<TextView>(R.id.location_textview)
                hello.text = cityname
            }
        }

    }

    companion object {

        @JvmStatic
        fun newInstance() =
            HomeFragment().apply {
                arguments = Bundle().apply {}
            }
    }
}