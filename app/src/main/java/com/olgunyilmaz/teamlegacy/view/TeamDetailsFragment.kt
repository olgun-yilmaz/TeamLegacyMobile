package com.olgunyilmaz.teamlegacy.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.olgunyilmaz.teamlegacy.databinding.FragmentTeamDetailsBinding


class TeamDetailsFragment : Fragment() {
    private lateinit var binding: FragmentTeamDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentTeamDetailsBinding.inflate(inflater,container,false)
        val view = binding.root
        return view
    }
}