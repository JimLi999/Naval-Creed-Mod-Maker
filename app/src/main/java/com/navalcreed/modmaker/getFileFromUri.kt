package com.navalcreed.modmaker

import android.annotation.SuppressLint
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import kotlin.math.pow

object GetFileFromUri {
    @SuppressLint("Recycle")
    fun name(context: Context, uri: Uri?): String? {
        val cursor: Cursor? = context.contentResolver.query(uri!!, null, null, null, null, null)
        try {
            if (cursor != null && cursor.moveToFirst()) {
                val displayName: String = cursor.getString(
                    cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                )
                Log.e("Return Name", "Display Name: $displayName")
                return displayName
            }
        } finally {
            cursor!!.close()
        }
        return null
    }

    @SuppressLint("Recycle")
    fun size(context: Context, uri: Uri?):String?{
        val cursor: Cursor? = context.contentResolver.query(uri!!, null, null, null, null, null)
        try {
            if (cursor != null && cursor.moveToFirst()) {
                val sizeIndex: Int = cursor.getColumnIndex(OpenableColumns.SIZE)
                var size: String? = null
                var tem:Long=0
                tem = if (!cursor.isNull(sizeIndex))
                    cursor.getString(sizeIndex).toLong()
                else {
                    0
                }

                size = android.text.format.Formatter.formatShortFileSize(context, tem)

                Log.e("Return Size", "Size: $size")
                return size
            }
        } finally {
            cursor!!.close()
        }
        return null
    }

}
