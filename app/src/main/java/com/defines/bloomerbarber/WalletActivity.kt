package com.defines.bloomerbarber

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.RecyclerView
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.prev_tans_wallet_list.view.*

class WalletActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wallet_activity)
        val recyclerView: RecyclerView =
            findViewById(R.id.activity_wallet_activity_prev_trans_recycler_view)

        val adapter= GroupAdapter<GroupieViewHolder>()
        adapter.add(PreviousRecycler("",",","","","",""))
        adapter.add(PreviousRecycler("",",","","","",""))
        adapter.add(PreviousRecycler("",",","","","",""))
        adapter.add(PreviousRecycler("",",","","","",""))
        adapter.add(PreviousRecycler("",",","","","",""))
        recyclerView.adapter=adapter


    }

    class PreviousRecycler(
        val date: String,
        val fromId: String,
        val toId: String,
        val transactionId: String,
        val amount: String,
        val typeTrans: String
    ) :
        Item<GroupieViewHolder>() {
        override fun bind(viewHolder: GroupieViewHolder, position: Int) {
            viewHolder.itemView.textView6.text = "hello"
        }

        override fun getLayout(): Int {
            return R.layout.prev_tans_wallet_list
        }

    }
}