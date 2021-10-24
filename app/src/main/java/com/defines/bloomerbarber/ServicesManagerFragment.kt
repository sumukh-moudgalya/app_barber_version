package com.defines.bloomerbarber
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.fragment_services_manager.*
import kotlinx.android.synthetic.main.service_list.view.*
import java.io.Serializable


class ServicesManagerFragment: Fragment() {
    // TODO: Rename and change types of parameters


    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val myfrag=inflater.inflate(R.layout.fragment_services_manager, container, false)
        auth=FirebaseAuth.getInstance()
        val serviceButton: TextView =myfrag.findViewById(R.id.fragment_service_manager_add_service_button)
        add_service_info(auth)
        serviceButton.setOnClickListener {
            val intent= Intent(activity,ServiceAdderActivity::class.java)
            startActivity(intent)

        }
        return myfrag
    }

    private fun add_service_info(auth: FirebaseAuth) {
        val currentUser=auth.currentUser
        val uid=currentUser!!.uid
        val ref=FirebaseDatabase.getInstance().getReference("/services/$uid").orderByKey()
        ref.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                val adapter= GroupAdapter<GroupieViewHolder>()

                p0.children.forEach {
                    Log.d("PreviousTest","fetch_users${it.toString()}")
                    val graph=it.getValue(ServiceOffered::class.java)
                    if(graph!=null){
                        adapter.add(ServiceInfo(graph))

                    }else{
                        Toast.makeText(activity,"No services has been added yet",Toast.LENGTH_SHORT).show()
                    }





                }




                fragment_services_manager_services_recycler_view.adapter=adapter
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(activity,"Error with the server",Toast.LENGTH_SHORT).show()
            }
        })
    }


    companion object {

        @JvmStatic
        fun newInstance() =
            ServicesManagerFragment().apply {
                arguments = Bundle().apply {}
            }
    }


    class ServiceInfo(val service:ServiceOffered):Item<GroupieViewHolder>(){
        override fun bind(viewHolder: GroupieViewHolder, position: Int) {
            viewHolder.itemView.service_list_service_name.text=service.name
            viewHolder.itemView.service_list_cost.text=service.cost.toString()
            viewHolder.itemView.service_list_description.text=service.description
            viewHolder.itemView.service_list_average_time.text=service.avgTime.toString()

        }

        override fun getLayout(): Int {
            return R.layout.service_list
        }


    }
}