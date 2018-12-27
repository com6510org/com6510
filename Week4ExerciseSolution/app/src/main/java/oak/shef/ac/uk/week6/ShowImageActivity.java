/*
 * Copyright (c) 2017. This code has been developed by Fabio Ciravegna, The University of Sheffield. All rights reserved. No part of this code can be used without the explicit written permission by the author
 */

package oak.shef.ac.uk.week6;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;

public class ShowImageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message2);

        Bundle b = getIntent().getExtras();
        int position=-1;
        if(b != null) {
            position = b.getInt("position");
            if (position!=-1){
                ImageView imageView = (ImageView) findViewById(R.id.image);
                ImageElement element= MyAdapter.getItems().get(position);
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
                }
            }

        }

        FloatingActionButton fabShowInformation = (FloatingActionButton) findViewById(R.id.fab_show_information);
        fabShowInformation.setOnClickListener(new View.OnClickListener() {
            View popupView = getLayoutInflater().inflate(R.layout.activity_popupwindow, null);
            @Override
            public void onClick(View view) {
                PopupWindow popupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
                popupWindow.setBackgroundDrawable(new ColorDrawable(Color.WHITE));
                popupWindow.showAtLocation(view,Gravity.BOTTOM, 0, 0);
            }
        });


    }

}
