package com.defines.bloomerbarber
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.io.Serializable
@Parcelize
class ShopPromo(
    val uid: String,
    val usedCustomers : ArrayList<String>
):Parcelable,
    Serializable{
    constructor():this("",ArrayList<String>())
}