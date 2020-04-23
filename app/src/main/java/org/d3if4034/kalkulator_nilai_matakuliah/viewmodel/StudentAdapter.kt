package org.d3if4034.kalkulator_nilai_matakuliah.viewmodel


import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import org.d3if4034.kalkulator_nilai_matakuliah.R
import org.d3if4034.kalkulator_nilai_matakuliah.database.Student
import org.d3if4034.kalkulator_nilai_matakuliah.databinding.RecyclerviewStudentBinding
import org.d3if4034.kalkulator_nilai_matakuliah.recyclerview.RecyclerViewClickListener

class StudentAdapter(private val student: List<Student>) :
    RecyclerView.Adapter<StudentAdapter.StudentViewHolder>() {

    var listener: RecyclerViewClickListener? = null

    inner class StudentViewHolder(
        val recyclerviewStudentBinding: RecyclerviewStudentBinding
    ) : RecyclerView.ViewHolder(recyclerviewStudentBinding.root)

    override fun getItemCount() = student.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = StudentViewHolder(
        DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.recyclerview_student, parent, false
        )
    )

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: StudentViewHolder, position: Int) {
        holder.recyclerviewStudentBinding.tvName.text = student[position].studentName
        holder.recyclerviewStudentBinding.tvIdClass.text =
            student[position].studentId + " - " + student[position].studentClass
        holder.recyclerviewStudentBinding.tvStatusGradeScore.text =
            student[position].passStatus + " [" + student[position].finalIndex + " (" + student[position].finalScore + ")]"
        if (student[position].isFinished) {
            when (student[position].finalIndex) {
                "A" -> holder.recyclerviewStudentBinding.listStudent.setBackgroundColor(
                    Color.parseColor(
                        "#400000FF"
                    )
                )
                "AB" -> holder.recyclerviewStudentBinding.listStudent.setBackgroundColor(
                    Color.parseColor(
                        "#400080FF"
                    )
                )
                "B" -> holder.recyclerviewStudentBinding.listStudent.setBackgroundColor(
                    Color.parseColor(
                        "#4000FFFF"
                    )
                )
                "BC" -> holder.recyclerviewStudentBinding.listStudent.setBackgroundColor(
                    Color.parseColor(
                        "#4000FF40"
                    )
                )
                "C" -> holder.recyclerviewStudentBinding.listStudent.setBackgroundColor(
                    Color.parseColor(
                        "#4080FF00"
                    )
                )
                "D" -> holder.recyclerviewStudentBinding.listStudent.setBackgroundColor(
                    Color.parseColor(
                        "#40FFC000"
                    )
                )
                "F" -> holder.recyclerviewStudentBinding.listStudent.setBackgroundColor(
                    Color.parseColor(
                        "#40FF0000"
                    )
                )
            }
        }
        holder.recyclerviewStudentBinding.listStudent.setOnClickListener {
            listener?.onItemClicked(it, student[position])
        }
    }
}