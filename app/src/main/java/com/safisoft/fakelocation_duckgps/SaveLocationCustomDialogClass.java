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
import android.widget.TextView;

import java.io.IOException;

public class SaveLocationCustomDialogClass extends Dialog implements
        android.view.View.OnClickListener {

    public Activity c;
    public Dialog d;
    public ImageButton yes, no;
    public TextView Latitude_txv , Longitude_txv ;
    public EditText location_name_edtx ;
    public LinearLayout layout_locname ;

    public Cursor cursor = null;
    DbConnction myDbHelper;

    public SaveLocationCustomDialogClass(Activity a) {
        super(a);
        // TODO Auto-generated constructor stub
        this.c = a;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.save_dialog);
        yes = (ImageButton) findViewById(R.id.btn_yes);
        no = (ImageButton) findViewById(R.id.btn_no);
        yes.setOnClickListener(this);
        no.setOnClickListener(this);

        Latitude_txv = (TextView)findViewById(R.id.Latitude_txv);
        Longitude_txv = (TextView)findViewById(R.id.Longitude_txv);
        location_name_edtx=(EditText)findViewById(R.id.location_name_edtx);
        layout_locname =(LinearLayout)findViewById(R.id.layout_locname);





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