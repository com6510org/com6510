/*
 * Copyright (c) 2017. This code has been developed by Fabio Ciravegna, The University of Sheffield. All rights reserved. No part of this code can be used without the explicit written permission by the author
 */

package oak.shef.teamCuphead.uk.com6510.view;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import oak.shef.teamCuphead.uk.com6510.R;
import oak.shef.teamCuphead.uk.com6510.model.FotoData;

public class ShowImageActivity extends AppCompatActivity {
    private Activity activity;
    private FloatingActionButton fabShowInformation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message2);

        //make it full screen
        ActionBar actionBar = getSupportActionBar();
        View decorView = getWindow().getDecorView();
        int option = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(option);
        actionBar.hide();

        activity= this;




        final Bundle b = getIntent().getExtras();
        int position=-1;
        if(b != null) {
            position = b.getInt("position");

            if (position!=-1){
                ImageView imageView = (ImageView) findViewById(R.id.image);
                FotoData element= MyAdapter.getItems().get(position);

                if (element!=null) {
                    Bitmap myBitmap = BitmapFactory.decodeFile(element.getPath());
                    imageView.setImageBitmap(myBitmap);
                }
            }

        }


        fabShowInformation = (FloatingActionButton) findViewById(R.id.fab_show_information);
        fabShowInformation.setVisibility(View.INVISIBLE);
        fabShowInformation.setOnClickListener(new View.OnClickListener() {
            //View popupView = getLayoutInflater().inflate(R.layout.activity_popupwindow, null);
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), ShowInfoActivity.class);
                intent.putExtra("bundle", b);
                startActivity(intent);
            }
        });


    }

    //not showing navigation and action bar while screen being touched
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus && Build.VERSION.SDK_INT >= 19) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
            fabShowInformation.setVisibility(View.INVISIBLE);
        }
    }

    //show floating button and navigation while screen being touched
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        fabShowInformation.setVisibility(View.VISIBLE);
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        fabShowInformation.setVisibility(View.VISIBLE);
        return false;
    }


}

