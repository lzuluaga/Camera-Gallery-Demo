package co.cedesistemas.myapplicationcamera

import android.Manifest
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import co.cedesistemas.myapplicationcamera.Permissions.isGrantedPermissions
import co.cedesistemas.myapplicationcamera.Permissions.verifyPermissions
import co.cedesistemas.myapplicationcamera.databinding.ActivityMainBinding
import com.bumptech.glide.Glide
import java.io.File
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var photoFile: File? =  null
    private var attachmentType = AttachmentType.None

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setListener()
    }

    private fun setListener() {
        binding.imgCamera.setOnClickListener {  }
        binding.imgGalley.setOnClickListener { showGallery() }
    }

    private fun showGallery() {
        attachmentType = AttachmentType.Gallery
        if (isGrantedPermissions(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)){
            showGalleryIntent()
        } else {
            val permissions = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
            verifyPermissions(this, permissions)
        }
    }

    private fun showGalleryIntent() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        intent.putExtra(Intent.EXTRA_MIME_TYPES, "image/*")
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false)
        startActivityForResult(intent, REQUEST_CODE_GALLERY)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == REQUEST_CODE_PERMISSION && attachmentType == AttachmentType.Gallery){
            showGalleryIntent()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_GALLERY){
            getGalleryImage(data)
        }

    }

    private fun getGalleryImage(data: Intent?) {
        data?.let {
            showImage(it.data.toString())
        }
    }

    private fun showImage(image: String) {
        Glide.with(this).load(image).into(binding.imgShow)
    }


}
