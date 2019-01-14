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
import android.widget.Toast;

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

/**
 * ShowInfoActivity is the activity to display detailed information of the photo in ShowImageActivity
 * it retrieve the path of the photo from the photo element passed from ShowImageActivity and then
 * using path to request other detailed information of this photo from database.
 * detailed information being displayed includes:
 * <ul>
 * <li>The photo title
 * <li>The photo description
 * <li>The photo date
 * <li>The photo location
 * </ul>
 * detailed information can be modified by the user includes:
 * <ul>
 * <li>The photo title
 * <li>The photo description
 * </ul>
 * <p>
 * While photo has location information, this activity will get its latitude
 * and longitude and show a marker of this latitude and longitude on google map
 * suppose the photo is without location information, a toast text is used to
 * inform user that this photo does not have location information
 */
public class ShowInfoActivity extends AppCompatActivity implements OnMapReadyCallback {
    private Activity activity;
    private GoogleMap mMap;
    private Button buttonSave;
    private TextView textViewTitle, textEditTitle, textViewDesc, textEditDesc, textViewDate;
    private Double Latitude, Longitude;
    private SupportMapFragment mapFragment;
    private FotoData element;
    private ImageView imageView;

    private MyDAO mDBDao;
    private FotoData fd;
    String path; //use path instead of id to retrieve photo information from database

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        /**
         * initialise database
         */
        MyRoomDatabase db = MyRoomDatabase.getDatabase(this);
        mDBDao = db.myDao();
        activity = this;

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_info);

        /**
         * make it full screen but showing navigation
         */
        ActionBar actionBar = getSupportActionBar();
        View decorView = getWindow().getDecorView();
        int option = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(option);
        actionBar.hide();

        /**
         * button for saving modified detailed information
         */
        buttonSave = (Button) findViewById(R.id.buttonSave);
        buttonSave.setVisibility(View.INVISIBLE);

        /**
         * fragment for google map
         */
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        /**
         * get photo element passed from ShowImageActivity
         */
        final Bundle b = getIntent().getBundleExtra("bundle");
        int position = -1;
        if (b != null) {
            position = b.getInt("position");
            if (position != -1) {
                imageView = (ImageView) findViewById(R.id.image); //image view
                textViewTitle = (TextView) findViewById(R.id.textViewTitle); //text view displaying title
                textEditTitle = (TextView) findViewById(R.id.textEditTitle); //text view for editing title
                textViewDesc = (TextView) findViewById(R.id.textViewDesc); //text view displaying description
                textEditDesc = (TextView) findViewById(R.id.textEditDesc); //text view editing description
                textViewDate = (TextView) findViewById(R.id.textViewDate); //text view displaying date
                textEditTitle.setVisibility(View.INVISIBLE);
                textViewTitle.setVisibility(View.VISIBLE);
                textEditDesc.setVisibility(View.INVISIBLE);
                textViewDesc.setVisibility(View.VISIBLE);
                element = MyAdapter.getItems().get(position);
                fd = element;

                /**
                 * get photo information from element
                 */
                if (element != null) {
                    Bitmap myBitmap = BitmapFactory.decodeFile(element.getPath());
                    imageView.setImageBitmap(myBitmap);
                    textViewTitle.setText(element.getTitle());
                    textEditTitle.setText(element.getTitle());
                    textViewDesc.setText(element.getDescription());
                    textEditDesc.setText(element.getDescription());
                    textViewDate.setText(element.getDate());
                    Latitude = (element.getLatitude());
                    Longitude = (element.getLongitude());
                    if (Latitude != 0.0 && Longitude != 0.0) {
                        mapFragment.getView().setVisibility(View.VISIBLE);  //hide google map while photo has location information
                    } else {
                        mapFragment.getView().setVisibility(View.INVISIBLE);  //hide google map while photo has no location information
                        Toast.makeText(this, "Location information not found", Toast.LENGTH_SHORT).show();
                    }
                    path = element.getPath();
                }
            }

        }

        /**
         * while user click on displaying text view, hide displaying text view, show editing text view and save button
         */
        textViewTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textEditTitle.setVisibility(View.VISIBLE);
                textViewTitle.setVisibility(View.INVISIBLE);
                buttonSave.setVisibility(View.VISIBLE);
            }
        });

        /**
         * while user click on displaying text view, hide displaying text view, show editing text view and save button
         */
        textViewDesc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textEditDesc.setVisibility(View.VISIBLE);
                textViewDesc.setVisibility(View.INVISIBLE);
                buttonSave.setVisibility(View.VISIBLE);
            }
        });

        /**
         * while user click on save button, get user input information using RetrieveFotodataWithPathAsyncTask
         * and save these information into database using UpdateAsyncTask
         * launch CameraActivity to refresh the initialised photo list
         */
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = textEditTitle.getText().toString();
                String desc = textEditDesc.getText().toString();
                fd.setTitle(title);
                fd.setDescription(desc);
                textEditTitle.setVisibility(View.INVISIBLE);
                textEditDesc.setVisibility(View.INVISIBLE);
                textViewTitle.setText(element.getTitle());
                textViewTitle.setVisibility(View.VISIBLE);
                textViewDesc.setText(element.getDescription());
                textViewDesc.setVisibility(View.VISIBLE);
                buttonSave.setVisibility(View.INVISIBLE);
                RetrieveFotodataWithPathAsyncTask retriveFotodataWithPathAsyncTask = new RetrieveFotodataWithPathAsyncTask(mDBDao, new AsyncResponsere() {
                    public void processFinish(FotoData output) {
                        if (output != null) {
                            output.setTitle(textEditTitle.getText().toString());
                            output.setDescription(textEditDesc.getText().toString());
                            new UpdateAsyncTask(mDBDao).execute(output);
                        }
                    }
                });
                retriveFotodataWithPathAsyncTask.execute(path);
                Intent intent = new Intent(getBaseContext(), CameraActivity.class);
                startActivity(intent);
            }
        });


    }

    /**
     * this method retrieves photo data from database according to its path
     */
    public class RetrieveFotodataWithPathAsyncTask extends AsyncTask<String, Void, FotoData> {
        private MyDAO mAsyncTaskDao;
        public AsyncResponsere delegate = null;

        RetrieveFotodataWithPathAsyncTask(MyDAO dao, AsyncResponsere asyncResponsere) {
            mAsyncTaskDao = dao;
            delegate = asyncResponsere;
        }

        /**
         * Class constructor.
         *
         * @param fotopath photo path.
         */
        @Override
        protected FotoData doInBackground(final String... fotopath) {

            String path = fotopath[0];
            FotoData fd = mAsyncTaskDao.retrieveSelectFotoPath(path);
            return fd;
        }

        protected void onPostExecute(FotoData result) {
            delegate.processFinish(result);
        }
    }

    /**
     * this method update photo data in database
     */
    private static class UpdateAsyncTask extends AsyncTask<FotoData, Void, Void> {
        private MyDAO mAsyncTaskDao;

        UpdateAsyncTask(MyDAO dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final FotoData... fotoData) {

            int i = mAsyncTaskDao.update(fotoData);
            return null;
        }
    }

    /**
     * add marker to the photo location on map and set zoom
     *
     * @param googleMap google map.
     */
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);
        // Add a marker in Sydney and move the camera
        LatLng fotoposition = new LatLng(Latitude, Longitude);
        mMap.addMarker(new MarkerOptions().position(fotoposition).title("Position of current photo"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(fotoposition, 14.0f));
    }

    /**
     * still full screen but showing navigation while focus changed
     */
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
