package com.example.dispensable.popal;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startFullScreen(view);
            }
        });
    }

    public void startFullScreen(View view) {
        EditText editText = (EditText) findViewById(R.id.noise_value);
        String message = editText.getText().toString();
        Intent intent = new Intent(this, CanvasActivity.class);
        intent.putExtra("noise_value", message);
        startActivity(intent);
    }
}
