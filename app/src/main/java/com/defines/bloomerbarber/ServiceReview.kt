package com.defines.bloomerbarber
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.io.Serializable

@Parcelize
class ServiceReview(
    val id:String,
    val review:String,
    val rating:Double,
    val serviceId:String,
    val userId:String,
    val shopId:String
): Parcelable,
    Serializable {
    constructor():this("","",0.0,"","","")
}