package com.navalcreed.modmaker

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import java.io.File
import java.io.IOException
import kotlin.system.exitProcess

private const val PROJECT_GUN_SOUND = 1

@SuppressLint("StaticFieldLeak")
private lateinit var tvProjectName: TextView
@SuppressLint("StaticFieldLeak")
private lateinit var tvDescription: TextView
@SuppressLint("StaticFieldLeak")
private lateinit var tvUri: TextView
@SuppressLint("StaticFieldLeak")
private lateinit var tvName: TextView
@SuppressLint("StaticFieldLeak")
private lateinit var tvSize: TextView

private lateinit var projectLocation: String
@SuppressLint("StaticFieldLeak")
private lateinit var firstSpinner: Spinner
@SuppressLint("StaticFieldLeak")
private lateinit var secondSpinner: Spinner
@SuppressLint("StaticFieldLeak")
private lateinit var thirdSpinner: Spinner
@SuppressLint("StaticFieldLeak")
private lateinit var fourthSpinner: Spinner

@SuppressLint("StaticFieldLeak")
private lateinit var btnapply: Button
@SuppressLint("StaticFieldLeak")
private lateinit var btnselect: Button
@SuppressLint("StaticFieldLeak")
private lateinit var btnbuild: Button

//for saving what user chose
var firstPos: Int =0
var secondPos: Int =0
lateinit var fileName: String

private lateinit var uri: Uri
private lateinit var filePath: String

private lateinit var gsPath: Array<String>
private lateinit var antiaircraftPath: Array<String>
private lateinit var aamsPath: Array<String>        //Medium and Short Range Anti Aircraft
private lateinit var aalPath: Array<String>        //Long Range Anti Aircraft

private lateinit var gunneryPath: Array<String>         //Gunnery
private lateinit var gunneryMainPath: Array<String>        //Gunnery Main Battery
private lateinit var gunnerySecondaryPath: Array<String>        //Gunnery Secondary

private lateinit var shellHitPath: Array<String>        //shell hit
private lateinit var shellHitPathBattleship: Array<String>    //shell hit Battleship
private lateinit var shellHitPathCarrier: Array<String>    //shell hit Carrier
private lateinit var shellHitPathCruiser: Array<String>    //shell hit Cruiser
private lateinit var shellHitPathDestroyer: Array<String>    //shell hit Destroyer
private lateinit var shellHitPathCritical: Array<String>    //shell hit Critical
private lateinit var shellHitPathSplashed: Array<String>    //shell hit splashed

private lateinit var torpedolaunchPath: Array<String>    //torpedo launch

private lateinit var torpedohitPath: Array<String>    //torpedo hit

private lateinit var planeDivePath: Array<String>    //plane Dive
private lateinit var planeDiveStukaPath: Array<String>    //stuka Dive
private lateinit var planeDiveOthersPath: Array<String>    //Others Dive

//ArrayAdapter
private lateinit var gsPathArrayAdapter:ArrayAdapter<String>
lateinit var antiaircraftAdapter: ArrayAdapter<String>
lateinit var aamsAdapter: ArrayAdapter<String>      //Medium and Short Range Anti Aircraft
lateinit var aalAdapter: ArrayAdapter<String>       //Long Range Anti Aircraft

lateinit var gunneryAdapter: ArrayAdapter<String>
lateinit var gunneryMainAdapter:ArrayAdapter<String>        //Gunnery Main Battery
lateinit var gunnerySecondaryAdapter:ArrayAdapter<String>        //Gunnery Secondary

lateinit var shellHitAdapter:ArrayAdapter<String>       //shell hit
lateinit var shellHitBattleshipAdapter:ArrayAdapter<String>    //shell hit Battleship
lateinit var shellHitCarrierAdapter:ArrayAdapter<String>    //shell hit Carrier
lateinit var shellHitCruiserAdapter:ArrayAdapter<String>    //shell hit Cruiser
lateinit var shellHitDestroyerAdapter:ArrayAdapter<String>    //shell hit Destroyer
lateinit var shellHitCriticalAdapter:ArrayAdapter<String>    //shell hit Critical
lateinit var shellHitSplashedAdapter:ArrayAdapter<String>    //shell hit splashed

lateinit var torpedoLaunchAdapter:ArrayAdapter<String>    //torpedo launch

lateinit var torpedoHitAdapter:ArrayAdapter<String>    //torpedo hit

lateinit var planeDiveAdapter:ArrayAdapter<String>    //plane Dive
lateinit var planeDiveStukaAdapter:ArrayAdapter<String>    //stuka Dive
lateinit var planeDiveOthersAdapter:ArrayAdapter<String>    //Others Dive

class Sound_PRIM : AppCompatActivity() {
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editing)

        //get and set project name
        findId()
        val projectName =
            intent.getStringExtra("ProjectName")  //get from intent which send from MainActivity
        projectLocation = getExternalFilesDir("SoundEffect_PRIM")?.path + "/" + projectName
        tvProjectName.text = getString(R.string.project_name) + projectName

        projectName?.let {
            FileManager.checkInfoPreview(
                getExternalFilesDir("SoundEffect_PRIM")!!.path,
                externalCacheDir!!.path,
                "SoundEffect_PRIM",
                it
            )
        }
        initSpinner()

        btnapply.setOnClickListener{
            if(!File(projectLocation).exists()){
                try{
                    File(projectLocation).mkdirs()
                }catch (e: IOException){
                    val builder = AlertDialog.Builder(this)
                    val info=
                        "${getString(R.string.get_Exception_info_head)}\n$e \n${getString(R.string.get_Exception_info_feet)}"
                    builder.setTitle(R.string.get_Exception_title)
                    builder.setMessage(info)
                    builder.setPositiveButton(R.string.OK) {
                            _, _ ->
                        exitProcess(0)
                    }
                    builder.show()
                }
            }
            try {
                if (filePath.let { it1 -> FileManager.fileCopy(it1,"$projectLocation/$fileName") }){
                    Toast.makeText(applicationContext,R.string.Success,Toast.LENGTH_SHORT).show()
                }
            }catch (e:Exception){
                val builder = AlertDialog.Builder(this)
                val info=getString(R.string.get_Exception_info_head)+" "+e.toString()+" "+getString(R.string.get_Exception_info_feet)
                builder.setTitle(R.string.get_Exception_title)
                builder.setMessage(info)
                builder.setPositiveButton(R.string.OK) {
                        _, _ ->
                    exitProcess(0)
                }
                builder.show()
            }
        }
        btnselect.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.putExtra("android.content.extra.SHOW_ADVANCED", true)
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            intent.type = "audio/x-wav"
            openActivityForResult(intent)
        }
        btnbuild.setOnClickListener {
            val intent = Intent(applicationContext,BuildModPack::class.java)
            val bundle=Bundle()
            bundle.putInt("type", PROJECT_GUN_SOUND)
            bundle.putString("name",projectName)
            intent.putExtra("project",bundle)
            startActivity(intent)
        }

        firstSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                firstPos=position       //save what user chose
                fileName= null.toString()

                when (position) {
                    0 -> {
                        secondSpinner.adapter=antiaircraftAdapter
                    }
                    1 -> {
                        secondSpinner.adapter=gunneryAdapter
                    }
                    2 -> {
                        secondSpinner.adapter=shellHitAdapter
                    }
                    3 -> {
                        secondSpinner.adapter=torpedoLaunchAdapter
                    }
                    4 -> {
                        secondSpinner.adapter=torpedoHitAdapter
                    }
                    5 -> {
                        secondSpinner.adapter=planeDiveAdapter
                    }
                    else -> {
                        secondSpinner.adapter=null
                        thirdSpinner.adapter=null
                    }
                }
                updateSecondSpinner()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }
    private fun updateSecondSpinner() {
        secondSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                secondPos=position        //save what user chose
                //init
                thirdSpinner.isEnabled=true

                when(firstPos){         //get what user chose for showing correct option
                    0->{
                        when (position) {
                            0 -> {
                                thirdSpinner.adapter=aamsAdapter
                            }
                            1 -> {
                                thirdSpinner.adapter=aalAdapter
                            }
                            else -> {
                                thirdSpinner.adapter=null
                            }
                        }
                    }
                    1 -> {
                        when (position) {
                            0 -> {
                                thirdSpinner.adapter=gunneryMainAdapter
                            }
                            1 -> {
                                thirdSpinner.adapter=gunnerySecondaryAdapter
                            }
                            else -> {
                                thirdSpinner.adapter=null
                            }
                        }
                    }
                    2 -> {
                        when (position){
                            0 -> {
                                thirdSpinner.adapter=shellHitBattleshipAdapter
                            }
                            1 -> {
                                thirdSpinner.adapter=shellHitCarrierAdapter
                            }
                            2 -> {
                                thirdSpinner.adapter=shellHitCruiserAdapter
                            }
                            3 -> {
                                thirdSpinner.adapter=shellHitDestroyerAdapter
                            }
                            4 -> {
                                thirdSpinner.adapter=shellHitCriticalAdapter
                            }
                            5 -> {
                                thirdSpinner.adapter=shellHitSplashedAdapter
                            }
                            else -> {
                                thirdSpinner.adapter=null
                            }
                        }
                    }
                    3 -> {
                        thirdSpinner.adapter=null
                        thirdSpinner.isEnabled=false
                        fileName= torpedolaunchPath[position]
                    }
                    4 -> {
                        thirdSpinner.adapter=null
                        thirdSpinner.isEnabled=false
                        fileName= torpedohitPath[position]
                    }
                    5 -> {
                        when (position){
                            0 -> {
                                thirdSpinner.adapter=planeDiveStukaAdapter
                            }
                            1 -> {
                                thirdSpinner.adapter=planeDiveOthersAdapter
                            }
                            else -> {
                                thirdSpinner.adapter=null
                            }
                        }
                    }
                    else -> {
                        thirdSpinner.adapter=null
                        thirdSpinner.isEnabled=false
                    }
                }
                updateThirdSpinner()
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }
    }

    private fun updateThirdSpinner(){
        thirdSpinner.onItemSelectedListener=object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long) {
                when(firstPos){
                    0 -> {
                        when(secondPos){
                            0 -> {
                                fileName= aamsPath[position]
                            }
                            1 -> {
                                fileName= aalPath[position]
                            }
                        }
                    }
                    1 -> {
                        when(secondPos){
                            0 -> {
                                fileName= gunneryMainPath[position]
                            }
                            1 -> {
                                fileName= gunnerySecondaryPath[position]
                            }
                        }
                    }
                    2 -> {
                        when(secondPos){
                            0 -> {
                                fileName= shellHitPathBattleship[position]
                            }
                            1 -> {
                                fileName= shellHitPathCarrier[position]
                            }
                            2 -> {
                                fileName= shellHitPathCruiser[position]
                            }
                            3 -> {
                                fileName= shellHitPathDestroyer[position]
                            }
                            4 -> {
                                fileName= shellHitPathCritical[position]
                            }
                            5 -> {
                                fileName= shellHitPathSplashed[position]
                            }
                        }
                    }
                    5 -> {
                        when(secondPos){
                            0 -> {
                                fileName= planeDiveStukaPath[position]
                            }
                            1 -> {
                                fileName= planeDiveOthersPath[position]
                            }
                        }
                    }
                }

            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

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
        shellHitPathBattleship=resources.getStringArray(R.array.ShellHit_Battleship)
        shellHitPathCarrier=resources.getStringArray(R.array.ShellHit_Carrier)
        shellHitPathCruiser=resources.getStringArray(R.array.ShellHit_Cruiser)
        shellHitPathDestroyer=resources.getStringArray(R.array.ShellHit_Destroyer)
        shellHitPathCritical=resources.getStringArray(R.array.ShellHit_Critical)
        shellHitPathSplashed=resources.getStringArray(R.array.ShellHit_Splashed)

        torpedolaunchPath=resources.getStringArray(R.array.torpedo_launch)

        torpedohitPath=resources.getStringArray(R.array.torpedo_hit)

        planeDivePath=resources.getStringArray(R.array.PlaneDive)
        planeDiveStukaPath=resources.getStringArray(R.array.PlaneDive_Stuka)
        planeDiveOthersPath=resources.getStringArray(R.array.PlaneDive_Others)

        //initialize array adapter
        gsPathArrayAdapter=toArrayAdapter(gsPath)
        antiaircraftAdapter=toArrayAdapter(antiaircraftPath)
        aamsAdapter=toArrayAdapter(aamsPath)
        aalAdapter=toArrayAdapter(aalPath)

        gunneryAdapter=toArrayAdapter(gunneryPath)
        gunneryMainAdapter=toArrayAdapter(gunneryMainPath)
        gunnerySecondaryAdapter=toArrayAdapter(gunnerySecondaryPath)

        shellHitAdapter=toArrayAdapter(shellHitPath)
        shellHitBattleshipAdapter=toArrayAdapter(shellHitPathBattleship)
        shellHitCarrierAdapter=toArrayAdapter(shellHitPathCarrier)
        shellHitCruiserAdapter=toArrayAdapter(shellHitPathCruiser)
        shellHitDestroyerAdapter=toArrayAdapter(shellHitPathDestroyer)
        shellHitCriticalAdapter=toArrayAdapter(shellHitPathCritical)
        shellHitSplashedAdapter=toArrayAdapter(shellHitPathSplashed)

        torpedoLaunchAdapter=toArrayAdapter(torpedolaunchPath)

        torpedoHitAdapter=toArrayAdapter(torpedohitPath)

        planeDiveAdapter=toArrayAdapter(planeDivePath)
        planeDiveStukaAdapter=toArrayAdapter(planeDiveStukaPath)
        planeDiveOthersAdapter=toArrayAdapter(planeDiveOthersPath)



        //set spinner's default status
        firstSpinner.adapter=gsPathArrayAdapter
        secondSpinner.adapter=antiaircraftAdapter
        thirdSpinner.adapter=aamsAdapter
        fourthSpinner.isEnabled=false
        fourthSpinner.isVisible=false
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

    private fun toArrayAdapter(String_Array:Array<String>?): ArrayAdapter<String> {
        return ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,String_Array as Array<out String>)
    }

    @SuppressLint("SetTextI18n")
    private val startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data
            // Handle the Intent
            //do stuff here
            if (data != null) {
                uri = data.data!!
                Log.e("Return Uri", "Uri: " + uri.toString())
                tvUri.text=getString(R.string.file_uri)+uri.toString()
                tvName.text=getString(R.string.file_name)+GetFileFromUri.name(this,uri)
                tvSize.text=getString(R.string.file_size)+GetFileFromUri.size(this,uri)

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