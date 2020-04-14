#import "React/RCTViewManager.h"

@interface RCT_EXTERN_MODULE(TbtNavigationManager, RCTViewManager)

RCT_EXPORT_VIEW_PROPERTY(onProgressChange, RCTDirectEventBlock)
RCT_EXPORT_VIEW_PROPERTY(origin, NSArray)
RCT_EXPORT_VIEW_PROPERTY(destination, NSArray)
RCT_EXPORT_VIEW_PROPERTY(isMuted, BOOL)
RCT_EXPORT_VIEW_PROPERTY(shouldSimulateRoute, BOOL)

@end
