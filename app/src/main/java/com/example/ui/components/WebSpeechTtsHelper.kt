package com.example.ui.components

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient

class WebSpeechTtsHelper(
    private val context: Context,
    private val onInitComplete: (Boolean) -> Unit = {}
) {
    private var webView: WebView? = null
    private var isReady = false
    val isEngineReady: Boolean
        get() = isReady
    private var pendingSpeech: (() -> Unit)? = null
    private var onSpeechDoneCallback: (() -> Unit)? = null
    private val mainHandler = Handler(Looper.getMainLooper())

    init {
        mainHandler.post {
            initializeWebView()
        }
    }

    private fun initializeWebView() {
        try {
            Log.d("WebSpeechTTS", "Pre-creating WebView cache directories to prevent Chromium errors...")
            try {
                val cacheDir = context.cacheDir
                // Pre-create 'js' cache directory
                val webViewCodeCacheJsDir = java.io.File(cacheDir, "WebView/Default/HTTP Cache/Code Cache/js")
                if (!webViewCodeCacheJsDir.exists()) {
                    val created = webViewCodeCacheJsDir.mkdirs()
                    Log.d("WebSpeechTTS", "Pre-created WebView code cache directory (js): $created - ${webViewCodeCacheJsDir.absolutePath}")
                }
                val dummyFile = java.io.File(webViewCodeCacheJsDir, ".dummy")
                if (!dummyFile.exists()) {
                    val fileCreated = dummyFile.createNewFile()
                    Log.d("WebSpeechTTS", "Pre-created .dummy file inside js cache: $fileCreated")
                }

                // Pre-create 'wasm' cache directory to prevent chromium opendir errors
                val webViewCodeCacheWasmDir = java.io.File(cacheDir, "WebView/Default/HTTP Cache/Code Cache/wasm")
                if (!webViewCodeCacheWasmDir.exists()) {
                    val created = webViewCodeCacheWasmDir.mkdirs()
                    Log.d("WebSpeechTTS", "Pre-created WebView code cache directory (wasm): $created - ${webViewCodeCacheWasmDir.absolutePath}")
                }
                val dummyWasmFile = java.io.File(webViewCodeCacheWasmDir, ".dummy")
                if (!dummyWasmFile.exists()) {
                    val fileCreated = dummyWasmFile.createNewFile()
                    Log.d("WebSpeechTTS", "Pre-created .dummy file inside wasm cache: $fileCreated")
                }
            } catch (dirEx: Exception) {
                Log.e("WebSpeechTTS", "Failed to pre-create WebView cache directories", dirEx)
            }

            Log.d("WebSpeechTTS", "Initializing WebView for Web Speech API...")
            webView = WebView(context).apply {
                settings.javaScriptEnabled = true
                settings.mediaPlaybackRequiresUserGesture = false // Crucial for automatic audio playback
                settings.cacheMode = android.webkit.WebSettings.LOAD_NO_CACHE
                
                addJavascriptInterface(object {
                    @JavascriptInterface
                    fun onSpeechStarted() {
                        Log.d("WebSpeechTTS", "Web Speech API: Pronunciation started.")
                    }

                    @JavascriptInterface
                    fun onSpeechFinished() {
                        Log.d("WebSpeechTTS", "Web Speech API: Pronunciation finished.")
                        mainHandler.post {
                            onSpeechDoneCallback?.invoke()
                        }
                    }

                    @JavascriptInterface
                    fun onSpeechError(error: String) {
                        Log.e("WebSpeechTTS", "Web Speech API Error: $error")
                        mainHandler.post {
                            onSpeechDoneCallback?.invoke()
                        }
                    }
                }, "AndroidTTS")

                webViewClient = object : WebViewClient() {
                    override fun onPageFinished(view: WebView?, url: String?) {
                        super.onPageFinished(view, url)
                        Log.d("WebSpeechTTS", "WebView loaded environment successfully.")
                        isReady = true
                        onInitComplete(true)
                        pendingSpeech?.invoke()
                        pendingSpeech = null
                    }
                }

                // Inject beautiful HTML5 and JS wrapper to handle the HTML5 Web Speech synthesis nicely
                val htmlContent = """
                    <!DOCTYPE html>
                    <html>
                    <head>
                        <meta charset="utf-8">
                    </head>
                    <body>
                        <script>
                            window.synth = window.speechSynthesis;
                            
                            function speak(text, langCode, rate) {
                                try {
                                    if (!window.synth) {
                                        AndroidTTS.onSpeechError("SpeechSynthesis not supported on this device/WebView");
                                        return;
                                    }
                                    
                                    // Cancel any active utterance first
                                    window.synth.cancel();
                                    
                                    var utterance = new SpeechSynthesisUtterance(text);
                                    utterance.lang = langCode;
                                    utterance.rate = rate;
                                    utterance.pitch = 1.0;
                                    
                                    if (window.synth.getVoices) {
                                        var voices = window.synth.getVoices();
                                        var langVoices = voices.filter(function(v) {
                                            return v.lang.toLowerCase().replace('_', '-').indexOf(langCode.toLowerCase().replace('_', '-')) !== -1;
                                        });
                                        if (langVoices.length > 0) {
                                            var bestVoice = langVoices.find(function(v) {
                                                var name = v.name.toLowerCase();
                                                return name.indexOf("natural") !== -1 || name.indexOf("google") !== -1 || name.indexOf("neural") !== -1;
                                            }) || langVoices.find(function(v) {
                                                var name = v.name.toLowerCase();
                                                return name.indexOf("female") !== -1 || name.indexOf("f-") !== -1;
                                            }) || langVoices[0];
                                            utterance.voice = bestVoice;
                                        }
                                    }
                                    
                                    utterance.onstart = function() {
                                        AndroidTTS.onSpeechStarted();
                                    };
                                    
                                    utterance.onend = function() {
                                        AndroidTTS.onSpeechFinished();
                                    };
                                    
                                    utterance.onerror = function(event) {
                                        AndroidTTS.onSpeechError(event.error || "playback error");
                                    };
                                    
                                    window.synth.speak(utterance);
                                } catch (e) {
                                    AndroidTTS.onSpeechError(e.toString());
                                }
                            }
                            
                            function stopSpeech() {
                                if (window.synth) {
                                    window.synth.cancel();
                                }
                            }
                        </script>
                    </body>
                    </html>
                """.trimIndent()

                loadDataWithBaseURL(null, htmlContent, "text/html", "UTF-8", null)
            }
        } catch (e: Exception) {
            Log.e("WebSpeechTTS", "Failed to initialize WebSpeech WebView", e)
            onInitComplete(false)
        }
    }

    fun speak(text: String, langCode: String, speed: Float, onDone: () -> Unit) {
        onSpeechDoneCallback = onDone
        
        val webLocale = mapLangCodeToLocale(langCode)
        val safeText = escapeJsString(text)
        val jsCmd = "speak(\"$safeText\", \"$webLocale\", $speed);"
        
        mainHandler.post {
            if (isReady) {
                executeJs(jsCmd)
            } else {
                pendingSpeech = {
                    executeJs(jsCmd)
                }
            }
        }
    }

    fun stop() {
        mainHandler.post {
            if (isReady) {
                executeJs("stopSpeech();")
            }
        }
    }

    fun shutdown() {
        mainHandler.post {
            try {
                webView?.destroy()
                webView = null
                isReady = false
            } catch (e: Exception) {
                Log.e("WebSpeechTTS", "Error shutting down WebSpeech WebView", e)
            }
        }
    }

    private fun executeJs(script: String) {
        try {
            webView?.evaluateJavascript(script, null)
        } catch (e: Exception) {
            Log.e("WebSpeechTTS", "JS Evaluation error: $script", e)
        }
    }

    private fun escapeJsString(str: String): String {
        return str.replace("\\", "\\\\")
            .replace("\"", "\\\"")
            .replace("\'", "\\\'")
            .replace("\n", "\\n")
            .replace("\r", "\\r")
    }

    private fun mapLangCodeToLocale(langCode: String): String {
        return when (langCode) {
            "te" -> "te-IN"
            "ta" -> "ta-IN"
            "hi" -> "hi-IN"
            "ar" -> "ar-SA"
            "kn" -> "kn-IN"
            "ml" -> "ml-IN"
            "bn" -> "bn-IN"
            "mr" -> "mr-IN"
            "gu" -> "gu-IN"
            else -> "en-US"
        }
    }
}
