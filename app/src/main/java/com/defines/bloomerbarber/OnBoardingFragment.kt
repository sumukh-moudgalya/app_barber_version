package com.defines.bloomerbarber

import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.LottieImageAsset
import kotlinx.android.synthetic.main.fragment_on_boarding.view.*
import org.json.JSONObject

class OnBoardingFragment: Fragment(){
    private lateinit var title: String
    private lateinit var description: String
    private lateinit var backgroundColor: String
    private lateinit var imageResource :String
    private lateinit var tvTitle: AppCompatTextView
    private lateinit var tvDescription: AppCompatTextView
    private lateinit var image: LottieAnimationView
    private lateinit var layout: RelativeLayout
    private lateinit var mFakeStatusBar: View
    private lateinit var button : AppCompatButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            title = requireArguments().getString(ARG_PARAM1)!!
            description = requireArguments().getString(ARG_PARAM2)!!
            imageResource=requireArguments().getString(ARG_PARAM3)!!
            backgroundColor = requireArguments().getString(ARG_PARAM4)!!
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val rootLayout: View =
            inflater.inflate(R.layout.fragment_on_boarding, container, false)
        tvTitle = rootLayout.text_onboarding_title
        tvDescription = rootLayout.text_onboarding_description
        image = rootLayout.image_onboarding
        layout = rootLayout.layout_container
        button= rootLayout.text_onboarding_button
        button.setOnClickListener {
            if(imageResource=="Location"){
                if (ActivityCompat.checkSelfPermission(
                        requireContext(),
                        android.Manifest.permission.ACCESS_FINE_LOCATION
                    )
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        requireContext(),
                        android.Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    ActivityCompat.requestPermissions(
                        requireActivity(),
                        arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                        101
                    )


                }
                Toast.makeText(requireContext(),"Location Permision Granted.Click On finish",Toast.LENGTH_LONG).show()

            }
        }
        mFakeStatusBar = rootLayout.fake_statusbar_view
        tvTitle.text = title
        tvDescription.text = description
        layout.setBackgroundColor(Color.parseColor(backgroundColor))
        mFakeStatusBar.setBackgroundColor(Color.parseColor(backgroundColor))
        if(imageResource=="Location")
        image.setAnimation(R.raw.location)
        return rootLayout
    }

    companion object {
        // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
        private const val ARG_PARAM1 = "param1"
        private const val ARG_PARAM2 = "param2"
        private const val ARG_PARAM3 = "param3"
        private const val ARG_PARAM4 = "param4"
        fun newInstance(
            title: String?,
            description: String?,
            imageResource: String?,
            backgroundColor: String
        ): OnBoardingFragment {
            val fragment = OnBoardingFragment()
            val args = Bundle()
            args.putString(ARG_PARAM1, title)
            args.putString(ARG_PARAM2, description)
            args.putString(ARG_PARAM3, imageResource)
            args.putString(ARG_PARAM4, backgroundColor)
            fragment.arguments = args
            return fragment
        }
    }
}

