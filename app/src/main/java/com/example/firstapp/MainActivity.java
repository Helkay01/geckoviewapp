package com.example.firstapp;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        TextView textView = findViewById(R.id.helloText);
        Button button = findViewById(R.id.clickButton);
        
        button.setOnClickListener(v -> {
            textView.setText("Button clicked!");
        });
    }
}
