package com.defines.bloomerbarber

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.activity_permission.*

class Permission : AppCompatActivity() {
    private lateinit var mViewPager: ViewPager2
    private lateinit var textSkip: TextView
    private lateinit var textEnd: TextView
    private lateinit var btnNextStep: ImageButton



    override fun onCreate(savedInstanceState: Bundle?) {
        getSupportActionBar()?.hide()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_permission)
        mViewPager = viewPager
        textSkip = text_skip
        textEnd = text_end
        btnNextStep = btn_next_step
        mViewPager.adapter = OnboardingActivityAdapter(this, this)
        mViewPager.offscreenPageLimit = 1
        TabLayoutMediator(pageIndicator, mViewPager) { _, _ -> }.attach()
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                if (position == 0) {
                    btnNextStep.visibility = View.GONE
                    textEnd.visibility = View.VISIBLE
                    textSkip.visibility = View.GONE
                } else {
                    btnNextStep.visibility = View.VISIBLE
                    textEnd.visibility = View.GONE
                    textSkip.visibility = View.VISIBLE
                }
            }

            override fun onPageScrolled(arg0: Int, arg1: Float, arg2: Int) {}
            override fun onPageScrollStateChanged(arg0: Int) {}
        })



        textSkip.setOnClickListener {
            val mainIntent = Intent(this, ShopAdderActivity::class.java)
            startActivity(mainIntent)
            finish()

        }

        textEnd.setOnClickListener {
            val mainIntent = Intent(this, ShopAdderActivity::class.java)
            startActivity(mainIntent)
            finish()
        }

        val btnNextStep: ImageButton = btn_next_step
        btnNextStep.setOnClickListener {
            if (getItem() > mViewPager.childCount) {
                finish()
            } else {
                mViewPager.setCurrentItem(getItem() + 1, true)
            }
        }
    }

    private fun getItem(): Int {
        return mViewPager.currentItem
    }
}