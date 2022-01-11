package com.defines.bloomerbarber

import android.app.Dialog
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.etebarian.meowbottomnavigation.MeowBottomNavigation
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.io.Serializable
import java.text.SimpleDateFormat

private val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {
    companion object {
        var currentWallet: Wallet? = Wallet()
        val BOOKING_ELEMENT_KEY: String = "BOOKING_ELEMENT_KEY"
        val SERVICE_KEY: String = "SERVICE_KEY"
    }


    override fun onResume() {

//        recreate()
        super.onResume()
    }

    private lateinit var bottomNavigation: MeowBottomNavigation
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        window.statusBarColor = ContextCompat.getColor(this, R.color.primary)
        Log.d(TAG, "Mainctoasasdasdadadasdasdsss")
        setContentView(R.layout.activity_main)
        addFragment(HomeFragment.newInstance(), true)
        fetchWalletBarber()
        listenForIncomingOrderRequests(this)
        checkForcompletedOrders(this)
//        Connecting bottom navigation
        bottomNavigation = findViewById(R.id.bottom_navigation)
        bottomNavigation.show(3)

//        Adding bottom navigation menu items
        bottomNavigation.add(MeowBottomNavigation.Model(1, R.drawable.menu))
        bottomNavigation.add(MeowBottomNavigation.Model(2, R.drawable.analytics_icon))
        bottomNavigation.add(MeowBottomNavigation.Model(3, R.drawable.ic_baseline_home_24))
        bottomNavigation.add(MeowBottomNavigation.Model(4, R.drawable.orders))
        bottomNavigation.add(MeowBottomNavigation.Model(5, R.drawable.settings_icon))



        bottomNavigation.setOnShowListener {
            when (it.id) {
                4 -> {

                    replaceFragment(OrdersFragment.newInstance(), true)
                }
                3 -> {

                    replaceFragment(HomeFragment.newInstance(), true)
                }
                1 -> {

                    replaceFragment(ServicesManagerFragment.newInstance(), true)
                }
                2 -> {
                    replaceFragment(ReviewNavigatorFragment.newInstance(), true)
                }
                5 -> {
                    replaceFragment(SettingsFragment.newInstance(),true)
                }

            }
        }


    }

    private fun checkForcompletedOrders(mainActivity: MainActivity) {
        val uid = FirebaseAuth.getInstance().currentUser!!.uid
        val ref = FirebaseDatabase.getInstance().getReference("barber_orders/$uid/confirmed")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.forEach {
                    val bookingElement = it.getValue(BookingElement::class.java)
                    if (bookingElement != null) {
                        val hour = bookingElement.timeSlot.subSequence(0, 2).toString()
                        var minute = bookingElement.timeSlot.subSequence(2, 4).toString()

                        val date = bookingElement.date
                        val time = hour + ":" + minute
                        val timeSlot = date + " " + time
                        val dateFormatter = SimpleDateFormat("MM/dd/yyyy hh:mm")
                        val timeSlotTimeRaw = dateFormatter.parse(timeSlot)
                        Log.d(TAG, "Time=${timeSlotTimeRaw.getTime() / 1000} date=$timeSlot")
                        val timeSlotTimeStamp = timeSlotTimeRaw.getTime() / 1000
                        val completeTimeStamp =
                            timeSlotTimeStamp + (bookingElement.total_time * 60) + (10 * 60)
                        val currentTime = System.currentTimeMillis() / 1000
                        Log.d(TAG, "currTime====$currentTime")
                        if (currentTime >= completeTimeStamp) {
                            ref.child("${bookingElement.timeStamp}").removeValue()
                                .addOnSuccessListener {
                                    val refUserPending = FirebaseDatabase.getInstance()
                                        .getReference("users_orders/${bookingElement.user_uid}/confirmed/${bookingElement.timeStamp}")
                                    refUserPending.removeValue()
                                    bookingElement.orderStatus = "completed"
                                    val refCompletedBarber = FirebaseDatabase.getInstance()
                                        .getReference("barber_orders/$uid/completed/{${bookingElement.timeStamp}")
                                    refCompletedBarber.setValue(bookingElement)
                                        .addOnSuccessListener {
                                            Log.d(TAG, "refCompletedBarber has been updated")
                                            val refUserCompleted = FirebaseDatabase.getInstance()
                                                .getReference("users_orders/${bookingElement.user_uid}/completed/${bookingElement.timeStamp}")
                                            refUserCompleted.setValue(bookingElement)
                                                .addOnSuccessListener {
                                                    Log.d(TAG, "refUserCompleted has been updated")
                                                }
                                        }
                                }


                        }

                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(mainActivity, "Error in database", Toast.LENGTH_SHORT).show()

            }

        })

    }

    private fun listenForIncomingOrderRequests(mainActivity: MainActivity) {
        val uid = FirebaseAuth.getInstance().currentUser!!.uid
        val ref =
            FirebaseDatabase.getInstance().getReference("barber_orders/$uid/pending_confirmation/")
        ref.addChildEventListener(object : ChildEventListener {


            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val bookingElement = snapshot.getValue(BookingElement::class.java) ?: return
                val dialog = Dialog(mainActivity)
                dialog.setContentView(R.layout.incoming_appointment_request_layout)
                dialog.getWindow()!!.setBackgroundDrawable(ColorDrawable(0))
                val dismissButton: TextView =
                    dialog.findViewById(R.id.incoming_appointment_request_layout_dismiss_button)
                dismissButton.setOnClickListener {
                    dialog.cancel()
                }
                val requestTotal: TextView =
                    dialog.findViewById(R.id.incoming_appointment_request_layout_total_amount)
                val requestDate: TextView =
                    dialog.findViewById(R.id.incoming_appointment_request_layout_date)
                val requestTime: TextView =
                    dialog.findViewById(R.id.incoming_appointment_request_layout_time)
                val averageTime: TextView =
                    dialog.findViewById(R.id.incoming_appointment_request_layout_total_avg_time)
                val requestId: TextView =
                    dialog.findViewById(R.id.incoming_appointment_request_layout_request_id)

                requestTotal.text = "₹" + bookingElement.total_cost.toString()
                requestDate.text = bookingElement.date
                averageTime.text = bookingElement.total_time.toString() + " minutes"
                requestId.text = bookingElement.order_id
                val hour = bookingElement.timeSlot.subSequence(0, 2).toString()
                var minute = bookingElement.timeSlot.subSequence(2, 4).toString()
                if (hour.toInt() >= 12) {
                    minute += " pm"
                } else {
                    minute += " am"
                }

                requestTime.text = hour + " " + minute

                val navToDetailsOrder: TextView =
                    dialog.findViewById(R.id.incoming_appointment_request_layout_nav_button)

                navToDetailsOrder.setOnClickListener {
                    Log.d(TAG, "Nav button clicked")

                    val intent = Intent(mainActivity, BookingAppointmentDetailsActivity::class.java)
                    intent.putExtra(BOOKING_ELEMENT_KEY, bookingElement as Serializable)
                    startActivity(intent)
                    dialog.dismiss()
                }

                dialog.show()
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {

            }


            override fun onChildRemoved(snapshot: DataSnapshot) {

            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {

            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    private fun fetchWalletBarber() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        val ref = FirebaseDatabase.getInstance().getReference("wallet_barber/$uid")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                currentWallet = snapshot.getValue(Wallet::class.java)


            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    private fun replaceFragment(fragment: Fragment, isTransition: Boolean) {
        val fragmentTransition = supportFragmentManager.beginTransaction()
        fragmentTransition.replace(R.id.frame_layout, fragment)
            .addToBackStack(Fragment::class.java.simpleName).commit()
    }

    private fun addFragment(fragment: Fragment, isTransition: Boolean) {
        val fragmentTransition = supportFragmentManager.beginTransaction()
        fragmentTransition.replace(R.id.frame_layout, fragment)
            .addToBackStack(Fragment::class.java.simpleName).commit()


    }
}