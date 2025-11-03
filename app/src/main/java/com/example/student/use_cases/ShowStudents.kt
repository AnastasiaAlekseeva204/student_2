package com.example.student.use_cases

import com.example.student.model.StudentList
import com.example.student.viewModel.ErrorLoad
import com.example.student.viewModel.OK
import com.example.student.viewModel.StudentRepository
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async

class ShowStudents @Inject constructor(
    private val model:  StudentList,
    private val studentRepository: StudentRepository
) {
    suspend fun execute(scope : CoroutineScope) =
        scope.async(Dispatchers.IO){
            studentRepository.getAll()?.forEach {
                model.addStudent(it)
            }?.run {
                true
            }?:run {
               false
            }
        }.await()
}