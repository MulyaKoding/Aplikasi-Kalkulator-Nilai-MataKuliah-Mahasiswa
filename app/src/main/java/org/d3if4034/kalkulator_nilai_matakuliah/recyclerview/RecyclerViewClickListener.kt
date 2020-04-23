package org.d3if4034.kalkulator_nilai_matakuliah.recyclerview

import android.view.View
import org.d3if4034.kalkulator_nilai_matakuliah.database.Student

interface RecyclerViewClickListener {
    fun onItemClicked(view: View, student: Student)
}