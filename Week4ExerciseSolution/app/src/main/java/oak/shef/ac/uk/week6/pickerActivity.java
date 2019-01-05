package oak.shef.ac.uk.week6;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;

import java.util.Calendar;

public class pickerActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String CERO = "0";
    private static final String SLASH = "/";
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
        theDate = (EditText) findViewById(R.id.editTextDate);
       getDate = (ImageButton) findViewById(R.id.buttonDate);
        getDate.setOnClickListener(this);

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
                theDate.setText(fDay + SLASH + fMonth + SLASH + year);


            }
            //Estos valores deben ir en ese orden, de lo contrario no mostrara la fecha actual
            /**
             *También puede cargar los valores que usted desee
             */
        }, year, month, day);
        datePicker.show();

    }
}