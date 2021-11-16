package com.defines.bloomerbarber

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.defines.bloomerbarber.MainActivity.Companion.BOOKING_ELEMENT_KEY
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.cart_item_list_details_page.view.*
import java.util.ArrayList
import java.util.HashMap

var totalCost=0.0
var totalTime=0.0
private val TAG="BookingAppointmentDetailsActivity"
var count: HashMap<String, Int> = HashMap<String, Int>()

class BookingAppointmentDetailsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_booking_appointment_details)
        supportActionBar?.hide()
        val bookingElement:BookingElement=intent.extras!!.getSerializable(BOOKING_ELEMENT_KEY) as BookingElement
        val servicesSelectedRecyclerView: RecyclerView=findViewById(R.id.activity_booking_appointment_details_cart_recycler_view)
        val array_service=bookingElement.order_selected
        val totalTimeText: TextView =findViewById(R.id.activity_booking_appointment_details_total_time)
        val costBeforeTextText:TextView =findViewById(R.id.activity_booking_appointment_details_cost_before_tax)
        val finalCostText:TextView=findViewById(R.id.activity_booking_appointment_details_final_cost)
        val backButton:ImageView=findViewById(R.id.activity_booking_appointment_details_back_button)
        val confirmAppointment:TextView=findViewById(R.id.activity_booking_appointment_details_confirm_button)
        val confirmCardView: CardView=findViewById(R.id.activity_booking_appointment_details_confrim_cancel_cardview)
        val orderStatusText:TextView=findViewById(R.id.activity_booking_appointment_details_status_text)
        if(bookingElement.orderStatus=="Confirmed"){
            confirmCardView.visibility = View.GONE
            orderStatusText.text="Appointment Confirmed"
            orderStatusText.setTextColor(ContextCompat.getColor(this,R.color.white))
            orderStatusText.background=ContextCompat.getDrawable(this, R.drawable.confirmed_back_ground)
        }else if(bookingElement.orderStatus=="completed"){
            confirmCardView.visibility = View.GONE
            orderStatusText.text="Appointment Completed"
            orderStatusText.setTextColor(ContextCompat.getColor(this,R.color.white))
            orderStatusText.background=ContextCompat.getDrawable(this, R.drawable.add_back_non_empty)
        }

        confirmAppointment.setOnClickListener{
            val uid= FirebaseAuth.getInstance().currentUser!!.uid
            val refDelBarber=FirebaseDatabase.getInstance().getReference("/barber_orders/$uid/pending_confirmation/${bookingElement.timeStamp}")
            Toast.makeText(this,"clicked",Toast.LENGTH_SHORT).show()
            refDelBarber.removeValue().addOnSuccessListener {
                Log.d(TAG,"removed value from the ref")
            }.addOnFailureListener {
                Log.d(TAG,"Some error in database")
            }

            val refDelUser=FirebaseDatabase.getInstance().getReference("users_orders/${bookingElement.user_uid}/pending_confirmation/${bookingElement.timeStamp}")
            refDelUser.removeValue().addOnSuccessListener {
                Log.d(TAG,"removed value from the user ref")
            }.addOnFailureListener {
                Log.d(TAG,"Some error in database")
            }
            bookingElement.orderStatus="Confirmed"
            val refAddBarber=FirebaseDatabase.getInstance().getReference("/barber_orders/$uid/confirmed/${bookingElement.timeStamp}")
            val refAddUser=FirebaseDatabase.getInstance().getReference("users_orders/${bookingElement.user_uid}/confirmed/${bookingElement.timeStamp}")
            refAddBarber.setValue(bookingElement).addOnSuccessListener {
                Log.d(TAG,"Value has been moved to confirmed path")
            }.addOnFailureListener {
                Log.d(TAG,"Failure moving data")
            }
            refAddUser.setValue(bookingElement).addOnSuccessListener {
                Log.d(TAG,"Value has been moved to confirmed user path")
                val intent= Intent(this,AppointmentConfirmationActivity::class.java)
                startActivity(intent)
                finish()
            }.addOnFailureListener {
                Log.d(TAG,"Failure moving data")
            }



        }

        totalCost=bookingElement.total_cost
        totalTime=bookingElement.total_time

        backButton.setOnClickListener {
           onBackPressed()
        }

        for(x in array_service){
            if(x.name in count){
                count[x.name]=count[x.name]!!+1
            }else{
                count[x.name]=1
            }
        }

        val added_service = ArrayList<String>()

        val adapter = GroupAdapter<GroupieViewHolder>()


        for ( x in array_service){
            if (x.name !in added_service){

                added_service.add(x.name)
                adapter.add(ServiceAdder(x,count[x.name]!!))
            }
        }
        val hour=(totalTime/60).toInt().toString()
        val minute=(totalTime%60).toInt().toString()
        if(hour=="0"){
            totalTimeText.text=minute+" min"
        }else{
            totalTimeText.text=hour+" Hour "+minute+" min"
        }
        costBeforeTextText.text= "₹"+totalCost.toString()
        finalCostText.text="₹"+totalCost.toString()


        servicesSelectedRecyclerView.adapter=adapter
//      Updating order details
        val appointmentId:TextView=findViewById(R.id.activity_booking_appointment_details_order_id)
        val customerNameText:TextView=findViewById(R.id.activity_booking_appointment_details_customer_name)
        val appointmentDate:TextView=findViewById(R.id.activity_booking_appointment_details_order_date)
        val appointmentTime:TextView=findViewById(R.id.activity_booking_appointment_details_appointment_time)
        val appointmentStatus:TextView=findViewById(R.id.activity_booking_appointment_details_appointment_status)
        val paymentModeText:TextView=findViewById(R.id.activity_booking_appointment_details_payment_mode)
        val appointmentTotal:TextView=findViewById(R.id.activity_booking_appointment_details_appointment_total_cost)

        appointmentId.text=bookingElement.order_id
        val cusUid=bookingElement.user_uid
        val refCusName=FirebaseDatabase.getInstance().getReference("users/$cusUid/username")

        refCusName.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                customerNameText.text=snapshot.getValue(String::class.java)!!
                Log.d(TAG,"lul")

            }

            override fun onCancelled(error: DatabaseError) {
                customerNameText.text="error fetching data"
            }
        })

        appointmentDate.text=bookingElement.date
        val hourtemp=bookingElement.timeSlot.subSequence(0,2).toString()
        var minutetemp=bookingElement.timeSlot.subSequence(2,4).toString()
        if(hourtemp.toInt() >=12){
            minutetemp+=" pm"
        }else{
            minutetemp+=" am"
        }
        appointmentTime.text=hourtemp+":"+minutetemp
        appointmentStatus.text="Confirmation Pending"
        paymentModeText.text="Online"
        appointmentTotal.text= "₹"+totalCost.toString()



    }

    class ServiceAdder(var x: ServiceOffered, var i: Int) : Item<GroupieViewHolder>(){
        override fun bind(viewHolder: GroupieViewHolder, position: Int) {
            viewHolder.itemView.cart_item_list_details_page_service_name.text=x.name
            viewHolder.itemView.cart_item_list_details_page_cost.text="₹"+x.cost.toString()
            viewHolder.itemView.cart_item_list_details_page_description.text=x.description
            viewHolder.itemView.cart_item_list_details_page_time.text=x.avgTime.toString()+"mins"
            viewHolder.itemView.cart_item_list_details_page_cost_service.text=(x.cost*i.toDouble()).toString()
            viewHolder.itemView.cart_item_list_details_page_number_item.text=i.toString()


        }

        override fun getLayout(): Int {
            return R.layout.cart_item_list_details_page
        }

    }
}