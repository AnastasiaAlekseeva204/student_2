package com.example.student

import com.example.student.model.StudentList
import com.example.student.model.Student
import org.junit.Assert.assertEquals
import org.junit.Test

class ListStudentTest {
    @Test
    fun addStudent() {
        val listStudent = StudentList()
        val res = listStudent.addStudent(Student("Ivanov", "Vlad", "Vachislavovich", true, 21))
        assertEquals(true, res)
        val list = listStudent.list
        assertEquals(1, list.size)
        assertEquals(
            list.first(),
            Student(
                "Ivanov",
                "Vlad",
                "Vachislavovich",
                true,
                21
            )
        )
        run {
            val res = listStudent
                .addStudent(Student("Vasilkov", "Vasiliy", "Vladimirovich", true, 22))
            assertEquals(true, res)
        }
        run {
            val list = listStudent.list
            assertEquals(2, list.size)
            assertEquals(
                list.first(),
                Student(
                    "Ivanov",
                    "Vlad",
                    "Vachislavovich",
                    true,
                    21
                )
            )
            assertEquals(
                list.last(),
                Student(
                    "Vasilkov",
                    "Vasiliy",
                    "Vladimirovich",
                    true,
                    22
                )
            )
        }
        run {
            val res = listStudent.addStudent(Student("Ivanov", "Vlad", "Vachislavovich", true, 21))
            assertEquals(false, res)
        }
        run {
            val list = listStudent.list
            assertEquals(2, list.size)
            assertEquals(
                list.first(),
                Student(
                    "Ivanov",
                    "Vlad",
                    "Vachislavovich",
                    true,
                    21
                )
            )
            assertEquals(
                list.last(),
                Student(
                    "Vasilkov",
                    "Vasiliy",
                    "Vladimirovich",
                    true,
                    22
                )
            )
        }
    }

    @Test
    fun changeStudent() {
        val listStudent = StudentList()

        // Студент, которого будем менять
        val oldStudent = Student("Козлов", "Дмитрий", "Андреевич", true, 22)
        // Новые данные (изменили возраст и статус)
        val newStudent = Student("Козлов", "Дмитрий", "Андреевич", false, 23)
        listStudent.addStudent(oldStudent)
        //  Изменение
        val res = listStudent
            .changeStudent(oldStudent, newStudent)

        assertEquals(true, res)
        val list = listStudent.list
        assertEquals(1, list.size)
        assertEquals(list.first(), newStudent)

        run {

            //Попытка изменить несуществующего студента

            val listStudent = StudentList()

            // Студент, который есть в списке
            val existingStudent = Student("Волкова", "Анна", "Викторовна", true, 24)
            listStudent.addStudent(existingStudent)

            // Студент, которого нет в списке (пытаемся его изменить)
            val nonExistentStudent = Student("Сидорова", "Мария", "Ивановна", true, 20)
            // Новые данные (не важны, т.к. старый не найден)
            val newStudent = Student("Сидорова", "Мария", "Ивановна", true, 21)

            //  Попытка изменения несуществующего студента
            val res = listStudent.changeStudent(nonExistentStudent, newStudent)

            assertEquals(false, res)
            val list = listStudent.list
            assertEquals(1, list.size)
            assertEquals(list.first(), existingStudent)
        }
        run {
            val listStudent = StudentList()
            // Студент 1 (тот, которого хотим изменить)
            val targetStudent = Student("Федоров", "Павел", "Денисович", false, 23)
            // Студент 2 (тот, на кого хотим поменять targetStudent, но он УЖЕ есть в списке!)
            val existingStudent = Student("Морозова", "Ольга", "Геннадьевна", true, 20)

            listStudent.addStudent(targetStudent)
            listStudent.addStudent(existingStudent)

            //  Попытка изменить targetStudent на существующего existingStudent (создать дубликат)
            val res = listStudent.changeStudent(targetStudent, existingStudent)
            assertEquals(false, res) // Должен вернуть false из-за дубликата

            val list = listStudent.list
            assertEquals(2, list.size)

            // Проверяем, что список остался в первоначальном состоянии (без изменений)
            assertEquals(list[0], targetStudent)
            assertEquals(list[1], existingStudent)
        }
    }

    @Test
    fun removeStudent() {
        val listStudent = StudentList()

        // Создание студентов
        val studentMaria = Student("Сидорова", "Мария", "Ивановна", true, 20)
        val studentAnna = Student("Волкова", "Анна", "Викторовна", true, 24)
        val studentMaksim = Student("Зайцев", "Максим", "Игоревич", true, 21)

        listStudent.addStudent(studentMaria)
        listStudent.addStudent(studentAnna)
        listStudent.addStudent(studentMaksim)

        val res = listStudent.removeStudent(studentAnna)


        assertEquals(true, res)
        val list = listStudent.list
        assertEquals(2, list.size)

        assertEquals(true, list.contains(studentMaria))
        assertEquals(true, list.contains(studentMaksim))

        // Проверяем, что удаленный студент ОТСУТСТВУЕТ (ожидаем 'false')
        assertEquals(false, list.contains(studentAnna))

        run {
            val listStudent = StudentList()

            // Студент, который есть в списке
            val studentOlga = Student("Морозова", "Ольга", "Геннадьевна", true, 20)
            listStudent.addStudent(studentOlga)

            // Студент, которого нет в списке (попытаемся его удалить)
            val nonExistentPavel = Student("Федоров", "Павел", "Денисович", false, 23)

            // пытаемся удалить несуществующего студента
            val res = listStudent.removeStudent(nonExistentPavel)

            assertEquals(false, res)
            val list = listStudent.list
            assertEquals(1, list.size)
            assertEquals(list.first(), studentOlga)
        }
    }
}
