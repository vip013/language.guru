package com.example

import android.app.Application
import android.content.Context
import android.os.Build
import android.util.Log
import com.example.data.AdMobHelper
import java.io.File

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        cleanCorruptedVariationsSeed(this)
        AdMobHelper.initialize(this)
        com.example.data.CentralizedContentRepository.validateLanguageDataset()
    }

    private fun cleanCorruptedVariationsSeed(context: Context) {
        try {
            val baseDir = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                context.dataDir
            } else {
                context.filesDir.parentFile
            } ?: return

            val targets = listOf(
                File(baseDir, "app_webview/variations_seed"),
                File(baseDir, "app_webview/variations_seed_new"),
                File(baseDir, "app_webview/Default/variations_seed"),
                File(context.filesDir, "variations_seed")
            )

            for (file in targets) {
                if (file.exists() && file.length() < 64) {
                    file.delete()
                    Log.d("MyApplication", "Cleaned empty/corrupt variations seed file: ${file.name}")
                }
            }
        } catch (e: Exception) {
            Log.d("MyApplication", "Could not clean variations seed: ${e.message}")
        }
    }
}
