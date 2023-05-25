package com.example.plantillatrobamot;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

public class MainActivity extends AppCompatActivity {
    // Variables de lògica del joc
    private int lengthWord = 5;
    private int maxTry = 6;
    private int x = 0;
    private int y = 0;
    private int prova =0;
    private int numeropalabras=0;
    private String palabrasolucion="";
    private String palabraEnviada="";
    UnsortedArrayMapping lletresSolucio;
    UnsortedArrayMapping pistesDescobertes; //per guardar les pistes descobertes
    // Variables de construcció de la interfície
    private int num_combinacions;
    public static String grayColor = "#D9E1E8";
    public static String ColorCursor="#FCBA03";
    private int widthDisplay;
    private int heightDisplay;
    HashMap<String, String> diccionari = new HashMap<String, String>();
    java.util.Iterator iter;

    TreeMap<String,String> arbre= new TreeMap();
    private Iterator it;
    GradientDrawable gd2 = new GradientDrawable();
    GradientDrawable gd = new GradientDrawable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Object to store display information
        DisplayMetrics metrics = new DisplayMetrics();
        // Get display information
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        widthDisplay = metrics.widthPixels;
        heightDisplay = metrics.heightPixels;

        crearInterficie();
    }

    @Override
    protected void onStart() {
        super.onStart();
        hideSystemUI();
    }

    private void crearInterficie() {
        num_combinacions =0;
        crearGraella();
        llegirdiccionari();
        paraulasolucio();
        iniciarConjuntLletres();
        crearTeclat2();

        //prova();
    }

    private void paraulasolucio(){
        String clau="";
        Random ran = new Random();
        int numero = ran.nextInt(numeropalabras);
        iter = diccionari.entrySet().iterator();
        for (int i = 0; i < numero; i++) {
            if(iter.hasNext()){
                Map.Entry entry = (Map.Entry) iter.next();
                palabrasolucion = (String) entry.getValue();
            }
        }

    }

    private void crearGraella() {
        ConstraintLayout constraintLayout = findViewById(R.id.layout);

        // Definir les característiques del "pinzell"
        gd.setCornerRadius(5);
        gd.setStroke(3, Color.parseColor(grayColor));

        int textViewSize=150;
        int amplaria=145,altura=150;
        int separacion=20;
        // Crear un TextView
        for(int i=0; i<maxTry;i++){
            for (int j=0;j<lengthWord;j++){
                TextView textView = new TextView(this);
                textView.setText("");
                if(i==0 &&j==0){
                    gd2.setCornerRadius(5);
                    gd2.setStroke(3, Color.parseColor(ColorCursor));
                    textView.setBackground(gd2);
                }else{
                    textView.setBackground(gd);
                }
                String fila_columna=j+""+i;
                textView.setId(Integer.parseInt(fila_columna));
                textView.setWidth(textViewSize);
                textView.setHeight(textViewSize);
                // Posicionam el TextView
                textView.setX(amplaria+((j-1)*separacion)+(j*textViewSize));
                textView.setY(altura +((i-1)*separacion)+(i*textViewSize));
                textView.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
                textView.setTextSize(30);
                // Afegir el TextView al layout
                constraintLayout.addView(textView);
            }
        }
    }

    private void crearTeclat2(){

        ConstraintLayout constraintLayout = findViewById(R.id.layout);
        int buttonWidth = 103;
        int buttonHeight = 90;
        int separació_x = 15;

        ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);
        params.height = buttonHeight;
        params.width = buttonWidth;

        float altura = heightDisplay -50;
        float amplaria = widthDisplay;
        it = lletresSolucio.iterator();
        for(int i = 0; i<3 ;i++){
            amplaria = widthDisplay;
            for(int j =0;j <9;j++){
                Button boto = new Button(this);
                if(it.hasNext()){
                UnsortedArrayMapping.Pair p = (UnsortedArrayMapping.Pair) it.next();
                boto.setText(p.getKey().toString());}else{
                    boto.setText("");
                }
                boto.setLayoutParams(params);
                boto.setBackgroundColor(Color.parseColor(grayColor));
                boto.setX(amplaria - (buttonWidth+separació_x));
                boto.setY(altura - (buttonHeight + separació_x));
                amplaria = boto.getX();
                constraintLayout.addView(boto);
                boto.setOnClickListener(this::onClick);
            }
            altura = altura - (buttonHeight + separació_x);
        }

        float mig = widthDisplay/2;
        buttonWidth = 180;
        params = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);
        params.height = buttonHeight;
        params.width = buttonWidth;


        Button boto = new Button(this);
        boto.setText("Esborrar");
        boto.setLayoutParams(params);
        boto.setBackgroundColor(Color.parseColor(grayColor));
        boto.setX(mig-separació_x-buttonWidth);
        boto.setY(altura- (separació_x*2) -buttonHeight);
        constraintLayout.addView(boto);
        boto.setOnClickListener(this::onClick);


        boto = new Button(this);
        boto.setText("Enviar");
        boto.setLayoutParams(params);
        boto.setBackgroundColor(Color.parseColor(grayColor));
        boto.setX(mig+separació_x);
        boto.setY(altura- (separació_x*2) -buttonHeight);
        constraintLayout.addView(boto);

        boto.setOnClickListener(this::onClick);

        TextView paraula_sol = new TextView(this);
        paraula_sol.setText(palabrasolucion);

        paraula_sol.setId((int) 100);
        paraula_sol.setX(widthDisplay/2);
        paraula_sol.setY(100+heightDisplay/2);

        paraula_sol.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
        // Afegir el TextView al layout
        constraintLayout.addView(paraula_sol);

        TextView combinacions = new TextView(this);
        String s = ""+num_combinacions;
        combinacions.setText(s);
        combinacions.setId((int) 101);
        combinacions.setX(widthDisplay/2);
        combinacions.setY(150+heightDisplay/2);
        constraintLayout.addView(combinacions);

    }
    public void onClick(View v) {
        Button boto = (Button) v;
        String lletra = boto.getText().toString();

        TextView textactual=null;
        String id= x+""+y;
        if((lletra != "Enviar")&&(lletra != "Esborrar")&&(x!= lengthWord)){
            textactual = findViewById ( Integer . valueOf ( id ) . intValue () );
            TextView textsiguiente;
            textactual.setText(lletra);
            textactual.setBackground(gd);


            x+=1;
            id=x+""+y;
            if(x<lengthWord) {
                textactual = findViewById(Integer.valueOf(id).intValue());
                textactual.setBackground(gd2);
            }else{
                id = (x-1)+""+y;
                textactual = findViewById(Integer.valueOf(id).intValue());
                textactual.setBackground(gd2);
            }
        }else if((lletra == "Enviar")&&(y != maxTry-1)){
            if(x != lengthWord){
                Context context = getApplicationContext();
                CharSequence mostra = "LLetres Insuficients";
                int duration = Toast.LENGTH_LONG;

                Toast toast = Toast.makeText(context,mostra,duration);
                toast.show();
            }else {
                id = (x - 1) + "" + y;
                textactual = findViewById(Integer.valueOf(id).intValue());
                textactual.setBackground(gd);

                //Obtenció de la paraula TO DO

                TextView auxiliar;

                for(int x =0;x<lengthWord;x++){
                    auxiliar = findViewById(Integer.valueOf(x+""+y).intValue());
                    palabraEnviada += (String) auxiliar.getText();
                }

                comprobacio();

                x = 0;
                y += 1;
                id = x + "" + y;
                TextView textaux = findViewById(Integer.valueOf(id).intValue());
                textaux.setBackground(gd2);

            }


        }else if((lletra == "Esborrar")){
            if(x==lengthWord){
                x--;
                id=x+""+y;
            }
            textactual=findViewById(Integer.valueOf(id).intValue());
            textactual.setText("");
            x--;
            textactual.setBackground(gd);
            if (x<0){
                x++;
            }
            id=x+""+y;
            textactual=findViewById(Integer.valueOf(id).intValue());
            textactual.setBackground(gd2);
        }
    }

    private void iniciarConjuntLletres(){
        String abecedari ="ÇZYXWVUTRSRQPONMLKJIHGFEDCBA";
        String minuscula = abecedari.toLowerCase();

        //Iniciar mapping de les lletres
        lletresSolucio = new UnsortedArrayMapping<String,UnsortedLinkedListSet<Integer>>(abecedari.length());
        pistesDescobertes = new UnsortedArrayMapping<String,UnsortedLinkedListSet<Integer>>(abecedari.length());

        for (int i=0;i< abecedari.length();i++){
            UnsortedLinkedListSet<Integer> llistaPosicions= new UnsortedLinkedListSet<Integer>();
            UnsortedLinkedListSet<Integer> llistaPosicions1= new UnsortedLinkedListSet<Integer>();
            //Bucle per afegir la posició corresponent de les lletres que es troben a sa paraula solucio
            for (int j = 0; j < palabrasolucion.length(); j++) {
                if (palabrasolucion.charAt(j)==minuscula.charAt(i)){
                    llistaPosicions.add(j+1); //Posicio de la lletra incial ==1 (Pot ser se tendra que cambiar)
                }
            }

            lletresSolucio.put(abecedari.charAt(i),llistaPosicions);
            pistesDescobertes.put(abecedari.charAt(i),llistaPosicions1);
        }
    }
    private void llegirdiccionari(){
        InputStream is = getResources().openRawResource(R.raw.paraules);
        BufferedReader buffer=null;
        try{
            buffer = new BufferedReader(new InputStreamReader(is)) ;
            String linia = buffer.readLine();

            while(linia!=null){
                String [] paraules = linia.split(";");
                if(paraules[0].length()== lengthWord){
                    diccionari.put(paraules[0],paraules[1]);
                    arbre.put(paraules[0],paraules[1]);
                    numeropalabras++;
                }
                linia = buffer.readLine();
            }

        }catch(IOException error){
            System.out.println(error.toString());
        }finally{
            try{
                buffer.close();
            }catch(Exception error){
                System.out.println(error.toString());
            }
        }



    }

    private void comprobacio(){
        //Si la paraula no existeix
        String minuscula = palabraEnviada.toLowerCase();
        if(minuscula.equals(palabrasolucion)){
            Context context = getApplicationContext();
            CharSequence mostra = "Paraula Correcte";
            int duration = Toast.LENGTH_LONG;

            Toast toast = Toast.makeText(context,mostra,duration);
            toast.show();

            String id;
            for (int i = 0; i < palabraEnviada.length(); i++) {
                id = i+""+y;
                TextView textaux = findViewById(Integer.valueOf(id).intValue());
                textaux.setBackgroundColor(Color.GREEN);
            }
        }else{
            if (!diccionari.containsValue(minuscula)){
                Context context = getApplicationContext();
                CharSequence mostra = "Paraula no vàlida!";
                int duration = Toast.LENGTH_LONG;

                Toast toast = Toast.makeText(context,mostra,duration);
                toast.show();

                for(int x =0;x<lengthWord;x++){
                    String id = x+""+y;
                    TextView textaux = findViewById(Integer.valueOf(id).intValue());
                    textaux.setText("");
                }
                y-=1;
                palabraEnviada = "";
            }else {
                int x = 0;
                String id;
                for (int i = 0; i < palabraEnviada.length(); i++) {
                    id = i+""+y;
                    UnsortedLinkedListSet<Integer> aux = (UnsortedLinkedListSet<Integer>) lletresSolucio.get(palabraEnviada.charAt(i));
                    if (aux.isEmpty()){
                        TextView textaux = findViewById(Integer.valueOf(id).intValue());
                        textaux.setBackgroundColor(Color.GRAY);
                    }
                    if(aux.contains(i+1)){
                        TextView textaux = findViewById(Integer.valueOf(id).intValue());
                        textaux.setBackgroundColor(Color.GREEN);
                        UnsortedLinkedListSet<Integer> llistaPosicions= new UnsortedLinkedListSet<Integer>();
                        llistaPosicions = (UnsortedLinkedListSet<Integer>) pistesDescobertes.get(palabraEnviada.charAt(i));
                        llistaPosicions.add(i+1);
                        pistesDescobertes.put(palabraEnviada.charAt(i),llistaPosicions);
                        //Verd TO DO
                    }
                    if (!aux.isEmpty() && !aux.contains(i+1)){
                        TextView textaux = findViewById(Integer.valueOf(id).intValue());
                        textaux.setBackgroundColor(Color.YELLOW);
                        //Groc TO DO
                    }


                }
                //combinacions();
                palabraEnviada = "";
            }
        }


    }
    private int numero_restriccions(){
        Iterator it = pistesDescobertes.iterator();
        int n =0;
        while(it.hasNext()){
            UnsortedArrayMapping.Pair p = (UnsortedArrayMapping.Pair) it.next();
            n+=1;
        }

        return n;
    }
    private void combinacions(){
        TreeMap<String,String> arbre_aux= new TreeMap();
        //Iterator it = arbre.entrySet().iterator();
        int numero_total = numero_restriccions();
        int numero_descobert =0;
        String paraula="";
        String paraula_accent="";

        while(it.hasNext()){
            Map.Entry entry = (Map.Entry) it.next();
            paraula = (String) entry.getValue();
            paraula_accent = (String) entry.getKey();

            for(int i =0;i<paraula.length();i++){
                UnsortedLinkedListSet<Integer> aux = (UnsortedLinkedListSet<Integer>) pistesDescobertes.get(paraula.charAt(i));
                if(aux.isEmpty()){
                }else if(aux.contains(i+1)){
                    numero_descobert +=1;
                }
            }
            if(numero_total == numero_descobert){
                arbre_aux.put(paraula_accent,paraula);
                num_combinacions++;
            }
        }
        TextView textaux = findViewById(Integer.valueOf(101).intValue());
        textaux.setText(num_combinacions);


    }


    private void hideSystemUI() {
        // Enables regular immersive mode.
        // For "lean back" mode, remove SYSTEM_UI_FLAG_IMMERSIVE.
        // Or for "sticky immersive," replace it with SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE
                        // Set the content to appear under the system bars so that the
                        // content doesn't resize when the system bars hide and show.
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE  // no posar amb notch
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        // Hide the nav bar and status bar
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }
}
