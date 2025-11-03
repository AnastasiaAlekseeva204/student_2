package com.example.student.DI

import android.content.Context
import androidx.room.Room
import com.example.student.room.StudentDatabase
import com.example.student.room.StudentRepositoryRoom
import com.example.student.viewModel.StudentRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent


@Module
@InstallIn(SingletonComponent::class)
class RoomModule {

    @Provides
    fun provideStudentRepository(@ApplicationContext
                                 applicationContext: Context
    )
    : StudentRepository {
        val StudentDatabase = Room.databaseBuilder(
            applicationContext,
            StudentDatabase::class.java, "student",
        ).build()
       return StudentRepositoryRoom(StudentDatabase)
    }
}