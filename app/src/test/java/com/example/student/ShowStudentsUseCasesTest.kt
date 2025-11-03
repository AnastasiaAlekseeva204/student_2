package com.example.student

import com.example.student.model.Student
import com.example.student.model.StudentList
import com.example.student.use_cases.ShowStudents
import com.example.student.viewModel.StudentRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.assertFalse

class ShowStudentsUseCasesTest {

    private class MockStudentRepository : StudentRepository{
        class MockStudentRepositoryException: Exception()

        // Настройка возвращаемого значения для getAll(). null означает ошибку загрузки/отсутствие данных.
        var studentsToReturn: Sequence<Student>? = null

        override fun getAll(): Sequence<Student>? {
            return studentsToReturn
        }

        override fun insert(student: Student): Boolean {
            throw MockStudentRepositoryException()
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
    }

    private val student1 = Student("Абрамов", "Андрей", "Андреевич", true, 25)
    private val student2 = Student("Борисова", "Ольга", "Игоревна", false, 23)

    @Test
    fun showStudentsSuccess() {
        val repository = MockStudentRepository()
        repository.studentsToReturn = sequenceOf(student1, student2)

        val model = StudentList()

        val useCase = ShowStudents(model, repository)
        val scope = CoroutineScope(Dispatchers.Default)

        runBlocking {
            val res = useCase.execute(scope)

            //  Проверяем, что результат операции успешный (true)
            assertTrue(res, "Метод должен вернуть true при успешной загрузке")

            //  Проверяем, что модель содержит загруженных студентов
            val list = model.list
            assertEquals(2, list.size)
            assertTrue(list.contains(student1))
            assertTrue(list.contains(student2))
        }
    }


    // Репозиторий возвращает null (ошибка загрузки/нет данных)
    @Test
    fun showStudentsRepositoryReturnsNull() {
        val repository = MockStudentRepository()
        // Настраиваем mock, чтобы вернуть null
        repository.studentsToReturn = null

        val model = StudentList()

        val useCase = ShowStudents(model, repository)
        val scope = CoroutineScope(Dispatchers.Default)

        runBlocking {
            val res = useCase.execute(scope)
            assertFalse(res, "Метод должен вернуть false, если getAll() вернул null")
            assertTrue(model.list.isEmpty(), "Модель должна остаться пустой при неудаче загрузки")
        }
    }



    //Репозиторий возвращает пустую последовательность
    @Test
    fun showStudentsRepositoryReturnsEmptySequence() {
        val repository = MockStudentRepository()
        // Настраиваем mock, чтобы вернуть пустую последовательность
        repository.studentsToReturn = emptySequence()

        val model = StudentList()

        val useCase = ShowStudents(model, repository)
        val scope = CoroutineScope(Dispatchers.Default)

        runBlocking {
            val res = useCase.execute(scope)

            assertTrue(res, "Метод должен вернуть true, даже если список пуст (последовательность не null)")
            assertTrue(model.list.isEmpty(), "Модель должна быть пустой, так как загружен пустой список")
        }
    }
}