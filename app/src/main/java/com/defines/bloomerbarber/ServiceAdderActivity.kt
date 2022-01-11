package com.defines.bloomerbarber

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.CheckBox
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
        val hair_styling : CheckBox=findViewById(R.id.activity_service_adder_category_hair_styling)
        val body_grooming : CheckBox=findViewById(R.id.activity_service_adder_category_body_grooming)
        val hair_colouring : CheckBox=findViewById(R.id.activity_service_adder_category_hair_colouring)
        val makeup : CheckBox=findViewById(R.id.activity_service_adder_category_make_and_transformation)
        val spa : CheckBox=findViewById(R.id.activity_service_adder_category_spa_and_recreation)

        auth= Firebase.auth

        save_service_button.setOnClickListener{
            val user=auth.currentUser
            val uid=user!!.uid
            val timeStamp=(System.currentTimeMillis()/1000).toString()
            var ref=FirebaseDatabase.getInstance().getReference("services/$uid")

            val categories= hashMapOf<String,Boolean>()
            categories["Hair Styling"]=false
            categories["Body Grooming"]=false
            categories["Hair Colouring"]=false
            categories["Makeup And Transformation"]=false
            categories["Spa And Recreation"]=false

                categories["Hair Styling"] = hair_styling.isChecked


                categories["Body Grooming"] = body_grooming.isChecked


                categories["Hair Colouring"] = hair_colouring.isChecked


                categories["Makeup And Transformation"] = makeup.isChecked


                categories["Spa And Recreation"] = spa.isChecked


            ref=FirebaseDatabase.getInstance().getReference("services/$uid/$timeStamp")
            if (service_name.length()==0){
                Toast.makeText(this,"service name is empty",Toast.LENGTH_SHORT).show()
            }else if (cost_service.length()==0){
                Toast.makeText(this,"cost is empty",Toast.LENGTH_SHORT).show()
            }else if(average_time.length()==0){
                Toast.makeText(this,"average time is empty",Toast.LENGTH_SHORT).show()
            }else if(description.length()==0){
                Toast.makeText(this,"Description is empty",Toast.LENGTH_SHORT).show()
            }else if(!hair_styling.isChecked and !hair_colouring.isChecked and !spa.isChecked and !makeup.isChecked and !body_grooming.isChecked){
                Toast.makeText(this,"Select Any one of the categories",Toast.LENGTH_SHORT).show()
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
}