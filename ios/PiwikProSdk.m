#import "PiwikProSdk.h"

#import <PiwikPROSDK/PiwikPROSDK.h>

@implementation PiwikProSdk

RCT_EXPORT_MODULE()

RCT_REMAP_METHOD(init,
                 initWithBaseURL:(nonnull NSString*)baseURL
                 withSiteID:(nonnull NSString*)siteID
                 withOptions:(nonnull NSDictionary*)options
                 withResolver:(RCTPromiseResolveBlock)resolve
                 withRejecter:(RCTPromiseRejectBlock)reject)
{
  if ([PiwikTracker sharedInstance] != nil) {
    NSError* error = [NSError errorWithDomain:@"react-native-piwik-pro-sdk" code:0 userInfo:nil];
    reject(@"already_initialized", @"Tracker already initialized", error);
    return;
  }

  dispatch_async(dispatch_get_main_queue(), ^{
    PiwikTracker* tracker = [PiwikTracker sharedInstanceWithSiteID:siteID baseURL:[NSURL URLWithString:baseURL]];

    NSBundle* mainBundle = [NSBundle mainBundle];
    // Default value contains both version and build number
    // Set to only version, so that we are consistent with Android behaviour
    tracker.appVersion = [[mainBundle infoDictionary] objectForKey:@"CFBundleShortVersionString"];

    if (options[@"applicationDomain"] != nil) {
      tracker.appName = options[@"applicationDomain"];
    } else {
      tracker.appName = [mainBundle bundleIdentifier];
    }

    if (options[@"dispatchInterval"] != nil) {
      tracker.dispatchInterval = [options[@"dispatchInterval"] doubleValue];
    }

    if (options[@"isPrefixingEnabled"] != nil) {
      tracker.isPrefixingEnabled = [options[@"isPrefixingEnabled"] boolValue];
    }

    resolve(nil);
  });
}

RCT_REMAP_METHOD(trackScreen,
                 trackScreenWithPath:(nonnull NSString*)path
                 withResolver:(RCTPromiseResolveBlock)resolve
                 withRejecter:(RCTPromiseRejectBlock)reject)
{
  @try {
    [[PiwikTracker sharedInstance] sendView:path];
    resolve(nil);
  } @catch (NSException *exception) {
    NSError* error = [NSError errorWithDomain:@"react-native-piwik-pro-sdk" code:0 userInfo:nil];
    reject(exception.name, exception.reason, error);
  }
}

RCT_REMAP_METHOD(trackEvent,
                 trackScreenWithCategory:(nonnull NSString*)category
                 withAction:(nonnull NSString*)action
                 optionalArgs:(nonnull NSDictionary*)optionalArgs
                 withResolver:(RCTPromiseResolveBlock)resolve
                 withRejecter:(RCTPromiseRejectBlock)reject)
{
  @try {
    [[PiwikTracker sharedInstance]
     sendEventWithCategory:category
     action:action
     name:optionalArgs[@"name"]
     value:optionalArgs[@"value"]];
    resolve(nil);
  } @catch (NSException *exception) {
    NSError* error = [NSError errorWithDomain:@"react-native-piwik-pro-sdk" code:0 userInfo:nil];
    reject(exception.name, exception.reason, error);
  }
}

RCT_REMAP_METHOD(setCustomDimension,
                 setCustomDimensionForIndex:(NSUInteger)index
                 withValue:(nonnull NSString*)value
                 withScope:(nonnull NSString*)scope
                 withResolver:(RCTPromiseResolveBlock)resolve
                 withRejecter:(RCTPromiseRejectBlock)reject)
{
  @try {
    CustomDimensionScope dimensionScope;
      
    if ([scope isEqualToString:@"visit"]) {
      dimensionScope = CustomDimensionScopeVisit;
    } else if ([scope isEqualToString:@"action"]) {
      dimensionScope = CustomDimensionScopeAction;
    } else {
      NSError* error = [NSError errorWithDomain:@"react-native-piwik-pro-sdk" code:0 userInfo:nil];
      reject(@"Unsupported custom dimension scope", @"Supported custom dimension scopes are 'visit' and 'action'.", error);
      return;
    }
      
    [[PiwikTracker sharedInstance]
     setCustomDimensionForIndex:index
     value:value
     scope:dimensionScope];
    resolve(nil);
  } @catch (NSException *exception) {
    NSError* error = [NSError errorWithDomain:@"react-native-piwik-pro-sdk" code:0 userInfo:nil];
    reject(exception.name, exception.reason, error);
  }
}

RCT_REMAP_METHOD(dispatch,
                 dispatchWithResolver:(RCTPromiseResolveBlock)resolve
                 withRejecter:(RCTPromiseRejectBlock)reject)
{
  @try {
    [[PiwikTracker sharedInstance] dispatch];
    resolve(nil);
  } @catch (NSException *exception) {
    NSError* error = [NSError errorWithDomain:@"react-native-piwik-pro-sdk" code:0 userInfo:nil];
    reject(exception.name, exception.reason, error);
  }
}

@end
