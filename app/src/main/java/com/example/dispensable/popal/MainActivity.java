package com.example.dispensable.popal;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {

    public static final String EXTRA_MESSAGE = "com.example.dispensable.MESSAGE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Spinner selectedOpt = (Spinner) findViewById(R.id.spinner);
        selectedOpt.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {

                String[] languages = getResources().getStringArray(R.array.cases);
                Toast.makeText(MainActivity.this, "你点击的是:"+languages[pos], Toast.LENGTH_LONG).show();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Another interface callback
            }
        });
    }

    /** Call when the user click send button **/
    public void sendMessage(View view) {
        Intent intent = new Intent(this, FullscreenActivity.class);
        Toast.makeText(MainActivity.this, "btn clicked!!", Toast.LENGTH_SHORT).show();
        startActivity(intent);
    }

    public void addScenes(View view) {
        Intent intent = new Intent(this, ScenesActivity.class);
        startActivity(intent);
    }
}
