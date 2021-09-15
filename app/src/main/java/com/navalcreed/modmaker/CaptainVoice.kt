     package com.navalcreed.modmaker


import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.View
import android.widget.*
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import java.io.File
import java.util.*
import kotlin.system.exitProcess


//private static final int READ_REQUEST_CODE = 1;
private const val PROJECT_CAPTAIN_VOICE=0



@Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS", "NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class CaptainVoice : AppCompatActivity() {
    @SuppressLint("SetTextI18n")

    var tvProjectName:TextView?=null
    var tvDescription:TextView?=null
    var tvUri:TextView?=null
    var tvName:TextView?=null
    var tvSize:TextView?=null

    var projectLocation:String?=null
    var firstSpinner:Spinner?=null
    var secondSpinner:Spinner?=null
    var thirdSpinner:Spinner?=null
    var fourthSpinner:Spinner?=null

    var btnapply:Button?=null
    var btnselect:Button?=null
    var btnbuild:Button?=null

    //String,for saving where user want .wav go
    var firstPath:String ?=""
    var secondPath:String?=""
    var thirdPath:String?=""
    var fourthPath:String?=""

    var uri: Uri?=null
    var filePath:String?=null

    //know first spinner position to decide enable third spinner or not
    var firstpos=0
    var secondpos=0

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editing)
        //get and set project name
        findId()
        val projectName = intent.getStringExtra("ProjectName")  //get from intent which send from MainActivity
        projectLocation = getExternalFilesDir("CaptainVoice")?.path + "/" + projectName
        tvProjectName!!.text = getString(R.string.project_name) + projectName
        if (projectName != null) decompressRes() //decompress Captain Voice example zip to project folder

        projectName?.let { FileManager.checkInfoPreview(getExternalFilesDir("CaptainVoice")!!.path,externalCacheDir!!.path,"CaptainVoice",it) }

        btnapply!!.setOnClickListener{
            if(uri!=null)
                try {
                    if (filePath?.let { it1 -> FileManager.fileCopy(it1,"$projectLocation/$firstPath$secondPath$thirdPath$fourthPath") } == true){
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
            bundle.putInt("type",PROJECT_CAPTAIN_VOICE)
            bundle.putString("name",projectName)
            intent.putExtra("project",bundle)
            startActivity(intent)
        }
    }


    private fun findId() {
        tvProjectName = findViewById(R.id.tv_project_name_title)
        tvDescription = findViewById(R.id.tv_description)
        tvUri=findViewById(R.id.tv_uri)
        tvName=findViewById(R.id.tv_name)
        tvSize=findViewById(R.id.tv_size)

        firstSpinner = findViewById(R.id.firstSP)
        secondSpinner = findViewById(R.id.secondSP)
        thirdSpinner = findViewById(R.id.thirdSP)
        fourthSpinner = findViewById(R.id.fourthSP)

        btnapply=findViewById(R.id.btn_apply)
        btnselect=findViewById(R.id.btn_select)
        btnbuild=findViewById(R.id.btn_build)
    }


    private fun decompressRes() {
        if (!File(projectLocation).exists())
            Decompress.unzipFromAssets(this, "CaptainVoice.zip", projectLocation)//decompress Captain Voice example zip to project folder
        //else if()
        //get file list
        val arrayAdapter: ArrayAdapter<String>
        var arrad = arrayOf<String>() //String[]
        File(projectLocation).listFiles().sortedArrayDescending()/*force sort by name z-a*/.reversedArray()/*force reverse to a-z*/.forEach {
            val s = it.toString().split('/')
            arrad += (s[s.size - 1])
        }
        //set adapter
        arrayAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, arrad)
//        for (element in arrad)Log.v("Array ", element) /*for debug use*/
        firstSpinner!!.adapter = arrayAdapter
        //check spinner selection
        firstListPosition(arrad)
    }

    private fun firstListPosition(array: Array<String>) {
        firstSpinner!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            @RequiresApi(Build.VERSION_CODES.N)
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val first: String
                firstpos = position
                try {
                    first = array[position]
                    firstPath = "$first/"
                    Log.e("FirstPos", first)
                    getChildFileList(1)
                } catch (e: Exception) {
                    Log.e("FirstException", e.toString())
                }
                when (position) {
                    0 -> {
                        tvDescription!!.text = Html.fromHtml(getString(R.string.des_Autopilot), Html.FROM_HTML_MODE_LEGACY)
                    }//Auto Pilot
                    1 -> {
                        tvDescription!!.text = Html.fromHtml(getString(R.string.des_Battlestart), Html.FROM_HTML_MODE_LEGACY)
                    }//Battle Start
                    2 -> {
                        tvDescription!!.text = Html.fromHtml(getString(R.string.des_Cons), Html.FROM_HTML_MODE_LEGACY)
                    }//Consumable
                    3 -> {
                        tvDescription!!.text = Html.fromHtml(getString(R.string.des_CriticalFlooding), Html.FROM_HTML_MODE_LEGACY)
                    }//Flooding
                    4 -> {
                        tvDescription!!.text = Html.fromHtml(getString(R.string.des_Detection), Html.FROM_HTML_MODE_LEGACY)
                    }//Detection
                    5 -> {
                        tvDescription!!.text = Html.fromHtml(getString(R.string.des_Domination), Html.FROM_HTML_MODE_LEGACY)
                    }//Domination
                    6 -> {
                        tvDescription!!.text = Html.fromHtml(getString(R.string.des_DoubleKill), Html.FROM_HTML_MODE_LEGACY)
                    }//Double kill
                    7 -> {
                        tvDescription!!.text = Html.fromHtml(getString(R.string.des_Fire), Html.FROM_HTML_MODE_LEGACY)
                    }//Fire
                    8 -> {
                        tvDescription!!.text = Html.fromHtml(getString(R.string.des_FirstKill), Html.FROM_HTML_MODE_LEGACY)
                    }//First kill
                    9 -> {
                        tvDescription!!.text = Html.fromHtml(getString(R.string.des_FriendlyHit), Html.FROM_HTML_MODE_LEGACY)
                    }//Friendly Hit
                    10 -> {
                        tvDescription!!.text = Html.fromHtml(getString(R.string.des_HitAndKill), Html.FROM_HTML_MODE_LEGACY)
                    }//Hit and Kill
                    11 -> {
                        tvDescription!!.text = Html.fromHtml(getString(R.string.des_LastHope), Html.FROM_HTML_MODE_LEGACY)
                    }//Last Hope
                    12 -> {
                        tvDescription!!.text = Html.fromHtml(getString(R.string.des_ModuleDisable), Html.FROM_HTML_MODE_LEGACY)
                    }//Module disable
                    13 -> {
                        tvDescription!!.text = Html.fromHtml(getString(R.string.des_PlaneGroupId), Html.FROM_HTML_MODE_LEGACY)
                    }//Plane Group Id
                    14 -> {
                        tvDescription!!.text = Html.fromHtml(getString(R.string.des_PlaneState), Html.FROM_HTML_MODE_LEGACY)
                    }//Plane State
                    15 -> {
                        tvDescription!!.text = Html.fromHtml(getString(R.string.des_TeamKillPunishment), Html.FROM_HTML_MODE_LEGACY)
                    }//TeamKill Punishment
                    else->null
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }
    }

    private fun secondListPosition(array: Array<String>) {
        secondSpinner!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val second = array[position]
                secondpos = position
                when (firstpos) {
                    1, 3, 6, 7, 8, 9, 11, 15 -> {
                        thirdSpinner!!.isEnabled = false
                        fourthSpinner!!.isEnabled = false
                        secondPath = second
                        thirdPath=""
                        fourthPath=""
                    }//Battle Start,Flooding,Double kill,Fire,First kill,Friendly Hit,Last Hope,TeamKill Punishment
                    10, 12 -> {
                        thirdSpinner!!.isEnabled = true
                        secondPath = "$second/"
                    }
                    13, 14 -> {
                        thirdSpinner!!.isEnabled = true
                        fourthSpinner!!.isEnabled = true
                        secondPath = "$second/"
                    }
                    else -> {
                        thirdSpinner!!.isEnabled = true
                        fourthSpinner!!.isEnabled = false
                        secondPath = "$second/"
                    }

                }
                try {
                    getChildFileList(2)
//                    Log.e("SecondPos", second)

                } catch (e: Exception) {
                    Log.e("SecondException", e.toString())
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }
    }

    private fun thirdListPosition(array: Array<String>) {
        thirdSpinner!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val s = array[position]
                thirdPath=s
                when (firstpos) {
                    10 -> {
                        when (secondpos) {
                            2 -> {
                                fourthSpinner!!.isEnabled = false
                                thirdPath = s
                                fourthPath=""
                            }
                            else -> {
                                fourthSpinner!!.isEnabled = true
                                thirdPath = "$s/"
                            }
                        }
                    }
                    12 -> {
                        when (secondpos) {
                            1 -> {
                                fourthSpinner!!.isEnabled = true
                                thirdPath = "$s/"
                            }
                            else -> {
                                fourthSpinner!!.isEnabled = false
                                thirdPath = s
                                fourthPath=""
                            }
                        }
                    }
                    13, 14 -> {
                        thirdPath = "$s/"
                    }
                    else -> {
                        thirdPath = s
                        fourthPath=""
                    }
                }
                getChildFileList(3)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

        }
    }

    private fun fourthListPosition(array: Array<String>){
        fourthSpinner!!.onItemSelectedListener=object :AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                fourthPath=array[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }
    }

    private fun getChildFileList(step: Int) {
        var final: String
        val parent = when (step) {
            1 -> {
                File("$projectLocation/$firstPath")
            }
            2 -> {
                File("$projectLocation/$firstPath/$secondPath")
            }
            3 -> {
                File("$projectLocation/$firstPath/$secondPath$thirdPath")
            }
            else -> {
                null
            }
        }
        var array = arrayOf<String>()
        var arrayAdapter: ArrayAdapter<String>? = null
        if (parent!!.isDirectory) {
            parent.listFiles().sortedArrayDescending()/*force sort by name z-a*/.reversedArray()/*force reverse to a-z*/.forEach {
                val s = it.toString().split('/')
                final = s[s.size - 1]
                if (!it.isDirectory) {
                    Log.i("getChildFileList", "getFiles and check it is txt or wav $final")
                    try {
                        val ss = final.split('.')
                        if (ss[ss.size - 1].lowercase(Locale.ROOT) == "txt") {
                            val file = when (step) {
                                1 -> File("$projectLocation/$firstPath/$final")
                                2 -> File("$projectLocation/$firstPath/$secondPath/$final")
                                3 -> File("$projectLocation/$firstPath/$secondPath/$thirdPath/$final")
                                else -> null
                            }

                            Log.i("getChildFileType", ss[ss.size - 1].lowercase(Locale.ROOT))
                            val list = file!!.readText()
                            array = list.split(",").toTypedArray()
                            arrayAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, array)
                            Log.i("FileText", list.split(",").toString())
                        }
                    } catch (e: Exception) {
                        Log.e("FileText", e.toString())
                    }
                } else {
                    Log.i("getChildFileList", final)
                    array += final
                    arrayAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, array)
                }
            }
        }

        when (step) {
            1 -> {
                secondSpinner!!.adapter = arrayAdapter
                fourthSpinner !!. adapter = null
                secondListPosition(array)
            }
            2 -> {
                thirdSpinner!!.adapter = arrayAdapter
                fourthSpinner !!. adapter = null
                thirdListPosition(array)
            }
            3 -> {
                when (firstpos) {
                    10 -> {
                        arrayAdapter = when (secondpos) {
                            2 -> {
                                null
                            }
                            else -> {
                                ArrayAdapter(this, android.R.layout.simple_list_item_1, array)
                            }
                        }
                    }
                    12 -> {
                        arrayAdapter = when (secondpos) {
                            1 -> {
                                ArrayAdapter(this, android.R.layout.simple_list_item_1, array)
                            }
                            else -> {
                                null
                            }
                        }
                    }
                    13, 14 -> {
                        ArrayAdapter(this, android.R.layout.simple_list_item_1, array)
                    }
                    else -> {
                        arrayAdapter =null
                    }
                }
                 fourthSpinner !!. adapter = arrayAdapter
                 fourthListPosition(array)
            }

        }
    }

    private val startForResult = registerForActivityResult(StartActivityForResult()) {
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




