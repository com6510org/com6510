package oak.shef.ac.uk.week6;

import android.app.Activity;
import android.arch.lifecycle.ViewModelProviders;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.TextView;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import oak.shef.ac.uk.week6.database.FotoData;
import oak.shef.ac.uk.week6.database.MyDAO;
import oak.shef.ac.uk.week6.database.MyRoomDatabase;


public class ShowSearchActivity  extends AppCompatActivity {

    private MyDAO mDBDao;
    private Activity activity;
    private MyViewModel myViewModel;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter  mAdapter;
    private List<ImageElement> myPictureList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);


        MyRoomDatabase db = MyRoomDatabase.getDatabase(this);
        mDBDao = db.myDao();

        activity = this;
        mRecyclerView = (RecyclerView) findViewById(R.id.grid_recycler_view);
        // set up the RecyclerView
        int numberOfColumns = 3;
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, numberOfColumns));


        myViewModel = ViewModelProviders.of(this).get(MyViewModel.class);


      QueryAllAsyncTask queryAllAsyncTask=new QueryAllAsyncTask(mDBDao,new AsyncResponse(){
            public void processFinish(List<FotoData> output) {
                if (!output.isEmpty()){
                    myPictureList.addAll(getFotoData(output));
                    Log.i("CheckPoint",myPictureList.size()+" !3! ");
                    mAdapter= new MyAdapter(myPictureList);
                    mRecyclerView.setAdapter(mAdapter);
                }
                else if(output.isEmpty()){

                    Log.i("CheckPoint",myPictureList.size()+"  !2!  ");

                   mAdapter= new MyAdapter(myPictureList);
                    mRecyclerView.setAdapter(mAdapter);
                   TextView textMessage = (TextView) findViewById(R.id.textMessage);
                   textMessage.setText("Sorry we couldn't find anything");
                }
            }
        });


        queryAllAsyncTask.execute();
    }


    private List<ImageElement> getFotoData(List<FotoData> returnedPath) {
        List<ImageElement> imageElementList= new ArrayList<>();
        for (FotoData path: returnedPath){
            ImageElement element= new ImageElement(path);
            imageElementList.add(element);
        }
        return imageElementList;
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
            String title= getIntent().getStringExtra("TITLE");
            String desc= getIntent().getStringExtra("DESC");
            String date= getIntent().getStringExtra("DATE");

            if (title.isEmpty() && desc.isEmpty() && !date.isEmpty() )
            {
                fd=mAsyncTaskDao.SearchFotosByDate( date);
            }
            else if(!title.isEmpty() && desc.isEmpty() && date.isEmpty())
            {
                fd=mAsyncTaskDao.SearchFotosByTitle( title);
            }
            else if(title.isEmpty() && !desc.isEmpty() && date.isEmpty())
            {
                fd=mAsyncTaskDao.SearchFotosByDescription( desc);
            }
            else if(!title.isEmpty() && !desc.isEmpty() && date.isEmpty())
            {
                fd=mAsyncTaskDao.SearchFotosByDescTitle( desc, title);
            }
            else if(title.isEmpty() && !desc.isEmpty() && !date.isEmpty())
            {
                fd=mAsyncTaskDao.SearchFotosByDescDate( desc, date);
            }
            else if(!title.isEmpty() && desc.isEmpty() && !date.isEmpty())
            {
                fd=mAsyncTaskDao.SearchFotosByTitleDate( title, date);
            }
            else
            {
                fd=mAsyncTaskDao.SearchFotos(title, desc, date);

            }


            return fd;
        }
        @Override
        protected void onPostExecute(List<FotoData> result) {
            delegate.processFinish(result);
        }
    }




}
