package com.shubh.openpdffromfile

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.shubh.openpdffromfile.databinding.ActivityMainBinding
import java.io.File


class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    private val REQUEST_CODE_PERMISSION = 100
    private val REQUEST_CODE_FILE = 200
    var file = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btnFile.setOnClickListener {
            openFileChooser()
        }


        binding.btnWebView.setOnClickListener {
            Intent(this, WebPage::class.java).also {
                it.putExtra("file", file)
                startActivity(it)
            }
        }
    }


//    private fun hasPermission(): Boolean {
//        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            checkSelfPermission(Manifest.permission.DYNAMIC_RECEIVER_NOT_EXPORTED_PERMISSION) == PackageManager.PERMISSION_GRANTED
//        } else true
//    }

//    private fun requestPermission() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            requestPermissions(
//                arrayOf(Manifest.permission.DYNAMIC_RECEIVER_NOT_EXPORTED_PERMISSION),
//                REQUEST_CODE_PERMISSION
//            )
//        }
//    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //  loadHTMLFile()
            } else {
            }
        }

    }

    private fun openFileChooser() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "text/html"
        startActivityForResult(intent,REQUEST_CODE_FILE)
     //   intent.addCategory(Intent.CATEGORY_OPENABLE)
       // startActivityForResult(Intent.createChooser(intent, "Select HTML File"), REQUEST_CODE_FILE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_FILE && resultCode == RESULT_OK) {
            val fileUri = data?.data
            fileUri?.let {
                val inputStream = contentResolver.openInputStream(it)
                val htmlContent = inputStream?.bufferedReader().use { reader ->
                    reader?.readText()
                }
                inputStream?.close()

                htmlContent?.let { content ->
                    Log.e(TAG, "HTML: $content", )
                    file=content

                }
            }
        }
    }







    fun getRealPathFromURI(contentUri: Uri?): String? {
        val proj = arrayOf(MediaStore.Files.FileColumns.DATA)
        val cursor: Cursor = managedQuery(contentUri, proj, null, null, null)
        val column_index: Int = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA)
        cursor.moveToFirst()
        return cursor.getString(column_index)
    }

    companion object{
        private const val TAG = "MainActivity"
    }


    fun getDisplayNameFromUri(context: Context, uri: Uri): String? {
        var displayName: String? = null
        val cursor = context.contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val displayNameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (displayNameIndex != -1) {
                    displayName = it.getString(displayNameIndex)
                }
            }
        }
        return displayName
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    fun getRelativePathFromUri(context: Context, uri: Uri): String? {
        val projection = arrayOf(MediaStore.Files.FileColumns.RELATIVE_PATH)
        val cursor = context.contentResolver.query(uri, projection, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val columnIndex = it.getColumnIndexOrThrow(MediaStore.Files.FileColumns.RELATIVE_PATH)
                return it.getString(columnIndex)
            }
        }
        return null
    }
}