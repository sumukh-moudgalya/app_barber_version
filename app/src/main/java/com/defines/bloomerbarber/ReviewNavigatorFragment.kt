package com.defines.bloomerbarber

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.ViewPager
import com.defines.bloomerbarber.Adapter.SectionPagerAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener

class ReviewNavigatorFragment : Fragment() {
    var myFragment: View? = null
    var viewPager: ViewPager? = null
    var tabLayout: TabLayout? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        myFragment = inflater.inflate(R.layout.fragment_review_navigator, container, false)
        viewPager = myFragment!!.findViewById(R.id.viewPager)
        tabLayout = myFragment!!.findViewById(R.id.tabLayout)
        return myFragment
    }

    //Call onActivity Create method
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setUpViewPager(viewPager)
        tabLayout!!.setupWithViewPager(viewPager)
        tabLayout!!.addOnTabSelectedListener(object : OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {}
            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
    }

    private fun setUpViewPager(viewPager: ViewPager?) {
        val adapter = SectionPagerAdapter(childFragmentManager)
        adapter.addFragment(ReviewFragment(), "Shop Reviews")
        adapter.addFragment(ServiceReviewFragment(), "Service Reviews")

        viewPager!!.adapter = adapter
    }

    companion object {

        @JvmStatic
        fun newInstance() =
            ReviewNavigatorFragment().apply {
                arguments = Bundle().apply {}
            }
    }
}
