package com.defines.bloomerbarber

import android.app.Dialog
import android.app.ProgressDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.ColorDrawable
import android.icu.text.SimpleDateFormat
import android.location.Geocoder
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.format.DateFormat.format
import android.text.method.LinkMovementMethod
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.categories_list.view.*
import java.io.Serializable
import java.lang.String.format
import java.text.MessageFormat.format
import java.util.*
import kotlin.collections.ArrayList

private val TAG = "ShopAdderActivity"

class ShopAdderActivity : AppCompatActivity() {
    lateinit var ImageUri : Uri
    private lateinit var auth: FirebaseAuth
    var count=0
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    var array=ArrayList<String>()
    val categories=ArrayList<String>()
    val timings = HashMap<String, ArrayList<String>>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(this)
        setContentView(R.layout.activity_shop_adder)
        val shop_name: EditText = findViewById(R.id.activity_shop_adder_name_shop)
        val address: EditText = findViewById(R.id.activity_shop_adder_shop_address)
        val google_map_link: EditText = findViewById(R.id.activity_shop_adder_google_map_link)
        val city: EditText = findViewById(R.id.activity_shop_adder_city_shop)
        val save_button: TextView = findViewById(R.id.activity_shop_adder_save_button)
        val open_image=findViewById<TextView>(R.id.activity_shop_adder_select_image_button)
        val bottomAnim: Animation = AnimationUtils.loadAnimation(this, R.anim.top_anim)
        val mon_switch=findViewById<Switch>(R.id.activity_shop_adder_monday_switch)
        val tues_switch=findViewById<Switch>(R.id.activity_shop_adder_tuesday_switch)
        val wednes_switch=findViewById<Switch>(R.id.activity_shop_adder_wednesday_switch)
        val thur_switch=findViewById<Switch>(R.id.activity_shop_adder_thursday_switch)
        val fri_switch=findViewById<Switch>(R.id.activity_shop_adder_friday_switch)
        val sat_switch=findViewById<Switch>(R.id.activity_shop_adder_saturday_switch)
        val sun_switch=findViewById<Switch>(R.id.activity_shop_adder_sunday_switch)
        var artist_no=1

//fetching location
        fetchlocation()
        val artists = resources.getStringArray(R.array.artists)
        val spinner : Spinner= findViewById(R.id.spinner_artists)
        if(spinner != null) {
            val adapter12 = ArrayAdapter(this,android.R.layout.simple_spinner_item, artists)
            spinner.adapter = adapter12
        }

        spinner.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                artist_no=artists[p2].toInt()
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }
        }
        timings.put("Monday", arrayListOf("NA","NA"))
        timings.put("Tuesday",arrayListOf("NA","NA"))
        timings.put("Wednesday",arrayListOf("NA","NA"))
        timings.put("Thursday",arrayListOf("NA","NA"))
        timings.put("Friday",arrayListOf("NA","NA"))
        timings.put("Saturday",arrayListOf("NA","NA"))
        timings.put("Sunday",arrayListOf("NA","NA"))


        mon_switch.setOnClickListener {
            val layout=findViewById<View>(R.id.constraintLayoutMonday)
            val switch=findViewById<Switch>(R.id.activity_shop_adder_monday_switch)
            val from=findViewById<TextView>(R.id.activity_shop_adder_monday_from)
            val to=findViewById<TextView>(R.id.activity_shop_adder_monday_to)
            timetopick(layout,switch,from,to,"Monday")

        }
        tues_switch.setOnClickListener {
            val layout=findViewById<View>(R.id.constraintLayoutTuesday)
            val switch=findViewById<Switch>(R.id.activity_shop_adder_tuesday_switch)
            val from=findViewById<TextView>(R.id.activity_shop_adder_tuesday_from)
            val to=findViewById<TextView>(R.id.activity_shop_adder_tuesday_to)
            timetopick(layout,switch,from,to,"Tuesday")

        }
        wednes_switch.setOnClickListener {
            val layout=findViewById<View>(R.id.constraintLayoutWednesday)
            val switch=findViewById<Switch>(R.id.activity_shop_adder_wednesday_switch)
            val from=findViewById<TextView>(R.id.activity_shop_adder_wednesday_from)
            val to=findViewById<TextView>(R.id.activity_shop_adder_wednesday_to)
            timetopick(layout,switch,from,to,"Wednesday")

        }
        thur_switch.setOnClickListener {
            val layout=findViewById<View>(R.id.constraintLayoutThursday)
            val switch=findViewById<Switch>(R.id.activity_shop_adder_thursday_switch)
            val from=findViewById<TextView>(R.id.activity_shop_adder_thursday_from)
            val to=findViewById<TextView>(R.id.activity_shop_adder_thursday_to)
            timetopick(layout,switch,from,to,"Thursday")

        }
        fri_switch.setOnClickListener {
            val layout=findViewById<View>(R.id.constraintLayoutFriday)

            val switch=findViewById<Switch>(R.id.activity_shop_adder_friday_switch)
            val from=findViewById<TextView>(R.id.activity_shop_adder_friday_from)
            val to=findViewById<TextView>(R.id.activity_shop_adder_friday_to)
            timetopick(layout,switch,from,to,"Friday")

        }
        sat_switch.setOnClickListener {
            val layout=findViewById<View>(R.id.constraintLayoutSaturday)
            val switch=findViewById<Switch>(R.id.activity_shop_adder_saturday_switch)
            val from=findViewById<TextView>(R.id.activity_shop_adder_saturday_from)
            val to=findViewById<TextView>(R.id.activity_shop_adder_saturday_to)
            timetopick(layout,switch,from,to,"Saturday")

        }
        sun_switch.setOnClickListener {
            val layout=findViewById<View>(R.id.constraintLayoutSunday)
            val switch=findViewById<Switch>(R.id.activity_shop_adder_sunday_switch)
            val from=findViewById<TextView>(R.id.activity_shop_adder_sunday_from)
            val to=findViewById<TextView>(R.id.activity_shop_adder_sunday_to)
            timetopick(layout,switch,from,to,"Sunday")

        }

        val category_list=findViewById<RecyclerView>(R.id.activity_shop_adder_recyclerView)


        val adapter1= GroupAdapter<GroupieViewHolder>()
        val uid = FirebaseAuth.getInstance().currentUser!!.uid
        val ref =
            FirebaseDatabase.getInstance().getReference("/Categories_")
        Log.d("Shop Adder Category", "fetch categories")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {


                p0.children.forEach {
                    Log.d("Shop Adder Category", "fetch categories${it.toString()}")
                    val shop = it.value
                    adapter1.add(categoryList(shop.toString()))
                    if (shop != null) {
                        Log.d("PreviousTests", "${shop}")
                    } else {
                   }


                }

                adapter1.setOnItemClickListener { item, view ->


                    val it=item as categoryList
                    Log.d("Adapter clicked","adapter ${it.s} clicked")
                    if(view.category_checkbox.isChecked) categories.add(it.s)
                    else if(it.s in categories && !view.category_checkbox.isChecked) categories.remove(it.s)
                }
                category_list.adapter=adapter1


            }

            override fun onCancelled(error: DatabaseError) {


            }
        })



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
            val simpleDateFormat = SimpleDateFormat("HH:mm:ss")
            val currentDateAndTime: String = simpleDateFormat.format(Date())
            val promoCode = (uid.toString().slice(1..2)+name_shop.slice(1..2)+currentDateAndTime.slice(3..4)+uid.slice(7..8)).toUpperCase()


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
                    array,
                    timings,
                    categories,
                    0.0,
                    0,
                    artist_no,
                    0L,
                    promoCode
                )

                val ref3=FirebaseDatabase.getInstance().getReference("shop_promo_gen/$promoCode")
                val promo=ShopPromo(
                    uid,
                    arrayListOf()
                )
                ref3.setValue(promo).addOnSuccessListener {
                    Log.d(TAG,"promo code added")
                }.addOnFailureListener {
                    Log.d(TAG,"$it")
                }
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
                    val dialog = Dialog(this)
                    dialog.setContentView(R.layout.promo_code_progress_bar)


                    dialog.getWindow()!!.setBackgroundDrawable(ColorDrawable(0))
                    val text : TextView=dialog.findViewById(R.id.share_text)
                    text.text="The Promo Code of Your Shop is $promoCode . Please Share it to customers  " +
                            "exciting offers."
                    dialog.show()
                    dialog.setCancelable(false)
                    val dismissButton: TextView =
                        dialog.findViewById(R.id.promo_share_cancel)
                    dismissButton.setOnClickListener {
                        dialog.cancel()
                        val intent = Intent(this, ArtistAdderActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                    val confirmButton: TextView =
                        dialog.findViewById(R.id.share_promo)
                    confirmButton.setOnClickListener {
                        val message: String="Please use the promocode $promoCode during installation of Bloomer App for reference and earn exciting offers."
                        val intent =Intent()
                        intent.action=Intent.ACTION_SEND
                        intent.putExtra(Intent.EXTRA_TEXT,message)
                        intent.type="text/plain"
                        startActivity(Intent.createChooser(intent,"Share to:"))

                    }

                }.addOnFailureListener {
                    Log.d(TAG, "Failed to add{${it.message}")
                    Toast.makeText(this, "Error.Try again!!!", Toast.LENGTH_SHORT).show()
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
    private fun selectimage() {
        val intent= Intent()
        intent.type="image/*"
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true)
        intent.action=Intent.ACTION_GET_CONTENT
        startActivityForResult(intent,100)
    }
    private fun fetchlocation() {

        val task = fusedLocationProviderClient.lastLocation
        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            )
            != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                101
            )
            return
        }

        task.addOnSuccessListener {
            if (it != null) {
                var geocoder = Geocoder(this, Locale.getDefault())
                var adress = geocoder.getFromLocation(it.latitude, it.longitude, 1)
                val cityname = adress[0].getAddressLine(0)
                var hello = findViewById<EditText>(R.id.activity_shop_adder_shop_address)
                hello.setText(cityname)
                val city_name=findViewById<EditText>(R.id.activity_shop_adder_city_shop)
                city_name.setText("${adress[0].locality}")
            }
        }

    }

    private fun timetopick(layout : View,switch : Switch,fromText: TextView,toText : TextView,day: String){
        if(switch.isChecked){
            layout.visibility=View.VISIBLE
        }else layout.visibility= View.GONE



        var from = ""
        var to=""
    fromText.setOnClickListener(){
        val cal=Calendar.getInstance()
        val timeSetListener =TimePickerDialog.OnTimeSetListener{timePicker,hour,minute ->
             cal.set(Calendar.HOUR_OF_DAY,hour)
            cal.set(Calendar.MINUTE,minute)

            from=SimpleDateFormat("HH:mm").format(cal.time).toString()
            fromText.text=from
            Log.d(TAG,from)
            timings[day]= arrayListOf(from,to)

        }
        TimePickerDialog(this,timeSetListener,cal.get(Calendar.HOUR_OF_DAY),cal.get(Calendar.MINUTE),true).show()
    }
        toText.setOnClickListener(){
            val cal=Calendar.getInstance()
            val timeSetListener =TimePickerDialog.OnTimeSetListener{timePicker,hour,minute ->
                cal.set(Calendar.HOUR_OF_DAY,hour)
                cal.set(Calendar.MINUTE,minute)

                to=(SimpleDateFormat("HH:mm").format(cal.time).toString())
                toText.text=to
                Log.d(TAG,to)
                timings[day]= arrayListOf(from,to)

            }
            TimePickerDialog(this,timeSetListener,cal.get(Calendar.HOUR_OF_DAY),cal.get(Calendar.MINUTE),true).show()
        }






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

                                if (progressDialog.isShowing){
                                    progressDialog.dismiss()

                                }
                                storageReference.downloadUrl.addOnSuccessListener {
                                    array.add(it.toString())
                                }

                            }
                            .addOnFailureListener {

                                if (progressDialog.isShowing) progressDialog.dismiss()
                                Toast.makeText(this, "$it", Toast.LENGTH_SHORT).show()
                                Log.d(TAG, "$it")

                            }



                    }
            }
        }

    }


}