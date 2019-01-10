package oak.shef.teamCuphead.uk.com6510;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.media.ExifInterface;
import android.os.AsyncTask;
import android.util.Log;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import oak.shef.teamCuphead.uk.com6510.database.FotoData;
import oak.shef.teamCuphead.uk.com6510.database.MyDAO;
import oak.shef.teamCuphead.uk.com6510.database.MyRoomDatabase;

class MyRepository extends ViewModel{
    private final MyDAO mDBDao;

    public MyRepository(Application application) {
        MyRoomDatabase db = MyRoomDatabase.getDatabase(application);
        mDBDao = db.myDao();
    }

    public LiveData<FotoData> getFotoData() {
        return mDBDao.retrieveOneFoto();
    }
    public void deleteAll(){
        new deleteAsyncTask(mDBDao).execute();
    }

    public void getAllPhotos(final AsyncResponse resp,final List<String> myPicturePath)
    {
       selectAllPathAsyncTask selectAll=new selectAllPathAsyncTask(mDBDao,new AsyncResponse(){
            public void processFinish(List<FotoData> allPaths) {
                // once the process of retrieving the data is finished
                // if  there is something in the list
                if (!allPaths.isEmpty()){
                    List<String> allStrings= new ArrayList<>();
                    for( FotoData e: allPaths)
                    {
                        if(e.getFototype()==2.0){
                            allStrings.add(e.getPath());
                        }

                    }
                    int x=0;
                    for( String e:  new ArrayList<>(allStrings))
                    {
                        if (!myPicturePath.contains(e))
                        {
                            new deleteOneAsyncTask(mDBDao).execute(allPaths.get(x));
                            allStrings.remove(e);

                        }
                        x++;
                    }

                    //add the elements that are in myPicturePath and not in allPaths to the database

                   for( String i: myPicturePath)
                    {
                        if (!allStrings.contains(i))
                        {
                            try {
                                String path = i;
                                ExifInterface exif = new ExifInterface(path);
                                String date = exif.getAttribute(ExifInterface.TAG_DATETIME);
                                String latitude = exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE);
                                String longitude = exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE);
                                String latitudeRef = exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE_REF);
                                String longitudeRef = exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF);
                                double lat =  score2dimensionalityLat(latitude);
                                double lon = score2dimensionalityLon(longitude);
                                if (latitudeRef != null && longitudeRef != null) {
                                    if (latitudeRef.equals("S")) {
                                        Log.i("CheckPoint",  "  !9!  ");
                                        lat = -lat;
                                    }
                                    if (longitudeRef.equals("W")) {
                                        Log.i("CheckPoint", "  !10!  ");
                                        lon = -lon;
                                    }
                                }
                                Log.i("Date", " path: " + path + "  Date: " + date + "  latitude: " + lat + "  longitude: " + lon + " latitudeRef: " + latitudeRef + " longitudeRef: " + longitudeRef);
                                new insertAsyncTask(mDBDao).execute(new FotoData("Add a title", "Add a description", path,date,lat,lon,2.0));
                            }
                            catch(Exception ee){
                                Log.i("Date", "date or location is not exist");
                            }


                        }
                    }

                }

                new QueryAllAsyncTask(mDBDao, resp).execute();
            }
        });


        selectAll.execute();


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

    public LiveData<FotoData> getFoto(String path) {
        return mDBDao.retrieveSelectFoto(path);
    }



     public void generateNewFoto(List<FotoData> listPhotos) {
       Iterator<FotoData> iter;
        for (iter = listPhotos.iterator(); iter.hasNext(); ) {
            FotoData f = iter.next();
            new insertAsyncTask(mDBDao).execute(f);
        }


    }


    private static class insertAsyncTask extends AsyncTask<FotoData, Void, Void> {
        private MyDAO mAsyncTaskDao;

        insertAsyncTask(MyDAO dao) {
            mAsyncTaskDao = dao;
        }
        @Override
        protected Void doInBackground(final FotoData... params) {
            mAsyncTaskDao.insert(params[0]);
            Log.i("MyRepository", "number generated: "+params[0].getPath()+"");
            // you may want to uncomment this to check if numbers have been inserted
            int ix=mAsyncTaskDao.howManyElements();
            Log.i("InsertNumber", ix+"");
            return null;
        }
    }


    private static class deleteOneAsyncTask extends AsyncTask<FotoData, Void, Void> {
        private MyDAO mAsyncTaskDao;
        deleteOneAsyncTask(MyDAO dao) {
            mAsyncTaskDao = dao;
        }
        @Override
        protected Void doInBackground(final FotoData... params) {
            mAsyncTaskDao.delete(params[0]);
            return null;
        }
    }


    private static class deleteAsyncTask extends AsyncTask<Void, Void, Void> {
        private MyDAO mAsyncTaskDao;

        deleteAsyncTask(MyDAO dao) {
            mAsyncTaskDao = dao;
        }
        @Override
        protected Void doInBackground(final Void... voids) {
            mAsyncTaskDao.deleteAllFOTO();
            // you may want to uncomment this to check if numbers have been inserted
            int ix=mAsyncTaskDao.howManyElements();
            Log.i("Delete number", ix+"");
            return null;
        }
    }


    private static class selectAllPathAsyncTask extends AsyncTask<Void, Void, List<FotoData>> {
        private MyDAO mAsyncTaskDao;
        public AsyncResponse delegate=null;
        selectAllPathAsyncTask(MyDAO dao ,AsyncResponse asyncResponse) {

            mAsyncTaskDao = dao;
            delegate = asyncResponse;
        }
        @Override
        protected List<FotoData> doInBackground(final Void... voids) {
            List<FotoData> p=new ArrayList<>();
            p=mAsyncTaskDao.retrieveAllFoto();
            return p;
        }
        @Override
        protected void onPostExecute(List<FotoData> result) {
            delegate.processFinish(result);
        }
    }


    //this returns all the data that is in the DB in a list of type FotoData
    public class QueryAllAsyncTask extends AsyncTask<Void, Void, List<FotoData>> {
        private MyDAO mAsyncTaskDao;
        public AsyncResponse delegate=null;

        QueryAllAsyncTask(MyDAO dao,AsyncResponse asyncResponse) {
            mAsyncTaskDao = dao;
            delegate = asyncResponse;
        }
        @Override
        protected List<FotoData> doInBackground(final Void... voids) {
            List<FotoData> fd=new ArrayList<>();
            fd=mAsyncTaskDao.retrieveAllFoto();
            return fd;
        }
        @Override
        protected void onPostExecute(List<FotoData> result) {
            delegate.processFinish(result);
        }
    }

}