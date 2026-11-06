package com.example.data

import android.app.Activity
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback

object AdMobHelper {

    private const val TAG = "AdMobHelper"

    // --------------------------------------------------
    // REAL ADMOB AD UNIT IDs
    // --------------------------------------------------

    // Banner
    const val BANNER_AD_UNIT_ID =
        "ca-app-pub-9590633818352325/5398270056"

    // Interstitial
    private const val INTERSTITIAL_AD_UNIT_ID =
        "ca-app-pub-9590633818352325/8357645316"

    // Rewarded
    private const val REWARDED_AD_UNIT_ID =
        "ca-app-pub-9590633818352325/2529286917"


    private var interstitialAd: InterstitialAd? = null
    private var rewardedAd: RewardedAd? = null

    private var isInterstitialLoading = false
    private var isRewardedLoading = false

    private val handler = Handler(Looper.getMainLooper())
    private var isInitialized = false


    // --------------------------------------------------
    // INITIALIZE ADMOB
    // --------------------------------------------------

    fun initialize(context: Context) {

        if (isInitialized) return

        try {

            val requestConfiguration =
                MobileAds.getRequestConfiguration()
                    .toBuilder()
                    .setTagForChildDirectedTreatment(
                        RequestConfiguration.TAG_FOR_CHILD_DIRECTED_TREATMENT_TRUE
                    )
                    .setMaxAdContentRating(
                        RequestConfiguration.MAX_AD_CONTENT_RATING_G
                    )
                    .build()

            MobileAds.setRequestConfiguration(requestConfiguration)

            MobileAds.initialize(context) {

                Log.d(TAG, "AdMob Initialized successfully.")

                isInitialized = true

                // Preload ads
                loadInterstitialAd(context.applicationContext)
                loadRewardedAd(context.applicationContext)
            }

        } catch (e: Exception) {

            Log.e(TAG, "Error initializing AdMob", e)
        }
    }


    private fun getAdRequest(): AdRequest {
        return AdRequest.Builder().build()
    }


    private var interstitialRetryCount = 0
    private var rewardedRetryCount = 0
    private const val MAX_RETRY_COUNT = 3

    // Google AdMob standard test ad units (used as fallback in debug builds when live ads have No Fill)
    private const val TEST_INTERSTITIAL_AD_UNIT_ID = "ca-app-pub-3940256099942544/1033173712"
    private const val TEST_REWARDED_AD_UNIT_ID = "ca-app-pub-3940256099942544/5224354917"
    const val TEST_BANNER_AD_UNIT_ID = "ca-app-pub-3940256099942544/6300978111"

    private fun getEffectiveInterstitialAdUnitId(): String {
        return if (com.example.BuildConfig.DEBUG && interstitialRetryCount >= 1) {
            TEST_INTERSTITIAL_AD_UNIT_ID
        } else {
            INTERSTITIAL_AD_UNIT_ID
        }
    }

    private fun getEffectiveRewardedAdUnitId(): String {
        return if (com.example.BuildConfig.DEBUG && rewardedRetryCount >= 1) {
            TEST_REWARDED_AD_UNIT_ID
        } else {
            REWARDED_AD_UNIT_ID
        }
    }

    // ==================================================
    // INTERSTITIAL ADS
    // ==================================================

    fun loadInterstitialAd(context: Context) {

        if (isInterstitialLoading || interstitialAd != null) {
            return
        }

        isInterstitialLoading = true

        val adUnitId = getEffectiveInterstitialAdUnitId()
        Log.d(TAG, "Loading Interstitial Ad using unit ID: $adUnitId...")

        InterstitialAd.load(
            context,
            adUnitId,
            getAdRequest(),

            object : InterstitialAdLoadCallback() {

                override fun onAdLoaded(ad: InterstitialAd) {

                    interstitialAd = ad
                    isInterstitialLoading = false
                    interstitialRetryCount = 0

                    Log.d(
                        TAG,
                        "Interstitial Ad loaded successfully."
                    )

                    ad.fullScreenContentCallback =
                        object : FullScreenContentCallback() {

                            override fun onAdDismissedFullScreenContent() {

                                interstitialAd = null

                                Log.d(
                                    TAG,
                                    "Interstitial Ad dismissed."
                                )

                                loadInterstitialAd(context)
                            }

                            override fun onAdFailedToShowFullScreenContent(
                                error: AdError
                            ) {

                                interstitialAd = null
                                isInterstitialLoading = false

                                Log.w(
                                    TAG,
                                    "Failed to show Interstitial: ${error.message}"
                                )

                                loadInterstitialAd(context)
                            }
                        }
                }


                override fun onAdFailedToLoad(
                    error: LoadAdError
                ) {

                    interstitialAd = null
                    isInterstitialLoading = false
                    interstitialRetryCount++

                    val isNoFill = error.code == AdRequest.ERROR_CODE_NO_FILL
                    if (isNoFill) {
                        Log.i(
                            TAG,
                            "Interstitial ad notice: No Fill (code ${error.code} - ad inventory pending approval or empty in region)."
                        )
                    } else {
                        Log.w(
                            TAG,
                            "Interstitial load notice: ${error.message} (code ${error.code})."
                        )
                    }

                    if (interstitialRetryCount <= MAX_RETRY_COUNT) {
                        val delayMs = 30000L * interstitialRetryCount
                        Log.d(TAG, "Scheduling interstitial reload in ${delayMs / 1000}s (attempt $interstitialRetryCount/$MAX_RETRY_COUNT)...")
                        handler.postDelayed(
                            {
                                loadInterstitialAd(context)
                            },
                            delayMs
                        )
                    } else {
                        Log.d(TAG, "Interstitial retry limit reached. Waiting for next user event.")
                    }
                }
            }
        )
    }


    fun showInterstitialAd(
        activity: Activity,
        onAdClosed: () -> Unit
    ) {

        activity.runOnUiThread {

            val ad = interstitialAd

            if (ad != null) {

                ad.fullScreenContentCallback =
                    object : FullScreenContentCallback() {

                        override fun onAdDismissedFullScreenContent() {

                            interstitialAd = null

                            Log.d(
                                TAG,
                                "Interstitial shown and dismissed."
                            )

                            onAdClosed()

                            loadInterstitialAd(
                                activity.applicationContext
                            )
                        }


                        override fun onAdFailedToShowFullScreenContent(
                            error: AdError
                        ) {

                            interstitialAd = null

                            Log.e(
                                TAG,
                                "Failed to show interstitial: ${error.message}"
                            )

                            onAdClosed()

                            loadInterstitialAd(
                                activity.applicationContext
                            )
                        }
                    }

                ad.show(activity)

            } else {

                Log.d(
                    TAG,
                    "Interstitial not ready, calling callback directly."
                )

                onAdClosed()

                loadInterstitialAd(
                    activity.applicationContext
                )
            }
        }
    }


    // ==================================================
    // REWARDED ADS
    // ==================================================

    fun loadRewardedAd(context: Context) {

        if (isRewardedLoading || rewardedAd != null) {
            return
        }

        isRewardedLoading = true

        val adUnitId = getEffectiveRewardedAdUnitId()
        Log.d(TAG, "Loading Rewarded Ad using unit ID: $adUnitId...")

        RewardedAd.load(
            context,
            adUnitId,
            getAdRequest(),

            object : RewardedAdLoadCallback() {

                override fun onAdLoaded(
                    ad: RewardedAd
                ) {

                    rewardedAd = ad
                    isRewardedLoading = false
                    rewardedRetryCount = 0

                    Log.d(
                        TAG,
                        "Rewarded Ad loaded successfully."
                    )

                    ad.fullScreenContentCallback =
                        object : FullScreenContentCallback() {

                            override fun onAdDismissedFullScreenContent() {

                                rewardedAd = null

                                Log.d(
                                    TAG,
                                    "Rewarded Ad dismissed."
                                )

                                loadRewardedAd(context)
                            }


                            override fun onAdFailedToShowFullScreenContent(
                                error: AdError
                            ) {

                                rewardedAd = null
                                isRewardedLoading = false

                                Log.w(
                                    TAG,
                                    "Failed to show Rewarded ad: ${error.message}"
                                )

                                loadRewardedAd(context)
                            }
                        }
                }


                override fun onAdFailedToLoad(
                    error: LoadAdError
                ) {

                    rewardedAd = null
                    isRewardedLoading = false
                    rewardedRetryCount++

                    val isNoFill = error.code == AdRequest.ERROR_CODE_NO_FILL
                    if (isNoFill) {
                        Log.i(
                            TAG,
                            "Rewarded ad notice: No Fill (code ${error.code} - ad inventory pending approval or empty in region)."
                        )
                    } else {
                        Log.w(
                            TAG,
                            "Rewarded ad load notice: ${error.message} (code ${error.code})."
                        )
                    }

                    if (rewardedRetryCount <= MAX_RETRY_COUNT) {
                        val delayMs = 30000L * rewardedRetryCount
                        Log.d(TAG, "Scheduling rewarded ad reload in ${delayMs / 1000}s (attempt $rewardedRetryCount/$MAX_RETRY_COUNT)...")
                        handler.postDelayed(
                            {
                                loadRewardedAd(context)
                            },
                            delayMs
                        )
                    } else {
                        Log.d(TAG, "Rewarded retry limit reached. Waiting for next user event.")
                    }
                }
            }
        )
    }


    fun showRewardedAd(
        activity: Activity,
        onRewardEarned: () -> Unit,
        onAdClosed: () -> Unit
    ) {

        activity.runOnUiThread {

            val ad = rewardedAd

            if (ad != null) {

                ad.fullScreenContentCallback =
                    object : FullScreenContentCallback() {

                        override fun onAdDismissedFullScreenContent() {

                            rewardedAd = null

                            Log.d(
                                TAG,
                                "Rewarded ad dismissed."
                            )

                            onAdClosed()

                            loadRewardedAd(
                                activity.applicationContext
                            )
                        }


                        override fun onAdFailedToShowFullScreenContent(
                            error: AdError
                        ) {

                            rewardedAd = null

                            Log.e(
                                TAG,
                                "Failed to show rewarded: ${error.message}"
                            )

                            onAdClosed()

                            loadRewardedAd(
                                activity.applicationContext
                            )
                        }
                    }


                ad.show(activity) { rewardItem ->

                    Log.d(
                        TAG,
                        "User earned reward: ${rewardItem.amount} ${rewardItem.type}"
                    )

                    onRewardEarned()
                }

            } else {

                Log.d(
                    TAG,
                    "Rewarded ad not ready."
                )

                onRewardEarned()

                onAdClosed()

                loadRewardedAd(
                    activity.applicationContext
                )
            }
        }
    }
}


// ==================================================
// BANNER AD
// ==================================================

@Composable
fun AdMobBanner(
    modifier: Modifier = Modifier
) {

    AndroidView(

        modifier = modifier
            .fillMaxWidth()
            .height(50.dp),

        factory = { context ->

            AdView(context).apply {

                setAdSize(AdSize.BANNER)

                val effectiveBannerUnitId = if (com.example.BuildConfig.DEBUG) {
                    // Try real unit id first, but handle no-fill gracefully
                    AdMobHelper.BANNER_AD_UNIT_ID
                } else {
                    AdMobHelper.BANNER_AD_UNIT_ID
                }

                adUnitId = effectiveBannerUnitId

                adListener = object : com.google.android.gms.ads.AdListener() {
                    override fun onAdFailedToLoad(error: LoadAdError) {
                        Log.i("AdMobBanner", "Banner ad notice: ${error.message} (code ${error.code})")
                        if (com.example.BuildConfig.DEBUG && adUnitId != AdMobHelper.TEST_BANNER_AD_UNIT_ID) {
                            adUnitId = AdMobHelper.TEST_BANNER_AD_UNIT_ID
                            loadAd(AdRequest.Builder().build())
                        }
                    }
                }

                loadAd(
                    AdRequest.Builder().build()
                )
            }
        },

        update = { }
    )
}
