package com.reactnativepiwikprosdk

import android.os.Build
import com.facebook.react.bridge.*
import pro.piwik.sdk.Piwik
import pro.piwik.sdk.Tracker
import pro.piwik.sdk.TrackerConfig
import pro.piwik.sdk.extra.DownloadTracker
import pro.piwik.sdk.extra.DownloadTracker.Extra
import pro.piwik.sdk.extra.DownloadTracker.Extra.Custom
import pro.piwik.sdk.extra.TrackHelper


class PiwikProSdkModule(reactContext: ReactApplicationContext) : ReactContextBaseJavaModule(reactContext) {
    private var tracker: Tracker? = null

    override fun getName(): String {
        return "PiwikProSdk"
    }

    @ReactMethod
    fun init(baseUrl: String, siteId: String, options: ReadableMap, promise: Promise) {
        if (this.tracker != null) {
            promise.reject(Error("Tracker already initialized"));
            return;
        }

        try {
            var tracker = Piwik.getInstance(this.reactApplicationContext)
                .newTracker(TrackerConfig.createDefault(baseUrl, siteId))

            if (options.hasKey("applicationDomain")) {
                tracker.setApplicationDomain(options.getString("applicationDomain"))
            } else {
                tracker.setApplicationDomain(reactApplicationContext.packageName)
            }

            if (options.hasKey("dispatchInterval")) {
                tracker.dispatchInterval = options.getInt("dispatchInterval").toLong()
            }

            if (options.hasKey("isPrefixingEnabled")) {
              tracker.isPrefixing = options.getBoolean("isPrefixingEnabled")
            }

            this.tracker = tracker
            promise.resolve(null)
        } catch (error: Exception) {
            promise.reject(error)
        }
    }

    @ReactMethod
    fun trackScreen(path: String, customDimensions: ReadableArray?, promise: Promise) {
        try {
            var tracker = this.tracker ?: throw Exception("Tracker is not initialized")
            getTrackHelperWithCustomDimensions(customDimensions)
                .screen(path)
                .with(tracker)

            promise.resolve(null)
        } catch (error: Exception) {
            promise.reject(error)
        }
    }

    @ReactMethod
    fun trackEvent(category: String, action: String, optionalArgs: ReadableMap, customDimensions: ReadableArray?, promise: Promise) {
        try {
            var tracker = this.tracker ?: throw Exception("Tracker is not initialized")
            var track = getTrackHelperWithCustomDimensions(customDimensions)
                .event(category, action)

            if (optionalArgs.hasKey("name")) {
                track.name(optionalArgs.getString("name"));
            }

            if (optionalArgs.hasKey("value")) {
                track.value(optionalArgs.getDouble("value").toFloat());
            }

            track.with(tracker)

            promise.resolve(null)
        } catch (error: Exception) {
            promise.reject(error)
        }
    }

    @ReactMethod
    fun dispatch(promise: Promise) {
        try {
            (this.tracker ?: throw Exception("Tracker is not initialized")).dispatch()
            promise.resolve(null);
        } catch (error: Exception) {
            promise.reject(error)
        }
    }

    private fun getTrackHelperWithCustomDimensions(customDimensions: ReadableArray?): TrackHelper {
        var trackHelper = TrackHelper.track()

        if (customDimensions == null) {
          return trackHelper
        }

        var iterations = customDimensions.size() - 1

        for (x in 0..iterations) {
          var customDimension = customDimensions.getMap(x)
          if (customDimension != null) {
            trackHelper.dimension(customDimension.getInt("index"), customDimension.getString("value"))
          }
        }

        return trackHelper
    }

    @ReactMethod
    fun setUserId(userId: String?, promise: Promise) {
        try {
            (this.tracker ?: throw Exception("Tracker is not initialized")).setUserId(userId)
            promise.resolve(null);
        } catch (error: Exception) {
            promise.reject(error)
        }
    }

    @ReactMethod
    fun setUserEmail(userEmail: String?, promise: Promise) {
        try {
            (this.tracker ?: throw Exception("Tracker is not initialized")).setUserMail(userEmail)
            promise.resolve(null);
        } catch (error: Exception) {
            promise.reject(error)
        }
    }

    @ReactMethod
    fun sendApplicationDownload(promise: Promise) {
        try {
            var tracker = this.tracker ?: throw Exception("Tracker is not initialized")

            var packageName = this.reactApplicationContext.packageName
            var info = this.reactApplicationContext.packageManager.getPackageInfo(packageName,0)

            var version = info.versionName

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
              version += " (" + info.getLongVersionCode() + ")";
            } else {
              version += " (" + info.versionCode + ")";
            }

            val downloadTracker = DownloadTracker(tracker)
            val extra: Extra = object : Custom() {
              override fun isIntensiveWork(): Boolean {
                return false
              }
              
              override fun buildExtraIdentifier(): String? {
                return packageName
              }
            }

          TrackHelper.track().download(downloadTracker).identifier(extra).force().version(version).with(tracker)

            promise.resolve(null);
        } catch (error: Exception) {
            promise.reject(error)
        }
    }
}
