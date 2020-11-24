package com.reactnativepiwikprosdk

import android.util.Log
import com.facebook.react.bridge.*
import pro.piwik.sdk.Piwik
import pro.piwik.sdk.Tracker
import pro.piwik.sdk.TrackerConfig
import pro.piwik.sdk.dispatcher.Packet
import pro.piwik.sdk.extra.CustomDimension
import pro.piwik.sdk.extra.TrackHelper
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class PiwikProSdkModule(reactContext: ReactApplicationContext) : ReactContextBaseJavaModule(reactContext) {
    private var tracker: Tracker? = null
    private var customDimensions: HashMap<Int, String> = HashMap()

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
    fun trackScreen(path: String, promise: Promise) {
        try {
            var tracker = this.tracker ?: throw Exception("Tracker is not initialized")
            TrackHelper.track(tracker.defaultTrackMe)
                .screen(path)
                .with(tracker)

          this.cleanActionDimensions(tracker)
          promise.resolve(null)
        } catch (error: Exception) {
            promise.reject(error)
        }
    }

    @ReactMethod
    fun trackEvent(category: String, action: String, optionalArgs: ReadableMap, promise: Promise) {
        try {
            var tracker = this.tracker ?: throw Exception("Tracker is not initialized")
            var track = TrackHelper.track(tracker.defaultTrackMe)
                .event(category, action)

            if (optionalArgs.hasKey("name")) {
                track.name(optionalArgs.getString("name"));
            }

            if (optionalArgs.hasKey("value")) {
                track.value(optionalArgs.getDouble("value").toFloat());
            }

            track.with(tracker)

            this.cleanActionDimensions(tracker)
            promise.resolve(null)
        } catch (error: Exception) {
            promise.reject(error)
        }
    }

    @ReactMethod
    fun setCustomDimension(index: Int, value: String, scope: String, promise: Promise) {
        try {
            var tracker = this.tracker ?: throw Exception("Tracker is not initialized")

            CustomDimension.setDimension(tracker.defaultTrackMe, index, value)
            customDimensions.put(index, scope)

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

    private fun cleanActionDimensions(tracker: Tracker) {
      customDimensions.forEach {
        if (it.value == "action") {
          CustomDimension.setDimension(tracker.defaultTrackMe, it.key, null)
        }

        customDimensions.remove(it.key)
      }
    }
}
