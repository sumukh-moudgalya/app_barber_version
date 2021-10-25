package com.defines.bloomerbarber

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.defines.bloomerbarber.BarberWalletActivity.Companion.WALLET_TRANS_TYPE
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.sql.Time
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.*

class WalletAddWithdrawActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wallet_add_withdraw)
        val transType = intent.extras!!.getSerializable(WALLET_TRANS_TYPE) as String
        val wallet = MainActivity.currentWallet
        val walletBalance: TextView = findViewById(R.id.activity_wallet_add_withdraw_wallet_balance)
        walletBalance.text = "₹" + "  " + wallet!!.balance.toString()
        val addOrWithdrawMoneytextView: TextView =
            findViewById(R.id.activity_wallet_add_withdraw_add_or_withdraw_text)
        val amountAddOrWithdraw: EditText =
            findViewById(R.id.activity_wallet_add_or_withdraw_amount)
        val withDrawAddNavButton: TextView =
            findViewById(R.id.activity_wallet_add_withdraw_add_text)
        if (transType == "ADD") {
            addOrWithdrawMoneytextView.text = "Enter the amount to Add"
            amountAddOrWithdraw.background =
                ContextCompat.getDrawable(this, R.drawable.light_green_add_money)
            Toast.makeText(this, "Add", Toast.LENGTH_SHORT).show()

        } else if (transType == "WITHDRAW") {
            addOrWithdrawMoneytextView.text = "Enter the amount to Withdraw"
            amountAddOrWithdraw.background =
                ContextCompat.getDrawable(this, R.drawable.light_red_withdraw_background)
            withDrawAddNavButton.text = "WITHDRAW"
            Toast.makeText(this, "Withdraw", Toast.LENGTH_SHORT).show()
        }

        withDrawAddNavButton.setOnClickListener {
            if (amountAddOrWithdraw.getText().toString().trim().isEmpty()) {
                Toast.makeText(this, "Enter the amount", Toast.LENGTH_SHORT).show()
            }
            var amount = 0.0
            try {
                amount = amountAddOrWithdraw.text.toString().toDouble()

            } catch (e: Exception) {
                Toast.makeText(this, "Please enter the amount properly", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val uid = FirebaseAuth.getInstance().currentUser!!.uid
            val ref = FirebaseDatabase.getInstance().getReference("wallet_barber/$uid")
            if (transType == "ADD") {

                wallet.balance += amount
                ref.setValue(wallet).addOnSuccessListener {
                    Toast.makeText(
                        this,
                        "The wallet balance has been updated",
                        Toast.LENGTH_SHORT
                    ).show()
                    val timestamp = (System.currentTimeMillis() / 1000).toString()
                    val ref2 = FirebaseDatabase.getInstance()
                        .getReference("wallet_transactions/${wallet.walletId}/$timestamp")
                    val currentBalance = wallet.balance
                    val previousBalance = wallet.balance - amount

                    val sdf = SimpleDateFormat("dd/MM/yyyy hh:mm:ss")
                    val c = Calendar.getInstance()
                    val year = c.get(Calendar.YEAR)
                    val month = c.get(Calendar.MONTH)
                    val day = c.get(Calendar.DAY_OF_MONTH)
                    val hour = c.get(Calendar.HOUR_OF_DAY)
                    val minute = c.get(Calendar.MINUTE)
                    val date= "$day/$month/$year"
                    val timeTransaction= "$hour:$minute"
                    val valueTransaction=amount
                    val transactionId=timestamp
                    val transactionObject = WalletTransaction(
                        transactionId,
                        valueTransaction,
                        previousBalance,
                        currentBalance,
                        timeTransaction,
                        date,
                        timestamp,
                        transType

                    )
                    ref2.setValue(transactionObject).addOnSuccessListener {
                        MainActivity.currentWallet = wallet
                        val intent = Intent(this, BarberWalletActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                }.addOnFailureListener {
                    Toast.makeText(this, "Try again", Toast.LENGTH_SHORT).show()
                }

            } else if (transType == "WITHDRAW") {
                if (amount > wallet.balance) {
                    Toast.makeText(
                        this,
                        "Enter amount less than wallet balance",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setOnClickListener
                } else {
                    val ref = FirebaseDatabase.getInstance().getReference("wallet_barber/$uid")
                    wallet.balance -= amount
                    ref.setValue(wallet).addOnSuccessListener {
                        Toast.makeText(
                            this,
                            "The wallet balance has been updated",
                            Toast.LENGTH_SHORT
                        ).show()
                        val timestamp = (System.currentTimeMillis() / 1000).toString()
                        val ref2 = FirebaseDatabase.getInstance()
                            .getReference("wallet_transactions/${wallet.walletId}/$timestamp")
                        val currentBalance = wallet.balance
                        val previousBalance = wallet.balance + amount

                        val sdf = SimpleDateFormat("dd/MM/yyyy hh:mm:ss")
                        val c = Calendar.getInstance()
                        val year = c.get(Calendar.YEAR)
                        val month = c.get(Calendar.MONTH)
                        val day = c.get(Calendar.DAY_OF_MONTH)
                        val hour = c.get(Calendar.HOUR_OF_DAY)
                        val minute = c.get(Calendar.MINUTE)
                        val date= "$day/$month/$year"
                        val timeTransaction= "$hour:$minute"
                        val valueTransaction=0-amount
                        val transactionId=timestamp
                        val transactionObject = WalletTransaction(
                            transactionId,
                            valueTransaction,
                            previousBalance,
                            currentBalance,
                            timeTransaction,
                            date,
                            timestamp,
                            transType

                        )
                        ref2.setValue(transactionObject).addOnSuccessListener {
                            MainActivity.currentWallet = wallet
                            val intent = Intent(this, BarberWalletActivity::class.java)
                            startActivity(intent)
                            finish()
                        }.addOnFailureListener {
                            Toast.makeText(this,"Error",Toast.LENGTH_SHORT).show()
                        }

                    }.addOnFailureListener {
                        Toast.makeText(this, "Try again", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

    }
}


