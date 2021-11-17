package com.defines.bloomerbarber

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.completed_appointment_list.view.*
import kotlinx.android.synthetic.main.confirmed_appointment_list.view.*
import kotlinx.android.synthetic.main.confirmed_appointment_list.view.confirmed_appointment_service_name
import kotlinx.android.synthetic.main.pending_appointment_list.view.*
import java.io.Serializable


class OrdersFragment : Fragment() {
    // TODO: Rename and change types of parameters


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val myFrag = inflater.inflate(R.layout.fragment_orders, container, false)
        val emptyPendingListText: TextView =
            myFrag.findViewById(R.id.fragment_orders_pending_empty_text)
        val pendingRecyclerView: RecyclerView =
            myFrag.findViewById(R.id.pending_confirmation_recycler_view)
        val confirmedRecyclerView: RecyclerView =
            myFrag.findViewById(R.id.fragment_orders_confirmed_order_recycler_view)
        val emptyConfirmedText: TextView =
            myFrag.findViewById(R.id.fragment_orders_confirmed_orders_empty_text)
        val completedRecyclerView: RecyclerView =
            myFrag.findViewById(R.id.fragment_orders_completed_orders_recycler_view)
        val emptyRecyclerText: TextView =
            myFrag.findViewById(R.id.fragment_orders_empty_confirmed_text)




        fetchPendingOrdersList(activity, pendingRecyclerView, emptyPendingListText)
        fetchConfirmedOrders(activity, confirmedRecyclerView, emptyConfirmedText)
        fetchCompletedOrders(activity, completedRecyclerView, emptyRecyclerText)
        return myFrag
    }

    private fun fetchCompletedOrders(
        activity: FragmentActivity?,
        completedRecyclerView: RecyclerView,
        emptyRecyclerText: TextView
    ) {
        val uid = FirebaseAuth.getInstance().currentUser!!.uid
        val completedPathCheck=FirebaseDatabase.getInstance().getReference("barber_orders/$uid/")
        completedPathCheck.addListenerForSingleValueEvent(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.hasChild("completed")){
                    emptyRecyclerText.visibility = View.GONE
                    val ref =
                        FirebaseDatabase.getInstance().getReference("barber_orders/$uid/completed/")
                    ref.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(p0: DataSnapshot) {

                            val adapter = GroupAdapter<GroupieViewHolder>()
                            p0.children.forEach {
                                val bookingElement = it.getValue(BookingElement::class.java)
                                if (bookingElement != null) {
                                    adapter.add(CompletedAppointmentAdder(bookingElement, activity))
                                }

                            }
                            completedRecyclerView.adapter = adapter

                        }

                        override fun onCancelled(error: DatabaseError) {
                            Toast.makeText(activity, "Server Error", Toast.LENGTH_SHORT).show()
                        }

                    })

                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    private fun fetchConfirmedOrders(
        activity: FragmentActivity?,
        confirmedRecyclerView: RecyclerView,
        emptyConfirmedText: TextView
    ) {
        val uid = FirebaseAuth.getInstance().currentUser!!.uid
        val confirmedPathCheck = FirebaseDatabase.getInstance().getReference("barber_orders/$uid/")
        confirmedPathCheck.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.hasChild("confirmed")) {
                    emptyConfirmedText.visibility = View.GONE
                    val ref =
                        FirebaseDatabase.getInstance().getReference("barber_orders/$uid/confirmed/")
                    ref.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(p0: DataSnapshot) {

                            val adapter = GroupAdapter<GroupieViewHolder>()
                            p0.children.forEach {
                                val bookingElement = it.getValue(BookingElement::class.java)
                                if (bookingElement != null) {
                                    adapter.add(ConfirmedAppointmentAdder(bookingElement, activity))
                                }

                            }
                            confirmedRecyclerView.adapter = adapter

                        }

                        override fun onCancelled(error: DatabaseError) {
                            Toast.makeText(activity, "Server Error", Toast.LENGTH_SHORT).show()
                        }

                    })
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })

    }

    private fun fetchPendingOrdersList(
        activity: FragmentActivity?,
        pendingRecyclerView: RecyclerView,
        emptyPendingListText: TextView
    ) {
        val uid = FirebaseAuth.getInstance().currentUser!!.uid
        val pendingPathCheck = FirebaseDatabase.getInstance().getReference("barber_orders/$uid/")
        pendingPathCheck.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun equals(other: Any?): Boolean {
                return super.equals(other)
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.hasChild("pending_confirmation")) {
                    emptyPendingListText.visibility = View.GONE
                    val ref =
                        FirebaseDatabase.getInstance()
                            .getReference("barber_orders/$uid/pending_confirmation/")
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
                } else {
                    pendingRecyclerView.visibility = View.GONE
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })


    }

    companion object {

        @JvmStatic
        fun newInstance() =
            OrdersFragment().apply {
                arguments = Bundle().apply {}
            }
    }
    class CompletedAppointmentAdder(
        val bookingElement: BookingElement,
        var activity: FragmentActivity?
    ):Item<GroupieViewHolder>(){
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

            viewHolder.itemView.completed_appointment_service_name.text = serviceName
            viewHolder.itemView.completed_appointment_list_cost.text = "₹" + cost.toString()
            viewHolder.itemView.completed_appointment_total_time.text = time.toString() + "mins"
            viewHolder.itemView.completed_appointment_month_text.text = monthMap[month.toInt()]
            viewHolder.itemView.completed_appointment_day.text = day
            val hour = bookingElement.timeSlot.subSequence(0, 2).toString()
            var minute = bookingElement.timeSlot.subSequence(2, 4).toString()
            if (hour.toInt() >= 12) {
                minute += " pm"
            } else {
                minute += " am"
            }
            viewHolder.itemView.completed_appointment_list_time_slot.text = hour + ":" + minute
            viewHolder.itemView.completed_appointment_see_more_button.setOnClickListener {
                val intent = Intent(activity, BookingAppointmentDetailsActivity::class.java)
                intent.putExtra(MainActivity.BOOKING_ELEMENT_KEY, bookingElement as Serializable)
                activity!!.startActivity(intent)
            }
        }

        override fun getLayout(): Int {
            return R.layout.completed_appointment_list
        }

    }
    class PendingAppointmentAdder(
        val bookingElement: BookingElement,
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
}

class ConfirmedAppointmentAdder(
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
        viewHolder.itemView.confirmed_appointment_service_name.text = serviceName
        viewHolder.itemView.confirmed_appointment_list_cost.text = "₹" + cost.toString()
        viewHolder.itemView.confirmed_appointment_total_time.text = time.toString() + "mins"
        viewHolder.itemView.confirmed_appointment_month_text.text = monthMap[month.toInt()]
        viewHolder.itemView.confirmed_appointment_day.text = day
        val hour = bookingElement.timeSlot.subSequence(0, 2).toString()
        var minute = bookingElement.timeSlot.subSequence(2, 4).toString()
        if (hour.toInt() >= 12) {
            minute += " pm"
        } else {
            minute += " am"
        }
        viewHolder.itemView.confirmed_appointment_list_time_slot.text = hour + ":" + minute
        viewHolder.itemView.confirmed_appointment_see_more_button.setOnClickListener {
            val intent = Intent(activity, BookingAppointmentDetailsActivity::class.java)
            intent.putExtra(MainActivity.BOOKING_ELEMENT_KEY, bookingElement as Serializable)
            activity!!.startActivity(intent)
        }

    }

    override fun getLayout(): Int {
        return R.layout.confirmed_appointment_list
    }

}
