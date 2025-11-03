package com.example.student.room
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
// entiti - табличка
@Entity(tableName = "student")
data class Student (
    @ColumnInfo(name = "lastName") val lastName: String,

    @ColumnInfo(name = "firstName") val firstName: String,

    @ColumnInfo(name = "middleName") val middleName: String,
    @ColumnInfo(name = "gender") val gender: Boolean,
    @ColumnInfo("age" ) val age: Int
    ){
    @PrimaryKey(autoGenerate = true) var uid: Int? = null
    //uid - уникальный айди
}

