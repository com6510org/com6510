package oak.shef.ac.uk.week6;


import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import android.view.View;
import android.widget.Button;

import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;

import java.util.Calendar;


public class SearchActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String CERO = "0";
    private static final String COLON = ":";
    public final Calendar c = Calendar.getInstance();
    final int month = c.get(Calendar.MONTH);
    final int day = c.get(Calendar.DAY_OF_MONTH);
    final int year = c.get(Calendar.YEAR);
    EditText theDate;
    ImageButton getDate;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_view);

        Button buttonSearch = (Button)findViewById(R.id.buttonSearch);
        final EditText textTitle = (EditText) findViewById(R.id.editTextTitle);
        final EditText textDesc = (EditText) findViewById(R.id.editTextDescription);
        theDate = (EditText) findViewById(R.id.editTextDate);
        getDate = (ImageButton) findViewById(R.id.buttonDate);
        getDate.setOnClickListener(this);
        buttonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title= textTitle.getText().toString();
                String desc= textDesc.getText().toString();
                String date= theDate.getText().toString();
                Intent intent = new Intent(getBaseContext(), ShowSearchActivity.class);
                intent.putExtra("TITLE", title);
                intent.putExtra("DESC", desc);
                intent.putExtra("DATE", date);
                startActivity(intent);
            }
        });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonDate:
                getTheSelectedDate();
                break;
        }
    }

    private void getTheSelectedDate() {
        DatePickerDialog datePicker = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                final int actualMonth = month + 1;
                String fDay= (dayOfMonth < 10) ? CERO + String.valueOf(dayOfMonth) : String.valueOf(dayOfMonth);
                String fMonth = (actualMonth < 10) ? CERO + String.valueOf(actualMonth) : String.valueOf(actualMonth);
                theDate.setText(year+ COLON + fMonth + COLON + fDay);


            }
        }, year, month, day);
        datePicker.show();

    }


}
