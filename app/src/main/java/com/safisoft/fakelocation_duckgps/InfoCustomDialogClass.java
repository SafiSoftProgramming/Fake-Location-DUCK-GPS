package com.safisoft.fakelocation_duckgps;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;

public class InfoCustomDialogClass extends Dialog implements
        View.OnClickListener {

    public Activity c;
    public Dialog d;
    public ImageButton yes, no;
    public TextView Latitude_txv , Longitude_txv ;
    public EditText location_name_edtx ;
    public LinearLayout layout_locname ;

    ImageButton btn_info_rateme , btn_info_apps , btn_info_facebook , btn_info_feesback , btn_info_permissions , btn_info_youtube ;


    public InfoCustomDialogClass(Activity a) {
        super(a);
        // TODO Auto-generated constructor stub
        this.c = a;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.info_dialog);
        no = findViewById(R.id.btn_no);
        no.setOnClickListener(this);


        btn_info_rateme = findViewById(R.id.btn_info_rateme);
        btn_info_apps = findViewById(R.id.btn_info_apps);
        btn_info_facebook = findViewById(R.id.btn_info_facebook);
        btn_info_feesback = findViewById(R.id.btn_info_feesback);
        btn_info_permissions = findViewById(R.id.btn_info_permissions);
        btn_info_youtube = findViewById(R.id.btn_info_youtube);



        btn_info_rateme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String appPackageName = getContext().getPackageName(); // getPackageName() from Context or Activity object
                try {
                    getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                } catch (android.content.ActivityNotFoundException anfe) {
                    getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                }

            }
        });
        btn_info_apps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/developer?id=SafiSoft")));
                } catch (android.content.ActivityNotFoundException anfe) {
                    getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/developer?id=SafiSoft")));
                }

            }
        });
        btn_info_facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("fb://page/301343817018905"));
                    getContext().startActivity(intent);
                } catch(Exception e) {
                    getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/SafiSoft.programming")));
                }

            }
        });
        btn_info_feesback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("mailto:SafiSoft.programmer@gmail.com")); // only email apps should handle this
                intent.putExtra(Intent.EXTRA_SUBJECT, "Fake Location Duck Feedback");
                if (intent.resolveActivity(getContext().getPackageManager()) != null) {
                    getContext().startActivity(intent);
                }

            }
        });
        btn_info_permissions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(getContext(), "ACCESS FINE LOCATION   Fake Location Duck Does Not Work Properly if You Didn't Allow This Permission.", Toast.LENGTH_LONG).show();


            }
        });

        btn_info_youtube.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/channel/UCtC8eqUUZmktsUoFznAL91w")));
                } catch (android.content.ActivityNotFoundException anfe) {
                    getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/channel/UCtC8eqUUZmktsUoFznAL91w")));
                }

            }
        });







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