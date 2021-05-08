package co.cedesistemas.myapplicationcamera

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.core.content.FileProvider
import co.cedesistemas.myapplicationcamera.Permissions.isGrantedPermissions
import co.cedesistemas.myapplicationcamera.Permissions.verifyPermissions
import co.cedesistemas.myapplicationcamera.databinding.ActivityMainBinding
import com.bumptech.glide.Glide
import java.io.File
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.Date

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var photoFile: File? = null
    private var attachmentType = AttachmentType.None

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setListener()
    }

    private fun setListener() {
        binding.imgCamera.setOnClickListener { showCamera() }
        binding.imgGalley.setOnClickListener { showGallery() }
    }

    private fun showCamera() {
        attachmentType = AttachmentType.Photo
        if (isGrantedPermissions(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            takePhotoIntent()
        } else {
            val permissions = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
            verifyPermissions(this, permissions)
        }
    }

    private fun takePhotoIntent() {
        photoFile = null
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (intent.resolveActivity(packageManager) != null) {
            try {
                photoFile = createImageFile()
            } catch (exception: Exception) {
                Toast.makeText(this, "$exception.message", Toast.LENGTH_SHORT).show()
            }
            photoFile?.let {
                val photoUri = FileProvider.getUriForFile(
                    this,
                    "co.cedesistemas",
                    it
                )
                val resolveInfoList = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)
                for (resolveInfo in resolveInfoList){
                    val packageName = resolveInfo.activityInfo.packageName
                    grantUriPermission(packageName, photoUri,
                        Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
                startActivityForResult(intent, REQUEST_CODE_CAMERA)
            }
        }
    }

    private fun createImageFile(): File? {
        val imageFileName = "photo_${SimpleDateFormat(FORMAT_DATE).format(Date())}"
        val storageDirectory: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        storageDirectory?.let {
            return File.createTempFile(imageFileName, SUFFIX_FILE_NAME, it)
        } ?: return null
    }


    private fun showGallery() {
        attachmentType = AttachmentType.Gallery
        if (isGrantedPermissions(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
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

        if (requestCode == REQUEST_CODE_PERMISSION && attachmentType == AttachmentType.Gallery) {
            showGalleryIntent()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_GALLERY) {
            getGalleryImage(data)
        } else if (resultCode == 0 && requestCode == REQUEST_CODE_CAMERA){
            if (photoFile?.length() == 0L){
                photoFile?.delete()
            }
        }else if (requestCode == REQUEST_CODE_CAMERA){
            showCameraIntent()
        }


    }

    private fun showCameraIntent() {
        photoFile?.let {
            showImage(it.path)
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
