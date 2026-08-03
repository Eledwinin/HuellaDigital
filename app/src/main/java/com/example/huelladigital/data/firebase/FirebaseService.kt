package com.example.huelladigital.data.firebase

import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore

object FirebaseService {
    val db: FirebaseFirestore by lazy{
        Firebase.firestore
    }
    val auth: FirebaseAuth by lazy {
        Firebase.auth
    }
}