package com.example.student.model

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StudentList @Inject constructor() {
    //val - не могу менять обьект списка
    //но сам список менять могу

    private val _list = mutableListOf<Student>()
    val list: List<Student>
        get() = _list.toList()
//.none - Она позволяет проверить, что ни один из элементов списка не соответствует определённому условию
    fun addStudent(student: Student) = if (
        _list.none { it == student }) {
        _list.add(student)
    } else {
        false
    }

    fun changeStudent(oldStudent: Student, newStudent: Student) =
        if (_list.filter { it != oldStudent } //filter здесь нужен, чтобы исключить из проверки на дублирование
            // старыХ объектов oldStudent, который собираются  заменяться.
            .none
            { it == newStudent })
        {
            val index = _list.indexOf(oldStudent)
            if (index != -1) {
                _list[index] = newStudent
                true
            }
            else {
                false
            }
        } else {
            false
        }

    fun removeStudent(student: Student) = _list.remove(student)

}