package com.touchpointreact;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.ReadableMapKeySetIterator;
import com.facebook.react.bridge.ReadableType;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.visioncritical.touchpointkit.utils.TouchPointActivity;
import com.visioncritical.touchpointkit.utils.TouchPointActivityInterface;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TouchPointKitBridge extends ReactContextBaseJavaModule implements TouchPointActivityInterface {
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
    public void configure(ReadableArray array, ReadableMap map) {
        TouchPointActivity.Companion.getShared().configure(toArrayList(array), toHashMap(map));
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
            TouchPointActivity.Companion.getShared().openActivity(context, screenName, this);
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
    public void disableAllLogs(Boolean disable) {
        TouchPointActivity.Companion.getShared().setDisableAllLogs(disable);
    }

    @ReactMethod
    public void disableCaching(Boolean caching) {
        TouchPointActivity.Companion.getShared().setDisableCaching(caching);
    }

    @ReactMethod
    public void shouldApplyAPIFilter(Boolean apiFilter) {
        TouchPointActivity.Companion.getShared().setDisableApiFilter(!apiFilter);
    }

    @ReactMethod
    public void setVisitor(ReadableMap map) {
        TouchPointActivity.Companion.getShared().setVisitor(toHashMap(map));
    }

    @Override
    public void onTouchPointActivityFinished() {
        Log.d("TouchPointKitBridge","onTouchPointActivityFinished...");
        this.reactContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                .emit("didActivityCompletedEvent", "TouchPointActivityFinished");
    }

    static HashMap<String, String> toHashMap(ReadableMap map) {
        HashMap<String, String> hashMap = new HashMap<>();
        ReadableMapKeySetIterator iterator = map.keySetIterator();
        while (iterator.hasNextKey()) {
            String key = iterator.nextKey();
            switch (map.getType(key)) {
                case Null:
                    hashMap.put(key, "");
                    break;
                case Boolean:
                    hashMap.put(key, "" + map.getBoolean(key));
                    break;
                case Number:
                    hashMap.put(key, "" + map.getDouble(key));
                    break;
                case String:
                    hashMap.put(key, map.getString(key));
                    break;
                default:
                    throw new IllegalArgumentException("Could not convert object with key: " + key + ".");
            }
        }
        return hashMap;
    }

    public static List<String> toArrayList(ReadableArray readableArray) {
        List<String> deconstructedList = new ArrayList<>(readableArray.size());
        for (int i = 0; i < readableArray.size(); i++) {
            ReadableType indexType = readableArray.getType(i);
            switch (indexType) {
                case Null:
                    deconstructedList.add(i, "");
                    break;
                case Boolean:
                    deconstructedList.add(i, "" + readableArray.getBoolean(i));
                    break;
                case Number:
                    deconstructedList.add(i, "" + readableArray.getDouble(i));
                    break;
                case String:
                    deconstructedList.add(i, readableArray.getString(i));
                    break;
                default:
                    throw new IllegalArgumentException("Could not convert object at index " + i + ".");
            }
        }
        return deconstructedList;
    }

}
