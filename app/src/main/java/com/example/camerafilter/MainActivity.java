package com.example.camerafilter;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    Button button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button = findViewById(R.id.button);

        // navigate to next screen
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startNewScreen();
            }
        });
    }
    void startNewScreen(){
        Intent intent = new Intent(MainActivity.this,Activity2.class);
        startActivity(intent);
    }
}


// toast messages


// Camera app

// 1 button click image
// 1 button to save the image in file manager
// image view

// open camera

// toGreyScale()

// save image