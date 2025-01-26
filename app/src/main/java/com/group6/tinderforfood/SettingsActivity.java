package com.group6.tinderforfood;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import static java.lang.Integer.valueOf;

public class SettingsActivity extends AppCompatActivity {

    private SeekBar pricebar;
    private SeekBar radiusbar;
    private TextView price;
    private Button pricereset;
    private TextView radius;
    private RadioGroup dietgroup; //this represents the entire radiogroup
    private RadioButton dietbutton; //this is going to store the value of the currently checked radiobutton
    public static String MY_PREFS = "MY_PREFS";
    private SharedPreferences preferences;
    int prefMode = MODE_PRIVATE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settingslayout);

        assert getSupportActionBar() != null;   //null check
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);   //show back button

        preferences = getSharedPreferences(MY_PREFS, prefMode);

        pricebar = (SeekBar) findViewById(R.id.seekBar2);

        radiusbar = (SeekBar) findViewById(R.id.seekBar3);

        price = (TextView) findViewById(R.id.textView7);

        radius = (TextView) findViewById(R.id.editText2);

        pricebar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){
            @Override
            public void onStopTrackingTouch(SeekBar seekBar){

            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar){

            }
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser){
                if(valueOf(progress)==0){
                    price.setText("$");
                } else if(valueOf(progress)==1){
                    price.setText("$$");
                } else if(valueOf(progress)==2){
                    price.setText("$$$");
                } else if(valueOf(progress)==3){
                    price.setText("$$$$");
                }
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(SettingsActivity.this);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("Price",toString().valueOf(progress+1));
                editor.commit();

            }
        });

        pricereset = (Button) findViewById(R.id.button8);

        pricereset.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                pricebar.setProgress(0);
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(SettingsActivity.this);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("Price","0");
                editor.commit();
            }

        });

        radiusbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){
            @Override
            public void onStopTrackingTouch(SeekBar seekBar){ //i'm not sure what these 2 methods are supposed to do, but i didn't have to change them.

            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar){

            }
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser){
                radius.setText(String.valueOf(progress+1)); //this changes the value in the textview so that it displays the seekbar value as it updates

                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(SettingsActivity.this); //this gets the sharedpreferences across the app
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("Radius",toString().valueOf((progress+1)*1609)); //this is what you push to the sharedpreferences
                editor.commit(); //this completes the action
            }
        });

        // This will get the radiogroup
        dietgroup = (RadioGroup)findViewById(R.id.radioGroup1);
        // This will get the radiobutton in the radiogroup that is checked
        dietbutton = (RadioButton)dietgroup.findViewById(dietgroup.getCheckedRadioButtonId());

        // This overrides the radiogroup onCheckListener
        dietgroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            public void onCheckedChanged(RadioGroup group, int checkedId)
            {
                // This will get the radiobutton that has changed in its check state
                RadioButton dietbutton = (RadioButton)group.findViewById(checkedId);
                // This puts the value (true/false) into the variable
                boolean isChecked = dietbutton.isChecked();
                // If the radiobutton that has changed in check state is now checked...
                if (isChecked)
                {
                }

                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(SettingsActivity.this);
                SharedPreferences.Editor editor = preferences.edit();
                if((String)dietbutton.getText()=="Meat-Eater"){
                    editor.remove("Diet");
                }else {
                    editor.putString("Diet", (String) dietbutton.getText());
                }
                editor.commit();
            }
        });
    }
    @Override
    public boolean onSupportNavigateUp(){ //this changes the back button function

        finish(); //this finishes current activity, making the app go back to the previous activity
        return true;
    }
}
