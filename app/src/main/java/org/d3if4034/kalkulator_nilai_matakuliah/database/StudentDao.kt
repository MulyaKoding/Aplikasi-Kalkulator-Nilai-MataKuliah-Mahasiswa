package org.d3if4034.kalkulator_nilai_matakuliah.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface StudentDao {
    @Insert
    fun addStudent(student: Student)

    @Update
    fun updateStudent(student: Student)

    @Query("SELECT * FROM student")
    fun getAllStudents(): LiveData<List<Student>>

    @Query("SELECT * FROM student ORDER BY studentName ASC")
    fun getSortName(): LiveData<List<Student>>

    @Query("SELECT * FROM student ORDER BY studentId ASC")
    fun getSortId(): LiveData<List<Student>>

    @Query("SELECT * FROM student ORDER BY finalScore DESC")
    fun getSortScore(): LiveData<List<Student>>

    @Query("SELECT * FROM student WHERE passStatus = 'Lulus' ORDER BY finalScore DESC")
    fun getListPass(): LiveData<List<Student>>

    @Query("SELECT * FROM student WHERE passStatus = 'Lulus' ORDER BY finalScore DESC LIMIT 10")
    fun getTopTen(): LiveData<List<Student>>

    @Query("DELETE FROM student WHERE id = :id")
    fun deleteStudent(id: Long)

    @Query("DELETE FROM student")
    fun clearAllStudents()
}