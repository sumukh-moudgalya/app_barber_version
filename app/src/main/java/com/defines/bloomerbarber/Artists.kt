package com.defines.bloomerbarber
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.io.Serializable

@Parcelize
class Artists(
    val uid:String,
    val artistsName:String,
    val email:String,
    val phoneNumber:String,
    val photoUrl:String
) : Parcelable,
    Serializable{

        constructor():this("","","","","")
}