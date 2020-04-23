package org.d3if4034.kalkulator_nilai_matakuliah


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
import org.d3if4034.kalkulator_nilai_matakuliah.database.StudentDatabase
import org.d3if4034.kalkulator_nilai_matakuliah.databinding.FragmentAddStudentBinding
import org.d3if4034.kalkulator_nilai_matakuliah.viewmodel.StudentViewModel
import java.text.DecimalFormat
import kotlin.math.floor
import kotlin.math.pow
import kotlin.math.roundToInt

class AddStudentFragment : Fragment() {
    private lateinit var binding: FragmentAddStudentBinding
    private lateinit var viewModel: StudentViewModel
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
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_add_student, container, false)

        val application = requireNotNull(this.activity).application
        val dataSource = StudentDatabase.getInstance(application).StudentDao
        val viewModelFactory = StudentViewModel.Factory(dataSource, application)
        viewModel = ViewModelProvider(this, viewModelFactory).get(StudentViewModel::class.java)

        binding.apply {
            btnHitung.setOnClickListener {
                if (!(binding.etNama.text.toString()
                        .isEmpty() || binding.etNim.text.toString()
                        .isEmpty() || binding.etKelas.text.toString()
                        .isEmpty() || binding.etNilaiT.text.toString()
                        .isEmpty() || binding.etNilaiA1.text.toString()
                        .isEmpty() || binding.etNilaiA2.text.toString()
                        .isEmpty() || binding.etNilaiA3.text.toString()
                        .isEmpty() || binding.etNilaiP.text.toString()
                        .isEmpty() || binding.etPresensi.text.toString()
                        .isEmpty())
                ) {
                    nama = etNama.text.toString()
                    nim = etNim.text.toString()
                    kelas = etKelas.text.toString()
                    nilaiT = (floor(etNilaiT.text.toString().toDouble() * 100) / 100f).toFloat()
                    nilaiA1 = (floor(etNilaiA1.text.toString().toDouble() * 100) / 100f).toFloat()
                    nilaiA2 = (floor(etNilaiA2.text.toString().toDouble() * 100) / 100f).toFloat()
                    nilaiA3 = (floor(etNilaiA3.text.toString().toDouble() * 100) / 100f).toFloat()
                    nilaiP = (floor(etNilaiP.text.toString().toDouble() * 100) / 100f).toFloat()
                    presensi = (floor(etPresensi.text.toString().toDouble() * 100) / 100f).toFloat()

                    if (isInRange(nilaiT) && isInRange(nilaiA1) && isInRange(nilaiA2) && isInRange(
                            nilaiA3
                        ) && isInRange(nilaiP) && isInRange(presensi) && isInRange(nilaiAkhir)
                    ) {
                        val df = DecimalFormat("#.##")
                        calculateScore(); convertToGrade(); showResult()
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
                   in 0.0..31.99 -> findNavController().navigate(R.id.action_addStudentFragment_to_mengulangFragment)
                   in 32.0..47.99 -> findNavController().navigate(R.id.action_addStudentFragment_to_tidakBaikFragment)
                   in 48.0..55.99 -> findNavController().navigate(R.id.action_addStudentFragment_to_kurangBaikFragment)
                   in 56.0..63.99 -> findNavController().navigate(R.id.action_addStudentFragment_to_cukupFragment)
                   in 64.0..71.99 -> findNavController().navigate(R.id.action_addStudentFragment_to_cukupBaikFragment)
                   in 72.0..79.99 -> findNavController().navigate(R.id.action_addStudentFragment_to_baikFragment)
                   in 80.0..100.0 -> findNavController().navigate(R.id.action_addStudentFragment_to_sangatBaikFragment2)

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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.action_button, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.commit -> {
                if ((!(binding.etNama.text.toString().isEmpty() || binding.etNim.text.toString()
                        .isEmpty() || binding.etKelas.text.toString()
                        .isEmpty() || binding.etNilaiT.text.toString()
                        .isEmpty() || binding.etNilaiA1.text.toString()
                        .isEmpty() || binding.etNilaiA2.text.toString()
                        .isEmpty() || binding.etNilaiA3.text.toString()
                        .isEmpty() || binding.etNilaiP.text.toString()
                        .isEmpty() || binding.etPresensi.text.toString()
                        .isEmpty())) && inputCheck() && isInRange(nilaiT) && isInRange(nilaiA1) && isInRange(
                        nilaiA2
                    ) && isInRange(nilaiA3) && isInRange(nilaiP) && isInRange(presensi) && isInRange(
                        nilaiAkhir
                    )
                ) {
                    insertData()
                    requireView().findNavController().popBackStack()
                    Snackbar.make(
                        requireView(),
                        "Data Mahasiswa/i berhasil ditambahkan!",
                        Snackbar.LENGTH_SHORT
                    ).show()
                } else {
                    Snackbar.make(
                        requireView(),
                        "Mohon diisi dengan lengkap DAN/ATAU nilainya terlalu tinggi atau terlalu rendah!",
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun insertData() {
        viewModel.onClickInsert(
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
    }

    private fun inputCheck(): Boolean {
        return when {
            binding.etNama.text.trim().isEmpty() || binding.etNim.text.trim()
                .isEmpty() || binding.etKelas.text.trim().isEmpty() -> false
            else -> true
        }
    }

    private fun isInRange(score: Float): Boolean {
        return score in 0.0..100.0
    }

    private fun reset() {
        hideResult()
        binding.etNama.text.clear()
        binding.etNim.text.clear()
        binding.etKelas.text.clear()
        binding.etNilaiT.text.clear()
        binding.etNilaiA1.text.clear()
        binding.etNilaiA2.text.clear()
        binding.etNilaiA3.text.clear()
        binding.etNilaiP.text.clear()
        binding.etPresensi.text.clear()
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
            else -> indeksAkhir = "Diluar Jangkauan!"
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
}