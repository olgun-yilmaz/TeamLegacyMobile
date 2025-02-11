package com.olgunyilmaz.teamlegacy.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.olgunyilmaz.teamlegacy.adapter.TeamAdapter
import com.olgunyilmaz.teamlegacy.databinding.FragmentDisplayTeamsBinding
import com.olgunyilmaz.teamlegacy.model.Team
import com.olgunyilmaz.teamlegacy.roomdb.TeamDatabase
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers


class DisplayTeamsFragment : Fragment() {
    private lateinit var binding: FragmentDisplayTeamsBinding
    private lateinit var compositeDisposable: CompositeDisposable


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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        compositeDisposable = CompositeDisposable()

        val db =
            activity?.let {
                activity ->  Room.databaseBuilder(activity.applicationContext,  TeamDatabase :: class.java, "Teams").build()
            }
        val teamDao = db?.teamDao()

        if (teamDao != null) {
            compositeDisposable.add(
                teamDao.fetchAll()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(this :: handleResponse)
            )
        }
    }

    private fun handleResponse(teamList : List<Team>){
        binding.recyclerView.layoutManager = LinearLayoutManager(activity)
        val adapter = TeamAdapter(teamList)
        binding.recyclerView.adapter = adapter
    }

}