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
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.categories_list.view.*

private val TAG="ServiceModifyDeleteActivity"
class ServiceModifyDeleteActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_service_modify_delete)
        val service=intent.extras!!.getSerializable(MainActivity.SERVICE_KEY) as ServiceOffered
        val serviceName: EditText =findViewById(R.id.activity_service_modify_delete_service_name)
        val serviceCost:EditText=findViewById(R.id.activity_service_modify_delete_service_cost)
        val serviceTime:EditText=findViewById(R.id.activity_service_modify_delete_service_time)
        val serviceDescription:EditText=findViewById(R.id.activity_service_modify_delete_service_description)
        val recyclerView : RecyclerView =findViewById((R.id.activity_service_modify_recyclerView))
        var categories=ArrayList<String>()

        serviceName.setText(service.name)
        serviceCost.setText(service.cost.toString())
        serviceTime.setText(service.avgTime.toString())
        serviceDescription.setText(service.description)
        categories=service.categories
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
                    adapter.add(categoryList(shop.toString(),categories))
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

        val deleteButton:TextView=findViewById(R.id.activity_service_modify_delete_del_button)
        val modifyButton:TextView=findViewById(R.id.activity_service_modify_delete_modify_button)
        deleteButton.setOnClickListener{
            val uid= FirebaseAuth.getInstance().currentUser!!.uid
            val ref=FirebaseDatabase.getInstance().getReference("services/$uid/${service.timeStamp}")
            ref.removeValue().addOnSuccessListener {
                val intent= Intent(this,ServiceSavingCompletionActivity::class.java)
                startActivity(intent)
                finish()
            }

        }

        modifyButton.setOnClickListener{
            val uid= FirebaseAuth.getInstance().currentUser!!.uid
            val ref=FirebaseDatabase.getInstance().getReference("services/$uid/${service.timeStamp}")

            if (serviceName.length()==0){
                Toast.makeText(this,"service name is empty", Toast.LENGTH_SHORT).show()
            }else if (serviceCost.length()==0){
                Toast.makeText(this,"cost is empty", Toast.LENGTH_SHORT).show()
            }else if(serviceTime.length()==0){
                Toast.makeText(this,"average time is empty", Toast.LENGTH_SHORT).show()
            }else if(serviceDescription.length()==0){
                Toast.makeText(this,"Description is empty", Toast.LENGTH_SHORT).show()
            }
            val name=serviceName.text.toString()
            var isCostInt=false
            var cost=0.0
            try{
                cost=serviceCost.text.toString().toDouble()
                isCostInt=true

            }catch (e: Exception){
                Toast.makeText(this,"Please enter the cost as a number in rupee", Toast.LENGTH_SHORT).show()

            }
            if (isCostInt){
                var isTimeInt=false
                var time=0
                try{
                    time=serviceTime.text.toString().toInt()
                    isTimeInt=true
                }catch(e:Exception){
                    Toast.makeText(this,"Please enter time in number of minutes required", Toast.LENGTH_SHORT).show()

                }

                if (isTimeInt){
                    val desc=serviceDescription.text.toString()

                    val service_to_be_added=ServiceOffered(
                        name,
                        cost,
                        time,
                        desc,
                        service.timeStamp,
                        categories
                    )

                    ref.setValue(service_to_be_added).addOnSuccessListener {
                        Log.d(TAG, "Finally the user is saved to database")
                        Toast.makeText(this,"Order saving successfully", Toast.LENGTH_SHORT).show()
                        val intent= Intent(this,ServiceSavingCompletionActivity::class.java)
                        startActivity(intent)
                        finish()
                    }.addOnFailureListener{
                        Log.d(TAG,"Failed to add{${it.message}")
                    }
                }
            }


        }


    }
}
class categoryList(val s: String,val category:ArrayList<String>) : Item<GroupieViewHolder>(){
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.category_checkbox.text=s
        if(s in category) viewHolder.itemView.category_checkbox.isChecked=true
        viewHolder.itemView.category_checkbox.setOnClickListener {
            Log.d("Adapter clicked","adapter clicked")
            viewHolder.itemView.constraint_category.performClick()

        }


    }

    override fun getLayout(): Int {
        return R.layout.categories_list
    }

}