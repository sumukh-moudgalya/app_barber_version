package com.defines.bloomerbarber

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.io.Serializable
import java.util.ArrayList

@Parcelize
class ServiceOffered(
    val name: String,
    val cost: Double,
    val avgTime: Int,
    val description: String,
    val timeStamp:String,
    val categories: ArrayList<String>
) : Parcelable, Serializable {
    constructor() : this("NULL", 0.0, 0, "NULL","", ArrayList<String>())
}