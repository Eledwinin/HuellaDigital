package com.example.huelladigital.data.firebase

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

object FirebaseService {
    val db: FirebaseFirestore by lazy{
        Firebase.firestore
    }
}