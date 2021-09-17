package com.navalcreed.modmaker

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.View
import android.widget.*
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import java.io.File
import java.util.*
import kotlin.system.exitProcess

private const val PROJECT_GUN_SOUND = 1
class GunSound : AppCompatActivity() {
    var tvProjectName: TextView? = null
    var tvDescription: TextView? = null
    var tvUri: TextView? = null
    var tvName: TextView? = null
    var tvSize: TextView? = null

    var projectLocation: String? = null
    var firstSpinner: Spinner? = null
    var secondSpinner: Spinner? = null
    var thirdSpinner: Spinner? = null
    var fourthSpinner: Spinner? = null

    var btnapply: Button? = null
    var btnselect: Button? = null
    var btnbuild: Button? = null

    //Int,for saving what user chose
    var firstPos: Int =0
    var secondPos: Int =0
    var thirdPos: Int =0

    var uri: Uri? = null
    var filePath: String? = null

    var gsPath:Array<String>? = null
    var antiaircraftPath:Array<String>?=null
    var aamsPath:Array<String>?=null        //Medium and Short Range Anti Aircraft
    var aalPath:Array<String>?=null        //Long Range Anti Aircraft
    var gunneryPath:Array<String>?=null
    var gunneryMainPath:Array<String>?=null        //Gunnery Main Battery
    var gunnerySecondaryPath:Array<String>?=null        //Gunnery Secondary
    var shellHitPath:Array<String>?=null        //shell hit

    lateinit var gsPathArrayAdapter:ArrayAdapter<String>
    lateinit var antiaircraftAdapter: ArrayAdapter<String>
    lateinit var aamsAdapter: ArrayAdapter<String>      //Medium and Short Range Anti Aircraft
    lateinit var aalAdapter: ArrayAdapter<String>       //Long Range Anti Aircraft
    lateinit var gunneryAdapter: ArrayAdapter<String>
    lateinit var gunneryMainAdapter:ArrayAdapter<String>        //Gunnery Main Battery
    lateinit var gunnerySecondaryAdapter:ArrayAdapter<String>        //Gunnery Secondary
    lateinit var shellHitAdapter:ArrayAdapter<String>       //shell hit

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editing)

        //get and set project name
        findId()
        val projectName =
            intent.getStringExtra("ProjectName")  //get from intent which send from MainActivity
        projectLocation = getExternalFilesDir("GunSound")?.path + "/" + projectName
        tvProjectName!!.text = getString(R.string.project_name) + projectName

        projectName?.let {
            FileManager.checkInfoPreview(
                getExternalFilesDir("SoundEffect_PRIM")!!.path,
                externalCacheDir!!.path,
                "SoundEffect_PRIM",
                it
            )
        }
        initSpinner()

        btnapply!!.setOnClickListener{

        }
        btnselect!!.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.putExtra("android.content.extra.SHOW_ADVANCED", true)
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            intent.type = "audio/x-wav"
            openActivityForResult(intent)
        }
        btnbuild!!.setOnClickListener {
            val intent = Intent(applicationContext,BuildModPack::class.java)
            val bundle=Bundle()
            bundle.putInt("type", PROJECT_GUN_SOUND)
            bundle.putString("name",projectName)
            intent.putExtra("project",bundle)
            startActivity(intent)
        }

        firstSpinner!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View,
                position: Int,
                id: Long
            ) {
                firstPos=position       //save what user chose
                when (position) {
                    0 -> {
                        secondSpinner!!.adapter=antiaircraftAdapter
                    }
                    1 -> {
                        secondSpinner!!.adapter=gunneryAdapter
                    }
                    2 -> {
                        secondSpinner!!.adapter=shellHitAdapter
                    }
                    else -> {
                        secondSpinner!!.adapter=null
                        thirdSpinner!!.adapter=null
                    }
                }
                freshsecondSpinner()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }



    }

    private fun freshsecondSpinner() {
        secondSpinner!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View,
                position: Int,
                id: Long
            ) {
                secondPos=position        //save what user chose
                when(firstPos){         //get what user chose for showing correct option
                    0->{
                        when (position) {
                            0 -> {
                                thirdSpinner!!.adapter=aamsAdapter
                            }
                            1 -> {
                                thirdSpinner!!.adapter=aalAdapter
                            }
                            else -> {
                                thirdSpinner!!.adapter=null
                            }
                        }
                    }
                    1 -> {
                        when (position) {
                            0 -> {
                                thirdSpinner!!.adapter=gunneryMainAdapter
                            }
                            1 -> {
                                thirdSpinner!!.adapter=gunnerySecondaryAdapter
                            }
                            else -> {
                                thirdSpinner!!.adapter=null
                            }
                        }
                    }

                    else -> {
                        thirdSpinner!!.adapter=null
                    }
                }

            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun initSpinner() {
        //get resources array
        gsPath=resources.getStringArray(R.array.GSPath)
        antiaircraftPath=resources.getStringArray(R.array.AAPath)
        aamsPath=resources.getStringArray(R.array.AAMaSRPath)
        aalPath=resources.getStringArray(R.array.AALRPath)
        gunneryPath=resources.getStringArray(R.array.GunneryPath)
        gunneryMainPath=resources.getStringArray(R.array.Gunnery_Main_Caliber)
        gunnerySecondaryPath=resources.getStringArray(R.array.Gunnery_SecondaryPath)
        shellHitPath=resources.getStringArray(R.array.ShellHitPath)

        //initialize array adapter
        gsPathArrayAdapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, gsPath as Array<out String>)
        antiaircraftAdapter=ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,antiaircraftPath as Array<out String>)
        aamsAdapter=ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,aamsPath as Array<out String>)
        aalAdapter=ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,aalPath as Array<out String>)
        gunneryAdapter=ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,gunneryPath as Array<out String>)
        gunneryMainAdapter=ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,gunneryMainPath as Array<out String>)
        gunnerySecondaryAdapter=ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,gunnerySecondaryPath as Array<out String>)
        shellHitAdapter=ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,shellHitPath as Array<out String>)

        //set spinner adapter
        firstSpinner!!.adapter=gsPathArrayAdapter
        secondSpinner!!.adapter=antiaircraftAdapter
        thirdSpinner!!.adapter=aamsAdapter
    }

    private fun findId() {
        tvProjectName = findViewById(R.id.tv_project_name_title)
        tvDescription = findViewById(R.id.tv_description)
        tvUri = findViewById(R.id.tv_uri)
        tvName = findViewById(R.id.tv_name)
        tvSize = findViewById(R.id.tv_size)

        firstSpinner = findViewById(R.id.firstSP)
        secondSpinner = findViewById(R.id.secondSP)
        thirdSpinner = findViewById(R.id.thirdSP)
        fourthSpinner = findViewById(R.id.fourthSP)

        btnapply = findViewById(R.id.btn_apply)
        btnselect = findViewById(R.id.btn_select)
        btnbuild = findViewById(R.id.btn_build)
    }





    private val startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data
            // Handle the Intent
            //do stuff here
            if (data != null) {
                uri = data.data
                Log.e("Return Uri", "Uri: " + uri.toString())
                tvUri!!.text=getString(R.string.file_uri)+uri.toString()
                tvName!!.text=getString(R.string.file_name)+GetFileFromUri.name(this,uri)
                tvSize!!.text=getString(R.string.file_size)+GetFileFromUri.size(this,uri)

                val temp=externalCacheDir.toString().split('/')
                Log.e("Return", "temp: " + temp[1]+"/"+temp[2]+"/"+temp[3]+"/")
                val  uritemp=uri!!.path!!.split(':')
                Log.e("Return uritemp", "uritemp: " + uritemp[1])
                filePath=temp[1]+"/"+temp[2]+"/"+temp[3]+"/"+ uritemp[1]
            }
        }
    }
    private fun openActivityForResult(intent: Intent) {
        startForResult.launch(intent)
    }
}
