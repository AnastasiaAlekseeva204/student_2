package com.example.student.use_cases

import com.example.student.model.Student
import com.example.student.model.StudentList
import com.example.student.viewModel.StudentRepository
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async

class RemoveStudent @Inject constructor (
    private val model:  StudentList,
    private val studentRepository: StudentRepository
) {
    suspend fun execute(scope: CoroutineScope, student: Student) =
        if (model.removeStudent(student)) {
            scope.async(Dispatchers.IO) {
                if (studentRepository.delete(student)) {
                    true
                } else {
                    model.addStudent(student)
                    false
                }
            }.await()
        } else {
            false
        }
}