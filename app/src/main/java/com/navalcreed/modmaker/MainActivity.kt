package com.navalcreed.modmaker

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat

private const val REQUEST_EXTERNAL_STORAGE = 1
private val PERMISSIONS_STORAGE = arrayOf(
    Manifest.permission.READ_EXTERNAL_STORAGE,
    Manifest.permission.WRITE_EXTERNAL_STORAGE
)
class MainActivity : AppCompatActivity() {

    private var btnCaptainVoice:Button ?= null
    private var btnGunsound:Button ?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val permission =
            ActivityCompat.checkSelfPermission(applicationContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) and
                    ActivityCompat.checkSelfPermission(applicationContext, Manifest.permission.READ_EXTERNAL_STORAGE)

        if (PackageManager.PERMISSION_GRANTED!=permission) {
            // We don't have permission so alert the user
            val builder = AlertDialog.Builder(this)
            builder.setTitle(R.string.get_Permissions_title)
            builder.setMessage(R.string.get_Permissions_info)
            builder.setPositiveButton(R.string.accept) {
                    _, _ ->
                ActivityCompat.requestPermissions(
                    this,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
                )       //normal permissions

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    val uri = Uri.parse("package:${BuildConfig.APPLICATION_ID}")
                    startActivity(
                        Intent(
                            Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION,
                            uri
                        )
                    )
                }       //special permissions from Android 11(R) and above
            }
            builder.setNegativeButton(R.string.deny){_, _ ->
                finishAffinity()   //force shut down all activities
            }
            builder.show()

        }
        getId()
        btnCaptainVoice!!.setOnClickListener {
            getProjectName(0)
        }
        btnGunsound!!.setOnClickListener {
            getProjectName(1)
        }

    }
    private fun getId(){
        btnCaptainVoice=findViewById(R.id.btn_captain_voice)
        btnGunsound=findViewById(R.id.btn_gun_sound)
    }
    private fun getProjectName(projectType:Int) {
        var intent:Intent?=null
        val builder = AlertDialog.Builder(this)
        val inflater = layoutInflater
        builder.setTitle(getString(R.string.get_project_name))
        val dialogLayout = inflater.inflate(R.layout.get_project_name, null)
        val editText  = dialogLayout.findViewById<EditText>(R.id.editText)
        builder.setView(dialogLayout)
        builder.setPositiveButton(R.string.OK) {
                _, _ ->
                if(!editText.text.isNullOrBlank()) {
                    when(projectType){
                        0->{
                            intent=Intent(applicationContext,CaptainVoice::class.java)
                        }
                        1 -> {
                            //intent=Intent(applicationContext,GunSound::class.java)
                            intent=Intent(applicationContext,Sound_PRIM::class.java)
                        }
                    }
                    intent!!.putExtra("ProjectName", editText.text.toString())
                    startActivity(intent)
                }
                else{
                    Toast.makeText(applicationContext, R.string.Err_NPN, Toast.LENGTH_SHORT).show()
                }
            }
        builder.show()
    }

    override fun onBackPressed() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(R.string.get_exit_title)
        builder.setMessage(R.string.get_exit_info)
        builder.setPositiveButton(R.string.Yes) {
                _, _ ->
            finishAffinity()   //force shut down all activities
        }
        builder.setNegativeButton(R.string.Wait){_, _ ->

        }
        builder.show()
    }
}