package com.reactlibrary;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Callback;

public class MapboxNavigationManager extends SimpleViewManager<MapboxNavigationView> {

    private final ReactApplicationContext reactContext;

    public MapboxNavigationModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;
    }

    @Override
    public String getName() {
        return "MapboxNavigation";
    }

    @NotNull
    @Override
    public NavigationView createViewInstance(@Nonnull ThemedReactContext reactContext) {
        return new NavigationView(reactContext);
    }

    @Override
    public void onDropViewInstance(@NonNull NavigationView view) {
        view.onDropViewInstance();
        super.onDropViewInstance(view);
    }

    @Override
    public Map<String, Object> getExportedCustomDirectEventTypeConstants() {
        return MapBuilder.of("onProgressChange", MapBuilder.of("registrationName", "onProgressChange"));
    }

    @ReactProp(name = "origin")
    public void setOrigin(TbtNavigationView view, @Nullable ReadableArray sources) {
        if (sources == null) {
            view.setOrigin(null);
            return;
        }

        view.setOrigin(Point.fromLngLat(sources.getDouble(0), sources.getDouble(1)));
    }

    @ReactProp(name = "destination")
    public void setDestination(TbtNavigationView view, @Nullable ReadableArray sources) {
        if (sources == null) {
            view.setDestination(null);
            return;
        }

        view.setDestination(Point.fromLngLat(sources.getDouble(0), sources.getDouble(1)));
    }

    @ReactProp(name = "shouldSimulateRoute")
    public void setShouldSimulateRoute(NavigationView view, boolean shouldSimulateRoute) {
        view.setShouldSimulateRoute(shouldSimulateRoute);
    }

    @ReactProp(name = "isMuted")
    public void setIsMuted(NavigationView view, boolean isMuted) {
        view.setIsMuted(isMuted);
    }
}
