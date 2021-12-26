package com.navalcreed.modmaker

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.GsonBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Default
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File


class BuildModPack : AppCompatActivity() {

    var tvProjectName: TextView? = null
    var tvProjectType: TextView? = null
    var tvSize: TextView? = null

    var editAuthor: EditText? = null
    var editPackName: EditText? = null
    var editModInfo: EditText? = null
    var editModVersion: EditText? = null

    var imPreview: ImageView? = null

    var btnSelect: Button? = null
    var btnBuild: Button? = null

    var modtype: String? = null
    var modTypeInt: Int?=null

    var checkDeleteAfterBuild: CheckBox? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_build_mod_pack)

        findView()

        val bundle: Bundle? = intent.getBundleExtra("project")
        modTypeInt=bundle!!.getInt("type")
        tvProjectName!!.text = bundle.getString("name")
        tvProjectType!!.text = when (modTypeInt) {
            0 -> getString(R.string.title_captain_voice_create)
            1 -> getString(R.string.title_gun_sound_create)
            else -> ""
        }
        modtype = when (modTypeInt) {
            0 -> "CaptainVoice"
            1 -> "SoundEffect_PRIM"
            else -> ""
        }
        //bundle.getString("name")?.let {FileManager.checkCacheInfoPreview(getExternalFilesDir(modtype)!!.path,externalCacheDir!!.path,modtype!!,it) }
        //val info=File("${getExternalFilesDir(modtype)!!.path}/${bundle.getString("name")}/mod.info")

//        if(info.exists()){
//            val jsonString = FileManager.readTXT("${getExternalFilesDir(modtype)!!.path}/${bundle.getString("name")}/mod.info")
//            Log.e("Json",jsonString)
//            val gsonBuilder=GsonBuilder()
//            gsonBuilder.setPrettyPrinting()
//            val gson=gsonBuilder
//                .setLenient()
//                .create()
//            val modinfo: Modinfo = gson.fromJson(jsonString, Modinfo::class.java)
//            Log.e("modinfo",modinfo.getAuthor().toString())
//        }

        btnBuild!!.setOnClickListener {
            if (checkNUll()) {
                val errBuilder = AlertDialog.Builder(this)
                errBuilder.setMessage(
                    getString(R.string.get_Exception_info_head) + "\n" + getString(
                        R.string.Err_02
                    )
                )
                errBuilder.setTitle(getString(R.string.get_Exception_title))
                errBuilder.setPositiveButton(R.string.OK) { _, _ ->

                }
                errBuilder.show()
            } else {
                val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
                intent.putExtra("android.content.extra.SHOW_ADVANCED", true)
                openActivityForResult(intent, AppIntent.REQUEST_OPEN_FILE)
            }
        }

        btnSelect!!.setOnClickListener {
            val mimeTypes = arrayOf("image/jpeg", "image/png")
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            intent.type = "image/*"
            intent.putExtra(Intent.EXTRA_MIME_TYPES,mimeTypes)
            openActivityForResult(intent,AppIntent.REQUEST_OPEN_IMAGE)
        }

    }
    private fun checkNUll(): Boolean {
        return (editAuthor!!.text.isNullOrBlank() || editModInfo!!.text.isNullOrBlank() || editModVersion!!.text.isNullOrBlank() || editPackName!!.text.isNullOrBlank())
    }

    private fun findView() {
        tvProjectName = findViewById(R.id.tv_project_name)
        tvProjectType = findViewById(R.id.tv_project_type)
        tvSize = findViewById(R.id.tv_size)

        editAuthor = findViewById(R.id.edit_author)
        editPackName = findViewById(R.id.edit_pack_name)
        editModInfo = findViewById(R.id.edit_mod_info)
        editModVersion = findViewById(R.id.edit_mod_version)

        imPreview = findViewById(R.id.im_preview)

        btnSelect = findViewById(R.id.btn_select)
        btnBuild = findViewById(R.id.btn_build)

        checkDeleteAfterBuild = findViewById(R.id.check_delete_after_build)


    }
    @SuppressLint("InflateParams")
    private val startForResultFile =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data = result.data
                // Handle the Intent
                //do stuff here
                if (data != null) {
                    val uri = data.data
                    Log.e("tree uri", data.toString())
                    val author: String = editAuthor!!.text.toString()
                    val name: String = editPackName!!.text.toString()
                    val info: String = editModInfo!!.text.toString()
                    val ver: Int = editModVersion!!.text.toString().toInt()
                    var jsonString = "{\n"+
                            "\"author\":\"$author\",\n"+
                            "\"minSupportVer\": 5,\n"+
                            "\"modInfo\":\"$info\",\n"+
                            "\"modType\":\"$modtype\",\n"+
                            "\"name\":\"$name\",\n"+
                            "\"preview\": \"\",\n"+
                            "\"hasPreview\": false,\n"+
                            "\"targetVer\": 6,\n"+
                            "\"ver\":$ver\n"+
                            "}"
                    val gsonBuilder=GsonBuilder()
                    gsonBuilder.setPrettyPrinting()
                    val gson=gsonBuilder.create()

                    val modinfo: Modinfo = gson.fromJson(jsonString, Modinfo::class.java)
                    jsonString = gson.toJson(modinfo)
                    Log.e("Json",jsonString)


                    val temp = externalCacheDir.toString().split('/')
                    val uritemp = uri!!.path!!.split(':')
                    val filePath = temp[1] + "/" + temp[2] + "/" + temp[3] + "/" + uritemp[1]

                    val builder1 = AlertDialog.Builder(this)
                    val inflater = layoutInflater
                    builder1.setTitle(getString(R.string.running))
                    val dialogLayout = inflater.inflate(R.layout.loading_progress, null)
                    builder1.setView(dialogLayout)
                    val deleteAfterBuild=checkDeleteAfterBuild!!.isChecked
                    val message: AlertDialog = builder1.create()
                    message.setCanceledOnTouchOutside(false)
                    message.show()

                    //Async
                    CoroutineScope(Default).launch {
                        val originPath=getExternalFilesDir(modtype)?.path + "/" + tvProjectName!!.text
                        FileManager.writeTXT(
                            jsonString,
                            File("$originPath/mod.info")
                        )
                        when(modTypeInt){
                            1->{
                                ZipManager.zip("$originPath/", filePath,
                                    "$name.ncmod",
                                    false
                                )
                            }
                            else->{
                                ZipManager.zip("$originPath/", filePath,
                                    "$name.ncmod",
                                    false
                                )
                            }
                        }

                        if(deleteAfterBuild) FileManager.deleteRecursive(File(originPath))
//                        if(File("$originPath/mod.info").exists())File("$originPath/mod.info").delete()
//                        if(File("$originPath/mod.preview").exists())File("$originPath/mod.preview").delete()
                        //finnish
                        message.dismiss()
                        updateToast()
                        val intent = Intent(applicationContext,MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                }
            }
        }

    private val startForResultImage = //picture
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data = result.data
                // Handle the Intent
                //do stuff here
                if (data != null) {

                    val uri = data.data
                    Log.e("tree uri", data.toString())

                    val temp = externalCacheDir.toString().split('/')
                    val uritemp = uri!!.path!!.split(':')
                    val filePath = temp[1] + "/" + temp[2] + "/" + temp[3] + "/" + uritemp[1]
                    tvSize!!.text=GetFileFromUri.size(this,uri)

                    //Async
                    CoroutineScope(IO).launch {
                        val bitmap=BitmapFactory.decodeFile(filePath)
                        updateImageView(bitmap)
                        copyPicfile(filePath)
                    }
                }
            }
        }

    private fun openActivityForResult(intent: Intent, REQUEST_CODE:Int) {
        when(REQUEST_CODE){
            AppIntent.REQUEST_OPEN_FILE->startForResultFile.launch(intent)
            AppIntent.REQUEST_OPEN_IMAGE->startForResultImage.launch(intent)
        }

    }
    private suspend fun updateToast() {
        withContext(Main) {
            Toast.makeText(applicationContext, R.string.Success, Toast.LENGTH_SHORT).show()
        }
    }
    private suspend fun updateImageView(bitmap: Bitmap){
        withContext(Main) {
            imPreview!!.setImageBitmap(bitmap)
        }
    }
    private suspend fun copyPicfile(filePath:String){
        withContext(Default){
            FileManager.fileCopy(filePath,getExternalFilesDir(modtype as String)?.path + "/" + tvProjectName!!.text + "/mod.preview")
        }
    }

}