package oak.shef.ac.uk.week6;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;



public class SearchActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_view);

        Button buttonSearch = (Button)findViewById(R.id.buttonSearch);
        final EditText textTitle = (EditText) findViewById(R.id.editTextTitle);
        final EditText textDesc = (EditText) findViewById(R.id.editTextDescription);
        buttonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title= textTitle.getText().toString();
                String desc= textDesc.getText().toString();
                Intent intent = new Intent(getBaseContext(), ShowSearchActivity.class);
                intent.putExtra("TITLE", title);
                intent.putExtra("DESC", desc);
                startActivity(intent);
            }
        });
    }

}
