package com.olgunyilmaz.teamlegacy.view

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import com.olgunyilmaz.teamlegacy.R
import com.olgunyilmaz.teamlegacy.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var fragmentManager: FragmentManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        fragmentManager = supportFragmentManager
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.team_menu,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.add_team){
            goToAddTeamFragment("new")
        }
        return super.onOptionsItemSelected(item)
    }

    private fun goToAddTeamFragment(info : String){
        val fragmentTransaction = fragmentManager.beginTransaction()
        val addTeamsFragment = TeamDetailsFragment()

        val bundle = Bundle()
        bundle.putString("info",info)
        addTeamsFragment.arguments = bundle

        fragmentTransaction.replace(R.id.fragmentContainerView,addTeamsFragment).commit()
    }
}