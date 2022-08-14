package com.safisoft.fakelocation_duckgps;

import android.app.Activity;
import android.app.Dialog;
import android.database.Cursor;
import android.database.SQLException;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.IOException;

public class SettingsDialog extends Dialog implements
        View.OnClickListener {

    public Activity c;
    public Dialog d;
    public ImageButton btn_go_settings ,btn_go_dev ;
    public ImageButton go, del,ex;
    public TextView Latitude_txv , Longitude_txv,textv_showselctedName,textv_showselctedLatLong ;
    public EditText location_name_edtx ;
    public LinearLayout layout_locname ,liner_selected_data;
    public Cursor cursor = null;
    DbConnction myDbHelper;
    SeekBar seekBar ;



    public SettingsDialog(Activity a) {
        super(a);
        // TODO Auto-generated constructor stub
        this.c = a;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.settings_dialog);
        del = findViewById(R.id.btn_no);
        ex = findViewById(R.id.btn_ex);



        Latitude_txv = findViewById(R.id.Latitude_txv);
        Longitude_txv = findViewById(R.id.Longitude_txv);
        location_name_edtx= findViewById(R.id.location_name_edtx);
        liner_selected_data= findViewById(R.id.liner_selected_data);
        textv_showselctedName= findViewById(R.id.textv_showselctedName);
        textv_showselctedLatLong= findViewById(R.id.textv_showselctedLatLong);
        btn_go_settings = findViewById(R.id.btn_go_settings);
        btn_go_dev = findViewById(R.id.btn_go_dev);
        seekBar = findViewById(R.id.seekBar);


        myDbHelper = new DbConnction(getContext());
        try {
            myDbHelper.createDataBase();
        } catch (IOException ioe) {
            throw new Error("Unable to create database");
        }


        try {
            myDbHelper.openDataBase();
        } catch (SQLException sqle) {
            throw sqle;
        }



        // position of the seekbar when start settings dialog
        cursor = myDbHelper.query_user_data("settings_accuracy", null, null, null, null, null, null);
        cursor.moveToPosition(0);
        String s_accuracy = cursor.getString(1);
        if(s_accuracy.equals("3000")){seekBar.setProgress(0);}
        if(s_accuracy.equals("2000")){seekBar.setProgress(1);}
        if(s_accuracy.equals("1000")){seekBar.setProgress(2);}



        cursor.close();







// update accuracy val in database to read it in Gps_startup_Service class
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(progress == 0){
                    myDbHelper.updateRecord("settings_accuracy", "accuracy", "1","3000");

                }
                if(progress == 1){
                    myDbHelper.updateRecord("settings_accuracy", "accuracy", "1", "2000");

                }
                if(progress == 2){
                    myDbHelper.updateRecord("settings_accuracy", "accuracy", "1", "1000");

                }

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });




        ex.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });







    }






    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_yes:
                c.finish();
                break;
            case R.id.btn_ex:
                dismiss();
                break;
            default:
                break;
        }
        dismiss();
    }
}