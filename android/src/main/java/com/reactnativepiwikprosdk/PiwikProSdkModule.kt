package com.reactnativepiwikprosdk

import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.ReactMethod
import com.facebook.react.bridge.ReadableMap
import com.facebook.react.bridge.ReadableArray
import com.facebook.react.bridge.Promise

import pro.piwik.sdk.Piwik
import pro.piwik.sdk.Tracker
import pro.piwik.sdk.TrackerConfig
import pro.piwik.sdk.extra.TrackHelper
import java.net.URL

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
    fun trackCampaign(url: String, promise: Promise) {
        try {
            var tracker = this.tracker ?: throw Exception("Tracker is not initialized")
            TrackHelper.track().campaign(URL(url)).with(tracker)
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
}
