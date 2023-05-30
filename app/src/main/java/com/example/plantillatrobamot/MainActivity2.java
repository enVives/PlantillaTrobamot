package com.example.plantillatrobamot;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.widget.TextView;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

public class MainActivity2 extends AppCompatActivity {
    private TextView t;
    String paraulasolucio;
    String definicio ="";

    Thread thread = new Thread(new Runnable() {
        @Override
        public void run() {
            try {
                String json = agafaHTML(paraulasolucio);
                JSONObject jObject = new JSONObject(json);
                definicio = jObject.getString("d");
            }catch (Exception error){
                System.out.println(error.toString());
            }
        }
    });
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        t = findViewById(R.id.textView);

        Intent intent = getIntent() ;
        String messageRestriccions = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
        String messageParaulesPossibles=intent.getStringExtra(MainActivity.EXTRA_MESSAGE2);
        paraulasolucio=intent.getStringExtra(MainActivity.EXTRA_MESSAGE3);

        if(messageRestriccions!=null){
            t.setText(messageRestriccions+"\n\n"+messageParaulesPossibles);
        }else{
            t.setText(paraulasolucio+"\n");
            thread.run();
            t.setText(Html.fromHtml(definicio));
        }

    }



    public  String agafaHTML(String paraula){
        String linea="";
        try {
            URL definicio = new URL("https://www.vilaweb.cat/paraulogic/?diec="+ paraula);
            BufferedReader in = new BufferedReader (new InputStreamReader(definicio. openStream () ) ) ;
            StringBuffer buffer = new StringBuffer();
            while ((linea = in.readLine()) != null) {
                buffer.append(linea);
                buffer.append("\n");  // Agregar un salto de línea si lo deseas
            }
            linea= buffer.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return linea;
    }

}