package com.mapbox.navigation;

import androidx.annotation.NonNull;

import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.common.MapBuilder;
import com.facebook.react.uimanager.SimpleViewManager;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.annotations.ReactProp;
import com.mapbox.geojson.Point;

import org.jetbrains.annotations.NotNull;

import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class NavigationManager extends SimpleViewManager<NavigationView> {

    @NotNull
    @Override
    public String getName() {
        return "RNMapboxNavigation";
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
