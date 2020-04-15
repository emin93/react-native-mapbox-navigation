package com.reactlibrary;

import androidx.annotation.NonNull;

import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.common.MapBuilder;
import com.facebook.react.uimanager.SimpleViewManager;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.annotations.ReactProp;
import com.facebook.react.bridge.ReactApplicationContext;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;

import org.jetbrains.annotations.NotNull;

import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class MapboxNavigationManager extends SimpleViewManager<MapboxNavigationView> {

    ReactApplicationContext mCallerContext;

    public MapboxNavigationManager(ReactApplicationContext reactContext) {
        mCallerContext = reactContext;
        mCallerContext.runOnUiQueueThread(new Runnable() {
            @Override
            public void run() {
                Mapbox.getInstance(mCallerContext, "pk.eyJ1IjoiZW1pbjkzIiwiYSI6ImNrOTFxenJscDAwamgzbm1za3Iya2k1bzgifQ.ub5B7UXS5o9tLyPfXwIyDA");
            }
        });
    }

    @Override
    public String getName() {
        return "MapboxNavigation";
    }

    @NotNull
    @Override
    public MapboxNavigationView createViewInstance(@Nonnull ThemedReactContext reactContext) {
        return new MapboxNavigationView(reactContext);
    }

    @Override
    public void onDropViewInstance(@NonNull MapboxNavigationView view) {
        view.onDropViewInstance();
        super.onDropViewInstance(view);
    }

    @Override
    public Map getExportedCustomDirectEventTypeConstants() {
        return MapBuilder.of("onProgressChange", MapBuilder.of("registrationName", "onProgressChange"));
    }

    @ReactProp(name = "origin")
    public void setOrigin(MapboxNavigationView view, @Nullable ReadableArray sources) {
        if (sources == null) {
            view.setOrigin(null);
            return;
        }

        view.setOrigin(Point.fromLngLat(sources.getDouble(0), sources.getDouble(1)));
    }

    @ReactProp(name = "destination")
    public void setDestination(MapboxNavigationView view, @Nullable ReadableArray sources) {
        if (sources == null) {
            view.setDestination(null);
            return;
        }

        view.setDestination(Point.fromLngLat(sources.getDouble(0), sources.getDouble(1)));
    }

    @ReactProp(name = "shouldSimulateRoute")
    public void setShouldSimulateRoute(MapboxNavigationView view, boolean shouldSimulateRoute) {
        view.setShouldSimulateRoute(shouldSimulateRoute);
    }

    @ReactProp(name = "isMuted")
    public void setIsMuted(MapboxNavigationView view, boolean isMuted) {
        view.setIsMuted(isMuted);
    }
}
