package com.defines.bloomerbarber

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintSet
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

/**
 * A simple [Fragment] subclass.
 * Use the [SettingsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SettingsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val myFrag=inflater.inflate(R.layout.fragment_settings, container, false)
        val user = FirebaseAuth.getInstance().currentUser
        val profile_pic_view: ImageView = myFrag.findViewById(R.id.fragment_settings_profile_pic)

        val shop_name: TextView=myFrag.findViewById(R.id.fragment_settings_shop_name)
        val user_name : TextView=myFrag.findViewById(R.id.fragment_settings_user_name)
        val edit_shop: View=myFrag.findViewById(R.id.fragment_settings_edit_shop)

        val signout : View? =myFrag.findViewById(R.id.fragment_settings_log_out)
        Glide.with(this).load(user!!.photoUrl.toString()).circleCrop().into(profile_pic_view)
        val ref=FirebaseDatabase.getInstance().getReference("barber/${user.uid}/username")
        ref.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                user_name.text=snapshot.value.toString()
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
        val ref1=FirebaseDatabase.getInstance().getReference("shop_info/${user.uid}/name_shop")
        ref1.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val data=snapshot.value as String
                shop_name.text=data
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
        signout!!.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent= Intent(activity,LoginActivity::class.java)
            intent.flags= Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
        edit_shop.setOnClickListener {
            val intent= Intent(activity,ShopModify::class.java)
            startActivity(intent)
        }
        return myFrag
    }

    companion object {

        @JvmStatic
        fun newInstance() =
            SettingsFragment().apply {
                arguments = Bundle().apply{}
            }
    }


}