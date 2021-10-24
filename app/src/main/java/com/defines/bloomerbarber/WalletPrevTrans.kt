package com.defines.bloomerbarber

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.io.Serializable
@Parcelize
class WalletPrevTrans(
    var transactionId : String,
    var valueTransaction : Double,
    var prevBalance : Double,
    var currentBalance : Double,
    var transactionTime : String,
    var transactionDate : String,
    var timeStamp : String
): Parcelable,Serializable {
    constructor():this("",0.0,0.0,0.0,"","","")
}