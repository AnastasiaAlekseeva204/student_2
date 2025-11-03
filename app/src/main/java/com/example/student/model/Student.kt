package com.example.student.model

import kotlinx.serialization.Serializable

@Serializable
data class Student(
    val lastName: String,
    val firstName: String,
    val middleName: String,
    val gender: Boolean,
    val age: Int
)