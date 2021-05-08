package co.cedesistemas.myapplicationcamera

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat

object Permissions {

    fun isGrantedPermissions(context: Context, permissionType: String): Boolean {
        val permission = ActivityCompat.checkSelfPermission(context, permissionType)
        return permission == PackageManager.PERMISSION_GRANTED
    }

    fun verifyPermissions(activity: Activity, permissionsType: Array<String>){
        ActivityCompat.requestPermissions(activity, permissionsType, REQUEST_CODE_PERMISSION)
    }

}
