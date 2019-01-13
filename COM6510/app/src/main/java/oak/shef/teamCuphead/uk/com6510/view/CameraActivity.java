/*
 * Copyright (c) 2017. This code has been developed by Fabio Ciravegna, The University of Sheffield. All rights reserved. No part of this code can be used without the explicit written permission by the author
 *
 * some inspiration taken from https://stackoverflow.com/questions/40587168/simple-android-grid-example-using-recyclerview-with-gridlayoutmanager-like-the
 */

package oak.shef.teamCuphead.uk.com6510.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.media.ExifInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;

import java.io.File;

import java.util.ArrayList;
import java.util.List;

import oak.shef.teamCuphead.uk.com6510.viewmodel.MyViewModel;
import oak.shef.teamCuphead.uk.com6510.R;
import oak.shef.teamCuphead.uk.com6510.database.AsyncResponse;
import oak.shef.teamCuphead.uk.com6510.model.FotoData;
import oak.shef.teamCuphead.uk.com6510.CommonMethod.ChangeFileToFotodata;
import oak.shef.teamCuphead.uk.com6510.CommonMethod.PermissionCheck;
import oak.shef.teamCuphead.uk.com6510.CommonMethod.StoreIntoRoom;
import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;


/**
 * This activity is the main activity showed in this app.
 * In this activity include the Easyimage(2.0.3)
 * Easyimage is used to add the camera and gallery function.
 * Easyimage will return the file which the camera and gallery function
 * returned. However, to improve the performance, we can only store the photo path in
 * to Room database.
 * ChangeFileToFotodate is to change the data from file to the FotoData.
 * In this activity also need to listen the user's location through use the google map
 * location request function.
 */
public class CameraActivity extends AppCompatActivity {

    private PermissionCheck permissionCheck;
    private static final String TAG = "CameraActivity";
    private List<FotoData> myPictureList = new ArrayList<>();
    private List<FotoData> initdatalist = new ArrayList<>();
    private RecyclerView.Adapter mAdapter;
    private RecyclerView mRecyclerView;
    private int numberOfColumns;
    private List<String> myPicturePath = new ArrayList<>();
    private Activity activity;
    private Context context;
    private MyViewModel myViewModel;
    private LocationRequest mLocationRequest;
    private FusedLocationProviderClient mFusedLocationClient;
    private StoreIntoRoom storeIntoRoom = new StoreIntoRoom();
    private ChangeFileToFotodata changeFileToFotodata = new ChangeFileToFotodata();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //set the layout
        setContentView(R.layout.activity_camera);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        context = this;
        activity = this;
        mRecyclerView = (RecyclerView) findViewById(R.id.grid_recycler_view);
        // set up the RecyclerView
        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            numberOfColumns = 3;
        } else {
            numberOfColumns = 4;
        }
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, numberOfColumns));
        myViewModel = ViewModelProviders.of(this).get(MyViewModel.class);
        permissionCheck = new PermissionCheck();
        //myViewModel.deleteAllElement(); If you want to clean the database.
        permissionCheck.checkPermissions(context, activity);
        AsyncResponse response = new AsyncResponse() {
            public void processFinish(List<FotoData> output) {
                // once the process of retrieving the data is finished
                // if  there is something in the list
                if (!output.isEmpty()) {
                    for (int i = 0; i < output.size(); i++) {
                        initdatalist.add(output.get(i));
                    }
                    myPictureList.addAll(initdatalist);
                    mAdapter = new MyAdapter(myPictureList);
                    mRecyclerView.setAdapter(mAdapter);

                }
                //if the list is empty
                else {
                    myPicturePath = myViewModel.getImagesPath(activity);

                    myPictureList.addAll(myViewModel.initData(myPicturePath));
                    myViewModel.generateNewFoto(myViewModel.initData(myPicturePath));
                    mAdapter = new MyAdapter(myPictureList);
                    mRecyclerView.setAdapter(mAdapter);
                }
            }
        };

        myPicturePath = myViewModel.getImagesPath(activity);
        myViewModel.getAllPhotos(response, myPicturePath);
        myViewModel.initFunction(this);
        // set up the FloatingActionButton for openCamera
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_camera);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myViewModel.startLocationUpdates(context, activity);
                EasyImage.openCamera(getActivity(), 0);

            }
        });
        // set up the FloatingActionButton for openGallery
        FloatingActionButton fabGallery = (FloatingActionButton) findViewById(R.id.fab_gallery);
        fabGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EasyImage.openGallery(getActivity(), 0);
            }
        });

        // set up the FloatingActionButton for openMap
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

        Intent intent1 = new Intent(this, SearchActivity.class);
        this.startActivity(intent1);
        return true;
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

            /**
             * Easyimage will handle activity result and give back the result list
             * @param imageFiles This is the lis of file which the user picked
             */
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

    /**
     * Easyimage will handle activity result and give back the result list
     * Here is retrive the Fotodata from the database.
     * If the value not exist in the database , can add the photo in to Room.
     * Set the adpter notify the change of the Mypicture list.
     * The FotoDate includes:
     * <ul>
     * <li>The photo title
     * <li>The photo description
     * <li>The photo path
     * <li>The photo date
     * <li>The photo latitude
     * <li>The photo longitude
     * <li>The photo type
     * </ul>
     *
     * @param returnedPhotos This is the lis of file which the user picked
     */
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
                        List<FotoData> fdlist = storeIntoRoom.storeIntoRoom(finalPath, myViewModel.returnMyLocation());
                        myViewModel.generateNewFoto(fdlist);
                    }
                }
            });


        }
        myPictureList.addAll(changeFileToFotodata.getFotoData(returnedPhotos, myViewModel.returnMyLocation()));
        mAdapter.notifyDataSetChanged();
        mRecyclerView.scrollToPosition(returnedPhotos.size() - 1);
        myViewModel.stopLocationUpdates();
    }

    public Activity getActivity() {
        return activity;
    }

    @Override
    protected void onResume() {
        super.onResume();
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        myViewModel.startLocationUpdates(context, activity);
    }


}
