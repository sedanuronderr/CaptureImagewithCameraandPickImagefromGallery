package com.seda.captureimagewithcameraandpickimagefromgallery

import android.app.Activity
import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Binder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import coil.load
import coil.transform.CircleCropTransformation
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.karumi.dexter.listener.single.PermissionListener
import com.seda.captureimagewithcameraandpickimagefromgallery.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val CAMERA_REQUEST_CODE = 1

   private val GALLERY_REQUEST_CODE=2
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonCamera.setOnClickListener {
            cameraCheckPermission()
        }

        binding.buttonGallery.setOnClickListener {
            galleryCheckPermission()

        }
    }




                  //Gallery//
    private fun galleryCheckPermission() {
        Dexter.withContext(this).withPermission( android.Manifest.permission.READ_EXTERNAL_STORAGE)
            .withListener(object :PermissionListener{
                override fun onPermissionGranted(p0: PermissionGrantedResponse?) {
              gallery()

                }

                override fun onPermissionDenied(p0: PermissionDeniedResponse?) {
         Toast.makeText(this@MainActivity,"Denied",Toast.LENGTH_LONG).show()
                    showDialogFor()
                }

                override fun onPermissionRationaleShouldBeShown(
                    p0: PermissionRequest?,
                    p1: PermissionToken?
                ) {
                    TODO("Not yet implemented")
                }

            }).onSameThread().check()
    }

    private fun gallery() {

        val intent = Intent(Intent.ACTION_PICK)
        intent.type ="image/*"
        startActivityForResult(intent,GALLERY_REQUEST_CODE)

    }


                                    //Camera//

    private fun cameraCheckPermission() {
        Dexter.withContext(this).withPermissions(
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.CAMERA
        ).withListener(

            object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                 report?.let {
                     if(report.areAllPermissionsGranted()){
                         camera()

                     }
                 }

                }

                override fun onPermissionRationaleShouldBeShown(
                    p0: MutableList<PermissionRequest>?,
                    p1: PermissionToken?
                ) {
                   showDialogFor()
                }
            }

        ).onSameThread().check()
    }

    private fun showDialogFor() {
        AlertDialog.Builder(this)
            .setMessage("ıt looks like you have turned off permissions")
            .setPositiveButton("Go to settings"){_,_->

                try {
                    val intent =Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts("package",packageName,null)
                    intent.data = uri
                    startActivity(intent)

                }catch (e:ActivityNotFoundException){
                    e.printStackTrace()
                }
            }
            .setNegativeButton("Cancel"){dialog,_->
                dialog.dismiss()

            }.show()
    }

    private fun camera() {



        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        try {
            startActivityForResult(intent,CAMERA_REQUEST_CODE)
        } catch (e: ActivityNotFoundException) {
            // display error state to the user
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK){
            when(requestCode){
                CAMERA_REQUEST_CODE->{
                    val bitmap = data?.extras?.get("data") as Bitmap
                    binding.imageView.load(bitmap){
                        crossfade(true)
                        crossfade(1000)
                        transformations(CircleCropTransformation())

                    }
                }
                GALLERY_REQUEST_CODE->{
               //     val bitmap = data?.extras?.get("data") as Bitmap
                    binding.imageView.load(data?.data){
                        crossfade(true)
                        crossfade(1000)
                        transformations(CircleCropTransformation())

                    }
                }
            }
        }

    }
}

