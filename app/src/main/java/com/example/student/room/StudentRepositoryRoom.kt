package com.example.student.room

import com.example.student.model.Student
import com.example.student.viewModel.StudentRepository
import java.sql.SQLDataException

//T возращает либо null либо ничего
class StudentRepositoryRoom(studentDatabase: StudentDatabase): StudentRepository {
    private val studentDAO = studentDatabase.studentDAO()
    private fun <T> executeStatementWithChecking(
        executor: () -> T
    ): T? {
        var result: T? = null
        try {
            result = executor()
        } catch (_: SQLDataException) { // _ = значит что переменная не интересует

        }
        return result
    }

    override fun insert(student: Student) = executeStatementWithChecking {
        studentDAO.insertAll(
            com.example.student.room.Student(
                lastName = student.lastName,
                firstName = student.firstName,
                middleName = student.middleName,
                gender = student.gender,
                age = student.age
            )
        )
    }!=null

    override fun update(
        oldStudent: Student,
        newStudent: Student
    ) = executeStatementWithChecking {
        studentDAO.update(
            oldStudent.lastName,
            oldStudent.firstName,
            oldStudent.middleName,
            oldStudent.gender,
            oldStudent.age,
            newStudent.lastName,
            newStudent.firstName,
            newStudent.middleName,
            newStudent.gender,
            newStudent.age
        )
    }!=null

    override fun delete(student: Student) = executeStatementWithChecking {
        studentDAO.delete(
            student.lastName,
            student.firstName,
            student.middleName,
            student.gender,
            student.age
        )
    }!=null

    //map  выполняет дополнительное действие (временно) преобразует в студента в моделе
    override fun getAll() = executeStatementWithChecking {
        studentDAO.getAll().asSequence().map { student ->
            Student(
                student.lastName,
                student.firstName,
                student.middleName,
                student.gender,
                student.age
            )
        }
    }
}
