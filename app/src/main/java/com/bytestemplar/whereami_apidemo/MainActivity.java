package com.bytestemplar.whereami_apidemo;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, LocationListener
{

    private GoogleMap mMap = null;
    private LocationManager _locman;
    private String          _provider;
    private TextView        _location_label;

    @Override
    protected void onCreate( Bundle savedInstanceState )
    {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );

        _location_label = (TextView) findViewById( R.id.tvLocation );
        Toolbar toolbar = (Toolbar) findViewById( R.id.toolbar );
        setSupportActionBar( toolbar );

        FloatingActionButton fab = (FloatingActionButton) findViewById( R.id.fab );
        fab.setOnClickListener( new View.OnClickListener()
        {
            @Override
            public void onClick( View view )
            {
                Log.i( "BT", "Trying to get location" );
                Snackbar.make( view, "This DID do something, at one point.", Snackbar.LENGTH_LONG ).setAction( "Action", null ).show();
                //onLocationChanged( _locman.getLastKnownLocation( _provider ) );
            }
        } );

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById( R.id.map );
        mapFragment.getMapAsync( this );

        setupGPS();
    }

    private void setupGPS()
    {

        _locman = (LocationManager) getSystemService( Context.LOCATION_SERVICE );
        if ( !_locman.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
            // TODO: AlertDialog
            Intent i = new Intent( Settings.ACTION_LOCATION_SOURCE_SETTINGS );
            startActivity( i );
        }

        Criteria crit = new Criteria();
        _provider = _locman.getBestProvider( crit, false );
        if ( ActivityCompat.checkSelfPermission( this, Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED &&
             ActivityCompat.checkSelfPermission( this, Manifest.permission.ACCESS_COARSE_LOCATION ) != PackageManager.PERMISSION_GRANTED ) {

            ActivityCompat.requestPermissions( this, new String[] { Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION }, 100 );
            return;
        }

        startGPS();
    }

    public void onRequestPermissionsResult( int requestCode, String[] permissions, int[] grantResults )
    {
        startGPS();
    }

    private void startGPS()
    {
        _locman.requestLocationUpdates( _provider, 5000, 0, this );
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
    public void onMapReady( GoogleMap googleMap )
    {
        mMap = googleMap;
    }

    @Override
    public void onLocationChanged( Location location )
    {
        //Log.i( "BT", "Location changed: " + location.getLatitude() + " - " + location.getLongitude() );

        if ( mMap != null ) {
            LatLng newpos = new LatLng( location.getLatitude(), location.getLongitude() );
            mMap.animateCamera( CameraUpdateFactory.newLatLngZoom( newpos, 15.0f ) );

            GeocodingRequest.makeRequest( location, new GeocodingRequest.GeocodingResponseHandler()
            {
                @Override
                public void onGeocodingResponse( final String response )
                {
                    runOnUiThread( new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            _location_label.setText( response );
                            mMap.clear();
                            mMap.addMarker( new MarkerOptions()
                                                    .position( newpos )
                                                    .title( response )
                                                    .icon( BitmapDescriptorFactory.defaultMarker( BitmapDescriptorFactory.HUE_AZURE ) ) );
                        }
                    } );

                }
            } );
        }
    }

    @Override
    public void onStatusChanged( String provider, int status, Bundle extras )
    {

    }

    @Override
    public void onProviderEnabled( String provider )
    {

    }

    @Override
    public void onProviderDisabled( String provider )
    {

    }
}
