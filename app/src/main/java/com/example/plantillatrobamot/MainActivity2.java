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
    //definició de variables
    private TextView t;
    private TextView t1;
    private TextView t2;
    private boolean bol = false;
    String paraulasolucio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        //
        t = findViewById(R.id.textView);
        t1 = findViewById(R.id.textView3);
        t2 = findViewById(R.id.textView2);
        //rebuda de missatge
        Intent intent = getIntent() ;
        String messageRestriccions = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
        String messageParaulesPossibles=intent.getStringExtra(MainActivity.EXTRA_MESSAGE2);
        paraulasolucio=intent.getStringExtra(MainActivity.EXTRA_MESSAGE3);
        //comprobació si hi ha restriccions(derrota)
        if(messageRestriccions!=null){
            t1.setText("Oh Oh Oh Oh........");
            t2.setText(paraulasolucio);
            t.setText("Definició no trobada");
            bol=true;
        }else{//cas de victoria
            t1.setText("Enhorabona!!!!!");
            t2.setText(paraulasolucio);
            t.setText("Definició no trobada");
        }
        
        //thread per obtenir les definicions
        Thread thread = new Thread (new Runnable () {
            @Override
            public void run () {
                try {
                    JSONObject jObject = new JSONObject (agafaHTML(paraulasolucio));
                    String def = jObject . getString ("d") ;
                    TextView definicio=findViewById(R.id.textView);
                    //comprobacio de si hi ha definicio
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

    //metode que permet obtenir la definició
    public  String agafaHTML(String paraula){
        String linea="";
        try {
            //accedim a la pagina web
            URL definicio = new URL("https://www.vilaweb.cat/paraulogic/?diec="+paraula);
            BufferedReader in = new BufferedReader (new InputStreamReader(definicio.openStream()));
            StringBuffer buffer = new StringBuffer();
            //lectura de la definicio
            while ((linea = in.readLine()) != null) {
                buffer.append(linea);
            }
            linea= buffer.toString();
            //tancam el buffer
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return linea;
    }

}