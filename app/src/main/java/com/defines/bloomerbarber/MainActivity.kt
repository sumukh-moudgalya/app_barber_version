package com.defines.bloomerbarber

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.etebarian.meowbottomnavigation.MeowBottomNavigation
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MainActivity : AppCompatActivity() {
    companion object{
        var currentWallet:Wallet?=Wallet()
    }
    private lateinit var bottomNavigation:MeowBottomNavigation
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        window.statusBarColor = ContextCompat.getColor(this, R.color.primary)
        setContentView(R.layout.activity_main)
        addFragment(HomeFragment.newInstance(),true)
        fetchWalletBarber()

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

    private fun fetchWalletBarber() {
        val uid= FirebaseAuth.getInstance().currentUser?.uid
        val ref= FirebaseDatabase.getInstance().getReference("wallet_barber/$uid")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                currentWallet=snapshot.getValue(Wallet::class.java)


            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
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