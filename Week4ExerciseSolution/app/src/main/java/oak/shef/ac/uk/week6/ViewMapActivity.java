package oak.shef.ac.uk.week6;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import oak.shef.ac.uk.week6.database.FotoData;
import oak.shef.ac.uk.week6.database.MyDAO;
import oak.shef.ac.uk.week6.database.MyRoomDatabase;

public class ViewMapActivity extends FragmentActivity implements OnMapReadyCallback {

    private MyDAO mDBDao;
    private MarkerOptions mMarkOption;
    private GoogleMap mMap;
    private LocationRequest mLocationRequest;
    private FusedLocationProviderClient mFusedLocationClient;
    protected int getLayoutId() {
        return R.layout.activity_view_map;
    }
    private static final int ACCESS_FINE_LOCATION = 123;
    private Location mCurrentLocation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        setUpMap();
        MyRoomDatabase db = MyRoomDatabase.getDatabase(this);
        mDBDao = db.myDao();

        FloatingActionButton FAB = (FloatingActionButton) findViewById(R.id.myLocationButton);
        FAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startLocationUpdates();
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude()), 14.0f));

            }
        });

        QueryAllAsyncTask queryAllAsyncTask=new QueryAllAsyncTask(mDBDao,new AsyncResponse(){
            public void processFinish(List<FotoData> output) {
                Log.i("CheckPoint",output.size()+" !1! ");
                if (!output.isEmpty()){
                    for(int i=0;i<output.size();i++){
                        Bitmap bm=BitmapFactory.decodeFile(output.get(i).getPath());
                        int width = bm.getWidth();
                        int height = bm.getHeight();
                        int newWidth =  80;
                        int newHeight = 80;
                        float scaleWidth = ((float) newWidth) / width;
                        float scaleHeight = ((float) newHeight) / height;
                        Matrix matrix = new Matrix();
                        matrix.postScale(scaleWidth, scaleHeight);
                        Bitmap newbm = Bitmap.createBitmap(bm, 0, 0, width, height, matrix,true);
                        mMarkOption = new MarkerOptions();
                        mMarkOption.icon(BitmapDescriptorFactory.fromBitmap(newbm));
                        mMarkOption.position(new LatLng(output.get(i).getLatitude(), output.get(i).getLongitude()));
                        mMarkOption.title(output.get(i).getTitle());
                        mMarkOption.snippet(output.get(i).getDescription());
                        Marker mMarker = mMap.addMarker(mMarkOption);
                        mMarker.showInfoWindow();
                        String path=output.get(i).getPath();

                    }
                }
                else if(output.isEmpty()){

                }
            }
        });
        queryAllAsyncTask.execute();
    }





    private void setUpMap() {
        ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMapAsync(this);
    }

    public class QueryAllAsyncTask extends AsyncTask<Void, Void, List<FotoData>> {
        private MyDAO mAsyncTaskDao;
        public AsyncResponse delegate=null;

        QueryAllAsyncTask(MyDAO dao,AsyncResponse asyncResponse) {
            mAsyncTaskDao = dao;
            delegate = asyncResponse;
        }
        @Override
        protected List<FotoData> doInBackground(final Void... voids) {
            // you may want to uncomment this to check if photo path have been inserted
            List<FotoData> fd=new ArrayList<>();
            fd=mAsyncTaskDao.retrieveAllFoto();
            return fd;
        }
        @Override
        protected void onPostExecute(List<FotoData> result) {
            delegate.processFinish(result);
        }
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
    private Marker marker;
    LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);
            mCurrentLocation = locationResult.getLastLocation();
            mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
            Log.i("MYMAP", "new location " + mCurrentLocation.toString());
            MarkerOptions markerOptions=new MarkerOptions();
            if (mMap != null)
                marker.setPosition(new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude()));

        }
    };

    @SuppressLint("MissingPermission")
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, null /* Looper */);
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

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);
        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        marker=mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 14.0f));

    }

}
