package com.bytestemplar.whereami_apidemo;

import android.location.Location;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class GeocodingRequest
{
    private static final String GM_API_KEY = "AIzaSyCMOf7KXK3BgVT_rXPiFuNpF8aSuyog5C0";

    public interface GeocodingResponseHandler
    {
        void onGeocodingResponse( String response );
    }

    public static void makeRequest( Location loc, final GeocodingResponseHandler callback )
    {
        OkHttpClient client = new OkHttpClient();

        String url = "https://maps.googleapis.com/maps/api/geocode/json?key=" + GM_API_KEY + "&latlng=" + loc.getLatitude() + "," + loc.getLongitude();

        Request request = new Request.Builder()
                .url( url )
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

    private static String parseJSONResponse( String json ) throws JSONException
    {
        JSONObject root    = new JSONObject( json );
        JSONArray  results = root.getJSONArray( "results" );
        JSONObject first   = results.getJSONObject( 0 );
        return first.getString( "formatted_address" );
    }
}
