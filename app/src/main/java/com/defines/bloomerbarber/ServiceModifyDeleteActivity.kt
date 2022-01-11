package com.defines.bloomerbarber

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
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

        serviceName.setText(service.name)
        serviceCost.setText(service.cost.toString())
        serviceTime.setText(service.avgTime.toString())
        serviceDescription.setText(service.description)

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
                        hashMapOf()
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