package com.safisoft.fakelocation_duckgps;

import android.app.Activity;
import android.app.Dialog;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class User_Guide extends Dialog implements
        View.OnClickListener {

    public Activity c;
    public Dialog d;
    public ImageButton btn_go_settings ,btn_go_dev ;
    public ImageButton go, del,ex;
    public TextView Latitude_txv , Longitude_txv,textv_showselctedName,textv_showselctedLatLong ;
    public EditText location_name_edtx ;
    public LinearLayout layout_locname ,liner_selected_data;
    public ListView simpleList ;
    public Cursor cursor = null;
    DbConnction myDbHelper;
    ArrayList<String> all_saved_locations = new ArrayList<String>();


    public User_Guide(Activity a) {
        super(a);
        // TODO Auto-generated constructor stub
        this.c = a;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.user_guide);
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

        ex.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }



    public void review (){

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



    public void disms(){
        dismiss();
    }

}