package com.navalcreed.modmaker

import android.annotation.SuppressLint
import android.app.Activity
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

private const val PROJECT_GUN_SOUND = 1
class GunSound : AppCompatActivity() {
    private var tvProjectName: TextView? = null
    private var tvDescription: TextView? = null
    private var tvUri: TextView? = null
    private var tvName: TextView? = null
    private var tvSize: TextView? = null

    private var projectLocation: String? = null
    private var firstSpinner: Spinner? = null
    var secondSpinner: Spinner? = null
    var thirdSpinner: Spinner? = null
    private var fourthSpinner: Spinner? = null

    private var btnapply: Button? = null
    private var btnselect: Button? = null
    private var btnbuild: Button? = null

    //for saving what user chose
    var firstPos: Int =0
    var secondPos: Int =0
    var fileName:String? = null

    private var uri: Uri? = null
    private var filePath: String? = null

    private var gsPath:Array<String>? = null
    private var antiaircraftPath:Array<String>?=null
    private var aamsPath:Array<String>?=null        //Medium and Short Range Anti Aircraft
    private var aalPath:Array<String>?=null        //Long Range Anti Aircraft

    private var gunneryPath:Array<String>?=null         //Gunnery
    private var gunneryMainPath:Array<String>?=null        //Gunnery Main Battery
    private var gunnerySecondaryPath:Array<String>?=null        //Gunnery Secondary

    private var shellHitPath:Array<String>?=null        //shell hit
    private var shellHitPathBattleship:Array<String>?=null    //shell hit Battleship
    private var shellHitPathCarrier:Array<String>?=null    //shell hit Carrier
    private var shellHitPathCruiser:Array<String>?=null    //shell hit Cruiser
    private var shellHitPathDestroyer:Array<String>?=null    //shell hit Destroyer
    private var shellHitPathCritical:Array<String>?=null    //shell hit Critical
    private var shellHitPathSplashed:Array<String>?=null    //shell hit splashed

    private var torpedolaunchPath:Array<String>?=null    //torpedo launch

    private var torpedohitPath:Array<String>?=null    //torpedo hit

    private var planeDivePath:Array<String>?=null    //plane Dive
    private var planeDiveStukaPath:Array<String>?=null    //stuka Dive
    private var planeDiveOthersPath:Array<String>?=null    //Others Dive

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

    lateinit var torpedolaunchAdapter:ArrayAdapter<String>    //torpedo launch

    lateinit var torpedoHitAdapter:ArrayAdapter<String>    //torpedo hit

    lateinit var planeDiveAdapter:ArrayAdapter<String>    //plane Dive
    lateinit var planeDiveStukaAdapter:ArrayAdapter<String>    //stuka Dive
    lateinit var planeDiveOthersAdapter:ArrayAdapter<String>    //Others Dive

    @SuppressLint("SetTextI18n")
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
                    3 -> {
                        secondSpinner!!.adapter=torpedolaunchAdapter
                    }
                    4 -> {
                        secondSpinner!!.adapter=torpedoHitAdapter
                    }
                    5 -> {
                        secondSpinner!!.adapter=planeDiveAdapter
                    }
                    else -> {
                        secondSpinner!!.adapter=null
                        thirdSpinner!!.adapter=null
                    }
                }
                updateSecondSpinner()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

    }

    private fun updateSecondSpinner() {
        secondSpinner!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View,
                position: Int,
                id: Long
            ) {
                secondPos=position        //save what user chose
                //init
                thirdSpinner!!.isEnabled=true
                fileName=null

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
                    2 -> {
                        when (position){
                            0 -> {
                                thirdSpinner!!.adapter=shellHitBattleshipAdapter
                            }
                            1 -> {
                                thirdSpinner!!.adapter=shellHitCarrierAdapter
                            }
                            2 -> {
                                thirdSpinner!!.adapter=shellHitCruiserAdapter
                            }
                            3 -> {
                                thirdSpinner!!.adapter=shellHitDestroyerAdapter
                            }
                            4 -> {
                                thirdSpinner!!.adapter=shellHitCriticalAdapter
                            }
                            5 -> {
                                thirdSpinner!!.adapter=shellHitSplashedAdapter
                            }
                            else -> {
                                thirdSpinner!!.adapter=null
                            }
                        }
                    }
                    5 -> {
                        when (position){
                            0 -> {
                                thirdSpinner!!.adapter=planeDiveStukaAdapter
                            }
                            1 -> {
                                thirdSpinner!!.adapter=planeDiveOthersAdapter
                            }
                            else -> {
                                thirdSpinner!!.adapter=null
                            }
                        }
                    }
                    else -> {
                        thirdSpinner!!.adapter=null
                        thirdSpinner!!.isEnabled=false
                    }
                }
                updateThirdSpinner()
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }
    }

    private fun updateThirdSpinner(){
        thirdSpinner!!.onItemSelectedListener=object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View,
                position: Int,
                id: Long) {
                when(firstPos){
                    0 -> {
                        when(secondPos){
                            0 -> {
                                fileName= aamsPath?.get(position)
                            }
                            1 -> {
                                fileName= aalPath?.get(position)
                            }
                        }
                    }
                    1 -> {
                        when(secondPos){
                            0 -> {

                            }
                            1 -> {

                            }
                        }
                    }
                    2 -> {
                        when(secondPos){
                            0 -> {

                            }
                            1 -> {

                            }
                            2 -> {

                            }
                            3 -> {

                            }
                            4 -> {

                            }
                            5 -> {

                            }
                        }
                    }
                    5 -> {
                        when(secondPos){
                            0 -> {

                            }
                            1 -> {

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

        torpedolaunchAdapter=toArrayAdapter(torpedolaunchPath)

        torpedoHitAdapter=toArrayAdapter(torpedohitPath)

        planeDiveAdapter=toArrayAdapter(planeDivePath)
        planeDiveStukaAdapter=toArrayAdapter(planeDiveStukaPath)
        planeDiveOthersAdapter=toArrayAdapter(planeDiveOthersPath)



        //set spinner's default status
        firstSpinner!!.adapter=gsPathArrayAdapter
        secondSpinner!!.adapter=antiaircraftAdapter
        thirdSpinner!!.adapter=aamsAdapter
        fourthSpinner!!.isEnabled=false
        fourthSpinner!!.isVisible=false
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
