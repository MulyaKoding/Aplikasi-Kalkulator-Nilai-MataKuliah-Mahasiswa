package org.d3if4034.kalkulator_nilai_matakuliah.viewmodel


import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.*
import org.d3if4034.kalkulator_nilai_matakuliah.database.Student
import org.d3if4034.kalkulator_nilai_matakuliah.database.StudentDao

class StudentViewModel(
    val database: StudentDao, application: Application
) : AndroidViewModel(application) {

    var student = database.getAllStudents()
    private var viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    fun onClickInsert(
        studentName: String,
        studentId: String,
        studentClass: String,
        scoreT: Float,
        scoreA1: Float,
        scoreA2: Float,
        scoreA3: Float,
        scoreP: Float,
        presence: Float,
        isFinished: Boolean,
        finalScore: Float,
        finalIndex: String,
        passStatus: String
    ) {
        uiScope.launch {
            val student = Student(
                0,
                studentName,
                studentId,
                studentClass,
                scoreT,
                scoreA1,
                scoreA2,
                scoreA3,
                scoreP,
                presence,
                isFinished,
                finalScore,
                finalIndex,
                passStatus
            )
            insert(student)
        }
    }

    private suspend fun sortName() {
        withContext(Dispatchers.IO) {
            student = database.getSortName()
        }
    }

    fun onClickSortName() {
        uiScope.launch {
            sortName()
        }
    }

    private suspend fun sortId() {
        withContext(Dispatchers.IO) {
            student = database.getSortId()
        }
    }

    fun onClickSortId() {
        uiScope.launch {
            sortId()
        }
    }

    private suspend fun sortScore() {
        withContext(Dispatchers.IO) {
            student = database.getSortScore()
        }
    }

    fun onClickSortScore() {
        uiScope.launch {
            sortScore()
        }
    }

    private suspend fun listPass() {
        withContext(Dispatchers.IO) {
            student = database.getListPass()
        }
    }

    fun onClickListPass() {
        uiScope.launch {
            listPass()
        }
    }

    private suspend fun topTen() {
        withContext(Dispatchers.IO) {
            student = database.getTopTen()
        }
    }

    fun onClickTopTen() {
        uiScope.launch {
            topTen()
        }
    }

    private suspend fun insert(student: Student) {
        withContext(Dispatchers.IO) {
            database.addStudent(student)
        }
    }

    fun onClickClear() {
        uiScope.launch {
            clear()
        }
    }

    private suspend fun clear() {
        withContext(Dispatchers.IO) {
            database.clearAllStudents()
        }
    }

    fun onClickUpdate(student: Student) {
        uiScope.launch {
            update(student)
        }
    }

    private suspend fun update(student: Student) {
        withContext(Dispatchers.IO) {
            database.updateStudent(student)
        }
    }

    fun onClickDelete(id: Long) {
        uiScope.launch {
            delete(id)
        }
    }

    private suspend fun delete(id: Long) {
        withContext(Dispatchers.IO) {
            database.deleteStudent(id)
        }
    }

    class Factory(
        private val dataSource: StudentDao, private val application: Application
    ) : ViewModelProvider.Factory {
        @Suppress("unchecked_cast")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(StudentViewModel::class.java)) {
                return StudentViewModel(dataSource, application) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}