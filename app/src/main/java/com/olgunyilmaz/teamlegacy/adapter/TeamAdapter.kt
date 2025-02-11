package com.olgunyilmaz.teamlegacy.adapter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.olgunyilmaz.teamlegacy.R
import com.olgunyilmaz.teamlegacy.databinding.RecyclerRowBinding
import com.olgunyilmaz.teamlegacy.model.Team

class TeamAdapter (val teamList : List<Team>) : RecyclerView.Adapter <TeamAdapter.TeamHolder>() {
    class TeamHolder(val binding : RecyclerRowBinding) : RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TeamHolder {
        val binding = RecyclerRowBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return TeamHolder(binding)
    }

    override fun getItemCount(): Int {
        return teamList.size
    }

    override fun onBindViewHolder(holder: TeamHolder, position: Int) {
        holder.binding.recyclerViewTextView.setText(teamList.get(position).name)
        holder.itemView.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("info","old")
            bundle.putSerializable("selectedTeam",teamList.get(position))

            val navController = Navigation.findNavController(holder.itemView)
            navController.navigate(R.id.action_displayTeamsFragment_to_teamDetailsFragment,bundle)
        }
    }
}