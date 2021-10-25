package com.defines.bloomerbarber
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.io.Serializable
@Parcelize
class Wallet(
    val walletId:String,
    var balance: Double

        ): Parcelable,
    Serializable{
        constructor():this("",0.0)
}