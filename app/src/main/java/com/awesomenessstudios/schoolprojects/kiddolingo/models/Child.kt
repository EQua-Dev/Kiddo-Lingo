package com.awesomenessstudios.schoolprojects.kiddolingo.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.UUID

@Parcelize
data class Child(
    val id: String = UUID.randomUUID().toString(),
    val name: String = "",
    val parentID: String = "",
    val dateAdded: String = "",
    val age: String = "",
    val ageGroup: String = "",
    val lastPlayedDate: String = "",
) : Parcelable
