//
//  TouchPointKitBridge.h
//  TouchpointReact
//
//  Created by Dinesh Tanwar on 17/02/21.
//

#import <Foundation/Foundation.h>
#import <React/RCTBridgeModule.h>
#import <React/RCTEventEmitter.h>
@import TouchPointKit;

NS_ASSUME_NONNULL_BEGIN

@interface TouchPointKitBridge : RCTEventEmitter <RCTBridgeModule, TouchPointActivityCompletionDelegate>

@end

NS_ASSUME_NONNULL_END
