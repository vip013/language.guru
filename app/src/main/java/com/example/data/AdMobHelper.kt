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

    // Your real AdMob Banner Ad Unit ID
    private const val BANNER_AD_UNIT_ID =
        "ca-app-pub-9590633818352325/5398270056"

    // Keep Google test IDs for Interstitial and Rewarded for now
    private const val INTERSTITIAL_AD_UNIT_ID =
        "ca-app-pub-3940256099942544/1033173712"

    private const val REWARDED_AD_UNIT_ID =
        "ca-app-pub-3940256099942544/5224354917"

    private var interstitialAd: InterstitialAd? = null
    private var rewardedAd: RewardedAd? = null

    private var isInterstitialLoading = false
    private var isRewardedLoading = false

    private val handler = Handler(Looper.getMainLooper())
    private var isInitialized = false

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

    // --------------------------------------------------
    // Interstitial Ads
    // --------------------------------------------------

    fun loadInterstitialAd(context: Context) {
        if (isInterstitialLoading || interstitialAd != null) return

        isInterstitialLoading = true

        Log.d(TAG, "Loading Interstitial Ad...")

        InterstitialAd.load(
            context,
            INTERSTITIAL_AD_UNIT_ID,
            getAdRequest(),
            object : InterstitialAdLoadCallback() {

                override fun onAdLoaded(ad: InterstitialAd) {
                    interstitialAd = ad
                    isInterstitialLoading = false

                    Log.d(TAG, "Interstitial Ad loaded successfully.")

                    ad.fullScreenContentCallback =
                        object : FullScreenContentCallback() {

                            override fun onAdDismissedFullScreenContent() {
                                interstitialAd = null
                                Log.d(TAG, "Interstitial Ad dismissed.")
                                loadInterstitialAd(context)
                            }

                            override fun onAdFailedToShowFullScreenContent(
                                error: AdError
                            ) {
                                interstitialAd = null
                                isInterstitialLoading = false

                                Log.e(
                                    TAG,
                                    "Failed to show Interstitial: ${error.message}"
                                )

                                loadInterstitialAd(context)
                            }
                        }
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    interstitialAd = null
                    isInterstitialLoading = false

                    Log.e(
                        TAG,
                        "Failed to load Interstitial: ${error.message}. Retrying in 15 seconds..."
                    )

                    handler.postDelayed(
                        {
                            loadInterstitialAd(context)
                        },
                        15000
                    )
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

    // --------------------------------------------------
    // Rewarded Ads
    // --------------------------------------------------

    fun loadRewardedAd(context: Context) {
        if (isRewardedLoading || rewardedAd != null) return

        isRewardedLoading = true

        Log.d(TAG, "Loading Rewarded Ad...")

        RewardedAd.load(
            context,
            REWARDED_AD_UNIT_ID,
            getAdRequest(),
            object : RewardedAdLoadCallback() {

                override fun onAdLoaded(ad: RewardedAd) {
                    rewardedAd = ad
                    isRewardedLoading = false

                    Log.d(TAG, "Rewarded Ad loaded successfully.")

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

                                Log.e(
                                    TAG,
                                    "Failed to show Rewarded ad: ${error.message}"
                                )

                                loadRewardedAd(context)
                            }
                        }
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    rewardedAd = null
                    isRewardedLoading = false

                    Log.e(
                        TAG,
                        "Failed to load Rewarded ad: ${error.message}. Retrying in 15 seconds..."
                    )

                    handler.postDelayed(
                        {
                            loadRewardedAd(context)
                        },
                        15000
                    )
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
                    "Rewarded ad not ready, calling rewards instantly as fallback."
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

// --------------------------------------------------
// Banner Ad
// --------------------------------------------------

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

                // Your real AdMob Banner Ad Unit ID
                adUnitId = "ca-app-pub-9590633818352325/5398270056"

                loadAd(
                    AdRequest.Builder().build()
                )
            }
        },

        update = { }
    )
}
