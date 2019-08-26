package com.chihab_eddine98.eatit_admin.controllers;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;

import com.chihab_eddine98.eatit_admin.common.Common;
import com.chihab_eddine98.eatit_admin.common.DirectionJSONParser;
import com.chihab_eddine98.eatit_admin.remote.IGeoCoordinates;
import com.google.android.gms.location.LocationListener;

import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Toast;

import com.chihab_eddine98.eatit_admin.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TrackOrder extends FragmentActivity
        implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private GoogleMap mMap;
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;
    private final static int LOCATION_PERMISSION_REQUEST = 1001;

    private Location mLastLocation;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mlocationRequest;
    private static int UPDATE_INTERVAL = 1000;
    private static int FATEST_INTERVAL = 5000;
    private static int DISPLACEMENT = 10;


    private IGeoCoordinates mService;


    private boolean hasNotPermissionForLocation() {
        boolean hasPermissionForLocation = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED;
        boolean hasPermissionForCoarse = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED;

        return hasPermissionForLocation && hasPermissionForCoarse;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_order);

        mService= Common.getGeoCodeService();

        if (hasNotPermissionForLocation()) {
            showPermissionDialog();
        } else {
            if (checkPlayServices()) {
                buildGoogleApiClient();
                createLocationRequest();
            }
        }

        displayLocation();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @SuppressLint("MissingPermission")
    private void displayLocation() {

        if (hasNotPermissionForLocation()) {
            showPermissionDialog();
        }

        // Draw our data in map
        else {

            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

            if(mLastLocation!=null)
            {
                double lat=mLastLocation.getLatitude();
                double lng=mLastLocation.getLongitude();

                // Ajouter un marker pour l'emplacement du domicile && zommer la caméra
                LatLng urLocation=new LatLng(lat,lng);

                mMap.addMarker(new MarkerOptions().position(urLocation).title(" Votre Position"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(urLocation));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(17.0f));

                // Ajouter Marker pour la commande et la direction
                drawTrackOrder(urLocation,Common.currentOrder.getAdresse());
            }
            else
            {
                Toast.makeText(this," Problème lors de l'obtention de votre Position !",Toast.LENGTH_SHORT).show();
            }

        }

    }

    // Cette méthode nous place un marker pour la commande en cours
    // et nous montre aussi la route ( directions )
    private void drawTrackOrder(final LatLng urLocation, final String orderAdresse)
    {
        String k="AIzaSyBdt7ULkf0VFi3dMwBETPLypun5r6WbyvY";
        mService.getGeoCode(orderAdresse,k).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {

                try {

                    JSONObject jsonObject=new JSONObject(response.body().toString());

                    // On convert notre adress ( de la commande ) en lat et longitude ( coordonnées )

                    String lat=((JSONArray) jsonObject.get("results"))
                                .getJSONObject(0)
                                .getJSONObject("geometry")
                                .getJSONObject("location")
                                .get("lat").toString();

                    String lng=((JSONArray)jsonObject.get("results"))
                            .getJSONObject(0)
                            .getJSONObject("geometry")
                            .getJSONObject("location")
                            .get("lng").toString();



                    LatLng orderLocation=new LatLng(Double.parseDouble(lat),Double.parseDouble(lng));
                    Bitmap bitmap= BitmapFactory.decodeResource(getResources(),R.drawable.order_tracking_marker);

                    bitmap=Common.scaleBitMap(bitmap,200,200);

                    MarkerOptions orderMarker=new MarkerOptions().icon(BitmapDescriptorFactory.fromBitmap(bitmap))
                                                                 .title(" Commande de #"+Common.currentOrder.getNom())
                                                                 .position(orderLocation);


                    mMap.addMarker(orderMarker);


                    // Ajouter la direction

                    String k="AIzaSyBdt7ULkf0VFi3dMwBETPLypun5r6WbyvY";

                    mService.getDirections(urLocation.latitude+","+urLocation.longitude,
                                            orderLocation.latitude+","+orderLocation.longitude,
                            k).enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {
                            new ParserTask().execute(response.body().toString());
                        }

                        @Override
                        public void onFailure(Call<String> call, Throwable t) {

                        }
                    });

                }catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });

    }


    // Start GoogleApi And Location Requests
    protected synchronized void buildGoogleApiClient() {

        mGoogleApiClient=new GoogleApiClient.Builder(this)
                             .addConnectionCallbacks(this)
                             .addOnConnectionFailedListener(this)
                             .addApi(LocationServices.API).build();

        mGoogleApiClient.connect();


    }

    private void createLocationRequest() {

        mlocationRequest=new LocationRequest();

        mlocationRequest.setInterval(UPDATE_INTERVAL);
        mlocationRequest.setFastestInterval(FATEST_INTERVAL);
        mlocationRequest.setSmallestDisplacement(DISPLACEMENT);
        mlocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

    }


    // Permission For share position
    private void showPermissionDialog() {

        ActivityCompat.requestPermissions(this,new String[]{
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
        },LOCATION_PERMISSION_REQUEST);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode)
        {
            case LOCATION_PERMISSION_REQUEST:

                if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED)
                {
                    if(checkPlayServices())
                    {
                        buildGoogleApiClient();
                        createLocationRequest();

                        displayLocation();
                    }
                }

                break;
        }

    }

    private boolean checkPlayServices() {

        int resultCode= GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);

        if (resultCode!=ConnectionResult.SUCCESS)
        {
            if(GooglePlayServicesUtil.isUserRecoverableError(resultCode))
            {
                GooglePlayServicesUtil.getErrorDialog(resultCode,this,PLAY_SERVICES_RESOLUTION_REQUEST).show();
            }
            else
            {
                Toast.makeText(this," Votre version Android n'est pas supportée ",Toast.LENGTH_SHORT).show();
            }

            return false;

        }

        return true;



    }

    //-----------------------------------------
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }

    @Override
    public void onLocationChanged(Location location) {

        mLastLocation=location;
        displayLocation();
    }



    @Override
    public void onConnected(@Nullable Bundle bundle)
    {
        displayLocation();
        startLocationUpdates();

    }

    @Override
    protected void onResume() {
        super.onResume();

        checkPlayServices();

    }

    @Override
    protected void onStart() {
        super.onStart();

        if (mGoogleApiClient!=null)
        {
            mGoogleApiClient.connect();
        }

    }

    @SuppressLint("MissingPermission")
    private void startLocationUpdates() {

        if (hasNotPermissionForLocation())
        {
            return;
        }

        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,mlocationRequest,this);
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private class ParserTask extends AsyncTask<String,Integer, List<List<HashMap<String,String>>>> {

        ProgressDialog dialog=new ProgressDialog(TrackOrder.this);



        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.setMessage("Recherche du chemin du livreur...");
            dialog.show();
        }

        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... strings) {


            JSONObject jsonObject;
            List<List<HashMap<String, String>>> routes=null;

            try {
                jsonObject=new JSONObject(strings[0]);
                DirectionJSONParser parser=new DirectionJSONParser();
                routes=parser.parse(jsonObject);

            } catch (JSONException e) {
                e.printStackTrace();
            }
            return routes;
        }


        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> lists) {
            super.onPostExecute(lists);
            dialog.dismiss();

            ArrayList points=null;
            PolylineOptions lineOptions=null;


            for (int i=0;i<lists.size();i++)
            {
                points=new ArrayList();
                lineOptions=new PolylineOptions();

                List<HashMap<String, String>> path=lists.get(i);

                for (int j=0;j<path.size();j++)
                {
                    HashMap<String, String> point=path.get(j);

                    double lat=Double.parseDouble(point.get("lat"));
                    double lng=Double.parseDouble(point.get("lng"));

                    LatLng position=new LatLng(lat,lng);

                    points.add(position);

                }

                lineOptions.addAll(points);
                lineOptions.width(12);
                lineOptions.color(Color.GREEN);
                lineOptions.geodesic(true);

            }

            mMap.addPolyline(lineOptions);


        }
    }
}

