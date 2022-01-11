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

        val hair_styling : CheckBox =findViewById(R.id.activity_service_modify_category_hair_styling)
        val body_grooming : CheckBox =findViewById(R.id.activity_service_modify_category_body_grooming)
        val hair_colouring : CheckBox =findViewById(R.id.activity_service_modify_category_hair_colouring)
        val makeup : CheckBox =findViewById(R.id.activity_service_modify_category_make_and_transformation)
        val spa : CheckBox =findViewById(R.id.activity_service_modify_category_spa_and_recreation)

        serviceName.setText(service.name)
        serviceCost.setText(service.cost.toString())
        serviceTime.setText(service.avgTime.toString())
        serviceDescription.setText(service.description)
        val categories= service.categories
        hair_styling.isChecked = service.categories["Hair Styling"]==false
        body_grooming.isChecked = service.categories["Body Grooming"]==false
        hair_colouring.isChecked = service.categories["Hair Colouring"]==false
        makeup.isChecked = service.categories["Makeup And Transformation"]==false
        spa.isChecked = service.categories["Spa And Recreation"]==false

        hair_styling.setOnClickListener {
            categories["Hair Styling"] = hair_styling.isChecked
        }
        body_grooming.setOnClickListener {
            categories["Body Grooming"] = body_grooming.isChecked
        }
        hair_colouring.setOnClickListener {
            categories["Hair Colouring"] = hair_colouring.isChecked
        }
        makeup.setOnClickListener {
            categories["Makeup And Transformation"] = makeup.isChecked
        }
        spa.setOnClickListener {
            categories["Spa And Recreation"] = spa.isChecked
        }
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
            }else if(!hair_styling.isChecked and !hair_colouring.isChecked and !spa.isChecked and !makeup.isChecked and !body_grooming.isChecked){
                Toast.makeText(this,"Select Any one of the categories",Toast.LENGTH_SHORT).show()
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