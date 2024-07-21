package com.awesomenessstudios.schoolprojects.kiddolingo.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Word(
    //val id: String = "",
    val word: String = "",
    val score: String = "",
    val definition: String = "",
    val level: String = ""
): Parcelable
