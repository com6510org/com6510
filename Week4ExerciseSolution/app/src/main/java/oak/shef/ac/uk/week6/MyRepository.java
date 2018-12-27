package oak.shef.ac.uk.week6;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.os.AsyncTask;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import oak.shef.ac.uk.week6.database.FotoData;
import oak.shef.ac.uk.week6.database.MyDAO;
import oak.shef.ac.uk.week6.database.MyRoomDatabase;

class MyRepository extends ViewModel{
    private final MyDAO mDBDao;

    public MyRepository(Application application) {
        MyRoomDatabase db = MyRoomDatabase.getDatabase(application);
        mDBDao = db.myDao();
    }

    /**
     * it gets the data when changed in the db and returns it to the ViewModel
     * @return
     */
    public LiveData<FotoData> getFotoData() {
        return mDBDao.retrieveOneFoto();
    }
    public void deletAll(){
        new deleteAsyncTask(mDBDao).execute();
    }
    public LiveData<FotoData> getFoto(String path) {
        return mDBDao.retrieveSelectFoto(path);
    }

//    }
    /**
     * called by the UI to request the generation of a new random number
     */
    public void generateNewFoto(String path,String date,String latitude,String longitude) {
        //insert in here a new foto maybe
        String t = "title example";
        String d= "description";
        String p= path;
        String da=date;
        String lat=latitude;
        String lon=longitude;
        new insertAsyncTask(mDBDao).execute(new FotoData(t, d, p,da,lat,lon));
    }


    private static class insertAsyncTask extends AsyncTask<FotoData, Void, Void> {
        private MyDAO mAsyncTaskDao;
        private LiveData<FotoData> fotoData;

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

}