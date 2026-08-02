package com.example.ui.components

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.webkit.JavascriptInterface
import android.webkit.PermissionRequest
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient

class WebSpeechRecognizerHelper(
    private val context: Context,
    private val onReady: () -> Unit = {},
    private val onListeningStarted: () -> Unit = {},
    private val onListeningStopped: () -> Unit = {},
    private val onResult: (finalText: String, interimText: String) -> Unit = { _, _ -> },
    private val onError: (error: String) -> Unit = {}
) {
    private var webView: WebView? = null
    private var isInitialized = false
    private val mainHandler = Handler(Looper.getMainLooper())

    init {
        mainHandler.post {
            initializeWebView()
        }
    }

    private fun initializeWebView() {
        try {
            Log.d("WebSpeechRecognizer", "Pre-creating WebView cache directories to prevent Chromium errors...")
            try {
                val cacheDir = context.cacheDir
                // Pre-create 'js' cache directory
                val webViewCodeCacheJsDir = java.io.File(cacheDir, "WebView/Default/HTTP Cache/Code Cache/js")
                if (!webViewCodeCacheJsDir.exists()) {
                    val created = webViewCodeCacheJsDir.mkdirs()
                    Log.d("WebSpeechRecognizer", "Pre-created WebView code cache directory (js): $created - ${webViewCodeCacheJsDir.absolutePath}")
                }
                val dummyFile = java.io.File(webViewCodeCacheJsDir, ".dummy")
                if (!dummyFile.exists()) {
                    dummyFile.createNewFile()
                }

                // Pre-create 'wasm' cache directory to prevent chromium opendir errors
                val webViewCodeCacheWasmDir = java.io.File(cacheDir, "WebView/Default/HTTP Cache/Code Cache/wasm")
                if (!webViewCodeCacheWasmDir.exists()) {
                    val created = webViewCodeCacheWasmDir.mkdirs()
                    Log.d("WebSpeechRecognizer", "Pre-created WebView code cache directory (wasm): $created - ${webViewCodeCacheWasmDir.absolutePath}")
                }
                val dummyWasmFile = java.io.File(webViewCodeCacheWasmDir, ".dummy")
                if (!dummyWasmFile.exists()) {
                    dummyWasmFile.createNewFile()
                }
            } catch (dirEx: Exception) {
                Log.e("WebSpeechRecognizer", "Failed to pre-create WebView cache directories", dirEx)
            }

            Log.d("WebSpeechRecognizer", "Initializing WebView for Speech Recognition API...")
            webView = WebView(context).apply {
                settings.javaScriptEnabled = true
                settings.cacheMode = android.webkit.WebSettings.LOAD_NO_CACHE
                
                addJavascriptInterface(object {
                    @JavascriptInterface
                    fun onListeningStarted() {
                        Log.d("WebSpeechRecognizer", "JSCallback: Listening started")
                        mainHandler.post { onListeningStarted() }
                    }

                    @JavascriptInterface
                    fun onListeningStopped() {
                        Log.d("WebSpeechRecognizer", "JSCallback: Listening stopped")
                        mainHandler.post { onListeningStopped() }
                    }

                    @JavascriptInterface
                    fun onResult(finalText: String, interimText: String) {
                        Log.d("WebSpeechRecognizer", "JSCallback: Result - final='$finalText', interim='$interimText'")
                        mainHandler.post { onResult(finalText, interimText) }
                    }

                    @JavascriptInterface
                    fun onError(error: String) {
                        Log.e("WebSpeechRecognizer", "JSCallback: Error='$error'")
                        mainHandler.post { onError(error) }
                    }
                }, "AndroidSpeech")

                webChromeClient = object : WebChromeClient() {
                    override fun onPermissionRequest(request: PermissionRequest) {
                        Log.d("WebSpeechRecognizer", "WebChromeClient: Granting mic permissions...")
                        request.grant(request.resources)
                    }
                }

                webViewClient = object : WebViewClient() {
                    override fun onPageFinished(view: WebView?, url: String?) {
                        super.onPageFinished(view, url)
                        Log.d("WebSpeechRecognizer", "WebView loaded recognition script.")
                        isInitialized = true
                        onReady()
                    }
                }

                val htmlContent = """
                    <!DOCTYPE html>
                    <html>
                    <head>
                        <meta charset="utf-8">
                    </head>
                    <body>
                        <script>
                            var recognition;
                            var isListening = false;
                            
                            function initRecognition() {
                                try {
                                    var SpeechRecognition = window.SpeechRecognition || window.webkitSpeechRecognition;
                                    if (!SpeechRecognition) {
                                        AndroidSpeech.onError("SpeechRecognition API not supported on this device's WebView component.");
                                        return;
                                    }
                                    
                                    recognition = new SpeechRecognition();
                                    recognition.continuous = true;
                                    recognition.interimResults = true;
                                    
                                    recognition.onstart = function() {
                                        isListening = true;
                                        AndroidSpeech.onListeningStarted();
                                    };
                                    
                                    recognition.onresult = function(event) {
                                        var interimTranscript = '';
                                        var finalTranscript = '';
                                        
                                        for (var i = event.resultIndex; i < event.results.length; ++i) {
                                            if (event.results[i].isFinal) {
                                                finalTranscript += event.results[i][0].transcript;
                                            } else {
                                                interimTranscript += event.results[i][0].transcript;
                                            }
                                        }
                                        
                                        AndroidSpeech.onResult(finalTranscript, interimTranscript);
                                    };
                                    
                                    recognition.onerror = function(event) {
                                        AndroidSpeech.onError(event.error || "Recognition error occurred");
                                    };
                                    
                                    recognition.onend = function() {
                                        isListening = false;
                                        AndroidSpeech.onListeningStopped();
                                    };
                                } catch (e) {
                                    AndroidSpeech.onError(e.toString());
                                }
                            }
                            
                            function startListening(langCode) {
                                try {
                                    if (!recognition) {
                                        initRecognition();
                                    }
                                    if (isListening) {
                                        recognition.stop();
                                    }
                                    recognition.lang = langCode;
                                    recognition.start();
                                } catch (e) {
                                    AndroidSpeech.onError(e.toString());
                                }
                            }
                            
                            function stopListening() {
                                try {
                                    if (recognition && isListening) {
                                        recognition.stop();
                                    }
                                } catch (e) {
                                    AndroidSpeech.onError(e.toString());
                                }
                            }
                        </script>
                    </body>
                    </html>
                """.trimIndent()
                
                loadDataWithBaseURL(null, htmlContent, "text/html", "UTF-8", null)
            }
        } catch (e: Exception) {
            Log.e("WebSpeechRecognizer", "Failed to initialize WebSpeechRecognizer WebView", e)
        }
    }

    fun startListening(langCode: String) {
        val webLocale = mapLangCodeToLocale(langCode)
        mainHandler.post {
            if (isInitialized) {
                Log.d("WebSpeechRecognizer", "Starting listening with locale: $webLocale")
                webView?.evaluateJavascript("startListening('$webLocale');", null)
            } else {
                Log.w("WebSpeechRecognizer", "WebView not initialized yet. Delaying start.")
            }
        }
    }

    fun stopListening() {
        mainHandler.post {
            if (isInitialized) {
                Log.d("WebSpeechRecognizer", "Stopping listening...")
                webView?.evaluateJavascript("stopListening();", null)
            }
        }
    }

    fun shutdown() {
        mainHandler.post {
            try {
                webView?.destroy()
                webView = null
                isInitialized = false
            } catch (e: Exception) {
                Log.e("WebSpeechRecognizer", "Error shutting down speech recognizer WebView", e)
            }
        }
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
