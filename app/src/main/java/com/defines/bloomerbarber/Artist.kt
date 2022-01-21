package com.defines.bloomerbarber

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.io.Serializable
@Parcelize
class Artist(
    val artist_name: String,
    val profile_url : String
): Parcelable,
    Serializable {
        constructor():this("","")
    }

