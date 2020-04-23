package org.d3if4034.kalkulator_nilai_matakuliah

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_home2.*
import org.d3if4034.kalkulator_nilai_matakuliah.R
import org.d3if4034.kalkulator_nilai_matakuliah.database.Student
import org.d3if4034.kalkulator_nilai_matakuliah.database.StudentDatabase
import org.d3if4034.kalkulator_nilai_matakuliah.databinding.FragmentAddStudentBinding
import org.d3if4034.kalkulator_nilai_matakuliah.databinding.FragmentHome2Binding
import org.d3if4034.kalkulator_nilai_matakuliah.recyclerview.RecyclerViewClickListener
import org.d3if4034.kalkulator_nilai_matakuliah.viewmodel.StudentAdapter
import org.d3if4034.kalkulator_nilai_matakuliah.viewmodel.StudentViewModel

class HomeFragment2 : Fragment(), RecyclerViewClickListener {
    private lateinit var binding: FragmentHome2Binding
    private lateinit var viewModel: StudentViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home2, container, false)
        val application = requireNotNull(this.activity).application
        val dataSource = StudentDatabase.getInstance(application).StudentDao
        val viewModelFactory = StudentViewModel.Factory(dataSource, application)
        viewModel = ViewModelProvider(this, viewModelFactory).get(StudentViewModel::class.java)

        binding.lifecycleOwner = this
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu, menu)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
        binding.fabAddStudent.setOnClickListener {
            it.findNavController().navigate(R.id.action_homeFragment2_to_addStudentFragment)
        }
    }

    private fun initUI() {
        viewModel.student.observe(viewLifecycleOwner, Observer {
            val studentAdapter = StudentAdapter(it)
            val recyclerView = binding.rvStudent

            recyclerView.apply {
                this.adapter = studentAdapter
                this.layoutManager = LinearLayoutManager(requireContext())
            }

            studentAdapter.listener = this
        })
    }

    override fun onItemClicked(view: View, student: Student) {
        when (view.id) {
            R.id.list_student -> {
                val bundle = bundleOf("dataStudent" to student)
                if (student.isFinished) {
                    view.findNavController()
                        .navigate(R.id.action_homeFragment2_to_readOnlyStudentFragment, bundle)
                } else {
                    view.findNavController()
                        .navigate(R.id.action_homeFragment2_to_editStudentFragment, bundle)
                }
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.urut_nama -> {
                viewModel.onClickSortName()
                true
            }
            R.id.urut_nim -> {
                viewModel.onClickSortId()
                true
            }
            R.id.urut_nilai -> {
                viewModel.onClickSortScore()
                true
            }
            R.id.daftar_lulus -> {
                viewModel.onClickListPass()
                true
            }
            R.id.sepuluh_besar -> {
                viewModel.onClickTopTen()
                true
            }
            R.id.hapus_semua -> {
                showDialogToDeleteAll()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showDialogToDeleteAll() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Peringatan!")
        builder.setMessage("Sekali Anda menghapus semua data yang sudah tersimpan, Anda tidak bisa mengembalikannya lagi! Apakah Anda yakin?")
        builder.setPositiveButton("Ya") { dialog, _ ->
            viewModel.onClickClear()
            dialog.dismiss()
            Snackbar.make(
                requireView(),
                "Semua data Mahasiswa/i berhasil dihapus!",
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