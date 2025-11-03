package com.example.student

import com.example.student.model.Student
import com.example.student.model.StudentList
import com.example.student.use_cases.AddStudent
import com.example.student.viewModel.StudentRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.junit.Test
import kotlin.test.assertEquals

class AddStudentUseCasesTest {
    private class MockStudentRepository : StudentRepository{
        class MockStudentRepositoryException: Exception()

        private val _history = mutableListOf<Student>()
        val history : List<Student>
                get() = _history.toList()
        //в момент чтения значений

        var result = true
        override fun insert(student: Student): Boolean {
           _history.add(student)
            return result
        }

        override fun update(
            oldStudent: Student,
            newStudent: Student
        ): Boolean {
            throw MockStudentRepositoryException()
        }

        override fun delete(student: Student): Boolean {
           throw MockStudentRepositoryException()
        }

        override fun getAll(): Sequence<Student>? {
           throw MockStudentRepositoryException()
        }
    }
    @Test
    fun addStudent() {
        val repository = MockStudentRepository()
        val model = StudentList()

        val useCase = AddStudent(model, repository)
        val scope = CoroutineScope(Dispatchers.Default)
        runBlocking {
            val res = useCase.execute(scope, "Савельев", "Виктор", "Михайлович", true, 24)
            assertEquals(true, res)
            val history = repository.history

            assertEquals(1, history.size)
            assertEquals(history.first(), Student("Савельев", "Виктор", "Михайлович", true, 24))
            val list = model.list
            assertEquals(1, list.size)
            assertEquals(list.first(), Student("Савельев", "Виктор", "Михайлович", true, 24))

            run {
                val res = useCase.execute(scope, "Савельев", "Виктор", "Михайлович", true, 24)
                assertEquals(false, res)
                val history = repository.history

                assertEquals(1, history.size)
                assertEquals(history.first(), Student("Савельев", "Виктор", "Михайлович", true, 24))
                val list = model.list
                assertEquals(1, list.size)
                assertEquals(list.first(), Student("Савельев", "Виктор", "Михайлович", true, 24))
            }
        }
    }
        @Test
        fun addStudentRepositoryFailed(){
            val repository = MockStudentRepository()
            repository.result = false
            val model = StudentList()

            val useCase = AddStudent(model,repository)
            val scope = CoroutineScope(Dispatchers.Default)
            runBlocking {
                val res = useCase.execute(scope, "Савельев", "Виктор", "Михайлович", true, 24)
                assertEquals(false,res)
                val history = repository.history
                assertEquals(1,history.size)
                assertEquals(history.first(),Student("Савельев","Виктор","Михайлович",true,24))
                val list = model.list
                assertEquals(true,list.isEmpty())
            }
        }
    }