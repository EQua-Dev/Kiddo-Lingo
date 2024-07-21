package com.awesomenessstudios.schoolprojects.kiddolingo.utils

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.awesomenessstudios.schoolprojects.kiddolingo.StrokeManager

object Common {

    val strokeManager = StrokeManager()
    var recognizedText: String = strokeManager.textRetrieved.toString()

    const val CATEGORY1 = "Category1"
    const val CATEGORY2 = "Category2"
    const val CATEGORY3 = "Category3"
    const val CATEGORY4 = "Category4"
    const val CATEGORY5 = "Category5"

    const val PARENTS_REF = "KiddoLingo Parents"
    const val KIDS_REF = "KiddoLingo Kids"
    const val QUIZ_REF = "KiddoLingo Quiz"

    val mAuth = FirebaseAuth.getInstance()


    val parentCollectionRef = Firebase.firestore.collection(PARENTS_REF)
    val kidCollectionRef = Firebase.firestore.collection(KIDS_REF)
    val quizCollectionRef = Firebase.firestore.collection(QUIZ_REF)

}