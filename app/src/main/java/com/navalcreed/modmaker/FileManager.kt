package com.navalcreed.modmaker

import android.util.Log
import java.io.*
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream

object FileManager {
    @Throws(IOException::class)
    fun fileCopy(oldFilePath: String, newFilePath: String): Boolean {
        if (!fileExists(oldFilePath)) {
            return false
        }
        val inputStream = FileInputStream(File(oldFilePath))
        val data = ByteArray(1024)
        val outputStream = FileOutputStream(File(newFilePath))
        while (inputStream.read(data) != -1) {
            outputStream.write(data)
        }
        inputStream.close()
        outputStream.close()
        return true
    }

    private fun fileExists(filePath: String): Boolean {
        val file = File(filePath)
        return file.exists()
    }

    fun deleteRecursive(fileOrDirectory: File) {
        if (fileOrDirectory.isDirectory) fileOrDirectory.listFiles().forEach { child ->
                deleteRecursive(
                    child
                )
            }
        fileOrDirectory.delete()
    }
    fun writeTXT(data: String, filepath: File): Boolean {
        try {
            val f = FileOutputStream(filepath)
            val pw = PrintWriter(f)
            pw.println(data)
            pw.flush()
            pw.close()
            f.close()
        } catch (e: IOException) {
            Log.e("Exception", "File write failed: $e")
            return false
        }
        return true
    }

    @Throws(IOException::class)
    fun readTXT(filePath: String):String {
        var s=""
        File(filePath).forEachLine {
            s+=it
        }
        return s
    }

    fun checkInfoPreview(ExternalModTypeDir:String, ExternalCacheDir:String, modType:String, projectName:String) {
        val projectLocation="$ExternalModTypeDir/$projectName"
        val info=File("$projectLocation/mod.info")
        val preview=File("$projectLocation/mod.preview")
        val cache=File("$ExternalCacheDir/$modType/$projectName")
        if(info.exists()){
            if(!cache.exists()){
                cache.mkdirs()
            }
            fileCopy(info.path,"$cache/mod.info")
            info.delete()
        }
        if(preview.exists()){
            if(!cache.exists()){
                cache.mkdirs()
            }
            fileCopy(preview.path,"$cache/mod.preview")
            preview.delete()
        }
    }

    fun checkCacheInfoPreview(ExternalModTypeDir:String, ExternalCacheDir:String, modType:String, projectName:String) {
        val projectLocation="$ExternalModTypeDir/$projectName"
        val info=File("$projectLocation/mod.info")
        val preview=File("$projectLocation/mod.preview")
        val cache=File("$ExternalCacheDir/$modType/$projectName")
        val cacheInfo=File("${cache.path}/mod.info")
        val cachePreview=File("${cache.path}/mod.preview")
        if(cacheInfo.exists()){
            fileCopy(cacheInfo.path,info.path)
            cacheInfo.delete()
        }
        if(cachePreview.exists()){
            fileCopy(cachePreview.path,preview.path)
            cachePreview.delete()
        }
    }
}
object ZipManager {
    private const val BUFFER_SIZE = 8192 //2048;
    private val TAG = ZipManager::class.java.name.toString()
    private var parentPath = ""
    fun zip(
        sourcePath: String,
        destinationPath: String,
        destinationFileName: String,
        includeParentFolder: Boolean
    ): Boolean {
        var destinationPath = destinationPath
        File(destinationPath).mkdirs()
        val fileOutputStream: FileOutputStream
        var zipOutputStream: ZipOutputStream? = null
        try {
            if (!destinationPath.endsWith("/")) destinationPath += "/"
            val destination = destinationPath + destinationFileName
            val file = File(destination)
            if (!file.exists()) file.createNewFile()
            fileOutputStream = FileOutputStream(file)
            zipOutputStream = ZipOutputStream(BufferedOutputStream(fileOutputStream))
            parentPath =
                if (includeParentFolder) File(sourcePath).parent.toString() + "/" else sourcePath
            zipFile(zipOutputStream, sourcePath)
        } catch (ioe: IOException) {
            Log.d(TAG, ioe.toString())
            return false
        } finally {
            if (zipOutputStream != null) try {
                zipOutputStream.close()
            } catch (e: IOException) {
            }
        }
        return true
    }

    @Throws(IOException::class)
    private fun zipFile(zipOutputStream: ZipOutputStream?, sourcePath: String) {
        val files = File(sourcePath)
        val fileList = files.listFiles()
        var entryPath = ""
        var input: BufferedInputStream
        for (file in fileList) {
            if (file.isDirectory) {
                zipFile(zipOutputStream, file.path)
            } else {
                val data = ByteArray(BUFFER_SIZE)
                val fileInputStream = FileInputStream(file.path)
                input = BufferedInputStream(fileInputStream, BUFFER_SIZE)
                entryPath = file.absolutePath.replace(parentPath, "")
                val entry = ZipEntry(entryPath)
                zipOutputStream!!.putNextEntry(entry)
                var count: Int
                while (input.read(data, 0, BUFFER_SIZE).also { count = it } != -1) {
                    zipOutputStream.write(data, 0, count)
                }
                input.close()
            }
        }
    }

    fun unzip(sourceFile: String?, destinationFolder: String?): Boolean {
        var zis: ZipInputStream? = null
        try {
            zis = ZipInputStream(BufferedInputStream(FileInputStream(sourceFile)))
            var ze: ZipEntry
            var count: Int
            val buffer = ByteArray(BUFFER_SIZE)
            while (zis.nextEntry.also { ze = it } != null) {
                var fileName: String = ze.name
                fileName = fileName.substring(fileName.indexOf("/") + 1)
                val file = File(destinationFolder, fileName)
                val dir = if (ze.isDirectory) file else file.parentFile
                if (!dir.isDirectory && !dir.mkdirs()) throw FileNotFoundException("Invalid path: " + dir.absolutePath)
                if (ze.isDirectory) continue
                val fout = FileOutputStream(file)
                fout.use { fout ->
                    while (zis.read(buffer).also { count = it } != -1) fout.write(buffer, 0, count)
                }
            }
        } catch (ioe: IOException) {
            Log.d(TAG, ioe.toString())
            return false
        } finally {
            if (zis != null) try {
                zis.close()
            } catch (e: IOException) {
            }
        }
        return true
    }

    fun saveToFile(destinationPath: String, data: String, fileName: String) {
        try {
            File(destinationPath).mkdirs()
            val file = File(destinationPath + fileName)
            if (!file.exists()) {
                file.createNewFile()
            }
            val fileOutputStream = FileOutputStream(file, true)
            fileOutputStream.write((data + System.getProperty("line.separator")).toByteArray())
        } catch (ex: FileNotFoundException) {
            Log.d(TAG, ex.toString())
        } catch (ex: IOException) {
            Log.d(TAG, ex.toString())
        }
    }
}