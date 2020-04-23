package org.d3if4034.kalkulator_nilai_matakuliah

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import org.d3if4034.kalkulator_nilai_matakuliah.databinding.FragmentMengulangBinding
import org.d3if4034.kalkulator_nilai_matakuliah.databinding.FragmentSangatBaikBinding

/**
 * A simple [Fragment] subclass.
 */
class MengulangFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        val binding: FragmentMengulangBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_mengulang, container, false)
        return binding.root
    }

}
