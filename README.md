# TouchPoint

## Android Requirements

- Android 6.0+
- Android Studio 4+

## Touchpoint SDK Android Integration:

###### Integarate SDK `touchpointkit` using following steps:
- Download .aar from `https://github.com/vcilabs/touchpoint-kit-android/blob/master/touchpointkit-debug.aar` and open `YOUR_PROJECT/android/` in `Android Studio`. 
- In `Android Studio` Right click on your project and select `Open Module Settings`.
- Click the `+` button in the top left corner of window to add a new module.
- Select `Import .JAR or .AAR Package` and click the `Next` button.
- Find the Downloaded .aar file using the ellipsis button `...` beside the `File name` field.

###### In `build.gradle` enter following dependencies:

```
implementation project(path: ':touchpointkit-debug')
implementation 'org.jetbrains.kotlin:kotlin-stdlib-jdk7:1.2.31'
implementation 'androidx.core:core-ktx:1.3.2'
implementation 'androidx.appcompat:appcompat:1.2.0'
implementation 'com.google.android.material:material:1.2.1'
implementation 'com.android.volley:volley:1.1.1'
implementation "androidx.startup:startup-runtime:1.0.0-rc01"
```

###### Add following strings to string.xml file under `YOUR_PROJECT/android/app/src/main/res/` directory.

```
<string name="api_key">API_KEY</string>
<string name="api_secret">API_SECRET</string>
<string name="pod_name">DEV2</string> <!--POD_NAME value must be any of: NA1, NA2, EU1, EU2, AP2, DEV1, DEV2, SIT1, PUB -->
<bool name="disable_api_filter">true</bool> <!--This is optional. Default is false-->
<bool name="enable_debug_logs">true</bool> <!--This is optional. Default is false-->
<bool name="disable_all_logs">false</bool> <!--This is optional. Default is false-->
<bool name="disable_caching">false</bool> <!--This is optional. Default is false-->
```

###### Import  TouchPoint SDK  `import com.visioncritical.touchpointkit.utils.TouchPointActivity` & add following code snippet on `MainApplication's onCreate`

```
List<String> screenNames =new ArrayList<String>();
screenNames.add("Demo 1");
screenNames.add("Demo 2");

HashMap<String, String> visitor = new HashMap<>();
visitor.put("id", "200");
visitor.put("email", "java@test.com");

TouchPointActivity.Companion.getShared().configure(screenNames, visitor)
```

###### NOTE: Use Above step to configure SDK through `MainApplication` file only if you are not calling this from .js file.

###### Create `TouchPointKitBridge.java` and add the following code into this:
```

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


```

###### Create `TouchPointKitPackage.java` and add the following code into this:
```
import com.facebook.react.ReactPackage;
import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.uimanager.ViewManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TouchPointKitPackage implements ReactPackage {

    @Override
    public List<ViewManager> createViewManagers(ReactApplicationContext reactContext) {
        return Collections.emptyList();
    }

    @Override
    public List<NativeModule> createNativeModules(
            ReactApplicationContext reactContext) {
        List<NativeModule> modules = new ArrayList<>();

        modules.add(new TouchPointKitBridge(reactContext));

        return modules;
    }

}
```

###### In `MainApplication.java` class's method `protected List<ReactPackage> getPackages()` add following line:
```
packages.add(new TouchPointKitPackage());
```

###### Now you can use TouchPoint SDK methods in App.js files as follows:
```
import {
  NativeModules,
  DeviceEventEmitter,
} from 'react-native';

// Register for event listening from SDK (activity complete event)
const subscription = DeviceEventEmitter.addListener('didActivityCompletedEvent', didActivityCompletedEvent);
NativeModules.TouchPointKitBridge.configure(SCREEN_NAMES, VISITOR);

//NOTE: Uncomment below line if you want to set visitor here and not calling above 'configure' function here (calling 'configure' function from 'MainApplication' file)
//NativeModules.TouchPointKitBridge.setVisitor(VISITOR); 

// NOTE: SCREEN_NAMES is an array. e.g. ['Demo 1', 'Demo 2']
// VISITOR is json, e.g. { id: 'VISITOR_ID', email: 'VISITOR_EMAIL' }

// SDK Configuration
NativeModules.TouchPointKitBridge.clearCache(); // For testing. It will clear 'Activity Seen' status
NativeModules.TouchPointKitBridge.enableDebugLogs(true); // Optional, default is false
NativeModules.TouchPointKitBridge.shouldApplyAPIFilter(false); // Optional, default is true

// To set screen name
NativeModules.TouchPointKitBridge.setScreen(SCREEN_NAME, true);

// To open TouchPoint activity
NativeModules.TouchPointKitBridge.openActivity(SCREEN_NAME);

```

## iOS Requirements 

- iOS 10.0+
- Xcode 11+

## Touchpoint SDK iOS Integration

###### Integrate `TouchPointKit` through cocoapods. Inside React-Native project, Go to `ios` folder, update `Podfile` as follows:
```
// Under project target add following line:
pod 'TouchPointKit', :git => 'https://github.com/vcilabs/touchpoint-kit-ios.git', :tag => '0.0.5'
```

###### Open terminal and cd to `YOUR_RN_PROJECT/ios/` and run `pod install` . `TouchPointKit` will be added to your ios project.

###### In `AppDelegate.m` file Import  TouchPoint SDK by adding  `@import TouchPointKit;` line at top of file & add following code snippet on `AppDelegate.m`'s  `didFinishLaunchingWithOptions` function

```
NSString *apiKey = API_KEY;
NSString *apiSecret = API_SECRET;
TouchPointPods pod = TouchPointPodsDev2; //Environment e.g. dev1, dev2, na1, na2 etc.
NSArray *screens = [[NSArray alloc] initWithObjects:@"Demo 1", @"Demo 2", nil];
NSDictionary *visitorDict = [[NSDictionary alloc] initWithObjectsAndKeys:@"id",@"179",@"email",@"dinesh_demogmailcom", nil];

[[TouchPointActivity shared] configureWithApiKey: apiKey apiSecret: apiSecret podName: pod screenNames: screens visitor: visitorDict];

//NOTE: visitorDict is optional. You can also set it later by calling `NativeModules.TouchPointKitBridge.setVisitor(VISITOR)` from .js file

```

###### NOTE: Use Above step to configure SDK through AppDelegate file only if you are not calling this from .js file.

###### Create TouchPointKitBridge.h, TouchPointKitBridge.m file inside ios project. Add following code to these files:

```
//  TouchPointKitBridge.h
#import <Foundation/Foundation.h>
#import <React/RCTBridgeModule.h>
#import <React/RCTEventEmitter.h>
@import TouchPointKit;

NS_ASSUME_NONNULL_BEGIN

@interface TouchPointKitBridge : RCTEventEmitter <RCTBridgeModule, TouchPointActivityCompletionDelegate>

@end

NS_ASSUME_NONNULL_END
```

```
//  TouchPointKitBridge.m
#import "TouchPointKitBridge.h"

@implementation TouchPointKitBridge
{
  bool hasListeners;
}

// Will be called when this module's first listener is added.
-(void)startObserving {
    hasListeners = YES;
}

// Will be called when this module's last listener is removed, or on dealloc.
-(void)stopObserving {
    hasListeners = NO;
}

RCT_EXPORT_MODULE();

RCT_EXPORT_METHOD(configure:(NSString *)apiKey apiSecret:(NSString *)apiSecret pod:(int)pod screens:(NSArray *)screens visitor:(NSDictionary *)visitor )
{
  [[TouchPointActivity shared] configureWithApiKey: apiKey apiSecret: apiSecret podName: pod screenNames: screens visitor: visitor];
}

RCT_EXPORT_METHOD(setVisitor:(NSDictionary *)visitor)
{
  [TouchPointActivity shared].visitor = visitor;
}

RCT_EXPORT_METHOD(enableDebugLogs:(BOOL)enable)
{
  [TouchPointActivity shared].enableDebugLogs = enable;
}

RCT_EXPORT_METHOD(disableAllLogs:(BOOL)disable)
{
  [TouchPointActivity shared].disableAllLogs = disable;
}

RCT_EXPORT_METHOD(shouldApplyAPIFilter:(BOOL)apiFilter)
{
  [TouchPointActivity shared].shouldApplyAPIFilter = apiFilter;
}

RCT_EXPORT_METHOD(disableCaching:(BOOL)caching)
{
  [TouchPointActivity shared].disableCaching = caching;
}

RCT_EXPORT_METHOD(setScreen:(NSString *)name banner:(BOOL)banner)
{
  [[TouchPointActivity shared] setScreenNameWithScreenName:name banner:banner];
}

RCT_EXPORT_METHOD(openActivity:(NSString *)name)
{
  if ([[TouchPointActivity shared] shouldShowActivityWithScreenName: name]) {
    //dispatch_sync(dispatch_get_main_queue(),^(void){
        [[TouchPointActivity shared] openActivityWithScreenName: name delegate: self];
    //});
  }
}

RCT_EXPORT_METHOD(clearCache)
{
  NSUserDefaults * userDefaults = [NSUserDefaults standardUserDefaults];
  NSDictionary * dict = [userDefaults dictionaryRepresentation];
  for (id key in dict) {
      [userDefaults removeObjectForKey:key];
  }
  [userDefaults synchronize];
}

- (dispatch_queue_t)methodQueue
{
    return dispatch_get_main_queue();
}

- (void) didActivityCompleted {
  if (hasListeners) {
    [self sendEventWithName:@"didActivityCompletedEvent" body:@"ActivityCompleted"];
  }
}

- (NSArray<NSString *> *)supportedEvents {
  return @[@"didActivityCompletedEvent"];
}

@end


```

###### From your App.js call `TouchPointKitBridge` methods using `NativeModules`.

````
import {
  NativeModules,
  NativeEventEmitter,
} from 'react-native';

// Register for event listening from SDK (activity complete event)
const { TouchPointKitBridge } = NativeModules;
const eventEmitter = new NativeEventEmitter(TouchPointKitBridge);
const subscription = eventEmitter.addListener('didActivityCompletedEvent', didActivityCompletedEvent);

const didActivityCompletedEvent = (event) => {
   console.log(event);
}


// SDK Configuration
NativeModules.TouchPointKitBridge.clearCache(); // For testing. It will clear 'Activity Seen' status
NativeModules.TouchPointKitBridge.configure(API_KEY, API_SECRET, POD_NAME, SCREEN_NAMES, VISITOR);
NativeModules.TouchPointKitBridge.enableDebugLogs(true); // Optional, default is false
NativeModules.TouchPointKitBridge.shouldApplyAPIFilter(false); // Optional, default is true

// NOTE: POD_NAME value is integer. na1=0, na2=1, eu1=2, eu2=3, ap2=4, dev1=5, dev2=6, sit1=7, pub=8
// API_KEY, API_SECRET are String values
// SCREEN_NAMES is an array. e.g. ['Demo 1', 'Demo 2']
// VISITOR is json, e.g. { id: 'VISITOR_ID', email: 'VISITOR_EMAIL' }


// To show default banner styling
NativeModules.TouchPointKitBridge.setScreen(
  'SCREEN_NAME',
  true
);

// To use custom component
NativeModules.TouchPointKitBridge.setScreen(
  'SCREEN_NAME',
  false
);


// Call this method on your custom component click event
openActivity = () => {
    NativeModules.TouchPointKitBridge.openActivity('SCREEN_NAME');
}

````
