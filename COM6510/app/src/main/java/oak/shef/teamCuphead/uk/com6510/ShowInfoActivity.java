/*
 * Copyright (c) 2017. This code has been developed by Fabio Ciravegna, The University of Sheffield. All rights reserved. No part of this code can be used without the explicit written permission by the author
 */

package oak.shef.teamCuphead.uk.com6510;

import android.app.Activity;
import android.arch.lifecycle.LiveData;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import oak.shef.teamCuphead.uk.com6510.database.FotoData;
import oak.shef.teamCuphead.uk.com6510.database.MyDAO;
import oak.shef.teamCuphead.uk.com6510.database.MyRoomDatabase;

public class ShowInfoActivity extends AppCompatActivity {
    private Activity activity;
    private Button buttonSave;
    TextView textView, textViewDesc, textViewDate;
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
        buttonSave = (Button) findViewById(R.id.buttonSave);
        Bundle b = getIntent().getBundleExtra("bundle");
        int position=-1;
        if(b != null) {
            position = b.getInt("position");
            if (position!=-1){
                ImageView imageView = (ImageView) findViewById(R.id.image);
                textView = (TextView) findViewById(R.id.textView);
                textViewDesc = (TextView) findViewById(R.id.textViewDesc);
                textViewDate = (TextView) findViewById(R.id.textViewDate);
                ImageElement element= MyAdapter.getItems().get(position);
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
                    textView.setText(element.fotodata.getTitle());
                    textViewDesc.setText(element.fotodata.getDescription());
                    textViewDate.setText(element.fotodata.getDate());
                }
            }

        }
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title= textView.getText().toString();
                String desc= textViewDesc.getText().toString();
                fd.setTitle(title);
                fd.setDescription(desc);
                Log.i("CheckPoint"," !4! "+ fd.toString());
                new UpdateAsyncTask(mDBDao).execute(fd);


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

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            // land do nothing is ok
        } else if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            // port do nothing is ok
        }
    }


}
