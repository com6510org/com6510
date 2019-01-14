package oak.shef.teamCuphead.uk.com6510.view;

import android.app.Activity;
import android.arch.lifecycle.ViewModelProviders;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import oak.shef.teamCuphead.uk.com6510.R;
import oak.shef.teamCuphead.uk.com6510.database.AsyncResponse;
import oak.shef.teamCuphead.uk.com6510.model.FotoData;
import oak.shef.teamCuphead.uk.com6510.database.MyDAO;
import oak.shef.teamCuphead.uk.com6510.database.MyRoomDatabase;
import oak.shef.teamCuphead.uk.com6510.viewmodel.MyViewModel;


public class ShowSearchActivity extends AppCompatActivity {

    private MyDAO mDBDao;
    private Activity activity;
    private MyViewModel myViewModel;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private List<FotoData> myPictureList = new ArrayList<>();

    /**
     * Gets the data send to it (title, description and date)
     * and creates an async process and send it to the viewModel with the data previously retrieved
     * finally it gets the data from the query back and display it in the view
     */
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

        String title = getIntent().getStringExtra("TITLE");
        String desc = getIntent().getStringExtra("DESC");
        String date = getIntent().getStringExtra("DATE");
        AsyncResponse resp = new AsyncResponse() {
            public void processFinish(List<FotoData> output) {
                if (!output.isEmpty()) {
                    myPictureList.addAll(output);
                    mAdapter = new MyAdapter(myPictureList);
                    mRecyclerView.setAdapter(mAdapter);
                } else if (output.isEmpty()) {


                    mAdapter = new MyAdapter(myPictureList);
                    mRecyclerView.setAdapter(mAdapter);
                    TextView textMessage = (TextView) findViewById(R.id.textMessage);
                    textMessage.setText("Sorry we couldn't find anything");
                }
            }
        };


        myViewModel.searchIt(title, desc, date, resp);
    }


}
