package com.awesomenessstudios.schoolprojects.kiddolingo.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class GameDoneDetailsToParse(
    val childId: String,
    val totalScore: String,
    val correctWords: List<Word>,
    val wrongWords: List<Word>,
    val totalWords: String,
    val difficulty: String
): Parcelable
