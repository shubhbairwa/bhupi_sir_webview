package com.shubh.openpdffromfile

import android.os.Bundle
import android.util.Log
import android.webkit.WebSettings
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import com.shubh.openpdffromfile.databinding.ActivityWebPageBinding


class WebPage : AppCompatActivity() {

    lateinit var binding: ActivityWebPageBinding
    var file=""
    companion object{
        private const val TAG = "WebPage"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding= ActivityWebPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        file=intent.getStringExtra("file").toString()

        Log.e(TAG, "onCreate: $file", )

        binding.WebView.setWebViewClient(WebViewClient())

        val webSettings: WebSettings = binding.WebView.getSettings()
        webSettings.javaScriptEnabled = true

        val filePath = "file:///android_asset/your_file.html"
      //  binding.WebView.loadUrl(file)
        binding.WebView.loadDataWithBaseURL(null, file, "text/html", "UTF-8", null)
    }
}