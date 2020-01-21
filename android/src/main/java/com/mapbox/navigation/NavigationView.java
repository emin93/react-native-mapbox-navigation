package com.mapbox.navigation;

import android.annotation.SuppressLint;
import android.location.Location;

import androidx.annotation.NonNull;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.LifecycleEventListener;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.events.RCTEventEmitter;
import com.mapbox.android.core.location.LocationEngine;
import com.mapbox.android.core.location.LocationEngineCallback;
import com.mapbox.android.core.location.LocationEngineProvider;
import com.mapbox.android.core.location.LocationEngineResult;
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
public class NavigationView extends NavigationView implements OnNavigationReadyCallback, ProgressChangeListener, SpeechAnnouncementListener {

    private ThemedReactContext context;
    private LifecycleEventListener lifecycleEventListener;

    private LocationEngine locationEngine;

    private DirectionsRoute directionsRoute;

    private Point destination = null;
    private boolean shouldSimulateRoute = false;
    private boolean isMuted = true;
    private boolean isNavigating = false;

    public NavigationView(ThemedReactContext context) {
        super(Objects.requireNonNull(context.getCurrentActivity()));

        this.context = context;

        locationEngine = LocationEngineProvider.getBestLocationEngine(context);
        onCreate(null);
        initialize(this);
        onResume();

        lifecycleEventListener = new LifecycleEventListener() {
            @Override
            public void onHostResume() {
                NavigationView.this.onResume();
            }

            @Override
            public void onHostPause() {
                NavigationView.this.onPause();
            }

            @Override
            public void onHostDestroy() {
                NavigationView.this.onDestroy();
            }
        };

        context.addLifecycleEventListener(lifecycleEventListener);
    }

    @Override
    public void onNavigationReady(boolean isRunning) {
        fetchCurrentLocation();
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

    public void setDestination(Point destination) {
        this.destination = destination;
        fetchCurrentLocation();
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

    @SuppressWarnings({"MissingPermission"})
    private void fetchCurrentLocation() {
        locationEngine.getLastLocation(new LocationEngineCallback<LocationEngineResult>() {
            @Override
            public void onSuccess(LocationEngineResult result) {
                Location location = result.getLastLocation();

                if (location == null) {
                    return;
                }

                Point origin = Point.fromLngLat(location.getLongitude(), location.getLatitude());
                fetchRoute(origin);
            }

            @Override
            public void onFailure(@NonNull Exception exception) {
            }
        });
    }

    private void fetchRoute(Point origin) {
        String accessToken = Mapbox.getAccessToken();

        if (accessToken == null || destination == null) {
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