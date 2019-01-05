/*
 * Copyright (c) 2017. This code has been developed by Fabio Ciravegna, The University of Sheffield. All rights reserved. No part of this code can be used without the explicit written permission by the author
 *
 * some inspiration taken from https://stackoverflow.com/questions/40587168/simple-android-grid-example-using-recyclerview-with-gridlayoutmanager-like-the
 */

package oak.shef.ac.uk.week6;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;

import java.io.File;

import java.io.IOException;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import oak.shef.ac.uk.week6.database.FotoData;
import oak.shef.ac.uk.week6.database.MyDAO;
import oak.shef.ac.uk.week6.database.MyRoomDatabase;
import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;

import android.annotation.SuppressLint;
import android.location.Location;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;



public class CameraActivity extends AppCompatActivity {

    private static final int REQUEST_READ_EXTERNAL_STORAGE = 2987;
    private static final int REQUEST_WRITE_EXTERNAL_STORAGE = 7829;
    private static final String TAG = "CameraActivity";
    private List<ImageElement> myPictureList = new ArrayList<>();
    private List<FotoData> initdata = new ArrayList<>();
    private RecyclerView.Adapter mAdapter;
    private RecyclerView mRecyclerView;
    private List<String> myPicturePath = new ArrayList<>();
    private MyDAO mDBDao;
    private Activity activity;
    private MyViewModel myViewModel;
    private static final int ACCESS_FINE_LOCATION = 123;
    private LocationRequest mLocationRequest;
    private Location mCurrentLocation;
    private FusedLocationProviderClient mFusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //set the layout
        setContentView(R.layout.activity_camera);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        MyRoomDatabase db = MyRoomDatabase.getDatabase(this);
        mDBDao = db.myDao();

        activity = this;
        mRecyclerView = (RecyclerView) findViewById(R.id.grid_recycler_view);
        // set up the RecyclerView
        int numberOfColumns = 3;
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, numberOfColumns));


        myViewModel = ViewModelProviders.of(this).get(MyViewModel.class);

        //myViewModel.deleteAllElement();




        AsyncResponse response = new AsyncResponse() {
            public void processFinish(List<FotoData> output) {
                // once the process of retrieving the data is finished
                // if  there is something in the list
                if (!output.isEmpty()) {
                    for (int i = 0; i < output.size(); i++) {
                        Log.i("Query", "out put size: " + output.size() + " out put path: " + output.get(i).getPath() + "");
                        initdata.add(output.get(i));
                    }
                    myPictureList.addAll(getFotoData(initdata));
                    Log.i("CheckPoint", myPictureList.size() + " !3! ");
                    mAdapter = new MyAdapter(myPictureList);
                    mRecyclerView.setAdapter(mAdapter);

                }
                //if the list is empty
                else if (output.isEmpty()) {
                    initData();
                    Log.i("CheckPoint", myPictureList.size() + "  !2!  ");

                    mAdapter = new MyAdapter(myPictureList);
                    mRecyclerView.setAdapter(mAdapter);
                }
            }
        };
        myPicturePath = getImagesPath(activity);
        myViewModel.getAllPhotos(response, myPicturePath);


        checkPermissions(getApplicationContext());

        initEasyImage();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_camera);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startLocationUpdates();
                EasyImage.openCamera(getActivity(), 0);

            }
        });

        FloatingActionButton fabGallery = (FloatingActionButton) findViewById(R.id.fab_gallery);
        fabGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EasyImage.openGallery(getActivity(), 0);
            }
        });

        FloatingActionButton ViewMAP = (FloatingActionButton) findViewById(R.id.view_map);
        ViewMAP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), ViewMapActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_camera, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // int id = item.getItemId();

        Intent intent1 = new Intent(this, SearchActivity.class);
        this.startActivity(intent1);
        return true;

        //return super.onOptionsItemSelected(item);
    }


    private void initEasyImage() {
        EasyImage.configuration(this)
                .setImagesFolderName("EasyImage sample")
                .setCopyTakenPhotosToPublicGalleryAppFolder(true)
                .setCopyPickedImagesToPublicGalleryAppFolder(false)
                .setAllowMultiplePickInGallery(true);
    }

    private double score2dimensionalityLat(String string) {
        double dimensionality = 0.0;
        if (null == string) {
            return dimensionality;
        }

        String[] split = string.split(",");
        for (int i = 0; i < split.length; i++) {

            String[] s = split[i].split("/");
            double v = Double.parseDouble(s[0]) / Double.parseDouble(s[1]);
            dimensionality = dimensionality + v / Math.pow(60, i);
        }
        return dimensionality;
    }

    private double score2dimensionalityLon(String string) {
        double dimensionality = 0.0;
        if (null == string) {
            return dimensionality;
        }

        String[] split = string.split(",");
        for (int i = 0; i < split.length; i++) {

            String[] s = split[i].split("/");
            double v = Double.parseDouble(s[0]) / Double.parseDouble(s[1]);
            dimensionality = dimensionality + v / Math.pow(60, i);
        }
        return dimensionality;
    }


    private void storeIntoRoom(String path) {
        List<FotoData> newList = new ArrayList<>();
        try {
            ExifInterface exif = new ExifInterface(path);
            String date = exif.getAttribute(ExifInterface.TAG_DATETIME);
            String latitudeRef = exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE_REF);
            String longitudeRef = exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF);
            String latitude = exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE);
            String longitude = exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE);
            double lat = score2dimensionalityLat(latitude);
            double lon = score2dimensionalityLon(longitude);
            if (latitudeRef != null && longitudeRef != null && date != null) {
                if (latitudeRef.equals("S")) {
                    Log.i("CheckPoint", myPictureList.size() + "  !9!  ");
                    lat = -lat;
                }
                if (longitudeRef.equals("W")) {
                    Log.i("CheckPoint", myPictureList.size() + "  !10!  ");
                    lon = -lon;
                }
            } else {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss");
                Date fotodate = new Date(System.currentTimeMillis());
                date = simpleDateFormat.format(fotodate);
                lat = mCurrentLocation.getLatitude();
                lon = mCurrentLocation.getLongitude();
            }
            newList.add(new FotoData("Add a title", "Add a description", path, date, lat, lon));
            myViewModel.generateNewFoto(newList);
        } catch (Exception ee) {
            Log.i("Date", "date or location is not exist");
        }

    }

    private void initData() {
        List<FotoData> newList = new ArrayList<>();
        myPicturePath = getImagesPath(activity);
        for (int i = 0; i < myPicturePath.size(); i++) {
            try {
                String path = myPicturePath.get(i);
                ExifInterface exif = new ExifInterface(path);
                String date = exif.getAttribute(ExifInterface.TAG_DATETIME);
                String latitudeRef = exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE_REF);
                String longitudeRef = exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF);
                String latitude = exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE);
                String longitude = exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE);

                double lat = score2dimensionalityLat(latitude);
                double lon = score2dimensionalityLon(longitude);
                if (latitudeRef != null && longitudeRef != null && date != null) {
                    if (latitudeRef.equals("S")) {
                        Log.i("CheckPoint", myPictureList.size() + "  !9!  ");
                        lat = -lat;
                    }
                    if (longitudeRef.equals("W")) {
                        Log.i("CheckPoint", myPictureList.size() + "  !10!  ");
                        lon = -lon;
                    }
                } else {
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss");
                    Date fotodate = new Date(System.currentTimeMillis());
                    date = simpleDateFormat.format(fotodate);
                    lat = mCurrentLocation.getLatitude();
                    lon = mCurrentLocation.getLongitude();
                }
                Log.i("Date", " path: " + path + "  Date: " + date + "  latitude: " + lat + "  longitude: " + lon + " latitudeRef: " + latitudeRef + " longitudeRef: " + longitudeRef);
                newList.add(new FotoData("Add a title", "Add a description", myPicturePath.get(i), date, lat, lon));
            } catch (Exception ee) {
                Log.i("Date", "date or location is not exist");
            }
        }
        myPictureList.addAll(getFotoData(newList));
         myViewModel.generateNewFoto(newList);


    }

    public static ArrayList<String> getImagesPath(Activity activity) {
        Uri uri;
        ArrayList<String> listOfAllImages = new ArrayList<String>();
        Cursor cursor;
        int column_index_data, column_index_folder_name;
        String PathOfImage = null;
        uri = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        String[] projection = {MediaStore.MediaColumns.DATA,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME};

        cursor = activity.getContentResolver().query(uri, projection, null,
                null, null);

        column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        column_index_folder_name = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);
        while (cursor.moveToNext()) {
            PathOfImage = cursor.getString(column_index_data);

            listOfAllImages.add(PathOfImage);
        }
        return listOfAllImages;
    }


    private void checkPermissions(final Context context) {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if (currentAPIVersion >= android.os.Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    android.support.v7.app.AlertDialog.Builder alertBuilder = new android.support.v7.app.AlertDialog.Builder(context);
                    alertBuilder.setCancelable(true);
                    alertBuilder.setTitle("Permission necessary");
                    alertBuilder.setMessage("External storage permission is necessary");
                    alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_READ_EXTERNAL_STORAGE);
                        }
                    });
                    android.support.v7.app.AlertDialog alert = alertBuilder.create();
                    alert.show();

                } else {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_READ_EXTERNAL_STORAGE);
                }

            }
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    android.support.v7.app.AlertDialog.Builder alertBuilder = new android.support.v7.app.AlertDialog.Builder(context);
                    alertBuilder.setCancelable(true);
                    alertBuilder.setTitle("Permission necessary");
                    alertBuilder.setMessage("Writing external storage permission is necessary");
                    alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_EXTERNAL_STORAGE);
                        }
                    });
                    android.support.v7.app.AlertDialog alert = alertBuilder.create();
                    alert.show();

                } else {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_WRITE_EXTERNAL_STORAGE);
                }

            }


        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        EasyImage.handleActivityResult(requestCode, resultCode, data, this, new DefaultCallback() {
            @Override
            public void onImagePickerError(Exception e, EasyImage.ImageSource source, int type) {
                //Some error handling
                e.printStackTrace();
            }

            @Override
            public void onImagesPicked(List<File> imageFiles, EasyImage.ImageSource source, int type) {
                onPhotosReturned(imageFiles);
            }

            @Override
            public void onCanceled(EasyImage.ImageSource source, int type) {
                //Cancel handling, you might wanna remove taken photo if it was canceled
                if (source == EasyImage.ImageSource.CAMERA) {
                    File photoFile = EasyImage.lastlyTakenButCanceledPhoto(getActivity());
                    if (photoFile != null) photoFile.delete();
                }
            }
        });
    }



    private void onPhotosReturned(List<File> returnedPhotos) {
        final List<FotoData> addedPhotos = new ArrayList<>();

        String path = null;
        for (int i = 0; i < returnedPhotos.size(); i++) {
            path = returnedPhotos.get(i).getPath();
            final String finalPath = path;
            myViewModel.getFotoDataToDisplay(path).observe(this, new Observer<FotoData>() {
                @Override
                public void onChanged(@Nullable final FotoData newValue) {
                    if (newValue == null) {

                        storeIntoRoom(finalPath);

                        ExifInterface exif = null;


                    } else {

                    }
                }
            });


        }
        myPictureList.addAll(getImageElements(returnedPhotos));
        mAdapter.notifyDataSetChanged();
        mRecyclerView.scrollToPosition(returnedPhotos.size() - 1);


    }

//WHAT IS THIS METHOD FOR
    private List<ImageElement> getImageElements(List<File> returnedPhotos) {
        List<ImageElement> imageElementList = new ArrayList<>();

        for (File file : returnedPhotos) {
            try {
                String path = file.getAbsolutePath();
                ExifInterface exif = new ExifInterface(path);
                String date = exif.getAttribute(ExifInterface.TAG_DATETIME);
                String latitude = exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE);
                String longitude = exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE);
                String latitudeRef = exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE_REF);
                String longitudeRef = exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF);
                double lat = score2dimensionalityLat(latitude);
                double lon = score2dimensionalityLon(longitude);
                if (latitudeRef != null && longitudeRef != null && date != null) {
                    if (latitudeRef.equals("S")) {
                        Log.i("CheckPoint", myPictureList.size() + "  !9!  ");
                        lat = -lat;
                    }
                    if (longitudeRef.equals("W")) {
                        Log.i("CheckPoint", myPictureList.size() + "  !10!  ");
                        lon = -lon;
                    }
                } else {
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss");
                    Date fotodate = new Date(System.currentTimeMillis());
                    date = simpleDateFormat.format(fotodate);
                    lat = mCurrentLocation.getLatitude();
                    lon = mCurrentLocation.getLongitude();
                }


                Log.i("CheckPoint", myPictureList.size() + "  !11!  ");
                Log.i("Date", " path: " + path + "  Date: " + date + "  latitude: " + lat + "  longitude: " + lon + " latitudeRef: " + latitudeRef + " longitudeRef: " + longitudeRef);
                ImageElement element = new ImageElement(new FotoData("Add a title", "Add a description", path, date, lat, lon));
                imageElementList.add(element);
            } catch (Exception ee) {
                Log.i("Date", "date or location is not exist");
            }
        }
        stopLocationUpdates();
        return imageElementList;
    }

    private List<ImageElement> getFotoData(List<FotoData> returnedPath) {
        List<ImageElement> imageElementList = new ArrayList<>();
        for (FotoData path : returnedPath) {
            ImageElement element = new ImageElement(path);
            imageElementList.add(element);
        }
        return imageElementList;
    }

    public Activity getActivity() {
        return activity;
    }


    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        ACCESS_FINE_LOCATION);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }

            return;
        }
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, null /* Looper */);
    }

    private void stopLocationUpdates() {
        mFusedLocationClient.removeLocationUpdates(mLocationCallback);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        startLocationUpdates();
    }

    private String mLastUpdateTime;
    LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);
            mCurrentLocation = locationResult.getLastLocation();
            mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
            Log.i("MAP", "new location " + mCurrentLocation.toString());
        }
    };


    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                            mLocationCallback, null /* Looper */);
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }


}
