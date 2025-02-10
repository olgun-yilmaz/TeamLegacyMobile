package com.olgunyilmaz.teamlegacy.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.olgunyilmaz.teamlegacy.databinding.FragmentDisplayTeamsBinding


class DisplayTeamsFragment : Fragment() {
    private lateinit var binding: FragmentDisplayTeamsBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDisplayTeamsBinding.inflate(inflater,container,false)
        val view = binding.root
        return view
    }

}