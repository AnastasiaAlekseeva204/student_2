package com.example.student


import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.room.Room
import com.example.student.model.Student
import com.example.student.room.StudentDatabase
import com.example.student.ui.theme.StudentTheme
import com.example.student.viewModel.ErrorAddNote
import com.example.student.viewModel.ErrorChangeNote
import com.example.student.viewModel.ErrorRemoveNote
import com.example.student.viewModel.NotAppViewModel
import com.example.student.viewModel.OK
import kotlinx.serialization.Serializable
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import com.example.student.room.StudentRepositoryRoom
import com.example.student.viewModel.ErrorLoad
import dagger.hilt.android.AndroidEntryPoint


@Serializable
class ListStudentNavigation

@Serializable
class AddStudentNavigation

@Serializable
class ChangeStudentNavigation(val lastName: String,
                              val firstName: String,
                              val middleName: String,
                              val gender: Boolean,
                              val age: Int)

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        /*val StudentDatabase = Room.databaseBuilder(
            applicationContext,
            StudentDatabase::class.java, "students",
        ).build()*/
        //val studentRepository = StudentRepositoryRoom(StudentDatabase)
        val notAppViewModel: NotAppViewModel by viewModels()
        //notAppViewModel.setStudentRepository(studentRepository)
        setContent {
            StudentTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MainUI(
                        notAppViewModel,
                        modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}




@Composable
fun MainUI(notAppViewModel: NotAppViewModel,
    modifier: Modifier = Modifier) {
    val studentList by notAppViewModel.listFlow.collectAsState()
    val status by notAppViewModel.statusFlow.collectAsState()
    val statusString = when(status){
        ErrorAddNote -> "Ошибка добавления студента"
        ErrorChangeNote -> "Ошибка изменения студента"
        ErrorRemoveNote -> "Ошибка удаления студента"
        OK -> null
        ErrorLoad -> "Ошибка чтения списка студентов"
    }
    //NavController говорит, куда идти, а NavHost показывает соответствующий composable экран.
    //Composable-функция, связанная с конкретными маршрутами в  навигации.
    //создание навигации(контроллер навигации)
    val navController = rememberNavController()
    NavHost(navController,
        startDestination = ListStudentNavigation()){
        composable<ListStudentNavigation>{
            StudentListScreen(studentList,
                statusString,
                {navController.navigate(AddStudentNavigation())},
                onChangeStudent = {student-> navController.navigate(ChangeStudentNavigation(student.lastName,
                    student.firstName,student.middleName,
                    student.gender,
                    student.age))},
                {student->notAppViewModel.removeStudent(student)},
                modifier)
        }
        composable<AddStudentNavigation>{
            StudentCreateScreen({lastName,firstName,middleName,gender,age->
                notAppViewModel.addStudent(lastName,firstName,middleName,gender,age)
                navController.navigate(ListStudentNavigation())
            })

        }
         composable<ChangeStudentNavigation>{
           val arguments = it.arguments
             if (arguments!=null){
                 val lastName = arguments.getString("lastName")
                 val firstName = arguments.getString("firstName")
                 val middleName = arguments.getString("middleName")
                 val gender = arguments.getBoolean("gender")
                 val age = arguments.getInt("age")
                 if (lastName!=null && firstName!=null && middleName!=null  && age!=0){
                     StudentChangeScreen(lastName,firstName,middleName,
                         gender,age,
                         onChange={newLastName,newFirstName,newMiddleName,newGender,newAge->
                             notAppViewModel.changeStudent(Student(lastName,firstName,middleName,gender,age.toInt()),
                                 newLastName,newFirstName,newMiddleName,newGender,newAge)
                             navController.navigate(ListStudentNavigation())
                         })
                 }
             }
         }
    }
}
@Composable
fun StudentChangeScreen(lastNameStart: String, firstNameStart: String, middleNameStart: String, genderStart: Boolean, ageStart: Int, onChange:(String, String, String, Boolean, Int)-> Unit, modifier: Modifier = Modifier) {
    // Используем rememberSaveable для сохранения состояния полей при повороте экрана
    var lastName by rememberSaveable { mutableStateOf(lastNameStart) }
    var firstName by rememberSaveable { mutableStateOf(firstNameStart) }
    var middleName by rememberSaveable { mutableStateOf(middleNameStart) }
    var gender by rememberSaveable { mutableStateOf(genderStart) }
    var isMale by rememberSaveable {
        mutableStateOf(genderStart == true)
    }
    if(gender == true){
        isMale = true
    }
    else{
        isMale = false
    }
    var age by rememberSaveable { mutableStateOf(ageStart) }

    Column(modifier.padding(16.dp)) {
        TextField(
            value = lastName,
            onValueChange = { lastName = it },
            label = { Text("Фамилия") }
        )
        TextField(
            value = firstName,
            onValueChange = { firstName = it },
            label = { Text("Имя") }
        )
        TextField(
            value = middleName,
            onValueChange = { middleName = it },
            label = { Text("Отчество") }
        )
        Row{
            //Checkbox представляет флажок, который может быть в отмеченном и неотмеченном состоянии
            Checkbox(
                checked = gender,
                onCheckedChange = {
                    gender = true
                }
            )
            Text("мужской")
            Checkbox(
                checked = !gender,
                onCheckedChange = {
                    gender = false
                }
            )
            Text("женский")
        }
        // row -  располагает вложенные компоненты в строку
        TextField(
            value = age.toString(),
            onValueChange = { age = it.toInt() },
            label = { Text("Возраст") }
        )
        Button(onClick = {onChange(lastName,firstName,middleName,gender,age.toInt())})
        {Text("Изменить студента")}
    }
}
@Composable
fun StudentCreateScreen(onCreate:(String,String,String,Boolean,Int)-> Unit, modifier: Modifier = Modifier) {
    // Используем rememberSaveable для сохранения состояния полей при повороте экрана
    var lastName by rememberSaveable { mutableStateOf("") }
    var firstName by rememberSaveable { mutableStateOf("") }
    var middleName by rememberSaveable { mutableStateOf("") }
    var gender by rememberSaveable { mutableStateOf(true) }
    var age by rememberSaveable { mutableStateOf("") }
    var isMale by rememberSaveable { mutableStateOf(true) }

    Column(modifier.padding(16.dp)) {
        TextField(
            value = lastName,
            onValueChange = { lastName = it },
            label = { Text("Фамилия") }
        )
        TextField(
            value = firstName,
            onValueChange = { firstName = it },
            label = { Text("Имя") }
        )
        TextField(
            value = middleName,
            onValueChange = { middleName = it },
            label = { Text("Отчество") }
        )
        Row{
            Checkbox(
                checked = gender,
                onCheckedChange = {gender = true}
            )
            Text("мужской")
            Checkbox(
                checked = !gender,
                onCheckedChange = {gender = false}
            )
            Text("женский")
        }
        TextField(
            value = age,
            onValueChange = { age = it },
            label = { Text("Возраст") }
        )
        Button(onClick = {
            //val gender: Boolean = if (isMale) "мужской" else "женский"
            onCreate(lastName,firstName,middleName,gender,age.toInt())})
        {Text("Добавь студента")}
    }
}

@Composable
fun StudentListScreen(studentList: List<Student>,
                      statusString: String?,
                      onAddStudent:()-> Unit,
                      onChangeStudent:(Student)->Unit,
                      onRemoveStudent:(Student)-> Unit,
                      modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
    ) {
        Text(
            "Список студентов",
            fontSize = 36.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 8.dp)
        )
        Button(onClick = onAddStudent){
            Text("Добавить студента",
                fontSize = 20.sp)
        }
        if (statusString !=null){
            Text(
                text = statusString,
                color = Color.Red,
                fontSize = 20.sp
            )
        }
        //создаёт список с вертикальной прокруткой
       LazyColumn(modifier.padding(16.dp)) {
            items(studentList) { student ->
                Row(modifier = Modifier.padding(vertical = 4.dp)) {
                    Text(
                        text = "ФИО: ${student.lastName} ${student.firstName} ${student.middleName}",
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = "Пол: ${if (student.gender) "мужской" else "женский"}",
                        modifier = Modifier.weight(0.7f)
                    )
                    Text(
                        text = "Возраст: ${student.age}",
                        modifier = Modifier.weight(0.5f)
                    )
                    Button(onClick = {onChangeStudent(student)}){
                        Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit student")
                    }
                    Button(onClick = {onRemoveStudent(student)}){
                        Text("X",
                            fontSize = 20.sp)
                    }

                }
            }
        }
    }
}
