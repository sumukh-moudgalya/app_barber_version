package com.defines.bloomerbarber

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter


class OnboardingActivityAdapter(
    fragmentActivity: FragmentActivity,
    private val context: Context
) :
    FragmentStateAdapter(fragmentActivity) {

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            else -> OnBoardingFragment.newInstance(
                "LOCATION PERMISSION",
                "PRESS ON ALLOW FOR GIVING LOCATION PERMISSION TO THE APPLICATION",
                "Location",
                "#FF5C58"
            )


        }
    }

    override fun getItemCount(): Int {
        return 1
    }
}