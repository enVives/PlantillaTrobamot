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
    private TextView t1;
    private TextView t2;

    private boolean bol = false;
    String paraulasolucio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        t = findViewById(R.id.textView);
        t1 = findViewById(R.id.textView3);
        t2 = findViewById(R.id.textView2);

        Intent intent = getIntent() ;
        String messageRestriccions = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
        String messageParaulesPossibles=intent.getStringExtra(MainActivity.EXTRA_MESSAGE2);
        paraulasolucio=intent.getStringExtra(MainActivity.EXTRA_MESSAGE3);

        if(messageRestriccions!=null){
            t1.setText("Oh Oh Oh Oh........");
            t2.setText(paraulasolucio);
            t.setText("Definició no trobada");
            bol=true;
        }else{
            t1.setText("Enhorabona!!!!!");
            t2.setText(paraulasolucio);
            t.setText("Definició no trobada");
        }
        
        
        Thread thread = new Thread (new Runnable () {
            @Override
            public void run () {
                try {
                    JSONObject jObject = new JSONObject (agafaHTML(paraulasolucio));
                    String def = jObject . getString ("d") ;
                    TextView definicio=findViewById(R.id.textView);
                    if (def.isEmpty()){
                        definicio.setText("Definició no trobada");
                    }else {
                       definicio.setText(Html.fromHtml(def, Html.FROM_HTML_MODE_LEGACY));
                    }
                } catch ( Exception e ) {
                    e . printStackTrace () ;
                }
            }
        });

        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if(bol){
            t.setText(t.getText()+"\n\n"+messageRestriccions+"\n\n"+messageParaulesPossibles);
        }


    }


    public  String agafaHTML(String paraula){
        String linea="";
        try {
            URL definicio = new URL("https://www.vilaweb.cat/paraulogic/?diec="+paraula);
            BufferedReader in = new BufferedReader (new InputStreamReader(definicio.openStream()));
            StringBuffer buffer = new StringBuffer();
            while ((linea = in.readLine()) != null) {
                buffer.append(linea);
            }
            linea= buffer.toString();
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return linea;
    }

}