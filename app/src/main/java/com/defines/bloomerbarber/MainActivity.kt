package com.defines.bloomerbarber

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.etebarian.meowbottomnavigation.MeowBottomNavigation

class MainActivity : AppCompatActivity() {
    private lateinit var bottomNavigation:MeowBottomNavigation
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        window.statusBarColor = ContextCompat.getColor(this, R.color.primary)
        setContentView(R.layout.activity_main)
        addFragment(HomeFragment.newInstance(),true)


//        Connecting bottom navigation
        bottomNavigation=findViewById(R.id.bottom_navigation)
        bottomNavigation.show(2)

//        Adding bottom navigation menu items
        bottomNavigation.add(MeowBottomNavigation.Model(1,R.drawable.menu))
        bottomNavigation.add(MeowBottomNavigation.Model(2,R.drawable.ic_baseline_home_24))
        bottomNavigation.add(MeowBottomNavigation.Model(3,R.drawable.orders))


        bottomNavigation.setOnShowListener{
            when(it.id){
                3->{

                    replaceFragment(OrdersFragment.newInstance(),true)
                }
                2->{

                    replaceFragment(HomeFragment.newInstance(),true)
                }
                1->{

                    replaceFragment(ServicesManagerFragment.newInstance(),true)
                }

            }
        }


    }

    private fun replaceFragment(fragment: Fragment, isTransition:Boolean){
        val fragmentTransition=supportFragmentManager.beginTransaction()
        fragmentTransition.replace(R.id.frame_layout,fragment).addToBackStack(Fragment::class.java.simpleName).commit()
    }
    private fun addFragment(fragment: Fragment, isTransition:Boolean){
        val fragmentTransition=supportFragmentManager.beginTransaction()
        fragmentTransition.replace(R.id.frame_layout,fragment).addToBackStack(Fragment::class.java.simpleName).commit()



    }
}