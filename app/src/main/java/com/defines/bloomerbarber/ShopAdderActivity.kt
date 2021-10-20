package com.defines.bloomerbarber

import android.app.ProgressDialog
import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.format.DateFormat.format
import android.util.Log
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.lang.String.format
import java.text.MessageFormat.format
import java.util.*
import kotlin.collections.ArrayList

private val TAG = "ShopAdderActivity"

class ShopAdderActivity : AppCompatActivity() {
    lateinit var ImageUri : Uri
    private lateinit var auth: FirebaseAuth
    var count=0
    var array=ArrayList<String>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_shop_adder)
        val shop_name: EditText = findViewById(R.id.activity_shop_adder_name_shop)
        val address: EditText = findViewById(R.id.activity_shop_adder_shop_address)
        val google_map_link: EditText = findViewById(R.id.activity_shop_adder_google_map_link)
        val city: EditText = findViewById(R.id.activity_shop_adder_city_shop)
        val save_button: TextView = findViewById(R.id.activity_shop_adder_save_button)
        val open_image=findViewById<TextView>(R.id.activity_shop_adder_select_image_button)





        val bottomAnim: Animation = AnimationUtils.loadAnimation(this, R.anim.top_anim)

        shop_name.animation = bottomAnim
        address.animation = bottomAnim
        google_map_link.animation = bottomAnim
        city.animation = bottomAnim
        save_button.animation = bottomAnim

        auth = FirebaseAuth.getInstance()

        open_image.setOnClickListener{
            selectimage()
        }





        save_button.setOnClickListener {
            val name_shop = shop_name.text.toString()
            val address_shop = address.text.toString()
            val city_shop = city.text.toString()
            val google_map_link_shop = google_map_link.text.toString()
            val user = auth.currentUser
            val uid = user!!.uid

            if (name_shop.isEmpty()) {
                Toast.makeText(this, "Name is empty", Toast.LENGTH_SHORT).show()
            } else if (address_shop.isEmpty()) {
                Toast.makeText(this, "Address is empty", Toast.LENGTH_SHORT).show()
            } else if (city_shop.isEmpty()) {
                Toast.makeText(this, "City is empty", Toast.LENGTH_SHORT).show()
            } else if (google_map_link_shop.isEmpty()) {
                Toast.makeText(this, "Map location link is empty", Toast.LENGTH_SHORT).show()
            } else if (array.isEmpty()) {
                Toast.makeText(this, "Please Add Images of the Shop", Toast.LENGTH_SHORT).show()
            } else {
                val ref = FirebaseDatabase.getInstance().getReference("/shop_info/$uid")
                val shop = Shop(
                    uid,
                    name_shop,
                    address_shop,
                    city_shop,
                    google_map_link_shop,
                    array

                )
                ref.setValue(shop).addOnSuccessListener {
                    val ref2 = FirebaseDatabase.getInstance()
                        .getReference("/barber/$uid/shopDetailsUploaded")

                    val isShop = true
                    ref2.setValue(isShop).addOnSuccessListener {
                        Log.d(TAG, "IsShop value has been updated")
                    }.addOnFailureListener {
                        Log.d(TAG, "IsShop value is NO$it")
                    }
                    Log.d(TAG, "Finally the user is saved to database")
                    Toast.makeText(
                        this,
                        "Shop has been successfully added saving",
                        Toast.LENGTH_SHORT
                    ).show()
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                }.addOnFailureListener {
                    Log.d(TAG, "Failed to add{${it.message}")
                    Toast.makeText(this, "Error.Try again!!!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }



    private fun selectimage() {
        val intent= Intent()
        intent.type="image/*"
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true)
        intent.action=Intent.ACTION_GET_CONTENT
        startActivityForResult(intent,100)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode==100 && resultCode== RESULT_OK){
            if(data!!.clipData!=null){
                count=data.clipData!!.itemCount.toInt()
                if(count!=4){
                    Toast.makeText(this,"Please Select 4 images",Toast.LENGTH_LONG).show()
                    return
                }

                val uid = FirebaseAuth.getInstance().currentUser!!.uid
                val progressDialog = ProgressDialog(this)
                    for(i in 0..count-1) {
                        progressDialog.setMessage("Uploading...")
                        progressDialog.setCancelable(false)
                        progressDialog.show()

                        ImageUri = data.clipData!!.getItemAt(i).uri

                        var storageReference =
                            FirebaseStorage.getInstance().getReference("images/shop_images/${uid}/$i")


                        storageReference.putFile(ImageUri)
                            .addOnSuccessListener {

                                if (progressDialog.isShowing) progressDialog.dismiss()

                            }
                            .addOnFailureListener {

                                if (progressDialog.isShowing) progressDialog.dismiss()
                                Toast.makeText(this, "$it", Toast.LENGTH_SHORT).show()
                                Log.d(TAG, "$it")

                            }

                        array.add(storageReference.downloadUrl.toString())

                    }
            }
        }

    }


}