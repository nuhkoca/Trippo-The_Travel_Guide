package com.nuhkoca.trippo.ui.nearby;

import android.Manifest;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.nuhkoca.trippo.R;
import com.nuhkoca.trippo.callback.IAlertDialogItemClickListener;
import com.nuhkoca.trippo.databinding.ActivityNearbyBinding;
import com.nuhkoca.trippo.helper.Constants;
import com.nuhkoca.trippo.model.remote.places.PlacesWrapper;
import com.nuhkoca.trippo.model.remote.places.Results;
import com.nuhkoca.trippo.repository.api.PlacesEndpointRepository;
import com.nuhkoca.trippo.ui.settings.ActivityType;
import com.nuhkoca.trippo.ui.settings.SettingsActivity;
import com.nuhkoca.trippo.util.AlertDialogUtils;
import com.nuhkoca.trippo.util.IntentUtils;
import com.nuhkoca.trippo.util.SnackbarUtils;

import java.util.List;
import java.util.Objects;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;
import timber.log.Timber;

public class NearbyActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        EasyPermissions.PermissionCallbacks,
        EasyPermissions.RationaleCallbacks,
        View.OnClickListener,
        SharedPreferences.OnSharedPreferenceChangeListener {

    private ActivityNearbyBinding mActivityNearbyBinding;
    private BottomSheetBehavior mBottomSheetBehavior;
    private GoogleMap mMap;
    private GoogleApiClient mGoogleAPIClient;

    private double mLatitude, mLongitude;
    private Location mLastLocation;
    private Marker mMarker;

    private int mBottomState = 4;
    private String mType;

    private float mMapZoom = 0;

    private SharedPreferences mSharedPreferences;

    private NearbyActivityViewModel mNearbyActivityViewModel;

    private int mReqCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivityNearbyBinding = DataBindingUtil.setContentView(this, R.layout.activity_nearby);
        setTitle(getString(R.string.nearby_name));

        mNearbyActivityViewModel = ViewModelProviders.of(this, new NearbyActivityViewModelFactory(getApplication(), PlacesEndpointRepository.getInstance())).get(NearbyActivityViewModel.class);

        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }

        prepareBottomSheet();
        prepareMap();

        if (savedInstanceState != null) {
            mBottomState = savedInstanceState.getInt(Constants.BOTTOM_SHEET_STATE);
            mType = savedInstanceState.getString(Constants.PLACE_TYPE_STATE);
            mMapZoom = savedInstanceState.getFloat(Constants.ZOOM_STATE);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (!TextUtils.isEmpty(mType)) {
                        nearbyPlace(mType);
                    }
                }
            }, 1000);
        }

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mSharedPreferences.registerOnSharedPreferenceChangeListener(this);

        mReqCode = getIntent().getIntExtra(Constants.PARENT_ACTIVITY_REQ_KEY, 0);

        if (mReqCode == 0) {
            setTitle(getIntent().getStringExtra(Constants.CITY_OR_COUNTRY_NAME_KEY));
        }
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private boolean isGPSEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        return locationManager != null && locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.common_menu, menu);

        menu.findItem(R.id.menuCommonSettings)
                .setTitle(getString(R.string.map_settings_title));

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemThatWasClicked = item.getItemId();

        switch (itemThatWasClicked) {
            case android.R.id.home:
                supportFinishAfterTransition();

                if (mReqCode > 0) {
                    NavUtils.navigateUpFromSameTask(this);
                } else {
                    super.onBackPressed();
                }

                return true;

            case R.id.menuCommonSettings:
                startActivity(new Intent(NearbyActivity.this, SettingsActivity.class)
                        .putExtra(Constants.ACTIVITY_TYPE_KEY, ActivityType.MAP.getActivityId()));

                return true;

            case R.id.menuCommonFeedback:
                new IntentUtils.Builder()
                        .setContext(this)
                        .setAction(IntentUtils.ActionType.REPORT)
                        .create();

                return true;

            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void prepareBottomSheet() {
        mBottomSheetBehavior = BottomSheetBehavior.from(Objects.requireNonNull(mActivityNearbyBinding.lBottomSheet).clBottomSheet);

        mBottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (mReqCode > 0) {
                    mBottomState = mBottomSheetBehavior.getState();
                } else {
                    if (newState == BottomSheetBehavior.STATE_DRAGGING) {
                        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

                        new SnackbarUtils.Builder()
                                .setView(mActivityNearbyBinding.clNearby)
                                .setMessage(getString(R.string.map_only_mode_warning_text))
                                .setLength(SnackbarUtils.Length.LONG)
                                .show(getString(R.string.dismiss_action_text), null)
                                .build();
                    }
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });

        mActivityNearbyBinding.lBottomSheet.clBottomSheet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mReqCode > 0) {
                    toggleBottomSheet();
                }
            }
        });

        mBottomSheetBehavior.setState(mBottomState);

        mActivityNearbyBinding.lBottomSheet.tvRestaurant.setOnClickListener(this);
        mActivityNearbyBinding.lBottomSheet.tvCafe.setOnClickListener(this);
        mActivityNearbyBinding.lBottomSheet.tvGasStation.setOnClickListener(this);
        mActivityNearbyBinding.lBottomSheet.tvATM.setOnClickListener(this);
        mActivityNearbyBinding.lBottomSheet.tvPharmacy.setOnClickListener(this);
        mActivityNearbyBinding.lBottomSheet.tvGrocery.setOnClickListener(this);
    }

    public void toggleBottomSheet() {
        if (mBottomSheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
            mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            mBottomState = mBottomSheetBehavior.getState();
        } else {
            mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            mBottomState = mBottomSheetBehavior.getState();
        }
    }

    private void prepareMap() {
        if (mMap == null) {
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
        }
    }

    private void reCenterMap() {
        LatLng latLng = new LatLng(mLatitude, mLongitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(mMapZoom));

        MarkerOptions markerOptions = new MarkerOptions()
                .position(latLng)
                .title(getString(R.string.my_position_text))
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));

        mMap.addMarker(markerOptions).showInfoWindow();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (mReqCode == 0) {
            if (mMapZoom == 0)
                mMapZoom = 8;

            getRequestFromCatalogue();
        } else {
            if (!isGPSEnabled()) {
                showGPSAlert();
            }

            if (mMapZoom == 0)
                mMapZoom = Constants.DEFAULT_ZOOM_LEVEL;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    locationPermissionsTask();
                } else {
                    buildGoogleAPIClient();
                    mMap.setMyLocationEnabled(true);
                }
            } else {
                buildGoogleAPIClient();
                mMap.setMyLocationEnabled(true);
            }
        }

        mMap.setBuildingsEnabled(true);
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.setPadding(0, 0, 0, 150);

        mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
                reCenterMap();

                if (!isGPSEnabled()) {
                    showGPSAlert();

                    return true;
                }

                return true;
            }
        });

        mMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
            @Override
            public void onCameraMove() {
                mMapZoom = mMap.getCameraPosition().zoom;
            }
        });
    }

    private void getRequestFromCatalogue() {
        String cityOrCountryName = "";

        if (getIntent() != null) {
            mLatitude = getIntent().getDoubleExtra(Constants.CATALOGUE_LAT_REQ, 0);
            mLongitude = getIntent().getDoubleExtra(Constants.CATALOGUE_LNG_REQ, 0);
            cityOrCountryName = getIntent().getStringExtra(Constants.CITY_OR_COUNTRY_NAME_KEY);
        }

        LatLng latLng = new LatLng(mLatitude, mLongitude);

        mMap.addMarker(new MarkerOptions().position(latLng).title(cityOrCountryName)).showInfoWindow();

        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(mMapZoom));

        mActivityNearbyBinding.lBottomSheet.clBottomSheet.setEnabled(false);
    }

    private synchronized void buildGoogleAPIClient() {
        mGoogleAPIClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        mGoogleAPIClient.connect();
    }

    private boolean hasLocationPermissions() {
        return EasyPermissions.hasPermissions(this, Constants.LOCATION_PERMISSIONS);
    }

    @AfterPermissionGranted(Constants.LOCATION_PERMISSIONS_REQ_CODE)
    public void locationPermissionsTask() {
        if (!hasLocationPermissions()) {
            EasyPermissions.requestPermissions(
                    this,
                    getString(R.string.rationale_location_permission),
                    Constants.NEARBY_LOCATION_PERMISSIONS_REQ_CODE,
                    Constants.LOCATION_PERMISSIONS);
        }
    }

    private LocationCallback getLocationCallback() {
        return new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);

                mLastLocation = locationResult.getLastLocation();

                if (mMarker != null)
                    mMarker.remove();

                mLatitude = mLastLocation.getLatitude();
                mLongitude = mLastLocation.getLongitude();

                LatLng latLng = new LatLng(mLatitude, mLongitude);
                MarkerOptions markerOptions = new MarkerOptions()
                        .position(latLng)
                        .title(getString(R.string.my_position_text))
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));

                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(mMapZoom));

                mMarker = mMap.addMarker(markerOptions);
                mMarker.showInfoWindow();

                if (mGoogleAPIClient != null)
                    LocationServices.getFusedLocationProviderClient(getApplicationContext()).removeLocationUpdates(this);
            }
        };
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        LocationRequest locationRequest = new LocationRequest();

        locationRequest.setInterval(Constants.MAP_INTERVAL);
        locationRequest.setFastestInterval(Constants.MAP_INTERVAL);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            LocationServices.getFusedLocationProviderClient(this).requestLocationUpdates(locationRequest, getLocationCallback(), Looper.myLooper());
        } else {
            locationPermissionsTask();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleAPIClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, connectionResult.getErrorMessage(), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        Timber.d("Permission granted: %s", String.valueOf(requestCode));
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this).build().show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRationaleAccepted(int requestCode) {
        Timber.d("Rationale accepted: %s", String.valueOf(requestCode));
    }

    @Override
    public void onRationaleDenied(int requestCode) {
        Timber.d("Rationale denied: %s", String.valueOf(requestCode));
    }

    @Override
    public void onBackPressed() {
        supportFinishAfterTransition();
        super.onBackPressed();
    }

    @Override
    public void onClick(View v) {
        int itemThatWasClicked = v.getId();
        String placeHolder = "";

        switch (itemThatWasClicked) {
            case R.id.tvRestaurant:
                placeHolder = getString(R.string.restaurant_holder);
                break;

            case R.id.tvCafe:
                placeHolder = getString(R.string.cafe_holder);
                break;

            case R.id.tvGasStation:
                placeHolder = getString(R.string.gas_station_holder);
                break;

            case R.id.tvATM:
                placeHolder = getString(R.string.atm_holder);
                break;

            case R.id.tvPharmacy:
                placeHolder = getString(R.string.pharmacy_holder);
                break;

            case R.id.tvGrocery:
                placeHolder = getString(R.string.grocery_holder);
                break;

            default:
                break;
        }


        if (!isGPSEnabled()) {
            showGPSAlert();
        } else {
            nearbyPlace(placeHolder);
        }

        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
    }

    private void nearbyPlace(final String type) {
        String location = mLatitude + "," + mLongitude;

        mType = type;

        mNearbyActivityViewModel.findNearbyPlaces(location, loadMapPreferencesFromSettings(mSharedPreferences), mType);
        mNearbyActivityViewModel.getNearbyPlaces().observe(this, new Observer<PlacesWrapper>() {
            @Override
            public void onChanged(@Nullable PlacesWrapper placesWrapper) {
                if (placesWrapper != null && placesWrapper.getResults().size() != 0) {
                    mMap.clear();

                    for (int i = 0; i < placesWrapper.getResults().size(); i++) {
                        MarkerOptions markerOptions = new MarkerOptions();

                        Results results = placesWrapper.getResults().get(i);

                        double lat = Double.parseDouble(results.getGeometry().getLocation().getLat());
                        double lng = Double.parseDouble(results.getGeometry().getLocation().getLng());
                        String placeName = results.getName();

                        LatLng latLng = new LatLng(lat, lng);
                        markerOptions.title(placeName);
                        markerOptions.position(latLng);

                        if (type.equals(getString(R.string.restaurant_holder))) {
                            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_restaurant_marker));
                        } else if (type.equals(getString(R.string.cafe_holder))) {
                            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_cafe_marker));
                        } else if (type.equals(getString(R.string.gas_station_holder))) {
                            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_gas_station_marker));
                        } else if (type.equals(getString(R.string.atm_holder))) {
                            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_atm_marker));
                        } else if (type.equals(getString(R.string.pharmacy_holder))) {
                            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_pharmacy_marker));
                        } else if (type.equals(getString(R.string.grocery_holder))) {
                            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_grocery_marker));
                        } else {
                            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                        }

                        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                        mMap.animateCamera(CameraUpdateFactory.zoomTo(mMapZoom));

                        mMap.addMarker(markerOptions);
                    }

                    reCenterMap();
                }
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(Constants.BOTTOM_SHEET_STATE, mBottomState);
        outState.putString(Constants.PLACE_TYPE_STATE, mType);
        outState.putFloat(Constants.ZOOM_STATE, mMapZoom);

        super.onSaveInstanceState(outState);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(getString(R.string.radius_key))) {
            if (!TextUtils.isEmpty(mType)) {
                loadMapPreferencesFromSettings(sharedPreferences);
                nearbyPlace(mType);
            }
        }
    }

    private String loadMapPreferencesFromSettings(SharedPreferences sharedPreferences) {
        return sharedPreferences.getString(getString(R.string.radius_key), getString(R.string.km_1500_radius_value));
    }

    private void showGPSAlert() {
        AlertDialogUtils.dialogWithAlert(this,
                null,
                getString(R.string.gps_warning_text),
                new IAlertDialogItemClickListener.Alert() {
                    @Override
                    public void onPositiveButtonClicked() {
                        startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                });
    }

    @Override
    public void onPause() {
        if (mGoogleAPIClient != null) {
            mGoogleAPIClient.disconnect();
        }

        super.onPause();
    }

    @Override
    public void onStop() {
        mNearbyActivityViewModel.onCleared();

        if (mGoogleAPIClient != null)
            mGoogleAPIClient.disconnect();

        if (mMap != null)
            mMap.clear();

        if (mMarker != null)
            mMarker.remove();

        super.onStop();
    }

    @Override
    protected void onDestroy() {
        if (mNearbyActivityViewModel != null) {
            mNearbyActivityViewModel.onCleared();
        }

        mSharedPreferences.unregisterOnSharedPreferenceChangeListener(this);

        if (mGoogleAPIClient != null)
            mGoogleAPIClient.disconnect();

        if (mMap != null)
            mMap.clear();

        if (mMarker != null)
            mMarker.remove();

        LocationServices.getFusedLocationProviderClient(getApplicationContext()).removeLocationUpdates(getLocationCallback());

        super.onDestroy();
    }
}