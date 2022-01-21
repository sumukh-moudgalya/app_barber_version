package com.defines.bloomerbarber

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.io.Serializable
@Parcelize
class Shop (
    val uid:String,
    val name_shop:String,
    val address:String,
    val city:String,
    val google_map_link:String,
    val shop_images: ArrayList<String>,
    val timings : HashMap<String, ArrayList<String>>,
    val categories : ArrayList<String>,
    val avgRating:Double,
    val numberOfRating:Int,
    val no_of_artists : Int

        ):Parcelable,
Serializable{
    constructor():this("","","","","", ArrayList<String>(),HashMap<String,ArrayList<String>>(),ArrayList<String>(),0.0,0,1)
}