package com.example.student.use_cases

import com.example.student.model.Student
import com.example.student.model.StudentList
import com.example.student.viewModel.StudentRepository
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async

class ChangeStudent @Inject constructor(
    private val model:  StudentList,
    private val studentRepository: StudentRepository
){
    suspend fun execute(scope: CoroutineScope, student: Student,lastName: String, firstName: String, middleName:String, gender:Boolean, age:Int): Boolean{

    val newStudent = Student(lastName, firstName, middleName, gender, age)
    return if (model.changeStudent(student, newStudent)) {
        val res = scope.async(Dispatchers.IO) {
             (studentRepository.update(
                    student,newStudent))
        }.await()
        if(!res){
            model.changeStudent(student,newStudent)
        }
        res
            } else {
                false
            }
    }
}
