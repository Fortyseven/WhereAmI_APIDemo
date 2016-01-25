package com.bytestemplar.whereami_apidemo;

import android.location.Location;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Fortyseven on 2016-01-24.
 */
public class GeocodingRequest
{
    private final       String    GM_API_KEY = "AIzaSyCMOf7KXK3BgVT_rXPiFuNpF8aSuyog5C0";
    public static final MediaType JSON       = MediaType.parse( "application/json; charset=utf-8" );

    public interface GeocodingResponseHandler
    {
        void onGeocodingResponse( String response );
    }

    public void makeRequest( Location loc, final GeocodingResponseHandler callback )
    {
        OkHttpClient client       = new OkHttpClient();
        RequestBody  request_body = RequestBody.create( JSON, "" );

        String url = "https://maps.googleapis.com/maps/api/geocode/json?key=" + GM_API_KEY + "&latlng=" + loc.getLatitude() + "," + loc.getLongitude();
        Log.i( "BT", "URL: " + url );
        Request request = new Request.Builder()
                .url( url )
                .post( request_body )
                .build();


        client.newCall( request ).enqueue( new Callback()
        {
            @Override
            public void onFailure( Call call, IOException e )
            {
                Log.e( "BT", "ERR: " + e.getMessage() );
            }

            @Override
            public void onResponse( Call call, Response response ) throws IOException
            {
                try {
                    callback.onGeocodingResponse( parseJSONResponse( response.body().string() ) );
                    Log.i( "BT", "Response: " + response );
                }
                catch ( Exception e ) {
                    Log.e( "BT", "ERR JSON: " + e.getMessage() );
                    callback.onGeocodingResponse( e.getMessage() );
                }
            }
        } );
    }

    private String parseJSONResponse( String json ) throws JSONException
    {
        JSONObject root    = new JSONObject( json );
        JSONArray  results = root.getJSONArray( "results" );
        JSONObject first   = results.getJSONObject( 0 );
        return first.getString( "formatted_address" );
    }
}
