package com.awesomenessstudios.schoolprojects.kiddolingo.models

import java.util.UUID

data class Parent(
    val userId: String = "",
    val name: String = "",
    val noOfChildren: Int = 0,
    val email: String = "",
    val dateJoined: String = ""
)
