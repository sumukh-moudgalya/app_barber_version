package com.defines.bloomerbarber

import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
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
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.appointments_today_list.view.*
import kotlinx.android.synthetic.main.pending_appointment_list.view.*
import java.io.Serializable
import java.text.SimpleDateFormat

private val TAG = "HomeFragment"

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
        val todayRecyclerView: RecyclerView =
            myfrag.findViewById(R.id.fragment_home_ongoing_order_recycler_view)
        val pendingRecyclerView: RecyclerView =
            myfrag.findViewById(R.id.fragment_home_pending_order_recycler_view)
        fetchTodayAppointment(this, todayRecyclerView)
        fetchlocation(myfrag)
        fetchlocation(myfrag)
        fetchPendingOrders(this,pendingRecyclerView)
        checkForcompletedOrders(activity)

        val profile_pic_view: ImageView = myfrag.findViewById(R.id.fragment_home_profile_pic)
        val wallet_nav: ImageView = myfrag.findViewById(R.id.fragment_home_wallet_nav_button)
        val user = FirebaseAuth.getInstance().currentUser

        Glide.with(this).load(user!!.photoUrl.toString()).circleCrop().into(profile_pic_view)

        val search_button: SearchView = myfrag.findViewById(R.id.fragment_home_search_view)
        search_button.setOnClickListener {
            val intent = Intent(activity, SearchActivity::class.java)
            startActivity(intent)

        }
        wallet_nav.setOnClickListener {
            val intent = Intent(activity, BarberWalletActivity::class.java)
            startActivity(intent)
        }
        // Inflate the layout for this fragment
        return myfrag
    }
    private fun checkForcompletedOrders(activity: FragmentActivity?) {
        val uid = FirebaseAuth.getInstance().currentUser!!.uid
        val ref = FirebaseDatabase.getInstance().getReference("barber_orders/$uid/confirmed")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.forEach {
                    val bookingElement=it.getValue(BookingElement::class.java)
                    if(bookingElement!=null){
                        val hour = bookingElement.timeSlot.subSequence(0, 2).toString()
                        var minute = bookingElement.timeSlot.subSequence(2, 4).toString()

                        val date=bookingElement.date
                        val time=hour+":"+minute
                        val timeSlot=date +" "+time
                        val dateFormatter= SimpleDateFormat("MM/dd/yyyy hh:mm")
                        val timeSlotTimeRaw=dateFormatter.parse(timeSlot)
                        Log.d(TAG,"Time=${timeSlotTimeRaw.getTime()/1000} date=$timeSlot")
                        val timeSlotTimeStamp=timeSlotTimeRaw.getTime()/1000
                        val completeTimeStamp=timeSlotTimeStamp+(bookingElement.total_time*60)+(10*60)
                        val currentTime=System.currentTimeMillis()/1000
                        Log.d(TAG,"currTime====$currentTime")
                        if(currentTime>=completeTimeStamp){
                            ref.child("${bookingElement.timeStamp}").removeValue().addOnSuccessListener {
                                val refUserPending=FirebaseDatabase.getInstance().getReference("users_orders/${bookingElement.user_uid}/confirmed/${bookingElement.timeStamp}")
                                refUserPending.removeValue()
                                bookingElement.orderStatus="completed"
                                val refCompletedBarber=FirebaseDatabase.getInstance().getReference("barber_orders/$uid/completed/{${bookingElement.timeStamp}")
                                refCompletedBarber.setValue(bookingElement).addOnSuccessListener {
                                    Log.d(TAG,"refCompletedBarber has been updated")
                                    val refUserCompleted=FirebaseDatabase.getInstance().getReference("users_orders/${bookingElement.user_uid}/completed/${bookingElement.timeStamp}")
                                    refUserCompleted.setValue(bookingElement).addOnSuccessListener {
                                        Log.d(TAG,"refUserCompleted has been updated")
                                    }
                                }
                            }


                        }

                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(activity, "Error in database", Toast.LENGTH_SHORT).show()

            }

        })

    }

    private fun fetchPendingOrders(myfrag: HomeFragment, pendingRecyclerView: RecyclerView) {
        val uid=FirebaseAuth.getInstance().currentUser!!.uid
        val ref=FirebaseDatabase.getInstance().getReference("barber_orders/$uid/pending_confirmation/")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {

                val adapter = GroupAdapter<GroupieViewHolder>()
                p0.children.forEach {
                    val bookingElement = it.getValue(BookingElement::class.java)
                    if (bookingElement != null) {
                        adapter.add(PendingAppointmentAdder(bookingElement, activity))

                    }

                }
                pendingRecyclerView.adapter = adapter

            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(activity, "Server Error", Toast.LENGTH_SHORT).show()
            }

        })
    }

    private fun fetchTodayAppointment(homeFragment: HomeFragment, todayRecyclerView: RecyclerView) {
        val uid = FirebaseAuth.getInstance().currentUser!!.uid
        val c = Calendar.getInstance()


        val sf: SimpleDateFormat = SimpleDateFormat("MM/dd/yyyy")
        val currDate = sf.format(Date())
        Log.d(TAG, "date=$currDate")
        val ref = FirebaseDatabase.getInstance().getReference("barber_orders/$uid/confirmed/")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {

                val adapter = GroupAdapter<GroupieViewHolder>()
                p0.children.forEach {
                    val bookingElement = it.getValue(BookingElement::class.java)
                    if (bookingElement != null) {
                        if (bookingElement.date == currDate) {
                            Log.d(TAG, "${bookingElement.date}")
                            adapter.add(TodayAppointmentAdder(bookingElement, activity))
                        }

                    }

                }
                todayRecyclerView.adapter = adapter

            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(activity, "Server Error", Toast.LENGTH_SHORT).show()
            }

        })

    }
    class PendingAppointmentAdder(
        var bookingElement: BookingElement,
        var activity: FragmentActivity?
    ) : Item<GroupieViewHolder>(){
        override fun bind(viewHolder: GroupieViewHolder, position: Int) {
            val serviceName = bookingElement.order_selected[0].name
            val cost = bookingElement.total_cost
            val time = bookingElement.total_time
            val monthMap = mapOf(
                1 to "January",
                2 to "February",
                3 to "March",
                4 to "April",
                5 to "May",
                6 to "June",
                7 to "July",
                8 to "August",
                9 to "September",
                10 to "October",
                11 to "November",
                12 to "December"
            )
            val array_date = bookingElement.date.split("/").toTypedArray()
            val month = array_date[0]
            val day = array_date[1]
            val year = array_date[2]

            viewHolder.itemView.pending_appointment_service_name.text = serviceName
            viewHolder.itemView.pending_appointment_list_cost.text = "₹" + cost.toString()
            viewHolder.itemView.pending_appointment_total_time.text = time.toString() + "mins"
            viewHolder.itemView.pending_appointment_month_text.text = monthMap[month.toInt()]
            viewHolder.itemView.pending_appointment_day.text = day
            val hour = bookingElement.timeSlot.subSequence(0, 2).toString()
            var minute = bookingElement.timeSlot.subSequence(2, 4).toString()
            if (hour.toInt() >= 12) {
                minute += " pm"
            } else {
                minute += " am"
            }
            viewHolder.itemView.pending_appointment_list_time_slot.text = hour + ":" + minute
            viewHolder.itemView.pending_appointment_see_more_button.setOnClickListener {
                val intent = Intent(activity, BookingAppointmentDetailsActivity::class.java)
                intent.putExtra(MainActivity.BOOKING_ELEMENT_KEY, bookingElement as Serializable)
                activity!!.startActivity(intent)
            }
        }

        override fun getLayout(): Int {
            return R.layout.pending_appointment_list
        }

    }
    class TodayAppointmentAdder(
        var bookingElement: BookingElement,
        var activity: FragmentActivity?
    ) : Item<GroupieViewHolder>() {
        override fun bind(viewHolder: GroupieViewHolder, position: Int) {
            val serviceName = bookingElement.order_selected[0].name
            val cost = bookingElement.total_cost
            val time = bookingElement.total_time
            val monthMap = mapOf(
                1 to "January",
                2 to "February",
                3 to "March",
                4 to "April",
                5 to "May",
                6 to "June",
                7 to "July",
                8 to "August",
                9 to "September",
                10 to "October",
                11 to "November",
                12 to "December"
            )
            val array_date = bookingElement.date.split("/").toTypedArray()
            val month = array_date[0]
            val day = array_date[1]
            val year = array_date[2]
            viewHolder.itemView.appointment_today_service_name.text = serviceName
            viewHolder.itemView.appointment_today_list_cost.text = "₹" + cost.toString()
            viewHolder.itemView.appointment_today_total_time.text = time.toString() + "mins"
            viewHolder.itemView.appointment_today_month_text.text = monthMap[month.toInt()]
            viewHolder.itemView.appointment_today_day.text = day
            val hour = bookingElement.timeSlot.subSequence(0, 2).toString()
            var minute = bookingElement.timeSlot.subSequence(2, 4).toString()
            if (hour.toInt() >= 12) {
                minute += " pm"
            } else {
                minute += " am"
            }
            viewHolder.itemView.appointment_today_list_time_slot.text = hour + ":" + minute
            viewHolder.itemView.appointment_today_see_more_button.setOnClickListener {
                val intent = Intent(activity, BookingAppointmentDetailsActivity::class.java)
                intent.putExtra(MainActivity.BOOKING_ELEMENT_KEY, bookingElement as Serializable)
                activity!!.startActivity(intent)
            }

        }

        override fun getLayout(): Int {
            return R.layout.appointments_today_list
        }

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