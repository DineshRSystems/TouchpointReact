//
//  TouchPointKitBridge.m
//  TouchpointReact
//
//  Created by Dinesh Tanwar on 17/02/21.
//

#import "TouchPointKitBridge.h"
@import TouchPointKit;

@implementation TouchPointKitBridge

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

RCT_EXPORT_METHOD(shouldApplyAPIFilter:(BOOL)apiFilter)
{
  [TouchPointActivity shared].shouldApplyAPIFilter = apiFilter;
}

RCT_EXPORT_METHOD(setScreen:(NSString *)name banner:(BOOL)banner)
{
  [[TouchPointActivity shared] setScreenNameWithScreenName:name banner:banner];
}

RCT_EXPORT_METHOD(openActivity:(NSString *)name)
{
  if ([[TouchPointActivity shared] shouldShowActivityWithScreenName: name]) {
    dispatch_sync(dispatch_get_main_queue(),^(void){
        [[TouchPointActivity shared] openActivityWithScreenName: name delegate: nil];
    });
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

@end
