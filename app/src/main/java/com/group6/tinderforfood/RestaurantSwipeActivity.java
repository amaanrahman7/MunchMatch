package com.group6.tinderforfood;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import android.content.SharedPreferences;

import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;

import android.support.constraint.ConstraintLayout;

import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.yelp.fusion.client.connection.YelpFusionApi;
import com.yelp.fusion.client.connection.YelpFusionApiFactory;
import com.yelp.fusion.client.models.ApiKey;
import com.yelp.fusion.client.models.Business;
import com.yelp.fusion.client.models.Coordinates;
import com.yelp.fusion.client.models.SearchResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Request.Builder;
import retrofit2.Call;
import retrofit2.Response;

public class RestaurantSwipeActivity extends AppCompatActivity {

    TextView mRestaurantTitle, mRating;
    ImageView restaurantImage;
    YelpFusionApiFactory apiFactory;
    YelpFusionApi yelpFusionApi;
    Map<String, String> mParams;
    OkHttpClient mClient;
    List<Restaurant> mRestaurants;
    int i, iLast;
    ProgressBar mLoading;
    boolean waiting = false;
    int rCount = 40;
    private final int MY_PERMISSIONS_REQUEST_FINE_LOCATION = 666;
    double mLongitude, mLatitude;
    Coordinates mCoordinate;


    public static String MY_PREFS = "MY_PREFS";
    private SharedPreferences mySharedPreferences;
    int prefMode = MODE_PRIVATE;

    //These are the shared settings that we gather from SharedPreferences
    String string1; //price
    String string2; //radius
    String string3; //diet
    String string4; //food category




    //NAVBAR VARS
    private DrawerLayout dl;
    private ActionBarDrawerToggle abdt;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.restaurant_swipe);
        ImageButton nobutton = findViewById(R.id.nobutton);
        ImageButton yesbutton = findViewById(R.id.yesbutton);

        //NAVBAR CODE

        dl = (DrawerLayout)findViewById(R.id.dl);
        abdt = new ActionBarDrawerToggle(this,dl,R.string.Open,R.string.Close);
        abdt.setDrawerIndicatorEnabled(true);

        dl.addDrawerListener(abdt);
        abdt.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        final NavigationView nav_view = (NavigationView)findViewById(R.id.nav_view);

        nav_view.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener()
        {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item){
                int id = item.getItemId();

                if(id == R.id.settings){
                    Toast.makeText(RestaurantSwipeActivity.this, "Settings",Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(RestaurantSwipeActivity.this, SettingsActivity.class);
                    startActivity(intent);
                } else if(id == R.id.refresh){
                    Toast.makeText(RestaurantSwipeActivity.this, "Refreshing...",Toast.LENGTH_SHORT).show();
                    finish();
                    startActivity(getIntent());
                }

                return true;
            }
        });


        //END OF NAVBAR


        mClient = new OkHttpClient();
        mRestaurantTitle = (TextView) findViewById(R.id.restaurantTitle);
        mRating = (TextView) findViewById(R.id.bottomText);
        restaurantImage = (ImageView) findViewById(R.id.restaurantImage);
        mLoading = (ProgressBar) findViewById(R.id.loading);
        mRestaurants = new ArrayList<>();
        i = 0;
        iLast = 0;

        nobutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newRestaurant();
            }
        });
        yesbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lastRestaurant();
            }
        });

        restaurantImage.setOnTouchListener(new OnSwipeTouchListener(this) {
            public void onSwipeTop() {
                sameRestaurantNewPic();
            }

            public void onSwipeRight() {
                lastRestaurant();
            }

            public void onSwipeLeft() {
                newRestaurant();
            }

            public void onSwipeBottom() {
                sameRestaurantPrevPic();
            }
        });


        // String apiKey = (String.valueOf(R.string.apiKey));//"hGAW2FySQrZqdHTxFT4s_fY-4OErTolDk-jyWn9r_6GKi0VCBw_mVcJuqidHQgNkfTSid0Rb4CS5pqrr2AoApLauOJUKalIig1V7Ye6aI2eMalROQzZcPTpiy5PAXHYx";
        apiFactory = new YelpFusionApiFactory();


        try {
            yelpFusionApi = apiFactory.createAPI("hGAW2FySQrZqdHTxFT4s_fY-4OErTolDk-jyWn9r_6GKi0VCBw_mVcJuqidHQgNkfTSid0Rb4CS5pqrr2AoApLauOJUKalIig1V7Ye6aI2eMalROQzZcPTpiy5PAXHYx");
        } catch (IOException e) {
            e.printStackTrace();
        }

        mParams = new HashMap<>();
        mParams.put("term", "restaurants");

        mParams.put("limit", "40");

        mParams = addPreferences(mParams,mySharedPreferences);



        LocationManager lm = (LocationManager) getSystemService((Context.LOCATION_SERVICE));
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_FINE_LOCATION);
            return;
        }
        Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);


        mParams.put("latitude", 33.7523+"");
        mParams.put("longitude", -84.3234+ "");

       // mParams.put("latitude", location.getLatitude()+"");
       // mParams.put("longitude", location.getLongitude()+ "");

        //33.7523
        //-84.3234
        new FetchPictures().execute("0");
        waitForRestaurant(true);

    }

    public Map addPreferences(Map<String,String> mParams, SharedPreferences mySharedPreferences){

        mySharedPreferences = PreferenceManager.getDefaultSharedPreferences(this); //this gets the sharedpreferences xml
        string1 = mySharedPreferences.getString("Price", ""); //this pulls data from each category
        string2 = mySharedPreferences.getString("Radius", "");
        string3 = mySharedPreferences.getString("Diet", "");
        string4 = mySharedPreferences.getString("Category","");

        System.out.println(string1);

        if(string1 != ""){
            //if the result isn't empty (the default value when we try to pull from sharedpreferences) then it fills the hashmap with the chosen option
            if(string1 == "0"){
                mParams.put("price","1,2,3,4");
            } else {
                mParams.put("price", string1);
            }
        }
        if(string2 != ""){
            mParams.put("radius",string2);
        }
        if(string3.equals("Vegan")){
            mParams.put("attributes","Vegan");
        } else if(string3.equals("Vegetarian")){
            mParams.put("attributes","Vegetarian");
        } else{
            mParams.remove("attributes");
        }

        if(string4 != ""){
            String diet = mParams.get("attributes");
            if (string3.equals("Meat-Eater")){
                diet = "";
            } else{
                diet = diet + ", ";
            }

            if (string4.equals("chinese")) {
                mParams.put("term",diet + "chinese");
            }
            else if (string4.equals("pizza")){
                mParams.put("term",diet + "pizza");
            }
            else if (string4.equals("barbeque")){
                mParams.put("term",diet + "bbq");
            }else if (string4.equals("sushi")){
                mParams.put("term",diet + "sushi");
            }else if (string4.equals("indian")){
                mParams.put("term",diet + "indian");
            }else if (string4.equals("vegetarian specialty")){
                mParams.put("term",diet + "vegetarian");
            }
        }
        return mParams;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        return abdt.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }


    public void initLocation() {
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
           // mLatitude = 33.7632;
            //mLongitude = 0;
            new FetchPictures().execute("0");
        } else {
            Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (location != null) {
                mLongitude = location.getLongitude();
                mLatitude = location.getLatitude();
                new FetchPictures().execute("0");
            } else {
                Toast.makeText(this, "Getting location...", Toast.LENGTH_SHORT).show();
                Criteria criteria = new Criteria();
                criteria.setAccuracy(Criteria.ACCURACY_FINE);
                criteria.setPowerRequirement(Criteria.POWER_HIGH);
                lm.requestSingleUpdate(criteria, new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        mCoordinate = new Coordinates();
                        mCoordinate.setLongitude(location.getLongitude());
                        mCoordinate.setLatitude(location.getLatitude());

                        new FetchPictures().execute("0");
                    }

                    @Override
                    public void onStatusChanged(String s, int i, Bundle bundle) {
                        Toast.makeText(RestaurantSwipeActivity.this, "GPS needed", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onProviderEnabled(String s) {
                        Toast.makeText(RestaurantSwipeActivity.this, "GPS needed", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onProviderDisabled(String s) {
                        Toast.makeText(RestaurantSwipeActivity.this, "GPS needed", Toast.LENGTH_SHORT).show();
                    }
                }, null);
            }
        }
        Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        mCoordinate = new Coordinates();
        mCoordinate.setLongitude(location.getLongitude());
        mCoordinate.setLatitude(location.getLatitude());
       //// ADD COORDINATES IF NECCESARY
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this, "Setting San Francisco as default location", Toast.LENGTH_SHORT).show();
                }
                initLocation();
                waitForRestaurant(true);
                return;
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }



    private void sameRestaurantPrevPic() {
        Restaurant r = mRestaurants.get(i);
        if (r.getCurrPic() > 0) {
            r.decCurrPic();
            waitForRestaurant(true);

        }
    }

    private void sameRestaurantNewPic() {
        Restaurant r = mRestaurants.get(i);
        if (r.getPictures().size() - 1 > r.getCurrPic()) {
            r.incCurrPic();
            waitForRestaurant(true);
            if(r.getCurrPic() - r.getiLast() > 5 && r.getPictures().size() - r.getCurrPic() < 7 ) {
                r.setiLast(r.getCurrPic());
            }
        }

    }

    private void lastRestaurant() {
        /*if(i >= 0 ) {
            i--;
            waitForRestaurant(true);

        }
        */
        Restaurant r = mRestaurants.get(i);
        String url = r.getMainUrl();
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }
    private void newRestaurant() {
        if(mRestaurants.size()-1 > i) {
            i++;
            waitForRestaurant(true);
            if (i - iLast > 5 && mRestaurants.size() - i < 7) {
                new FetchPictures().execute("" + rCount);
                rCount+= 40;
            }
        }
    }
   

    synchronized public void waitForRestaurant(boolean client) {
        if(client) {
            if(mRestaurants.size() > i &&
                    mRestaurants.get(i).getPictures().size() > mRestaurants.get(i).getCurrPic()) {
                // have data
                restaurantCallback();
            }else{
                waiting = true;
                mLoading.setVisibility(View.VISIBLE);
            }
        }else{
            if(waiting) {
                restaurantCallback();
                waiting = false;
                mLoading.setVisibility(View.INVISIBLE);
            }
        }
    }


    public void restaurantCallback() {
        displayRestaurant(mRestaurants.get(i));
    }

    public void displayRestaurant(Restaurant r) {
        Picasso.get().load(r.getPictures().get(r.getCurrPic())).into(restaurantImage);
        mRestaurantTitle.setText(r.getName());
        mRating.setText(r.getRating());
    }

     class FetchPictures extends AsyncTask<String, Restaurant, String> {

        List<Restaurant>  restaurants;
         @Override
         protected void onProgressUpdate(Restaurant... values) {
             super.onProgressUpdate(values);
             if (values != null) {
                 mRestaurants.add(values[0]);
                 waitForRestaurant(false);
             } else {
                 Toast.makeText(RestaurantSwipeActivity.this, "No data available for your location", Toast.LENGTH_SHORT).show();
             }
         }
        @Override
        protected String doInBackground(String... params) {
            mParams.put("offset", params[0]);

            Call<SearchResponse> call = yelpFusionApi.getBusinessSearch(mParams);
            Response<SearchResponse> response = null;
            try {
                System.out.println("************************************************");
                response = call.execute();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            if(response != null) {
                System.out.println("NOTTTT NULLOLLLLLLLLLLLLLLLLL");
                List<Business> businessList = new ArrayList<>();
                businessList= response.body().getBusinesses();

                restaurants = new ArrayList<>();
                Restaurant r;
                int i = 0;
                for (Business b : businessList) {
                    r = new Restaurant(b.getName(), b.getUrl());
                    r.setRating(b.getRating()+"");
                    restaurants.add(r);
                    fetchPictures(r, i);
                    i++;
                }
            }else{

                System.out.println("NNNNNUUUUUUUUUUUUUUUUULLLLLLLLLLLLLLLLLLLLL");
            }
            return null;
        }

        private void fetchPictures(Restaurant r, final int pos) {


                Request request = new Builder()
                        .url(r.getPicUrl())
                        .build();

                mClient.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(okhttp3.Call call, IOException e) {

                    }

                    @Override
                    public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {

                        List<String> pictures = RestaurantParser.getPictures(response.body().string());
                        if (pictures.size() > 0 ) {
                            restaurants.get(pos).setPictures(pictures);
                            publishProgress(restaurants.get(pos));
                        }

                    }
                }); {

                }


        }
    }
}
