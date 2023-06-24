package com.shubh.openpdffromfile

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.DocumentsContract
import android.provider.MediaStore


object FileUtils {


    fun getPath(context: Context, uri: Uri?): String? {
        if (uri == null) {
            return null
        }
        val scheme = uri.scheme
        if ("file" == scheme) {
            return uri.path
        } else if ("content" == scheme) {
            return getFilePathFromContentUri(context, uri)
        }
        return null
    }

    private fun getFilePathFromContentUri(context: Context, uri: Uri): String? {
        var filePath: String? = null
        if (DocumentsContract.isDocumentUri(context, uri)) {
            val documentId = DocumentsContract.getDocumentId(uri)
            if ("com.android.providers.media.documents" == uri.authority) {
                // MediaProvider
                val split = documentId.split(":").toTypedArray()
                val type = split[0]
                var contentUri: Uri? = null
                if ("image" == type) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                } else if ("video" == type) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                } else if ("audio" == type) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                }
                val selection = "_id=?"
                val selectionArgs = arrayOf(split[1])
                filePath = contentUri?.let { getDataColumn(context, it, selection, selectionArgs) }
            } else if ("com.android.providers.downloads.documents" == uri.authority) {
                // DownloadsProvider
                val contentUri = Uri.parse("content://downloads/public_downloads")
                filePath = getDataColumn(context, contentUri, "_id=?", arrayOf(documentId))
            }
        } else if ("content".equals(uri.scheme, ignoreCase = true)) {
            // MediaStore (and general)
            filePath = getDataColumn(context, uri, null, null)
        }
        return filePath
    }

    private fun getDataColumn(context: Context, uri: Uri, selection: String?, selectionArgs: Array<String>?): String? {
        var filePath: String? = null
        val projection = arrayOf(MediaStore.Files.FileColumns.DATA)
        var cursor: Cursor? = null
        try {
            cursor = context.contentResolver.query(uri, projection, selection, selectionArgs, null)
            if (cursor != null && cursor.moveToFirst()) {
                val columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA)
                filePath = cursor.getString(columnIndex)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            cursor?.close()
        }
        return filePath
    }








}