package com.awesomenessstudios.schoolprojects.kiddolingo.utils

import com.awesomenessstudios.schoolprojects.kiddolingo.models.Category
import kiddiesgame.kotlin.models.Difficulty
import java.util.Calendar

object HelperServices {

    fun whatAgeCategoryIsThisChild(age: Int): Category?{

        when(age) {
            in 4..6 -> {
                return Category.FourToSixInstance
            }
            in 7..9 -> {
                return Category.SevenToNineInstance
            }
            in 10..12 -> {
                return Category.TenToTwelveInstance
            }
            in 13..15 -> {
                return Category.ThirteenToFifteenInstance
            }
            in 16..18 -> {
                return Category.SixteenToEighteenInstance
            }
        }
        return null
    }


     fun getCategoryDetailsById(id: Int): Category? {
         return when (id) {
             Category.FourToSix().id -> Category.FourToSixInstance
             Category.SevenToNine().id -> Category.SevenToNineInstance
             Category.TenToTwelve().id -> Category.TenToTwelveInstance
             Category.ThirteenToFifteen().id -> Category.ThirteenToFifteenInstance
             Category.SixteenToEighteen().id -> Category.SixteenToEighteenInstance
             // Add cases for other subclasses similarly
             else -> null
         }
     }

     fun getDifficultyDetailsByTitle(title: String): Difficulty? {
         return when (title) {
             Difficulty.Easy().title -> Difficulty.EasyInstance
             Difficulty.Medium().title -> Difficulty.MediumInstance
             Difficulty.Hard().title -> Difficulty.HardInstance

             else -> null
         }
     }

    fun getGreeting(name: String): String {
        val calendar = Calendar.getInstance()
        val hourOfDay = calendar.get(Calendar.HOUR_OF_DAY)

        return when (hourOfDay) {
            in 0..11 -> "Good morning\n$name"
            in 12..17 -> "Good afternoon\n$name"
            else -> "Good evening\n$name"
        }
    }

 }