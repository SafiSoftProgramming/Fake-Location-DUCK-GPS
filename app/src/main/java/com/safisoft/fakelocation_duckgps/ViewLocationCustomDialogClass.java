package com.safisoft.fakelocation_duckgps;

import android.app.Activity;
import android.app.Dialog;
import android.database.Cursor;
import android.database.SQLException;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;

public class ViewLocationCustomDialogClass extends Dialog implements
        View.OnClickListener {

    public Activity c;
    public Dialog d;
    public ImageButton go, del,ex;
    public TextView Latitude_txv , Longitude_txv,textv_showselctedName,textv_showselctedLatLong ;
    public EditText location_name_edtx ;
    public LinearLayout layout_locname ,liner_selected_data;
    public ListView simpleList ;
    public Cursor cursor = null;
    DbConnction myDbHelper;
    ArrayList<String> all_saved_locations = new ArrayList<String>();


    public ViewLocationCustomDialogClass(Activity a) {
        super(a);
        // TODO Auto-generated constructor stub
        this.c = a;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.view_dialog);
        go = (ImageButton) findViewById(R.id.btn_yes);
        del = (ImageButton) findViewById(R.id.btn_no);
        ex = (ImageButton) findViewById(R.id.btn_ex);
        go.setOnClickListener(this);
        del.setOnClickListener(this);

        Latitude_txv = (TextView)findViewById(R.id.Latitude_txv);
        Longitude_txv = (TextView)findViewById(R.id.Longitude_txv);
        location_name_edtx=(EditText)findViewById(R.id.location_name_edtx);
       // layout_locname =(LinearLayout)findViewById(R.id.layout_locname);
        liner_selected_data=(LinearLayout)findViewById(R.id.liner_selected_data);
        textv_showselctedName= (TextView)findViewById(R.id.textv_showselctedName);
        textv_showselctedLatLong= (TextView)findViewById(R.id.textv_showselctedLatLong);


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


        cursor = myDbHelper.query_user_data("saved_location", null, null, null, null, null, null);
        cursor.moveToPosition(0);

        int count = cursor.getCount();

        for (int i = 0 ; i < count ; i++) {
            String locations = cursor.getString(1);
            all_saved_locations.add(locations);
            cursor.moveToNext();
        }

        simpleList = (ListView)findViewById(R.id.ListView_locations);
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getContext(), R.layout.layout_onetext, R.id.textv_locationName,all_saved_locations);
        simpleList.setAdapter(arrayAdapter);



    }



    public void review (){
        all_saved_locations.clear();
        cursor = myDbHelper.query_user_data("saved_location", null, null, null, null, null, null);
        cursor.moveToPosition(0);

        int count = cursor.getCount();

        for (int i = 0 ; i < count ; i++) {
            String locations = cursor.getString(1);
            all_saved_locations.add(locations);
            cursor.moveToNext();
        }

        simpleList = (ListView)findViewById(R.id.ListView_locations);
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getContext(), R.layout.layout_onetext, R.id.textv_locationName,all_saved_locations);
        simpleList.setAdapter(arrayAdapter);

        liner_selected_data.setVisibility(View.GONE);
        textv_showselctedName.setText("");
        textv_showselctedLatLong.setText("");
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_yes:
                c.finish();
                break;
            case R.id.btn_no:
                dismiss();
                break;
            default:
                break;
        }
        dismiss();
    }
}