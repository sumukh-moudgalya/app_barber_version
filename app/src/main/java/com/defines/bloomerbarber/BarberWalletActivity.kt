package com.defines.bloomerbarber

import android.app.Activity
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.internal.ContextUtils.getActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.activity_barber_wallet.*
import kotlinx.android.synthetic.main.fragment_services_manager.*
import kotlinx.android.synthetic.main.prev_tans_wallet_list.view.*
import java.security.AccessController.getContext

class BarberWalletActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_barber_wallet)
        auth=FirebaseAuth.getInstance()

        add_prev_trans_info(auth,this)

    }



    private fun add_prev_trans_info(auth: FirebaseAuth,context: Context) {
        val currentUser=auth.currentUser
        val uid=currentUser!!.uid
        val ref= FirebaseDatabase.getInstance().getReference("/Previous_Transaction_wallet/$uid").orderByKey()
        val adapter= GroupAdapter<GroupieViewHolder>()
        adapter.add(PrevTrans(WalletPrevTrans()))

        ref.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                val adapter= GroupAdapter<GroupieViewHolder>()

                p0.children.forEach {
                    Log.d("PreviousTest","fetch_users${it.toString()}")
                    val graph=it.getValue(WalletPrevTrans::class.java)
                    if(graph!=null){
                        adapter.add(BarberWalletActivity.PrevTrans(graph))

                    }else{
                        Toast.makeText(getApplicationContext(),"No Transactions Yet", Toast.LENGTH_SHORT).show()
                    }

                }


            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context,"Error with the server", Toast.LENGTH_SHORT).show()
            }


        })

        activity_barber_wallet_recycler_view.adapter=adapter
    }
    class PrevTrans(val transaction : WalletPrevTrans):Item<GroupieViewHolder>(){
        override fun bind(viewHolder: GroupieViewHolder, position: Int) {
            viewHolder.itemView.prev_trans_wallet_list_date.text="hihihi"
            viewHolder.itemView.prev_trans_wallet_list_time.text="hihi"


        }

        override fun getLayout(): Int {
            return R.layout.prev_tans_wallet_list
        }

    }

}