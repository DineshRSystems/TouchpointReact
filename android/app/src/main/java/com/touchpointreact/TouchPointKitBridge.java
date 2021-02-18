package com.touchpointreact;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.visioncritical.touchpointkit.utils.TouchPointActivity;

import java.util.HashMap;

public class TouchPointKitBridge extends ReactContextBaseJavaModule {
    private static ReactApplicationContext reactContext;

    TouchPointKitBridge(ReactApplicationContext context) {
        super(context);
        reactContext = context;
    }

    @Override
    public String getName() {
        return "TouchPointKitBridge";
    }

    @ReactMethod
    public void setScreen(String screenName, Boolean banner) {
        Context context = getCurrentActivity();

        if (context == null) {
            context = reactContext;
        }

        TouchPointActivity.Companion.getShared().setCurrentScreen(context, screenName, banner);
    }

    @ReactMethod
    public void openActivity(String screenName) {
        if(TouchPointActivity.Companion.getShared().shouldShowActivity(screenName)) {
            Context context = getCurrentActivity();

            if (context == null) {
                context = reactContext;
            }
            TouchPointActivity.Companion.getShared().openActivity(context, screenName, null);
        }
    }

    @ReactMethod
    public void clearCache() {
        Context context = getCurrentActivity();

        if (context == null) {
            context = reactContext;
        }

        SharedPreferences prefs = context.getSharedPreferences("com.visioncritical.touchpointkit", 0);
        prefs.edit().clear().apply();
    }

    @ReactMethod
    public void enableDebugLogs(Boolean enable) {
        TouchPointActivity.Companion.getShared().setEnableDebugLogs(enable);
    }

    @ReactMethod
    public void shouldApplyAPIFilter(Boolean apiFilter) {
        TouchPointActivity.Companion.getShared().setDisableApiFilter(!apiFilter);
    }

    @ReactMethod
    public void setVisitor(HashMap<String, String> visitor) {
        TouchPointActivity.Companion.getShared().setVisitor(visitor);
    }
}
