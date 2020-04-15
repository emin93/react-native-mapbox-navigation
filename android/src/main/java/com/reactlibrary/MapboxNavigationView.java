package com.reactlibrary;

import android.annotation.SuppressLint;
import android.location.Location;

import androidx.annotation.NonNull;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.LifecycleEventListener;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.events.RCTEventEmitter;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.services.android.navigation.ui.v5.NavigationView;
import com.mapbox.services.android.navigation.ui.v5.NavigationViewOptions;
import com.mapbox.services.android.navigation.ui.v5.OnNavigationReadyCallback;
import com.mapbox.services.android.navigation.ui.v5.listeners.SpeechAnnouncementListener;
import com.mapbox.services.android.navigation.ui.v5.map.NavigationMapboxMap;
import com.mapbox.services.android.navigation.ui.v5.voice.SpeechAnnouncement;
import com.mapbox.services.android.navigation.v5.navigation.MapboxNavigationOptions;
import com.mapbox.services.android.navigation.v5.navigation.NavigationRoute;
import com.mapbox.services.android.navigation.v5.routeprogress.ProgressChangeListener;
import com.mapbox.services.android.navigation.v5.routeprogress.RouteProgress;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@SuppressLint("ViewConstructor")
public class MapboxNavigationView extends NavigationView implements OnNavigationReadyCallback, ProgressChangeListener, SpeechAnnouncementListener {

    private ThemedReactContext context;
    private LifecycleEventListener lifecycleEventListener;

    private DirectionsRoute directionsRoute;

    private Point origin = null;
    private Point destination = null;
    private boolean shouldSimulateRoute = false;
    private boolean isMuted = true;
    private boolean isNavigating = false;

    public MapboxNavigationView(ThemedReactContext context) {
        super(Objects.requireNonNull(context.getCurrentActivity()));

        this.context = context;

        onCreate(null);
        initialize(this);
        onResume();

        lifecycleEventListener = new LifecycleEventListener() {
            @Override
            public void onHostResume() {
                MapboxNavigationView.this.onResume();
            }

            @Override
            public void onHostPause() {
                MapboxNavigationView.this.onPause();
            }

            @Override
            public void onHostDestroy() {
                MapboxNavigationView.this.onDestroy();
            }
        };

        context.addLifecycleEventListener(lifecycleEventListener);
    }

    @Override
    public void onNavigationReady(boolean isRunning) {
        fetchRoute();
    }

    @Override
    public void onProgressChange(Location location, RouteProgress routeProgress) {
        WritableMap event = Arguments.createMap();
        event.putDouble("longitude", location.getLongitude());
        event.putDouble("latitude", location.getLatitude());
        event.putDouble("distanceRemaining", routeProgress.distanceRemaining());
        event.putDouble("durationRemaining", routeProgress.durationRemaining());
        context.getJSModule(RCTEventEmitter.class).receiveEvent(getId(), "onProgressChange", event);
    }

    @Override
    public SpeechAnnouncement willVoice(SpeechAnnouncement announcement) {
        if (isMuted) {
            return null;
        }

        return announcement;
    }

    public void setOrigin(Point origin) {
        this.origin = origin;
        fetchRoute();
    }

    public void setDestination(Point destination) {
        this.destination = destination;
        fetchRoute();
    }

    public void setShouldSimulateRoute(boolean shouldSimulateRoute) {
        this.shouldSimulateRoute = shouldSimulateRoute;
    }

    public void setIsMuted(boolean isMuted) {
        this.isMuted = isMuted;
    }

    public void onDropViewInstance() {
        if (isNavigating) {
            stopNavigation();
        }

        context.removeLifecycleEventListener(lifecycleEventListener);
        lifecycleEventListener = null;
    }

    private void fetchRoute() {
        String accessToken = Mapbox.getAccessToken();

        if (accessToken == null || origin == null || destination == null) {
            return;
        }

        NavigationRoute.builder(getContext())
                .accessToken(accessToken)
                .origin(origin)
                .destination(destination)
                .build()
                .getRoute(new Callback<DirectionsResponse>() {
                    @Override
                    public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
                        DirectionsResponse responseBody = response.body();

                        if (responseBody == null || responseBody.routes().size() == 0) {
                            return;
                        }

                        directionsRoute = responseBody.routes().get(0);
                        start();
                    }

                    @Override
                    public void onFailure(Call<DirectionsResponse> call, Throwable t) {
                    }
                });
    }

    private void start() {
        if (directionsRoute == null) {
            return;
        }

        NavigationMapboxMap navigationMapboxMap = retrieveNavigationMapboxMap();

        if (navigationMapboxMap == null) {
            return;
        }

        MapboxNavigationOptions mapboxNavigationOptions = MapboxNavigationOptions.builder()
                .build();

        NavigationViewOptions options = NavigationViewOptions.builder()
                .directionsRoute(directionsRoute)
                .shouldSimulateRoute(shouldSimulateRoute)
                .progressChangeListener(this)
                .speechAnnouncementListener(this)
                .navigationOptions(mapboxNavigationOptions)
                .build();

        startNavigation(options);
        onStart();
        isNavigating = true;
    }
}