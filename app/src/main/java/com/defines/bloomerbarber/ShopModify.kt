package com.defines.bloomerbarber


import android.app.PendingIntent.getActivity
import android.app.ProgressDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.EditText
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.Glide.with
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.categories_list.view.*
import kotlinx.android.synthetic.main.shop_images_show.view.*
import java.util.*
import kotlin.collections.ArrayList
private val TAG = "ShopModifyActivity"

class ShopModify : AppCompatActivity() {
    private val context=this
    lateinit var ImageUri : Uri
    private lateinit var auth: FirebaseAuth
    var count=0
    val array=ArrayList<String>()
    val categories=ArrayList<String>()
    var timings = HashMap<String, ArrayList<String>>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shop_modify)
        var shop : Shop=Shop()

        val shop_name: EditText = findViewById(R.id.activity_shop_modify_name_shop)
        val address: EditText = findViewById(R.id.activity_shop_modify_shop_address)
        val google_map_link: EditText = findViewById(R.id.activity_shop_modify_google_map_link)
        val city: EditText = findViewById(R.id.activity_shop_modify_city_shop)
        val save_button: TextView = findViewById(R.id.activity_shop_modify_save_button)
        val open_image=findViewById<TextView>(R.id.activity_shop_modify_select_image_button)
        val bottomAnim: Animation = AnimationUtils.loadAnimation(this, R.anim.top_anim)
        val mon_switch=findViewById<Switch>(R.id.activity_shop_modify_monday_switch)
        val tues_switch=findViewById<Switch>(R.id.activity_shop_modify_tuesday_switch)
        val wednes_switch=findViewById<Switch>(R.id.activity_shop_modify_wednesday_switch)
        val thur_switch=findViewById<Switch>(R.id.activity_shop_modify_thursday_switch)
        val fri_switch=findViewById<Switch>(R.id.activity_shop_modify_friday_switch)
        val sat_switch=findViewById<Switch>(R.id.activity_shop_modify_saturday_switch)
        val sun_switch=findViewById<Switch>(R.id.activity_shop_modify_sunday_switch)
        var uid1 = FirebaseAuth.getInstance().currentUser!!.uid
        var ref1 =
            FirebaseDatabase.getInstance().getReference("/shop_info/${uid1}")
        ref1.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val shop=snapshot.getValue(Shop::class.java)!!
                timings=shop.timings

                for(i in shop.categories){
                    categories.add(i.toString())
                }
                shop_name.setText(shop.name_shop)
                address.setText(shop.address)
                city.setText(shop.address)
                google_map_link.setText(shop.google_map_link)
                Log.d(TAG,"$timings")
                for(i in timings){
                    if(i.key=="Monday" && i.value[0]!="NA" && i.value[1]!="NA"){
                        val layout : View=findViewById(R.id.activity_shop_modify_constraintLayoutMonday)
                        val from=findViewById<TextView>(R.id.activity_shop_modify_monday_from)
                        val to=findViewById<TextView>(R.id.activity_shop_modify_monday_to)
                        val switch=findViewById<Switch>(R.id.activity_shop_modify_monday_switch)
                        switch.isChecked=true
                        layout.visibility=View.VISIBLE
                        from.text=i.value[0]
                        to.text=i.value[1]

                    }else if(i.key=="Tuesday" && i.value[0]!="NA" && i.value[1]!="NA"){
                        val layout : View=findViewById(R.id.activity_shop_modify_constraintLayoutTuesday)
                        val from=findViewById<TextView>(R.id.activity_shop_modify_tuesday_from)
                        val to=findViewById<TextView>(R.id.activity_shop_modify_tuesday_to)
                        val switch=findViewById<Switch>(R.id.activity_shop_modify_tuesday_switch)
                        switch.isChecked=true
                        layout.visibility=View.VISIBLE
                        from.text=i.value[0]
                        to.text=i.value[1]

                    }else if(i.key=="Wednesday" && i.value[0]!="NA" && i.value[1]!="NA"){
                        val layout : View=findViewById(R.id.activity_shop_modify_constraintLayoutWednesday)
                        val from=findViewById<TextView>(R.id.activity_shop_modify_wednesday_from)
                        val to=findViewById<TextView>(R.id.activity_shop_modify_wednesday_to)
                        val switch=findViewById<Switch>(R.id.activity_shop_modify_wednesday_switch)
                        switch.isChecked=true
                        layout.visibility=View.VISIBLE
                        from.text=i.value[0]
                        to.text=i.value[1]

                    }else if(i.key=="Thursday" && i.value[0]!="NA" && i.value[1]!="NA"){
                        val layout : View=findViewById(R.id.activity_shop_modify_constraintLayoutThursday)
                        val from=findViewById<TextView>(R.id.activity_shop_modify_thursday_from)
                        val to=findViewById<TextView>(R.id.activity_shop_modify_thursday_to)
                        val switch=findViewById<Switch>(R.id.activity_shop_modify_thursday_switch)
                        switch.isChecked=true
                        layout.visibility=View.VISIBLE
                        from.text=i.value[0]
                        to.text=i.value[1]

                    }else if(i.key=="Friday" && i.value[0]!="NA" && i.value[1]!="NA"){
                        val layout : View=findViewById(R.id.activity_shop_modify_constraintLayoutFriday)
                        val from=findViewById<TextView>(R.id.activity_shop_modify_friday_from)
                        val to=findViewById<TextView>(R.id.activity_shop_modify_friday_to)
                        val switch=findViewById<Switch>(R.id.activity_shop_modify_friday_switch)
                        switch.isChecked=true
                        layout.visibility=View.VISIBLE
                        from.text=i.value[0]
                        to.text=i.value[1]

                    }else if(i.key=="Saturday" && i.value[0]!="NA" && i.value[1]!="NA"){
                        val layout : View=findViewById(R.id.activity_shop_modify_constraintLayoutSaturday)
                        val from=findViewById<TextView>(R.id.activity_shop_modify_saturday_from)
                        val to=findViewById<TextView>(R.id.activity_shop_modify_saturday_to)
                        val switch=findViewById<Switch>(R.id.activity_shop_modify_saturday_switch)
                        switch.isChecked=true
                        layout.visibility=View.VISIBLE
                        from.text=i.value[0]
                        to.text=i.value[1]

                    }else if(i.key=="Sunday" && i.value[0]!="NA" && i.value[1]!="NA"){
                        val layout : View=findViewById(R.id.activity_shop_modify_constraintLayoutSunday)
                        val from=findViewById<TextView>(R.id.activity_shop_modify_sunday_from)
                        val switch=findViewById<Switch>(R.id.activity_shop_modify_sunday_switch)
                        switch.isChecked=true

                        val to=findViewById<TextView>(R.id.activity_shop_modify_sunday_to)
                        layout.visibility=View.VISIBLE
                        from.text=i.value[0]
                        to.text=i.value[1]

                    }
                }

            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
        val recyclerView_image : RecyclerView=findViewById(R.id.image_uploaded_recyclerview)
        val adapter_image = GroupAdapter<GroupieViewHolder>()

        for(i in 0..3){
            val reff=FirebaseStorage.getInstance().getReference("images/shop_images/$uid1/$i")
            reff.downloadUrl.addOnSuccessListener {
                adapter_image.add(imageupload(it.toString(),this))
            }
        }

        recyclerView_image.adapter=adapter_image




        mon_switch.setOnClickListener {
            val layout=findViewById<View>(R.id.activity_shop_modify_constraintLayoutMonday)
            val switch=findViewById<Switch>(R.id.activity_shop_modify_monday_switch)
            val from=findViewById<TextView>(R.id.activity_shop_modify_monday_from)
            val to=findViewById<TextView>(R.id.activity_shop_modify_monday_to)
            timetopick(layout,switch,from,to,"Monday")

        }
        tues_switch.setOnClickListener {
            val layout=findViewById<View>(R.id.activity_shop_modify_constraintLayoutTuesday)
            val switch=findViewById<Switch>(R.id.activity_shop_modify_tuesday_switch)
            val from=findViewById<TextView>(R.id.activity_shop_modify_tuesday_from)
            val to=findViewById<TextView>(R.id.activity_shop_modify_tuesday_to)
            timetopick(layout,switch,from,to,"Tuesday")

        }
        wednes_switch.setOnClickListener {
            val layout=findViewById<View>(R.id.activity_shop_modify_constraintLayoutWednesday)
            val switch=findViewById<Switch>(R.id.activity_shop_modify_wednesday_switch)
            val from=findViewById<TextView>(R.id.activity_shop_modify_wednesday_from)
            val to=findViewById<TextView>(R.id.activity_shop_modify_wednesday_to)
            timetopick(layout,switch,from,to,"Wednesday")

        }
        thur_switch.setOnClickListener {
            val layout=findViewById<View>(R.id.activity_shop_modify_constraintLayoutThursday)
            val switch=findViewById<Switch>(R.id.activity_shop_modify_thursday_switch)
            val from=findViewById<TextView>(R.id.activity_shop_modify_thursday_from)
            val to=findViewById<TextView>(R.id.activity_shop_modify_thursday_to)
            timetopick(layout,switch,from,to,"Thursday")

        }
        fri_switch.setOnClickListener {
            val layout=findViewById<View>(R.id.activity_shop_modify_constraintLayoutFriday)

            val switch=findViewById<Switch>(R.id.activity_shop_modify_friday_switch)
            val from=findViewById<TextView>(R.id.activity_shop_modify_friday_from)
            val to=findViewById<TextView>(R.id.activity_shop_modify_friday_to)
            timetopick(layout,switch,from,to,"Friday")

        }
        sat_switch.setOnClickListener {
            val layout=findViewById<View>(R.id.activity_shop_modify_constraintLayoutSaturday)
            val switch=findViewById<Switch>(R.id.activity_shop_modify_saturday_switch)
            val from=findViewById<TextView>(R.id.activity_shop_modify_saturday_from)
            val to=findViewById<TextView>(R.id.activity_shop_modify_saturday_to)
            timetopick(layout,switch,from,to,"Saturday")

        }
        sun_switch.setOnClickListener {
            val layout=findViewById<View>(R.id.activity_shop_modify_constraintLayoutSunday)
            val switch=findViewById<Switch>(R.id.activity_shop_modify_sunday_switch)
            val from=findViewById<TextView>(R.id.activity_shop_modify_sunday_from)
            val to=findViewById<TextView>(R.id.activity_shop_modify_sunday_to)
            timetopick(layout,switch,from,to,"Sunday")

        }

        val category_list=findViewById<RecyclerView>(R.id.activity_shop_modify_recyclerView)


        val adapter1= GroupAdapter<GroupieViewHolder>()
        val uid = FirebaseAuth.getInstance().currentUser!!.uid
        val ref =
            FirebaseDatabase.getInstance().getReference("/Categories_")
        Log.d("Shop Modify Category", "fetch categories")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {


                p0.children.forEach {
                    Log.d("Shop Modify Category", "fetch categories${it.toString()}")
                    val shop = it.value
                    adapter1.add(categoryList(shop.toString(),categories))
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
                    0

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
                        "Shop has been successfully modified saving",
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
    class imageupload(val s : String, val context: Context): Item<GroupieViewHolder>(){
        override fun bind(viewHolder: GroupieViewHolder, position: Int) {

            Picasso.get()
                .load(s)
                .into(viewHolder.itemView.shop_images)
        }

        override fun getLayout(): Int {
            return R.layout.shop_images_show
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
    private fun selectimage() {
        val intent= Intent()
        intent.type="image/*"
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true)
        intent.action=Intent.ACTION_GET_CONTENT
        startActivityForResult(intent,100)
    }

    private fun timetopick(layout : View,switch : Switch,fromText: TextView,toText : TextView,day: String){
        if(switch.isChecked){
            layout.visibility=View.VISIBLE
        }else layout.visibility= View.GONE



        var from = ""
        var to=""
        fromText.setOnClickListener(){
            val cal= Calendar.getInstance()
            val timeSetListener = TimePickerDialog.OnTimeSetListener{ timePicker, hour, minute ->
                cal.set(Calendar.HOUR_OF_DAY,hour)
                cal.set(Calendar.MINUTE,minute)

                from= SimpleDateFormat("HH:mm").format(cal.time).toString()
                fromText.text=from
                Log.d(TAG,from)
                timings[day]= arrayListOf(from,to)

            }
            TimePickerDialog(this,timeSetListener,cal.get(Calendar.HOUR_OF_DAY),cal.get(Calendar.MINUTE),true).show()
        }
        toText.setOnClickListener(){
            val cal= Calendar.getInstance()
            val timeSetListener = TimePickerDialog.OnTimeSetListener{ timePicker, hour, minute ->
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

                val recycle : RecyclerView=findViewById(R.id.image_uploaded_recyclerview)
                val adapter= GroupAdapter<GroupieViewHolder>()
                for(i in 0..count-1) {
                    progressDialog.setMessage("Uploading...")
                    progressDialog.setCancelable(false)
                    progressDialog.show()


                    ImageUri = data.clipData!!.getItemAt(i).uri

                    adapter.add(imageupload(ImageUri.toString(),this))
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
                recycle.adapter=adapter


            }
        }

    }
}