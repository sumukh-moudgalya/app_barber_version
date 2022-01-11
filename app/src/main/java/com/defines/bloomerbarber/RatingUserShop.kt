package com.defines.bloomerbarber

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.io.Serializable

@Parcelize
class RatingUserShop(
    val user_uid:String,
    val shop_uid:String,
    var rating:Double,
    val order_id:String,
    val timestamp:String,
    var numberOfReviews: Int
): Parcelable,
    Serializable {
    constructor():this("","",0.0,"","",0)
}