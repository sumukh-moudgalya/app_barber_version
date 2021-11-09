package com.defines.bloomerbarber

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
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
            }
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
                        timeStamp
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
}