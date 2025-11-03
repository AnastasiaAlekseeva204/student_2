package com.example.student

import com.example.student.model.Student
import com.example.student.model.StudentList
import com.example.student.use_cases.AddStudent
import com.example.student.use_cases.ChangeStudent
import com.example.student.use_cases.RemoveStudent
import com.example.student.use_cases.ShowStudents
import com.example.student.viewModel.ErrorAddNote
import com.example.student.viewModel.ErrorChangeNote
import com.example.student.viewModel.ErrorRemoveNote
import com.example.student.viewModel.NotAppViewModel
import com.example.student.viewModel.OK
import com.example.student.viewModel.StudentRepository
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.test.setMain
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import kotlin.test.Test
import kotlin.test.assertEquals

private fun Dispatchers.resetMain() {
    TODO("Not yet implemented")
}

class NotAppViewModelTest {
    private class MockStudentRepository : StudentRepository {
        class MockStudentRepositoryException : Exception()

        private val _history = mutableListOf<Student>()
        val history: List<Student>
            get() = _history.toList()

        //в момент чтения значений
        private val _saved = mutableListOf<Student>()
        var result = true
        override fun insert(student: Student): Boolean {
            _history.add(student)
            if (result) _saved.add(student)
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
            return _saved.asSequence()
        }
    }

    @OptIn(DelicateCoroutinesApi::class, ExperimentalCoroutinesApi::class)
    private val mainThreadSurrogate = newSingleThreadContext("UI thread")

    @Before
    fun setUp() {
        Dispatchers.setMain(mainThreadSurrogate)
    }

    @Test
    fun addStudent() {
        Dispatchers.setMain(mainThreadSurrogate)
        val model = StudentList()
        val repository = MockStudentRepository()
        val addStudentUseCase = AddStudent(model, repository)
        val changeStudentUseCase = ChangeStudent(model, repository)
        val removeStudentUseCase = RemoveStudent(model, repository)
        val showStudentsUseCase = ShowStudents(model, repository)

        runBlocking {
            val viewModel = NotAppViewModel(
                addStudentUseCase,
                changeStudentUseCase,
                removeStudentUseCase,
                showStudentsUseCase,
                model
            )
            delay(500)
            viewModel.addStudent("Савельев", "Виктор", "Михайлович", true, 24)

            delay(500)
            val list = viewModel.listFlow.value
            val status = viewModel.statusFlow.value

            assertEquals(1, list.size)
            assertEquals(
                Student("Савельев", "Виктор", "Михайлович", true, 24),
                list.first()
            )
            assertEquals(OK, status)
            viewModel.addStudent("Савельев", "Виктор", "Михайлович", true, 24)
            delay(500)
            run {
                val list = viewModel.listFlow.value
                val status = viewModel.statusFlow.value

                assertEquals(1, list.size)
                assertEquals(
                    Student("Савельев", "Виктор", "Михайлович", true, 24),
                    list.first()
                )
                assertEquals(ErrorAddNote, status)
            }
            viewModel.addStudent("Савельев", "Виктор", "Михайлович", true, 24)
            delay(500)
            run {
                val list = viewModel.listFlow.value
                val status = viewModel.statusFlow.value

                assertEquals(1, list.size)
                assertEquals(
                    Student("Савельев", "Виктор", "Михайлович", true, 24),
                    list.first()
                )
                assertEquals(ErrorChangeNote, status)
            }
            viewModel.addStudent("Савельев", "Виктор", "Михайлович", true, 24)
            delay(500)
            run {
                val list = viewModel.listFlow.value
                val status = viewModel.statusFlow.value

                assertEquals(1, list.size)
                assertEquals(
                    Student("Савельев", "Виктор", "Михайлович", true, 24),
                    list.first()
                )
                assertEquals(ErrorRemoveNote, status)
            }

        }

    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @After
    fun tearDown() {
        Dispatchers.resetMain() // reset the main dispatcher to the original Main dispatcher
        mainThreadSurrogate.close()
    }
}

