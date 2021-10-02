package com.defines.bloomerbarber

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
private val TAG="ShopAdderActivity"
class ShopAdderActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shop_adder)
        val shop_name: EditText = findViewById(R.id.activity_shop_adder_name_shop)
        val address:EditText=findViewById(R.id.activity_shop_adder_shop_address)
        val google_map_link:EditText=findViewById(R.id.activity_shop_adder_google_map_link)
        val city:EditText=findViewById(R.id.activity_shop_adder_city_shop)
        val save_button: TextView = findViewById(R.id.activity_shop_adder_save_button)

        val bottomAnim: Animation = AnimationUtils.loadAnimation(this,R.anim.top_anim)

        shop_name.animation=bottomAnim
        address.animation=bottomAnim
        google_map_link.animation=bottomAnim
        city.animation=bottomAnim
        save_button.animation=bottomAnim

        auth= FirebaseAuth.getInstance()

        save_button.setOnClickListener{
            val name_shop=shop_name.text.toString()
            val address_shop=address.text.toString()
            val city_shop=city.text.toString()
            val google_map_link_shop=google_map_link.text.toString()
            val user=auth.currentUser
            val uid=user!!.uid
            if(name_shop.isEmpty()){
                Toast.makeText(this,"Name is empty",Toast.LENGTH_SHORT).show()
            }else if(address_shop.isEmpty()){
                Toast.makeText(this,"Address is empty",Toast.LENGTH_SHORT).show()
            }else if(city_shop.isEmpty()){
                Toast.makeText(this,"City is empty",Toast.LENGTH_SHORT).show()
            }else if(google_map_link_shop.isEmpty()){
                Toast.makeText(this,"Map location link is empty",Toast.LENGTH_SHORT).show()
            }else{
                val ref=FirebaseDatabase.getInstance().getReference("/shop_info/$uid")
                val shop=Shop(
                    uid,
                    name_shop,
                    address_shop,
                    city_shop,
                    google_map_link_shop

                )
                ref.setValue(shop).addOnSuccessListener {
                    Log.d(TAG, "Finally the user is saved to database")
                    Toast.makeText(this,"Shop has been successfully added saving",Toast.LENGTH_SHORT).show()
                    val intent= Intent(this,MainActivity::class.java)
                    startActivity(intent)
                }.addOnFailureListener{
                    Log.d(TAG,"Failed to add{${it.message}")
                    Toast.makeText(this,"Error.Try again!!!",Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


}