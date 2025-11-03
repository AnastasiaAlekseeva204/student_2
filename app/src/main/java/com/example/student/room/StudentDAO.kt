package com.example.student.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface StudentDAO {
    @Query("SELECT * FROM student")
    fun getAll(): List<Student>

    @Insert
    fun insertAll(vararg students: Student)
    //передача данных из функции
    @Query("DELETE FROM student WHERE lastName = :lastName and firstName = :firstName and middleName = :middleName and gender = :gender and age = :age")
    fun delete(lastName: String, firstName: String, middleName: String,gender: Boolean,age: Int)

    @Query("UPDATE  student SET lastName = :newLastName, firstName = :newFirstName, middleName = :newMiddleName, gender = :newGender, age = :newAge WHERE  lastName = :oldLastName and firstName = :oldFirstName and  middleName = :oldMiddleName and gender = :oldGender and age = :oldAge")
    fun update(oldLastName: String, oldFirstName: String, oldMiddleName: String,oldGender: Boolean,oldAge: Int,newLastName: String,newFirstName: String ,newMiddleName: String, newGender: Boolean,newAge: Int)
}