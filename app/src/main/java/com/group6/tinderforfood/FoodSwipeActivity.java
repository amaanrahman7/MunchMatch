package com.group6.tinderforfood;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class FoodSwipeActivity extends AppCompatActivity {

    //SharedPreferences variables
    public static String MY_PREFS = "MY_PREFS";
    private SharedPreferences preferences;
    int prefMode = MODE_PRIVATE;

    //FoodSwipe specific variables
    List<FoodCategory> categories;
    FoodCategory[] tempcategories;
    ImageView foodImage;
    TextView foodTitle;
    ProgressBar mLoading;
    int i,iLast;

    //NAVBAR VARS
    private DrawerLayout dl;
    private ActionBarDrawerToggle abdt;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.food_swipe);

        //NAVBAR CODE

        dl = (DrawerLayout) findViewById(R.id.dl);
        abdt = new ActionBarDrawerToggle(this, dl, R.string.Open, R.string.Close);
        abdt.setDrawerIndicatorEnabled(true);

        dl.addDrawerListener(abdt);
        abdt.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        final NavigationView nav_view = (NavigationView) findViewById(R.id.nav_view);

        nav_view.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                if (id == R.id.settings) {
                    Toast.makeText(FoodSwipeActivity.this, "Settings", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(FoodSwipeActivity.this, SettingsActivity.class);
                    startActivity(intent);
                } else if (id == R.id.refresh) {
                    Toast.makeText(FoodSwipeActivity.this, "Refreshing...", Toast.LENGTH_SHORT).show();
                    finish();
                    startActivity(getIntent());
                }

                return true;
            }
        });

        //END OF NAVBAR

        //grab preferences file
        preferences = getSharedPreferences(MY_PREFS, prefMode);

        //Swipe logic
        foodTitle = (TextView) findViewById(R.id.foodTitle);
        //mRating = (TextView) findViewById(R.id.bottomText); //not using bottomText for now
        foodImage = (ImageView) findViewById(R.id.foodImage);
        mLoading = (ProgressBar) findViewById(R.id.loading);
        TextView swipenote = findViewById(R.id.swipenote);
        categories = new ArrayList<>();
        i=0;
        iLast = 0;

        //GENERATE FOOD CATEGORIES
        tempcategories = populateCategories();
        //ADD TO ARRAYLIST RANDOMLY
        randomizeList(categories, tempcategories);

        foodImage.setOnTouchListener(new OnSwipeTouchListener(this) { //swipe listener
            public void onSwipeTop() {
            }
            public void onSwipeRight() {
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(FoodSwipeActivity.this);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("Category",categories.get(i).getName());
                editor.commit();

               // Toast.makeText(MainActivity.this, "Settings",Toast.LENGTH_SHORT).show(); //use this if you want to display a message after swiping on a category
                Intent intent = new Intent(FoodSwipeActivity.this, RestaurantSwipeActivity.class);
                finish();
                startActivity(intent);
            }
            public void onSwipeLeft() {
                nextCategory();
                swipenote.setVisibility(View.INVISIBLE);

            }

            public void onSwipeBottom() {
            }
        });

    }

    private FoodCategory[] populateCategories(){
        FoodCategory[] meatcategories = new FoodCategory[]{ //these will be all of the food categories
                new FoodCategory("Pizza"),
                new FoodCategory("Chinese"),
                new FoodCategory("Barbeque"),
                new FoodCategory("Sushi"),
                new FoodCategory("Indian"),
        };
        FoodCategory[] vegcategories = new FoodCategory[]{ //for vegetarians and vegans
                new FoodCategory("Pizza"),
                new FoodCategory("Indian"),
                new FoodCategory("Vegetarian Specialty"),
        };

        SharedPreferences mySharedPreferences = PreferenceManager.getDefaultSharedPreferences(FoodSwipeActivity.this); //this gets the sharedpreferences xml
        String string1 = mySharedPreferences.getString("Diet", ""); //this pulls data from each category

        if(string1.equals("Meat-Eater")) {
            return meatcategories;
        } else{
            return vegcategories;
        }
    }

    private void randomizeList(List<FoodCategory> categories, FoodCategory[] tempcategories){
        Random generator = new Random();
        for(int i = 0; i < 100; i++){ //randomize it however many times. right now it's 100
            int j = generator.nextInt(tempcategories.length); // this will give us 0,1,2,or 3 and etc depending on how many categories there are
            categories.add(tempcategories[j]);
            // etc.. continues with 3 other else if statements and objects
        }
    }

    private void nextCategory() {
        if(categories.size()-1 > i) {
            i++;
            changeCategory(true);
            /*
            if (i - iLast > 5 && categories.size() - i < 7) {
                new FetchPictures().execute("" + rCount);
                rCount+= 40;
            }
            */
        } else{
            i = 0;
            changeCategory(true);
        }
    }

    synchronized public void changeCategory(boolean client) {
        if(client) {
            if(categories.size() > i ) {
                // have data
                categoryCallback();
            }else{
                //waiting = true;
                mLoading.setVisibility(View.INVISIBLE);
            }
        }else{
           // if(waiting) {
                categoryCallback();
               // waiting = false;
                mLoading.setVisibility(View.INVISIBLE);
           // }
        }
    }
    public void categoryCallback() {
        displayCategory(categories.get(i));
    }
    public void displayCategory(FoodCategory r) {
        foodImage.setImageResource(r.getPicId());
        foodTitle.setText(r.getName());
        //mRating.setText(r.getRating()); //this changes bottomText when a new category is displayed
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        return abdt.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }
}
