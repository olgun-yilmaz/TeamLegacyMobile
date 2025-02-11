package com.olgunyilmaz.teamlegacy.view

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import androidx.navigation.Navigation
import androidx.navigation.Navigator
import androidx.room.Room
import androidx.room.RoomDatabase
import com.google.android.material.snackbar.Snackbar
import com.olgunyilmaz.teamlegacy.R
import com.olgunyilmaz.teamlegacy.databinding.FragmentTeamDetailsBinding
import com.olgunyilmaz.teamlegacy.model.Team
import com.olgunyilmaz.teamlegacy.roomdb.TeamDao
import com.olgunyilmaz.teamlegacy.roomdb.TeamDatabase
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.io.ByteArrayOutputStream
import java.io.OutputStream


class TeamDetailsFragment : Fragment() {
    private lateinit var binding: FragmentTeamDetailsBinding
    private lateinit var fragmentManager: FragmentManager

    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var permissionLauncher: ActivityResultLauncher<String>

    private lateinit var db : TeamDatabase
    private lateinit var teamDao: TeamDao
    private lateinit var compositeDisposable: CompositeDisposable

    private var info : String? = null
    private var selectedBitmap : Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        info = arguments?.getString("info")
        binding = FragmentTeamDetailsBinding.inflate(inflater,container,false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        registerLauncher()
        fragmentManager = requireActivity().supportFragmentManager

        compositeDisposable = CompositeDisposable()

        db = activity?.let { activity -> Room.databaseBuilder(activity.applicationContext,TeamDatabase :: class.java,"Teams").build() }!!
        teamDao = db.teamDao()

        if (info.equals("old")){
            binding.deleteButton.visibility = View.VISIBLE
            binding.saveButton.visibility = View.GONE

            binding.deleteButton.setOnClickListener {
                delete(it)
            }
        }else{
            binding.deleteButton.visibility = View.GONE
            binding.saveButton.visibility = View.VISIBLE

            binding.saveButton.setOnClickListener {
                save(it)
            }
            binding.imageView.setOnClickListener {
                selectImage(it)
            }
        }
    }

    private fun selectImage(view : View){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            requestPermission(view,Manifest.permission.READ_MEDIA_IMAGES)
        }else{
            requestPermission(view,Manifest.permission.READ_EXTERNAL_STORAGE)
        }

    }

    private fun save(view: View) {
        if (selectedBitmap == null){
            Toast.makeText(requireActivity().applicationContext,"You must choose a picture!",Toast.LENGTH_LONG).show()
        }else {
            val smallImg = makeImageSmaller(selectedBitmap!!, 300)
            val outputStream = ByteArrayOutputStream()
            smallImg.compress(Bitmap.CompressFormat.PNG, 50, outputStream)
            val byteArray = outputStream.toByteArray()

            val teamName = binding.nameText.text.toString()
            val aboutText = binding.aboutText.text.toString()
            var year = binding.yearText.text.toString().toDoubleOrNull()

            if (year == null) {
                year = 0.0
            }

            val team = Team(teamName, aboutText, year, byteArray)

            try{
                compositeDisposable.add(
                    teamDao.insert(team)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(this@TeamDetailsFragment :: handleResponse)
                )

            }catch (e : Exception){
                Toast.makeText(requireActivity(),e.localizedMessage,Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun makeImageSmaller(image : Bitmap, maxSize : Int) : Bitmap{
        var width = image.width
        var height = image.height
        val ratio = width.toDouble()/height.toDouble()

        if(ratio>=1){ //landscape
            width = maxSize
            height = (width.toDouble() / ratio).toInt()
        }else{ //portrait
            height = maxSize
            width = (height.toDouble() * ratio).toInt()
        }

        return Bitmap.createScaledBitmap(image,width,height,true)
    }

    private fun delete(view : View){
        handleResponse()
    }

    private fun requestPermission(view : View, permission : String){
        if(activity?.let {activity -> ContextCompat.checkSelfPermission(activity,permission) } != PackageManager.PERMISSION_GRANTED){
            if (activity?.let { activity -> ActivityCompat.shouldShowRequestPermissionRationale(activity,permission)} == true){
                Snackbar.make(view,"Permission need for gallery!",Snackbar.LENGTH_INDEFINITE)
                    .setAction("Give Permission",View.OnClickListener {
                        launchPermissionLauncher(permission)
                    }).show()
            }else{
                launchPermissionLauncher(permission)
            }
        }else{
            goToGallery()
        }

    }

    private fun goToGallery(){
        val intentToGalley = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        activityResultLauncher.launch(intentToGalley)
    }

    private fun handleResponse(){
        val fragmentTransaction = fragmentManager.beginTransaction()
        val displayTeamsFragment = DisplayTeamsFragment()
        fragmentTransaction.replace(R.id.fragmentContainerView,displayTeamsFragment).commit()
    }

    private fun registerLauncher(){
        activityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
            if (result.resultCode == RESULT_OK){
                val intentFromResult = result.data
                intentFromResult?.let {
                    val imageUri = intentFromResult.data
                    imageUri?.let {
                        try{

                            if(Build.VERSION.SDK_INT >= 28){
                                val source = activity?.let {
                                    activity -> ImageDecoder.createSource(activity.contentResolver,imageUri)
                                }

                                selectedBitmap = source?.let {
                                    source -> ImageDecoder.decodeBitmap(source)
                                }!!
                            }else{
                                selectedBitmap = activity?.let {
                                    activity -> MediaStore.Images.Media.getBitmap(activity.contentResolver,imageUri)
                                }!!
                            }
                            binding.imageView.setImageBitmap(selectedBitmap)

                        }catch (e : Exception){
                            e.printStackTrace()
                        }
                    }
                }
            }

        }

        permissionLauncher =  registerForActivityResult(ActivityResultContracts.RequestPermission()){result ->
            if (result){
                goToGallery()
            }else{
                Toast.makeText(activity,"Permission needed!",Toast.LENGTH_LONG).show()
            }
        }

    }

    private fun launchPermissionLauncher(permission : String){
        permissionLauncher.launch(permission)
    }

}