package com.example.plantillatrobamot;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.widget.TextView;

public class MainActivity2 extends AppCompatActivity {
    private TextView t;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        t = findViewById(R.id.textView);

        Intent intent = getIntent() ;
        String messageRestriccions = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
        String messageParaulesPossibles=intent.getStringExtra(MainActivity.EXTRA_MESSAGE2);

        t.setText(messageRestriccions+"\n\n"+messageParaulesPossibles);
    }

}