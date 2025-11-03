package com.example.student

import com.example.student.model.Student
import com.example.student.model.StudentList
import com.example.student.use_cases.RemoveStudent
import com.example.student.viewModel.StudentRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.assertFalse

class RemoveStudentUseCasesTest {
    private class MockStudentRepository : StudentRepository{
        class MockStudentRepositoryException: Exception()

        // Используется для отслеживания добавленных студентов (в случае, если это нужно для логики отката)
        private val _history = mutableListOf<Student>()
        val history : List<Student>
            get() = _history.toList()


        var deleteResult = true
        override fun delete(student: Student): Boolean {
            return deleteResult
        }

        override fun insert(student: Student): Boolean {
            _history.add(student) // Для отслеживания в случае отката, если понадобится
            return true
        }

        override fun update(
            oldStudent: Student,
            newStudent: Student
        ): Boolean {
            throw MockStudentRepositoryException()
        }


        override fun getAll(): Sequence<Student>? {
            throw MockStudentRepositoryException()
        }
    }


    private val testStudent = Student("Петров", "Иван", "Сергеевич", true, 22)

   //Успешное удаление студента
    @Test
    fun removeStudentSuccess() {
        val repository = MockStudentRepository()
        // Студент должен быть в модели перед попыткой удаления
        val model = StudentList()
        model.addStudent(testStudent)

        val useCase = RemoveStudent(model, repository)
        val scope = CoroutineScope(Dispatchers.Default)

        runBlocking {
            val res = useCase.execute(scope, testStudent)
            // Проверяем, что результат операции успешный
            assertEquals(true, res)
            // Проверяем, что студент удален из модели
            val list = model.list
            assertTrue(list.isEmpty(), "Список студентов в модели должен быть пустым")
        }
    }


    // Неудачное удаление из репозитория (требуется откат)
    @Test
    fun removeStudentRepositoryFailed() {
        val repository = MockStudentRepository()
        // Устанавливаем, что удаление из репозитория не удалось
        repository.deleteResult = false

        val model = StudentList()
        model.addStudent(testStudent)

        val useCase = RemoveStudent(model, repository)
        val scope = CoroutineScope(Dispatchers.Default)

        runBlocking {
            val res = useCase.execute(scope, testStudent)
            //  Проверяем, что общий результат операции неудачный
            assertEquals(false, res)

            // Проверяем, что студент был откатан и присутствует в модели
            val list = model.list
            assertFalse(list.isEmpty(), "Список студентов в модели не должен быть пустым после отката")
            assertEquals(1, list.size)
            assertEquals(testStudent, list.first(), "Откат не был выполнен, студент должен быть в списке")
        }
    }


    // Студент отсутствует в модели (нечего удалять)
    @Test
    fun removeStudentModelNotFound() {
        val repository = MockStudentRepository()
        val model = StudentList()

        val useCase = RemoveStudent(model, repository)
        val scope = CoroutineScope(Dispatchers.Default)

        runBlocking {
            val res = useCase.execute(scope, testStudent)
            // Проверяем, что результат операции неудачный, так как студента не было в модели
            assertEquals(false, res)

            val list = model.list
            assertTrue(list.isEmpty(), "Список студентов в модели должен быть пустым")
        }
    }
}