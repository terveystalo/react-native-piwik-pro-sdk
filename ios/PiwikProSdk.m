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
  dispatch_async(dispatch_get_main_queue(), ^{
    [PiwikTracker sharedInstanceWithSiteID:siteID baseURL:[NSURL URLWithString:baseURL]];
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
    reject(exception.name, exception.reason, [[NSError alloc] init]);
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
    reject(exception.name, exception.reason, [[NSError alloc] init]);
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
    reject(exception.name, exception.reason, [[NSError alloc] init]);
  }
}

@end
