/*
 * Copyright (c) 2017. This code has been developed by Fabio Ciravegna, The University of Sheffield. All rights reserved. No part of this code can be used without the explicit written permission by the author
 */

package oak.shef.teamCuphead.uk.com6510;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.arch.lifecycle.LiveData;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import oak.shef.teamCuphead.uk.com6510.database.FotoData;
import oak.shef.teamCuphead.uk.com6510.database.MyDAO;
import oak.shef.teamCuphead.uk.com6510.database.MyRoomDatabase;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import com.google.android.gms.maps.SupportMapFragment;

import java.text.DateFormat;
import java.util.Date;

public class ShowInfoActivity extends AppCompatActivity implements OnMapReadyCallback {
    private Activity activity;
    private GoogleMap mMap;
    private Button buttonSave;
    private TextView textViewTitle, textEditTitle, textViewDesc, textEditDesc, textViewDate, FocusField;
    private Double Latitude, Longitude;
    private SupportMapFragment mapFragment;
    private ImageElement element;
    private ImageView imageView;
    public Activity getActivity() {
        return activity;
    }
    private MyDAO mDBDao;
    private FotoData fd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        MyRoomDatabase db = MyRoomDatabase.getDatabase(this);
        mDBDao = db.myDao();
        activity = this;

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_info);

        //make it full screen
        ActionBar actionBar = getSupportActionBar();
        View decorView = getWindow().getDecorView();
        int option = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(option);
        actionBar.hide();

        buttonSave = (Button) findViewById(R.id.buttonSave);
        buttonSave.setVisibility(View.INVISIBLE);
        FocusField= (TextView) findViewById(R.id.textView8);

        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        final Bundle b = getIntent().getBundleExtra("bundle");
        int position=-1;
        if(b != null) {
            position = b.getInt("position");
            if (position!=-1){
                imageView = (ImageView) findViewById(R.id.image);
                textViewTitle = (TextView) findViewById(R.id.textViewTitle);
                textEditTitle = (TextView) findViewById(R.id.textEditTitle);
                textViewDesc = (TextView) findViewById(R.id.textViewDesc);
                textEditDesc = (TextView) findViewById(R.id.textEditDesc);
                textViewDate = (TextView) findViewById(R.id.textViewDate);
                textEditTitle.setVisibility(View.INVISIBLE);
                textViewTitle.setVisibility(View.VISIBLE);
                textEditDesc.setVisibility(View.INVISIBLE);
                textViewDesc.setVisibility(View.VISIBLE);
                element = MyAdapter.getItems().get(position);
                fd=element.fotodata;
                if (element.image!=-1) {
                    imageView.setImageResource(element.image);
                } else if (element.file!=null) {
                    Bitmap myBitmap = BitmapFactory.decodeFile(element.file.getAbsolutePath());
                    //print
                    imageView.setImageBitmap(myBitmap);
                }
                else if (element.path!=null) {
                    Bitmap myBitmap = BitmapFactory.decodeFile(element.path);
                    imageView.setImageBitmap(myBitmap);
                }
                else if (element.fotodata!=null) {
                    Bitmap myBitmap = BitmapFactory.decodeFile(element.fotodata.getPath());
                    imageView.setImageBitmap(myBitmap);
                    textViewTitle.setText(element.fotodata.getTitle());
                    textEditTitle.setText(element.fotodata.getTitle());
                    textViewDesc.setText(element.fotodata.getDescription());
                    textEditDesc.setText(element.fotodata.getDescription());
                    textViewDate.setText(element.fotodata.getDate());
                    Latitude = (element.fotodata.getLatitude());
                    Longitude = (element.fotodata.getLongitude());
                    Log.i("CheckPoint"," !6! "+"Latitude"+Latitude.toString()+ "Longitude:"+Longitude.toString());
                }
            }

        }

        textViewTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textEditTitle.setVisibility(View.VISIBLE);
                textViewTitle.setVisibility(View.INVISIBLE);
                buttonSave.setVisibility(View.VISIBLE);
            }
        });

        textViewDesc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textEditDesc.setVisibility(View.VISIBLE);
                textViewDesc.setVisibility(View.INVISIBLE);
                buttonSave.setVisibility(View.VISIBLE);
            }
        });


        FocusField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), ShowImageActivity.class);
                intent.putExtra("bundle", b);
                startActivity(intent);
            }
        });


        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title= textEditTitle.getText().toString();
                String desc= textEditDesc.getText().toString();
                fd.setTitle(title);
                fd.setDescription(desc);
                Log.i("CheckPoint"," !4! "+ fd.toString());
                new UpdateAsyncTask(mDBDao).execute(fd);
                textEditTitle.setVisibility(View.INVISIBLE);
                textEditDesc.setVisibility(View.INVISIBLE);
                textViewTitle.setText(element.fotodata.getTitle());
                textViewTitle.setVisibility(View.VISIBLE);
                textViewDesc.setText(element.fotodata.getDescription());
                textViewDesc.setVisibility(View.VISIBLE);
                buttonSave.setVisibility(View.INVISIBLE);
            }
        });

//
//        FloatingActionButton fabShowInformation = (FloatingActionButton) findViewById(R.id.fab_show_information);
//        fabShowInformation.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                new DetailInfo(getActivity());
//        }
//        });

    }
//
//    private View.OnClickListener listener = new View.OnClickListener() {
//        @Override
//        public void onClick(View v) {
//            switch (v.getId()){
//                case R.id.buttonSave:

//                    new UpdateAsyncTask(mDBDao).execute(element);
//                    break;
//            }
//        }
//    };



    private static class UpdateAsyncTask extends AsyncTask<FotoData, Void, Void> {
        private MyDAO mAsyncTaskDao;
        private LiveData<FotoData> fotoData;

        UpdateAsyncTask(MyDAO dao) {
            mAsyncTaskDao = dao;
        }
        @Override
        protected Void doInBackground(final FotoData... fotoData) {
            Log.i("CheckPoint"," !5! "+fotoData.toString());

            int i = mAsyncTaskDao.update(fotoData);
            return null;
        }
    }


    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);
        // Add a marker in Sydney and move the camera
        LatLng fotoposition = new LatLng(Latitude, Longitude);
        mMap.addMarker(new MarkerOptions().position(fotoposition).title("Position of current photo"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(fotoposition, 14.0f));
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
        }
    }


    @Override
    public void onConfigurationChanged (Configuration newConfig) {

        super.onConfigurationChanged(newConfig);

        setContentView(R.layout.activity_info);
    }


     /*
        if(mapFragment != null) {
            Log.d("-------------------", "space no null");
            FragmentManager fragmentManager = getFragmentManager();
            if (fragmentManager != null && !fragmentManager.isDestroyed()) {
                final FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                if (fragmentTransaction != null) {
                    fragmentTransaction.remove(mapFragment).commit();
                }
            }
        }


    }
        */

/*

    @Override
    public void onDestroyView() {
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if(mapFragment != null){
            getFragmentManager().beginTransaction().remove(mapFragment).commit();
        }
        super.onDestroyView();
    }

*/



}
