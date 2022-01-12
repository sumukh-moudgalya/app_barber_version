package com.defines.bloomerbarber

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.categories_list.view.*
import java.io.Serializable

private val TAG="ServiceAdderActivity"
class ServiceAdderActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        getSupportActionBar()?.hide()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_service_adder)
        val service_name: EditText =findViewById(R.id.activity_service_adder_service_name)
        val cost_service:EditText=findViewById(R.id.activity_service_adder_cost)
        val average_time:EditText=findViewById(R.id.activity_service_adder_average_time)
        val description:EditText=findViewById(R.id.activity_service_adder_description)
        val save_service_button: TextView =findViewById(R.id.activity_service_adder_save_button)
        val recyclerView : RecyclerView=findViewById((R.id.activity_service_adder_recyclerView))
 val categories=ArrayList<String>()
        val uid = FirebaseAuth.getInstance().currentUser!!.uid
        val ref =
            FirebaseDatabase.getInstance().getReference("/shop_info/${uid}/categories")
        Log.d("PreviousTest", "fetch_users")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                val adapter = GroupAdapter<GroupieViewHolder>()

                p0.children.forEach {
                    Log.d("PreviousTest", "fetch_users${it.toString()}")
                    val shop = it.getValue()
                    adapter.add(categoryList(shop.toString()))
                    if (shop != null) {
                        Log.d("PreviousTests", "${shop}")
                    } else {

                    }


                }


                adapter.setOnItemClickListener { item, view ->
                    val it=item as categoryList
                    Log.d("Adapter clicked","adapter ${it.s} clicked")
                    if(view.category_checkbox.isChecked) categories.add(it.s)
                    else if(it.s in categories && !view.category_checkbox.isChecked) categories.remove(it.s)

                }

                recyclerView.adapter = adapter


            }

            override fun onCancelled(error: DatabaseError) {


            }
        })
        auth= Firebase.auth

        save_service_button.setOnClickListener{
            val user=auth.currentUser
            val uid=user!!.uid
            val timeStamp=(System.currentTimeMillis()/1000).toString()
            var ref=FirebaseDatabase.getInstance().getReference("services/$uid")




            ref=FirebaseDatabase.getInstance().getReference("services/$uid/$timeStamp")
            if (service_name.length()==0){
                Toast.makeText(this,"service name is empty",Toast.LENGTH_SHORT).show()
            }else if (cost_service.length()==0){
                Toast.makeText(this,"cost is empty",Toast.LENGTH_SHORT).show()
            }else if(average_time.length()==0){
                Toast.makeText(this,"average time is empty",Toast.LENGTH_SHORT).show()
            }else if(description.length()==0){
                Toast.makeText(this,"Description is empty",Toast.LENGTH_SHORT).show()
            }else if(categories.isEmpty())
                Toast.makeText(this,"Select least One Category",Toast.LENGTH_SHORT).show()
            val name=service_name.text.toString()
            var isCostInt=false
            var cost=0.0
            try{
                cost=cost_service.text.toString().toDouble()
                isCostInt=true

            }catch (e: Exception){
                Toast.makeText(this,"Please enter the cost as a number in rupee",Toast.LENGTH_SHORT).show()

            }
            if (isCostInt){
                var isTimeInt=false
                var time=0
                try{
                     time=average_time.text.toString().toInt()
                    isTimeInt=true
                }catch(e:Exception){
                    Toast.makeText(this,"Please enter time in number of minutes required",Toast.LENGTH_SHORT).show()

                }

                if (isTimeInt){
                    val desc=description.text.toString()

                    val service_to_be_added=ServiceOffered(
                        name,
                        cost,
                        time,
                        desc,
                        timeStamp,
                        categories
                    )

                    ref.setValue(service_to_be_added).addOnSuccessListener {
                        Log.d(TAG, "Finally the user is saved to database")
                        Toast.makeText(this,"Order saving successfully",Toast.LENGTH_SHORT).show()
                        val intent= Intent(this,ServiceSavingCompletionActivity::class.java)
                        startActivity(intent)
                    }.addOnFailureListener{
                        Log.d(TAG,"Failed to add{${it.message}")
                    }
                }
            }





        }

    }
    class categoryList(val s: String) : Item<GroupieViewHolder>(){
        override fun bind(viewHolder: GroupieViewHolder, position: Int) {
            viewHolder.itemView.category_checkbox.text=s
            viewHolder.itemView.category_checkbox.setOnClickListener {
                Log.d("Adapter clicked","adapter clicked")
                viewHolder.itemView.constraint_category.performClick()

            }


        }

        override fun getLayout(): Int {
            return R.layout.categories_list
        }

    }


}