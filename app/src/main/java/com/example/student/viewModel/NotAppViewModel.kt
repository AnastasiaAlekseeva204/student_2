package com.example.student.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.student.model.Student
import com.example.student.model.StudentList
import com.example.student.use_cases.AddStudent
import com.example.student.use_cases.ChangeStudent
import com.example.student.use_cases.RemoveStudent
import com.example.student.use_cases.ShowStudents
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

//use-case это то для чего нужно приложение
//sealed - означает, что классы наследники нельзя будет добавить снаружи
sealed interface Status
object OK: Status
object ErrorAddNote: Status
object ErrorChangeNote: Status
object ErrorRemoveNote: Status
object ErrorLoad: Status
@HiltViewModel
class  NotAppViewModel @Inject constructor(
    private val addStudentCase : AddStudent,
    private val changeStudentCase: ChangeStudent,
    private val removeStudentCase: RemoveStudent,
    private val showStudentsCase: ShowStudents,
    private val model: StudentList,
) : ViewModel() {
    private val _listFlow = MutableStateFlow<List<Student>>(value = emptyList()) //
    val listFlow = _listFlow.asStateFlow()
    private val _statusFlow = MutableStateFlow<Status>(value = OK)
    val statusFlow = _statusFlow.asStateFlow()

    //launch  — билдер корутин, который запускает новую корутину и немедленно возвращает объект Job,
// не блокируя текущий поток.
    fun addStudent(
        lastName: String,
        firstName: String,
        middleName: String,
        gender: Boolean,
        age: Int
    ) {
        viewModelScope.launch {
            if (addStudentCase.execute(
                    viewModelScope,
                    lastName,
                    firstName,
                    middleName,
                    gender,
                    age
                )
            ) {
                _listFlow.value = model.list
                _statusFlow.value = OK
            } else {
                _statusFlow.value = ErrorAddNote
            }
        }
    }

    fun changeStudent(
        student: Student, lastName: String,
        firstName: String,
        middleName: String,
        gender: Boolean,
        age: Int
    ) {
        viewModelScope.launch {
            if (changeStudentCase.execute(
                    viewModelScope,
                    student,
                    lastName,
                    firstName,
                    middleName,
                    gender,
                    age
                )
            ) {
                _listFlow.value = model.list
                _statusFlow.value = OK
            } else {
                _statusFlow.value = ErrorChangeNote
            }
        }
    }

    // launch  — билдер корутин, который запускает новую корутину и немедленно возвращает объект Job,
    // не блокируя текущий поток.
    // корутины -  это блоки кода, которые выполняются асинхронно и не блокируют поток, из которого они запускаются.
    fun removeStudent(student: Student) {
        viewModelScope.launch {
            if (removeStudentCase.execute(viewModelScope, student)) {
                _listFlow.value = model.list
                _statusFlow.value = OK
            } else {
                _statusFlow.value = ErrorRemoveNote
            }
        }
    }
    // lateinit - снижает производительность, позволяет обьявлять свойства без инициализации
    // ?. работатет так: если результат был null, то он ничего не делает,
    // возращает null, если был не null то выполняется forEach(для которого) и делает все для записи студента
    // выполняем дополнительно вывод списка и статуса окей (.)
    // : - если результат предыдушего не нулл то ничего не делает, но если результат нулл, то он запускает функцию,
    // которая у нс делает статус errorload

    init {
        viewModelScope.launch {
            if (showStudentsCase.execute(viewModelScope)) {
                _listFlow.value = model.list
                _statusFlow.value = OK
            } else {
                _statusFlow.value = ErrorLoad
            }

        }
    }
}
