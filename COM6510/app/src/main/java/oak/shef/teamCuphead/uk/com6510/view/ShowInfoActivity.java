/*
 * Copyright (c) 2017. This code has been developed by Fabio Ciravegna, The University of Sheffield. All rights reserved. No part of this code can be used without the explicit written permission by the author
 */

package oak.shef.teamCuphead.uk.com6510.view;

import android.app.Activity;
import android.arch.lifecycle.LiveData;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

import oak.shef.teamCuphead.uk.com6510.R;
import oak.shef.teamCuphead.uk.com6510.database.AsyncResponsere;
import oak.shef.teamCuphead.uk.com6510.model.FotoData;
import oak.shef.teamCuphead.uk.com6510.database.MyDAO;
import oak.shef.teamCuphead.uk.com6510.database.MyRoomDatabase;


import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import com.google.android.gms.maps.SupportMapFragment;

public class ShowInfoActivity extends AppCompatActivity implements OnMapReadyCallback {
    private Activity activity;
    private GoogleMap mMap;
    private Button buttonSave;
    private TextView textViewTitle, textEditTitle, textViewDesc, textEditDesc, textViewDate;
    private Double Latitude, Longitude;
    private SupportMapFragment mapFragment;
    private FotoData element;
    private ImageView imageView;
    public Activity getActivity() {
        return activity;
    }
    private MyDAO mDBDao;
    private FotoData fd;
    String path;
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
        int option = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(option);
        actionBar.hide();

        buttonSave = (Button) findViewById(R.id.buttonSave);
        buttonSave.setVisibility(View.INVISIBLE);

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
                fd=element;



                if (element!=null) {
                    Bitmap myBitmap = BitmapFactory.decodeFile(element.getPath());
                    imageView.setImageBitmap(myBitmap);
                    textViewTitle.setText(element.getTitle());
                    textEditTitle.setText(element.getTitle());
                    textViewDesc.setText(element.getDescription());
                    textEditDesc.setText(element.getDescription());
                    textViewDate.setText(element.getDate());
                    Latitude = (element.getLatitude());
                    Longitude = (element.getLongitude());
                    path=element.getPath();

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



        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title= textEditTitle.getText().toString();
                String desc= textEditDesc.getText().toString();
                fd.setTitle(title);
                fd.setDescription(desc);
                textEditTitle.setVisibility(View.INVISIBLE);
                textEditDesc.setVisibility(View.INVISIBLE);
                textViewTitle.setText(element.getTitle());
                textViewTitle.setVisibility(View.VISIBLE);
                textViewDesc.setText(element.getDescription());
                textViewDesc.setVisibility(View.VISIBLE);
                buttonSave.setVisibility(View.INVISIBLE);
                RetriveFotodataWithPathAsyncTask retriveFotodataWithPathAsyncTask=new RetriveFotodataWithPathAsyncTask(mDBDao,new AsyncResponsere(){
                    public void processFinish(FotoData output) {
                        if(output!=null){


                            output.setTitle(textEditTitle.getText().toString());

                            output.setDescription(textEditDesc.getText().toString());
                            new UpdateAsyncTask(mDBDao).execute(output);

                        }

                    }
                });
                retriveFotodataWithPathAsyncTask.execute(path);
                Intent intent = new Intent(getBaseContext(), CameraActivity.class);
                intent.putExtra("bundle", b);
                startActivity(intent);
            }
        });



    }

    public class RetriveFotodataWithPathAsyncTask extends AsyncTask<String, Void, FotoData> {
        private MyDAO mAsyncTaskDao;
        public AsyncResponsere delegate=null;
        RetriveFotodataWithPathAsyncTask(MyDAO dao,AsyncResponsere asyncResponsere) {
            mAsyncTaskDao = dao;
            delegate = asyncResponsere;
        }

        @Override
        protected FotoData doInBackground(final String... fotopath) {

            String path=fotopath[0];
            FotoData fd= mAsyncTaskDao.retrieveSelectFotoPath(path);
            return fd;
        }
        protected void onPostExecute(FotoData result) {
            delegate.processFinish(result);
        }
    }


    private static class UpdateAsyncTask extends AsyncTask<FotoData, Void, Void> {
        private MyDAO mAsyncTaskDao;
        private LiveData<FotoData> fotoData;

        UpdateAsyncTask(MyDAO dao) {
            mAsyncTaskDao = dao;
        }
        @Override
        protected Void doInBackground(final FotoData... fotoData) {

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

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus && Build.VERSION.SDK_INT >= 19) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

}
