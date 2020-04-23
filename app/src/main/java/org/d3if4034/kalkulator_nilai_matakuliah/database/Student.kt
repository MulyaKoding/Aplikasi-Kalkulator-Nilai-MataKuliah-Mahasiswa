package org.d3if4034.kalkulator_nilai_matakuliah.database

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "student")
data class Student(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0L,

    @ColumnInfo(name = "studentName")
    var studentName: String,

    @ColumnInfo(name = "studentId")
    var studentId: String,

    @ColumnInfo(name = "studentClass")
    var studentClass: String,

    @ColumnInfo(name = "scoreT")
    var scoreT: Float,

    @ColumnInfo(name = "scoreA1")
    var scoreA1: Float,

    @ColumnInfo(name = "scoreA2")
    var scoreA2: Float,

    @ColumnInfo(name = "scoreA3")
    var scoreA3: Float,

    @ColumnInfo(name = "scoreP")
    var scoreP: Float,

    @ColumnInfo(name = "presence")
    var presence: Float,

    @ColumnInfo(name = "isFinished")
    var isFinished: Boolean,

    @ColumnInfo(name = "finalScore")
    var finalScore: Float,

    @ColumnInfo(name = "finalIndex")
    var finalIndex: String,

    @ColumnInfo(name = "passStatus")
    var passStatus: String
) : Parcelable