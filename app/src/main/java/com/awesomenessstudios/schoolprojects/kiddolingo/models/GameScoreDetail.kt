package com.awesomenessstudios.schoolprojects.kiddolingo.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.UUID

@Parcelize
data class GameScoreDetail(
    val lastPlayed: String = "",
    val highScore: String = "0",
    val lastScore: String = "0"
): Parcelable
