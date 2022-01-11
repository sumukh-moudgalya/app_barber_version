package com.defines.bloomerbarber

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.io.Serializable

@Parcelize
class ServiceOffered(
    val name: String,
    val cost: Double,
    val avgTime: Int,
    val description: String,
    val timeStamp:String,
    val categories: HashMap<String,Boolean>
) : Parcelable, Serializable {
    constructor() : this("NULL", 0.0, 0, "NULL","", hashMapOf<String,Boolean>())
}