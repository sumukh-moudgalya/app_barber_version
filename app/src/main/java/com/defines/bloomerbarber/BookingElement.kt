package com.defines.bloomerbarber

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.io.Serializable
import java.util.ArrayList

@Parcelize
class BookingElement(
    val order_id: String,
    val user_uid: String,
    val shop_uid: String,
    val total_cost: Double,
    val total_time: Double,
    val order_selected: ArrayList<ServiceOffered>,
    val date: String,
    val timeSlot: String,
    val timeStamp: String,
    var orderStatus: String,
    val isReviewed: Boolean,
    val rating:Double

) : Parcelable,
    Serializable {
    constructor() : this("", "", "", 0.0, 0.0, ArrayList<ServiceOffered>(), "", "", "", "", false,0.0)
}