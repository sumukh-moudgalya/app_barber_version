package com.defines.bloomerbarber
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.io.Serializable

@Parcelize
class Customer(
    val uid: String,
    val username: String,
    var profileImageUrl: String,
    val email: String,
    var default_language: String,
    var userType: String,
    var phoneNumber: String
) : Parcelable,
    Serializable {
    constructor() : this("", "", "", "", default_language = "english", "customer", "")
}