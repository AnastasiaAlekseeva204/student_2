package com.example.student.use_cases

import com.example.student.model.Student
import com.example.student.model.StudentList
import com.example.student.viewModel.StudentRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import javax.inject.Inject

class AddStudent @Inject constructor(
    private val model:  StudentList,
    private val studentRepository: StudentRepository
) {
    // suspend - может приостанавливаться
    // async - Эта функция применяется, когда надо получить из корутины некоторый результат.
    //await() -  ожидает, пока не будет получен результат
    suspend fun execute(
        scope: CoroutineScope,
        lastName: String,
        firstName: String,
        middleName: String,
        gender: Boolean,
        age: Int
    ) =
        Student(lastName, firstName, middleName, gender, age).let { student ->
            if (model.addStudent(student)) {
                val res = scope.async(Dispatchers.IO) {
                    studentRepository.insert(student)
                }.await()
                if (!res) {
                    model.removeStudent(student)
                }
                res
            } else {
                false
            }
        }
    }