package net.behpardaz.voting.activities;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import net.behpardaz.voting.R;

public class MapsActivity extends Fragment implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        com.google.android.gms.location.LocationListener, SeekBar.OnSeekBarChangeListener {
    private static final int MARKER_BOUNDS = 10;
    private static boolean isGPSDialogShown=false;
    private static int REQUEST_GPS=1;
    MapView mMapView;
    public static GoogleMap mMap;
    GoogleApiClient mGoogleApiClient;
    LocationRequest mLocationRequest;
    private static final String TAG = "MapActivity";
    private SeekBar distanceBar;
    View inflate;
    @Nullable

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        }
        inflate = inflater.inflate(R.layout.activity_maps, null);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mMapView = (MapView)  inflate.findViewById(R.id.map);
        mMapView.onCreate(savedInstanceState);

        if(savedInstanceState==null) {
            Log.e(TAG, "onCreateView: "+mMapView.getId() );
            mMapView.onResume(); // needed to get the map to display immediately
        }else
            mMapView.onCreate(savedInstanceState);
        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
            mainHandler = new Handler(getActivity().getApplicationContext().getMainLooper());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mMapView.getMapAsync(this);
//        FloatingActionButton fab = (FloatingActionButton) inflate.findViewById(R.id.floatingActionButton);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        progressDialog=new ProgressDialog(getContext());
        mLocationSource = new LongPressLocationSource();
        distanceBar = (SeekBar)inflate.findViewById(R.id.seekBarDist); // make seekbar object
        distanceBar.setOnSeekBarChangeListener(this);
        return inflate;
    }



    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    public boolean checkLocationPermission(){
        if (ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Asking user if explanation is needed
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                //Prompt the user once explanation has been shown
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);

            }
            return false;
        } else {
            return true;
        }
    }



    @Override
    public void onResume() {
        super.onResume();
        Log.e(TAG, "onResume: "+mMapView );
        if(mMapView!=null){
        mMapView.onResume();
        mLocationSource.onResume();
    }}

    @Override
    public void onPause() {
        super.onPause();
        mLocationSource.onPause();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = new LocationRequest();
//        mLocationRequest.setSmallestDisplacement(500);
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if(getActivity()!=null)
        if (ContextCompat.checkSelfPermission(getActivity().getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
                            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e(TAG,connectionResult.getErrorMessage());

    }
    public int getZoomLevel(double radius) {
            double scale = radius / 500;
            int zoomLevel =(int) (16 - Math.log(scale) / Math.log(2));
        return zoomLevel;
    }
    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        TextView viewById = (TextView) inflate.findViewById(R.id.textViewDistance);
        viewById.setText(String.valueOf(i));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(getZoomLevel(i)));
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    /**
     * A {@link LocationSource} which reports a new location whenever a user long presses the map
     * at
     * the point at which a user long pressed the map.
     */
    private class LongPressLocationSource implements LocationSource, GoogleMap.OnMapLongClickListener {

        private LocationSource.OnLocationChangedListener mListener;

        /**
         * Flag to keep track of the activity's lifecycle. This is not strictly necessary in this
         * case because onMapLongPress events don't occur while the activity containing the map is
         * paused but is included to demonstrate best practices (e.g., if a background service were
         * to be used).
         */
        private boolean mPaused;

        @Override
        public void activate(OnLocationChangedListener listener) {
            mListener = listener;
        }

        @Override
        public void deactivate() {
            mListener = null;
        }

        @Override
        public void onMapLongClick(LatLng point) {
            if (mListener != null && !mPaused) {
                Location location = new Location("LongPressLocationProvider");
                location.setLatitude(point.latitude);
                location.setLongitude(point.longitude);
                location.setAccuracy(100);
                marker.remove();
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(point,14));
//               mMap.animateCamera(CameraUpdateFactory.zoomTo(14));
                marker = mMap.addMarker(new MarkerOptions().position(point).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA)));
                TextView  viewById = (TextView)(getView().findViewById(R.id.latlongLocation));
                        viewById.setText(location.getLatitude()+" "+ location.getLongitude());
//                mListener.onLocationChanged(location);
            }
        }

        public void onPause() {
            mPaused = true;
        }

        public void onResume() {
            mPaused = false;
        }
    }

    private LongPressLocationSource mLocationSource;


    private void setUpMapIfNeeded() {
        if (mMap == null) {
            SupportMapFragment mapFrag = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
            mapFrag.getMapAsync(this);
            mMap.animateCamera(CameraUpdateFactory.zoomTo(14));

        }
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity().getApplicationContext())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
//        mGoogleApiClient.connect();
        enableLocationSettings(getActivity());
        new GetMyLocation().execute();
//        mainHandler.post(myRunnable);
        if (mGoogleApiClient!=null)
            Log.e(TAG, "mGoogleApiClient.isConnected()  "+mGoogleApiClient.isConnected());
    }
    public static boolean enableLocationSettings(final Activity activity) {
        boolean gps_enabled = false;
        boolean network_enabled = false;
        LocationManager locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
        try {
            gps_enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception e) {
        }
        try {
            network_enabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception e) {
        }

        if (!gps_enabled ) {
            if (!isGPSDialogShown) {
                GoogleApiClient googleApiClient = new GoogleApiClient.Builder(activity)
                        .addApi(LocationServices.API)
                        .build();
                googleApiClient.connect();

                LocationRequest locationRequest = LocationRequest.create();
                locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

                LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                        .addLocationRequest(locationRequest);

                builder.setAlwaysShow(true);

                PendingResult<LocationSettingsResult> result =
                        LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
                result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
                    @Override
                    public void onResult(LocationSettingsResult result) {
                        final Status status = result.getStatus();
                        final LocationSettingsStates state = result.getLocationSettingsStates();
                        Toast.makeText(activity, "location status "+status.getStatusMessage(), Toast.LENGTH_LONG).show();
                        switch (status.getStatusCode()) {
                            case LocationSettingsStatusCodes.SUCCESS:

                                break;
                            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:

                                try {
                                    status.startResolutionForResult(
                                            activity, REQUEST_GPS);
                                    isGPSDialogShown = true;
                                } catch (IntentSender.SendIntentException e) {

                                }
                                break;
                            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:

                                break;
                        }
                    }
                });
            }
            return false;
        }else {
            return true;
        }
    }
    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(final GoogleMap googleMap) {

        mMap = googleMap;
        setUpMapIfNeeded();

        mMap.setLocationSource(mLocationSource);
        mMap.setOnMapLongClickListener(mLocationSource);
        mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
//                enableLocationSettings(getActivity());
                buildGoogleApiClient();
                return false;
            }
        });
        // Add a marker in tehran and move the camera
        LatLng tehran = new LatLng(35.6892, 51.3890);
        marker=mMap.addMarker(new MarkerOptions().position(tehran).title("Marker in Tehran"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(tehran));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(tehran,14));

        View viewById = getView();

        if(viewById!=null) {
            TextView  viewlatlongLocation = (TextView)viewById.findViewById(R.id.latlongLocation);;
            viewlatlongLocation.setText("35.6892" + " " + "51.3890");
        }
        //show error dialog if GoolglePlayServices not available
//        if (!isGooglePlayServicesAvailable()) {
//            finish();
//        }

        if (getContext()==null || ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        buildGoogleApiClient();

        mMap.setMyLocationEnabled(true);

//            LocationManager locationManager = (LocationManager) this.getActivity().getSystemService(Context.LOCATION_SERVICE);
//            Criteria criteria = new Criteria();
//            String bestProvider = locationManager.getBestProvider(criteria, true);
//            Location location = locationManager.getLastKnownLocation(bestProvider);
//            if (location != null) {
//                onLocationChanged(location);
//            }
//        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(),location.getLongitude()), 15);
//        mMap.animateCamera(cameraUpdate);
//            locationManager.requestLocationUpdates(bestProvider, 0, 500, this);
            // Check if we were successful in obtaining the map.
//            if (mMap != null) {
//
//
//                mMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
//
//                    @Override
//                    public void onMyLocationChange(Location arg0) {
//                        // TODO Auto-generated method stub
//                        marker.remove();
//                        marker = mMap.addMarker(new MarkerOptions().position(new LatLng(arg0.getLatitude(), arg0.getLongitude())).title("It's Me!").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA)));
//                        mMap.animateCamera(CameraUpdateFactory.zoomTo(14));
//                        TextView  viewById = (TextView)getView().findViewById(R.id.latlongLocation);
//                        viewById.setText(arg0.getLatitude()+" "+ arg0.getLongitude());
//
//                    }
//                });
//
//            }
        //DO WHATEVER YOU WANT WITH GOOGLEMAP
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.setMyLocationEnabled(true);
        mMap.setTrafficEnabled(true);
//        mMap.setIndoorEnabled(true);
//        mMap.setBuildingsEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
    }
    // Get a handler that can be used to post to the main thread
    Handler mainHandler;



    public static Marker marker;
    private boolean isGooglePlayServicesAvailable() {
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this.getContext());
        if (ConnectionResult.SUCCESS == status) {
            return true;
        } else {
            GooglePlayServicesUtil.getErrorDialog(status, this.getActivity(), 0).show();
            return false;
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        if(getActivity()!=null)
        if (ContextCompat.checkSelfPermission(getActivity().getApplicationContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                Location mLastLocation = location;
                if(mLastLocation!=null) {
                    marker.remove();
                    marker=mMap.addMarker(new MarkerOptions().position(new LatLng(mLastLocation.getLatitude(),mLastLocation.getLongitude())).title("موقعیت شما").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA)));

                    TextView  viewById = (TextView)getView().findViewById(R.id.latlongLocation);
                    viewById.setText(mLastLocation.getLatitude()+" "+ mLastLocation.getLongitude());

                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()), 14));
                    progressDialog.dismiss();
                }

            }



        //stop location updates
        if (mGoogleApiClient != null) {
            if(mGoogleApiClient.isConnected())

                LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }

    }
    ProgressDialog progressDialog ;

    private class GetMyLocation extends AsyncTask<Void, Void, Void> {
        ConnectionResult connectionResult;

        @Override
        protected Void doInBackground(Void... voids) {
//            mGoogleApiClient.connect();
            connectionResult = mGoogleApiClient.blockingConnect();

//            onConnected(null);
            return null;        }

        @Override
        protected void onPreExecute() {

            progressDialog.setMessage("در حال جستجوی مکان فعلی شما");
            progressDialog.setCancelable(true);
            progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    // actually could set running = false; right here, but I'll
                    // stick to contract.
                    cancel(true);
                }
            });
            progressDialog.show();
            super.onPreExecute();
        }


        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (connectionResult.getErrorCode() == ConnectionResult.SUCCESS) {
                if (ContextCompat.checkSelfPermission(getActivity().getApplicationContext(),
                        Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {
                    Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                    if(mLastLocation!=null) {
                        marker.remove();
                        marker=mMap.addMarker(new MarkerOptions().position(new LatLng(mLastLocation.getLatitude(),mLastLocation.getLongitude())).title("موقعیت شما").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA)));

                        TextView  viewById = (TextView)getView().findViewById(R.id.latlongLocation);
                        viewById.setText(mLastLocation.getLatitude()+" "+ mLastLocation.getLongitude());

                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()), 14));

                    }

                }
            }
         }


    }
}
