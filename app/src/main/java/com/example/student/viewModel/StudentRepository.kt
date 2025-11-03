package com.example.student.viewModel

import com.example.student.model.Student

interface StudentRepository {
    fun insert(student: Student) :Boolean
    fun update(oldStudent: Student, newStudent: Student) : Boolean
    fun delete(student: Student) : Boolean
    fun getAll(): Sequence<Student>?
    //Sequence - последовательность, в отличие от листа мы не сохраняем данные,
    // а возращаем алгоритм получения данных
}