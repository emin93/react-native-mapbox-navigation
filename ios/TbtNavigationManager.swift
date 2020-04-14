@objc(TbtNavigationManager)
class TbtNavigationManager: RCTViewManager {
  override func view() -> UIView! {
    return TbtNavigationView();
  }

  override static func requiresMainQueueSetup() -> Bool {
    return true
  }
}
