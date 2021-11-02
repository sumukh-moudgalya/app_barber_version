package com.defines.bloomerbarber

import android.content.Context
import android.content.Intent
import android.graphics.Color.green
import android.graphics.Color.red
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
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

class BarberWalletActivity : AppCompatActivity() {
    companion object {
        const val WALLET_TRANS_TYPE: String = "WALLET_TRANS_TYPE"
    }

    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getSupportActionBar()?.hide()
        setContentView(R.layout.activity_barber_wallet)
        auth = FirebaseAuth.getInstance()
        val wallet = MainActivity.currentWallet
        val add_money_button: TextView = findViewById(R.id.activity_barber_wallet_add_money_button)
        val withdraw_money_button: TextView =
            findViewById(R.id.activity_barber_wallet_withdraw_button)
        val walletBalance: TextView = findViewById(R.id.activity_barber_wallet_balance)
        val walletId: TextView = findViewById(R.id.activity_barber_wallet_wallet_id)
        val transationRecycylerView: RecyclerView =
            findViewById(R.id.activity_barber_wallet_recycler_view)

        fetchtransactionList(transationRecycylerView)

        walletBalance.text = wallet!!.balance.toString()
        walletId.text = wallet!!.walletId
        withdraw_money_button.setOnClickListener {
            val intent = Intent(this, WalletAddWithdrawActivity::class.java)
            intent.putExtra(WALLET_TRANS_TYPE, "WITHDRAW")
            startActivity(intent)
            finish()
        }

        add_money_button.setOnClickListener {
            val intent = Intent(this, WalletAddWithdrawActivity::class.java)
            intent.putExtra(WALLET_TRANS_TYPE, "ADD")
            startActivity(intent)
            finish()
        }
    }

    private fun fetchtransactionList(transationRecycylerView: RecyclerView) {
        val wallet = MainActivity.currentWallet
        val currentUser = auth.currentUser
        val uid = currentUser!!.uid
        val ref =
            FirebaseDatabase.getInstance().getReference("wallet_transactions/${wallet!!.walletId}/")
                .orderByChild("timeStamp")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                val adapter = GroupAdapter<GroupieViewHolder>()
                val arrayTemp=ArrayList<WalletTransaction>()
                p0.children.forEach {
                    Log.d("PreviousTest", "fetch_users${it.toString()}")
                    val temp = it.getValue(WalletTransaction::class.java)
                    if (temp != null) {
                        arrayTemp.add(temp)

                    } else {
                        Toast.makeText(
                            applicationContext,
                            "No services has been added yet",
                            Toast.LENGTH_SHORT
                        ).show()
                    }


                }
                val revTemp=arrayTemp.asReversed()
                for (x in revTemp){
                    adapter.add(PrevTransaction(x,applicationContext))
                }




                transationRecycylerView.adapter = adapter
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(applicationContext, "Error with the server", Toast.LENGTH_SHORT)
                    .show()
            }
        })

    }

    class PrevTransaction(val transaction: WalletTransaction, val applicationContext: Context) :
        Item<GroupieViewHolder>() {
        override fun bind(viewHolder: GroupieViewHolder, position: Int) {
            if (transaction.transactionType == "ADD") {
                viewHolder.itemView.prev_trans_wallet_list_trans_type.text = "Money added to wallet"
                viewHolder.itemView.preav_trans_wallet_list_amount.text =
                    (transaction.valueTransaction).toString()
                viewHolder.itemView.preav_trans_wallet_list_amount.setTextColor(
                    ContextCompat.getColor(
                        applicationContext,
                        R.color.green
                    )
                )
                viewHolder.itemView.prev_trans_wallet_list_trans_image.setImageDrawable(
                    ContextCompat.getDrawable(applicationContext, R.drawable.add_money_wallet)
                )

            }else if(transaction.transactionType=="WITHDRAW"){
                viewHolder.itemView.prev_trans_wallet_list_trans_type.text="Money withdrawed from wallet"
                viewHolder.itemView.preav_trans_wallet_list_amount.text =
                    (transaction.valueTransaction).toString()
                viewHolder.itemView.preav_trans_wallet_list_amount.setTextColor(
                    ContextCompat.getColor(
                        applicationContext,
                        R.color.red
                    )
                )
                viewHolder.itemView.prev_trans_wallet_list_trans_image.setImageDrawable(
                    ContextCompat.getDrawable(applicationContext, R.drawable.withdraw)

                )
            }
            viewHolder.itemView.prev_trans_wallet_list_current_balance.text="CB:"+transaction.currentBalance.toString()
            viewHolder.itemView.prev_trans_wallet_list_time.text=transaction.transactionTime
            viewHolder.itemView.prev_trans_wallet_list_date.text=transaction.transactionDate
            viewHolder.itemView.prev_trans_wallet_list_transaction_id.text=transaction.transactionId

        }

        override fun getLayout(): Int {
            return R.layout.prev_tans_wallet_list
        }

    }


}