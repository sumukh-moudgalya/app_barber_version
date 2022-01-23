package com.defines.bloomerbarber

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.artist_adder_list.view.*
import java.util.regex.Pattern


private lateinit var shop: Shop
private val TAG = "ArtistAdderActivity"
private val profileUrl = HashMap<Int, Uri>()
private val artistDetails = HashMap<Int, HashMap<String, String>>()
private val artistNameTextView = HashMap<Int, EditText>()
private val emailTextViewArtist = HashMap<Int, EditText>()
private val phoneNumberTextView = HashMap<Int, EditText>()
private val profileImageView = HashMap<Int, ImageView>()


private fun isValidEmail(email: String): Boolean {
    return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches()
}

private fun isValidMobile(phone: String): Boolean {
    return if (!Pattern.matches("[a-zA-Z]+", phone)) {
        phone.length > 6 && phone.length <= 13
    } else false
}

class ArtistAdderActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_artist_adder)

        val artistRecycler: RecyclerView = findViewById(R.id.activity_artist_adder_recycler_view)
        val saveButton: TextView = findViewById(R.id.activity_artist_adder_save_button)

        val uid = FirebaseAuth.getInstance().currentUser!!.uid
        val shopRef = FirebaseDatabase.getInstance().getReference("shop_info/$uid")
        shopRef.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                shop = snapshot.getValue(Shop::class.java)!!
                val numberOfArtists = shop.noOfArtists
                val adapter = GroupAdapter<GroupieViewHolder>()
                for (x in 0 until numberOfArtists) {
                    Log.d(TAG, "x==$x")
                    adapter.add(ArtistViewAdder(x, this@ArtistAdderActivity))
                }
                artistRecycler.adapter = adapter
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
        saveButton.setOnClickListener {
            for (x in 0 until shop.noOfArtists) {
                val email = emailTextViewArtist[x]
                if (isValidEmail(email!!.text.toString())) {
                    val phoneNumber = phoneNumberTextView[x]!!
                    if (isValidMobile(phoneNumber.text.toString())) {
                        if (x in profileImageView) {
                            Toast.makeText(
                                this,
                                "All the fields have been set properly",
                                Toast.LENGTH_SHORT
                            ).show()

                        } else {
                            Toast.makeText(
                                this,
                                "Image not selected at artist $x",
                                Toast.LENGTH_SHORT
                            ).show()

                            return@setOnClickListener
                        }
                    } else {
                        Toast.makeText(
                            this,
                            "Phone Number invalid at artist $x",
                            Toast.LENGTH_SHORT
                        ).show()

                        return@setOnClickListener
                    }
                } else {
                    saveButton.animate().apply {
                        duration=1000
                        rotationYBy(360f)
                    }.withEndAction {
                        Toast.makeText(this, "Email invalid at artist ${x+1}", Toast.LENGTH_SHORT).show()

                    }.start()
                    return@setOnClickListener
                }
            }
            for (x in 0 until shop.noOfArtists) {

                val progressDialog = ProgressDialog(this)

                val email = emailTextViewArtist[x]!!.text.toString()
                val phoneNumber = phoneNumberTextView[x]!!.text.toString()
                val userName = artistNameTextView[x]!!.text.toString()
                progressDialog.setMessage("Artist data $userName is being Uploaded")
                progressDialog.setCancelable(false)
                progressDialog.show()
                val url = profileUrl[x]!!
                val uid = FirebaseAuth.getInstance().currentUser!!.uid
                val ref = FirebaseStorage.getInstance().getReference("/images/artists/$uid/$x")
                ref.putFile(url!!).addOnSuccessListener {
                    Log.d(
                        TAG,
                        "uploadImagetoFireBaseDatabase:Profile photo uploaded successfullyy"
                    )
                    ref.downloadUrl.addOnSuccessListener {
                        Log.d(TAG, "File Location:$it")
                        val artistRef =
                            FirebaseDatabase.getInstance().getReference("artist_info/$uid/$x")
                        val artistID =
                            uid.slice(0..6) + userName.slice(0..3) + (System.currentTimeMillis() / 1000).toString()
                                .slice(0..3) + x.toString()

                        val artist = Artists(artistID,
                            userName,
                            email,
                            phoneNumber,
                            it.toString()
                        )
                        artistRef.setValue(artist).addOnSuccessListener {
                            Log.d(TAG,"Artist $x with username $userName has been saved")
                            if (progressDialog.isShowing){
                                progressDialog.dismiss()

                            }
                        }
                    }.addOnFailureListener {
                        Log.d(TAG, "failed.${it.message}")
                    }
                }
            }
            val uid=FirebaseAuth.getInstance().currentUser!!.uid
            val barberArtistDetail=FirebaseDatabase.getInstance().getReference("barber/$uid/artistDetailUploaded")
            barberArtistDetail.setValue(true).addOnSuccessListener {
                val intent=Intent(this,MainActivity::class.java)
                startActivity(intent)
                finish()

            }



        }


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && data != null) {
            Log.d(TAG, ".onActivityResult:Photo was selected")
            profileUrl[requestCode] = data.data!!
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, data.data!!)
            val bitmapDrawable = BitmapDrawable(bitmap)
            profileImageView[requestCode]!!.animate().apply {
                    duration=1000
                    rotationYBy(180f)
                }.withEndAction {
                Glide.with(this).load(bitmap).circleCrop().into(profileImageView[requestCode]!!)

                }.start()
        } else {
            Log.d(TAG,"")
            Toast.makeText(applicationContext, "Profile Not Selected", Toast.LENGTH_SHORT).show()
        }


    }



    class ArtistViewAdder(val x: Int, val artistAdderActivity: ArtistAdderActivity) :
        Item<GroupieViewHolder>() {
        override fun bind(viewHolder: GroupieViewHolder, position: Int) {
            artistNameTextView[x] = viewHolder.itemView.artist_adder_list_artist_name
            emailTextViewArtist[x] = viewHolder.itemView.artist_adder_list_email_id
            phoneNumberTextView[x] = viewHolder.itemView.artist_adder_list_phone_number
            profileImageView[x] = viewHolder.itemView.artist_adder_list_profile_pic_adder
            viewHolder.itemView.artist_adder_list_profile_pic_adder.setOnClickListener {
                val intent = Intent(
                    Intent.ACTION_PICK,
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                )
                artistAdderActivity.startActivityForResult(intent, x)


            }
        }


        override fun getLayout(): Int {
            return R.layout.artist_adder_list
        }

    }


}