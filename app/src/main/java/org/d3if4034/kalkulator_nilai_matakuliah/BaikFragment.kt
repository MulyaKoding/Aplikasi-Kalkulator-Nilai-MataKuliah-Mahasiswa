package org.d3if4034.kalkulator_nilai_matakuliah

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import org.d3if4034.kalkulator_nilai_matakuliah.databinding.FragmentBaikBinding


/**
 * A simple [Fragment] subclass.
 */
class BaikFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        val binding: FragmentBaikBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_baik, container, false)
        return binding.root


    }

}
