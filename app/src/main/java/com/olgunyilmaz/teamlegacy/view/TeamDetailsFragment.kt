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
import com.google.android.material.snackbar.Snackbar
import com.olgunyilmaz.teamlegacy.databinding.FragmentTeamDetailsBinding


class TeamDetailsFragment : Fragment() {
    private lateinit var binding: FragmentTeamDetailsBinding
    private lateinit var fragmentManager: FragmentManager
    private lateinit var selectedBitmap : Bitmap

    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var permissionLauncher: ActivityResultLauncher<String>

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        registerLauncher()
        binding.saveButton.setOnClickListener {
            save(it)
        }
        binding.imageView.setOnClickListener {
            selectImage(it)
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
        val action = TeamDetailsFragmentDirections.actionTeamDetailsFragmentToDisplayTeamsFragment()
        Navigation.findNavController(view).navigate(action)
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