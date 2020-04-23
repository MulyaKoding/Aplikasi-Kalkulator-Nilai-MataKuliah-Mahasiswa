package org.d3if4034.kalkulator_nilai_matakuliah

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_add_student.*
import org.d3if4034.kalkulator_nilai_matakuliah.database.Student
import org.d3if4034.kalkulator_nilai_matakuliah.database.StudentDatabase
import org.d3if4034.kalkulator_nilai_matakuliah.databinding.FragmentEditStudentBinding
import org.d3if4034.kalkulator_nilai_matakuliah.viewmodel.StudentViewModel
import java.text.DecimalFormat
import kotlin.math.floor
import kotlin.math.pow
import kotlin.math.roundToInt

class EditStudentFragment : Fragment() {
    private lateinit var binding: FragmentEditStudentBinding
    private lateinit var viewModel: StudentViewModel
    private lateinit var data: Student
    private var nama = ""
    private var nim = ""
    private var kelas = ""
    private var nilaiT = 0.00f
    private var nilaiA1 = 0.00f
    private var nilaiA2 = 0.00f
    private var nilaiA3 = 0.00f
    private var nilaiP = 0.00f
    private var presensi = 0.00f
    private var selesai = false
    private var nilaiAkhir = 0.00f
    private var indeksAkhir = "F"
    private var statusLulus = "N/A"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_edit_student, container, false)

        val application = requireNotNull(this.activity).application
        val dataSource = StudentDatabase.getInstance(application).StudentDao
        val viewModelFactory = StudentViewModel.Factory(dataSource, application)
        viewModel = ViewModelProvider(this, viewModelFactory).get(StudentViewModel::class.java)

        binding.apply {
            btnHitung.setOnClickListener {
                if (!(binding.etNamaEdit.text.toString()
                        .isEmpty() || binding.etNimEdit.text.toString()
                        .isEmpty() || binding.etKelasEdit.text.toString()
                        .isEmpty() || binding.etNilaiTEdit.text.toString()
                        .isEmpty() || binding.etNilaiA1Edit.text.toString()
                        .isEmpty() || binding.etNilaiA2Edit.text.toString()
                        .isEmpty() || binding.etNilaiA3Edit.text.toString()
                        .isEmpty() || binding.etNilaiPEdit.text.toString()
                        .isEmpty() || binding.etPresensiEdit.text.toString()
                        .isEmpty())
                ) {
                    nama = binding.etNamaEdit.text.toString()
                    nim = binding.etNimEdit.text.toString()
                    kelas = binding.etKelasEdit.text.toString()
                    nilaiT = (floor(
                        binding.etNilaiTEdit.text.toString().toDouble() * 100
                    ) / 100f).toFloat()
                    nilaiA1 =
                        (floor(
                            binding.etNilaiA1Edit.text.toString().toDouble() * 100
                        ) / 100f).toFloat()
                    nilaiA2 =
                        (floor(
                            binding.etNilaiA2Edit.text.toString().toDouble() * 100
                        ) / 100f).toFloat()
                    nilaiA3 =
                        (floor(
                            binding.etNilaiA3Edit.text.toString().toDouble() * 100
                        ) / 100f).toFloat()
                    nilaiP = (floor(
                        binding.etNilaiPEdit.text.toString().toDouble() * 100
                    ) / 100f).toFloat()
                    presensi =
                        (floor(
                            binding.etPresensiEdit.text.toString().toDouble() * 100
                        ) / 100f).toFloat()
                    selesai = binding.cbSelesai.isChecked


                    if (selesai) {
                        tvJudulNilai.text = getString(R.string.nilai_akhir)
                        tvJudulIndeks.text = getString(R.string.indeks_akhir)
                    } else {
                        tvJudulNilai.text = getString(R.string.nilai_sementara)
                        tvJudulIndeks.text = getString(R.string.indeks_sementara)
                    }

                    if (isInRange(nilaiT) && isInRange(nilaiA1) && isInRange(nilaiA2) && isInRange(
                            nilaiA3
                        ) && isInRange(nilaiP) && isInRange(presensi) && isInRange(nilaiAkhir)
                    ) {
                        val df = DecimalFormat("#.##")
                        calculateScore(); convertToGrade(); passStatus(); showResult()
                        tvNilai.text = df.format(nilaiAkhir).toString()
                        tvIndeks.text = indeksAkhir
                    } else {
                        Toast.makeText(
                            activity,
                            "Nilainya terlalu tinggi atau terlalu rendah!",
                            Toast.LENGTH_SHORT
                        ).show()
                        hideResult()
                        //diubah ke snackbar
                    }
                } else {
                    Toast.makeText(activity, "Mohon diisi dengan lengkap!", Toast.LENGTH_SHORT)
                        .show()
                    hideResult()
                    //diubah ke snackbar
                }
            }

            btnReset.setOnClickListener {
                reset()
            }

            btnSaran.setOnClickListener {
                when (nilaiAkhir) {
                    in 0.0..31.99 -> findNavController().navigate(R.id.action_editStudentFragment_to_mengulangFragment)
                    in 32.0..47.99 -> findNavController().navigate(R.id.action_editStudentFragment_to_tidakBaikFragment)
                    in 48.0..55.99 -> findNavController().navigate(R.id.action_editStudentFragment_to_kurangBaikFragment)
                    in 56.0..63.99 -> findNavController().navigate(R.id.action_editStudentFragment_to_cukupFragment)
                    in 64.0..71.99 -> findNavController().navigate(R.id.action_editStudentFragment_to_cukupBaikFragment)
                    in 72.0..79.99 -> findNavController().navigate(R.id.action_editStudentFragment_to_baikFragment)
                    in 80.0..100.0 -> findNavController().navigate(R.id.action_editStudentFragment_to_sangatBaikFragment)

                }
            }
            btnShare.setOnClickListener {
                val shareIntent = Intent()
                shareIntent.action = Intent.ACTION_SEND
                shareIntent.type = "text/plain"
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Berat saya $nilaiAkhir")
                shareIntent.putExtra(
                    Intent.EXTRA_TEXT,
                    "Nilai Akhir : $nilaiAkhir\nIndeks Akhir : $indeksAkhir\nStatus Lulus : $statusLulus"
                )
                shareIntent.putExtra(Intent.EXTRA_EMAIL, "rahmatsnsd@gmail.com")
                startActivity(Intent.createChooser(shareIntent, "Bagikan hasil Nilai Mata Kuliah..."))
            }
        }
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getData()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.action_button, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.commit -> {
                if (binding.rbUpdate.isChecked) {
                    if ((!(binding.etNamaEdit.text.toString()
                            .isEmpty() || binding.etNimEdit.text.toString()
                            .isEmpty() || binding.etKelasEdit.text.toString()
                            .isEmpty() || binding.etNilaiTEdit.text.toString()
                            .isEmpty() || binding.etNilaiA1Edit.text.toString()
                            .isEmpty() || binding.etNilaiA2Edit.text.toString()
                            .isEmpty() || binding.etNilaiA3Edit.text.toString()
                            .isEmpty() || binding.etNilaiPEdit.text.toString()
                            .isEmpty() || binding.etPresensiEdit.text.toString()
                            .isEmpty())) && inputCheck() && isInRange(nilaiT) && isInRange(nilaiA1) && isInRange(
                            nilaiA2
                        ) && isInRange(nilaiA3) && isInRange(nilaiP) && isInRange(presensi) && isInRange(
                            nilaiAkhir
                        )
                    ) {
                        selesai = binding.cbSelesai.isChecked
                        if (selesai) {
                            showDialogToFinalUpdate()
                        } else {
                            showDialogToUpdate()
                        }
                    } else {
                        Snackbar.make(
                            requireView(),
                            "Mohon diisi dengan lengkap DAN/ATAU nilainya terlalu tinggi atau terlalu rendah!",
                            Snackbar.LENGTH_SHORT
                        ).show()
                    }
                } else if (binding.rbDelete.isChecked) {
                    showDialogToDelete()
                } else {
                    Snackbar.make(
                        requireView(),
                        "Mohon memilih salah satu perintah edit!",
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun getData() {
        if (arguments != null) {
            data = arguments?.getParcelable("dataStudent")!!
            nama = data.studentName
            nim = data.studentId
            kelas = data.studentClass
            nilaiT = data.scoreT
            nilaiA1 = data.scoreA1
            nilaiA2 = data.scoreA2
            nilaiA3 = data.scoreA3
            nilaiP = data.scoreP
            presensi = data.presence
            nilaiAkhir = data.finalScore
            indeksAkhir = data.finalIndex
            statusLulus = data.passStatus

            binding.etNamaEdit.setText(nama)
            binding.etNimEdit.setText(nim)
            binding.etKelasEdit.setText(kelas)
            binding.etNilaiTEdit.setText(nilaiT.toString())
            binding.etNilaiA1Edit.setText(nilaiA1.toString())
            binding.etNilaiA2Edit.setText(nilaiA2.toString())
            binding.etNilaiA3Edit.setText(nilaiA3.toString())
            binding.etNilaiPEdit.setText(nilaiP.toString())
            binding.etPresensiEdit.setText(presensi.toString())
            binding.tvNilai.text = nilaiAkhir.toString()
            binding.tvIndeks.text = indeksAkhir
        }
    }

    private fun updateData() {
        nama = binding.etNamaEdit.text.toString()
        nim = binding.etNimEdit.text.toString()
        kelas = binding.etKelasEdit.text.toString()
        nilaiT = (floor(
            binding.etNilaiTEdit.text.toString().toDouble() * 100
        ) / 100f).toFloat()
        nilaiA1 =
            (floor(
                binding.etNilaiA1Edit.text.toString().toDouble() * 100
            ) / 100f).toFloat()
        nilaiA2 =
            (floor(
                binding.etNilaiA2Edit.text.toString().toDouble() * 100
            ) / 100f).toFloat()
        nilaiA3 =
            (floor(
                binding.etNilaiA3Edit.text.toString().toDouble() * 100
            ) / 100f).toFloat()
        nilaiP = (floor(
            binding.etNilaiPEdit.text.toString().toDouble() * 100
        ) / 100f).toFloat()
        presensi =
            (floor(
                binding.etPresensiEdit.text.toString().toDouble() * 100
            ) / 100f).toFloat()
        selesai = binding.cbSelesai.isChecked
        calculateScore(); convertToGrade(); passStatus()

        val student = Student(
            data.id,
            nama,
            nim,
            kelas,
            nilaiT,
            nilaiA1,
            nilaiA2,
            nilaiA3,
            nilaiP,
            presensi,
            selesai,
            nilaiAkhir,
            indeksAkhir,
            statusLulus
        )
        viewModel.onClickUpdate(student)
    }

    private fun inputCheck(): Boolean {
        return when {
            binding.etNamaEdit.text.trim().isEmpty() || binding.etNimEdit.text.trim()
                .isEmpty() || binding.etKelasEdit.text.trim().isEmpty() -> false
            else -> true
        }
    }

    private fun isInRange(score: Float): Boolean {
        return score in 0.0..100.0
    }

    private fun calculateScore() {
        if (presensi >= 80 || !selesai) {
            nilaiAkhir =
                ((0.1f * nilaiT + 0.15f * nilaiA1 + 0.2f * nilaiA2 + 0.25f * nilaiA3 + 0.3f * nilaiP) * 100).roundToInt() / 100f
        } else {
            if (nilaiT > 80) nilaiT = 80f
            if (nilaiA1 > 80) nilaiA1 = 80f
            if (nilaiA2 > 80) nilaiA2 = 80f
            if (nilaiA3 > 80) nilaiA3 = 80f
            if (nilaiP > 80) nilaiP = 80f

            nilaiAkhir =
                ((0.1f * nilaiT + 0.15f * nilaiA1 + 0.2f * nilaiA2 + 0.25f * nilaiA3 + 0.3f * nilaiP) * 100 * (presensi / 80.0).pow(
                    2
                )).roundToInt() / 100f
        }
    }

    private fun convertToGrade() {
        when (nilaiAkhir) {
            in 0.0..31.99 -> {tv_indeks.setTextColor(resources.getColor(R.color.Indeks_E))
                tv_nilai.setTextColor(resources.getColor(R.color.Indeks_E))
                indeksAkhir = "E"}
            in 32.0..47.99 -> {tv_indeks.setTextColor(resources.getColor(R.color.Indeks_D))
                tv_nilai.setTextColor(resources.getColor(R.color.Indeks_D))
                indeksAkhir = "D"}
            in 48.0..55.99 -> {tv_indeks.setTextColor(resources.getColor(R.color.Indeks_C))
                tv_nilai.setTextColor(resources.getColor(R.color.Indeks_C))
                indeksAkhir = "C"}
            in 56.0..63.99 ->    {tv_indeks.setTextColor(resources.getColor(R.color.Indeks_BC))
                tv_nilai.setTextColor(resources.getColor(R.color.Indeks_BC))
                indeksAkhir = "BC"}
            in 64.0..71.99 ->   {tv_indeks.setTextColor(resources.getColor(R.color.Indeks_B))
                tv_nilai.setTextColor(resources.getColor(R.color.Indeks_B))
                indeksAkhir = "B"}
            in 72.0..79.99 ->   {tv_indeks.setTextColor(resources.getColor(R.color.Indeks_AB))
                tv_nilai.setTextColor(resources.getColor(R.color.Indeks_AB))
                indeksAkhir = "AB"}
            in 80.0..100.0 -> {tv_indeks.setTextColor(resources.getColor(R.color.Indeks_A))
                tv_nilai.setTextColor(resources.getColor(R.color.Indeks_A))
                indeksAkhir = "A"}
            else -> indeksAkhir = "Diluar jangkauan!"
        }
    }



    private fun passStatus() {
        if (selesai) {
            when (indeksAkhir) {
                "A", "AB", "B", "BC", "C" -> statusLulus = "Lulus"
                "D", "F" -> statusLulus = "Tidak Lulus"
            }
        } else {
            statusLulus = "N/A"
        }
    }

    private fun showResult() {
        binding.divider2.visibility = View.VISIBLE
        binding.dividerV.visibility = View.VISIBLE
        binding.tvJudulNilai.visibility = View.VISIBLE
        binding.tvJudulIndeks.visibility = View.VISIBLE
        binding.tvNilai.visibility = View.VISIBLE
        binding.tvIndeks.visibility = View.VISIBLE
        binding.btnSaran.visibility = View.VISIBLE
        binding.btnShare.visibility = View.VISIBLE
    }

    private fun hideResult() {
        binding.divider2.visibility = View.GONE
        binding.dividerV.visibility = View.GONE
        binding.tvJudulNilai.visibility = View.GONE
        binding.tvJudulIndeks.visibility = View.GONE
        binding.tvNilai.visibility = View.GONE
        binding.tvIndeks.visibility = View.GONE
        binding.btnSaran.visibility = View.GONE
        binding.btnShare.visibility = View.GONE
    }

    private fun reset() {
        hideResult()
        getData()
        binding.cbSelesai.isChecked.not()
        binding.rbUpdate.isChecked.not()
        binding.rbDelete.isChecked.not()
    }

    private fun showDialogToFinalUpdate() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Peringatan!")
        builder.setMessage("Sekali Anda mencentang \"Kuliah sudah berakhir?\", Anda tidak bisa merubahnya lagi! Apakah Anda yakin?")
        builder.setPositiveButton("Ya") { dialog, _ ->
            updateData()
            dialog.dismiss()
            requireView().findNavController().popBackStack()
            Snackbar.make(
                requireView(),
                "Data Mahasiswa/i berhasil dipermanenkan!",
                Snackbar.LENGTH_SHORT
            ).show()
        }
        builder.setNegativeButton("Tidak") { dialog, _ ->
            dialog.dismiss()
        }
        builder.setCancelable(false)
        builder.show()
    }

    private fun showDialogToUpdate() {
        updateData()
        requireView().findNavController().popBackStack()
        Snackbar.make(
            requireView(),
            "Data Mahasiswa/i berhasil diupdate!",
            Snackbar.LENGTH_SHORT
        ).show()
    }

    private fun showDialogToDelete() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Peringatan!")
        builder.setMessage("Sekali Anda menghapus data ini, Anda tidak bisa mengembalikannya lagi! Apakah Anda yakin?")
        builder.setPositiveButton("Ya") { dialog, _ ->
            viewModel.onClickDelete(data.id)
            dialog.dismiss()
            requireView().findNavController().popBackStack()
            Snackbar.make(
                requireView(),
                "Data Mahasiswa/i berhasil dihapus!",
                Snackbar.LENGTH_SHORT
            ).show()
        }
        builder.setNegativeButton("Tidak") { dialog, _ ->
            dialog.dismiss()
        }
        builder.setCancelable(false)
        builder.show()
    }
}
