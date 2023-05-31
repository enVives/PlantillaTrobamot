package com.example.plantillatrobamot;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.graphics.Color;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;
import android.content.Intent;

public class MainActivity extends AppCompatActivity {
    private final int posicioIncorrecte = 100;
    // Variables de lògica del joc
    private int lengthWord = 5;
    private int maxTry = 6;
    private int x = 0;
    private int y = 0;
    private int numeroparaules =0; //contador de paraules del diccionari
    private String paraulasolucio =""; //conté la paraula solució
    private String palabrasolucioaccent=""; //paraula solució amb accent
    private String palabraEnviada=""; //paraula que enviam per teclat
    private int num_combinacions;   //acumula el numero de solucions disponibles
    UnsortedArrayMapping lletresSolucio; //mapping del teclat, on key = lletra, value = llista de posicions
    UnsortedArrayMapping lletresCorrectes; //Mapping on, cada vegada que pitjam enviar, guardam les posicions
                        //de les lletres verdes i grogues, això ens interessa perquè amb aquestes lletres
                        //hi pot haver conflicte. Suposem que a la paraula solució hi ha dues lletres
                        //'B' i nosaltres n'hem posat 3, una a la posició correcte i les altres dues
                        //a posicions incorrectes. D'aquestes tres només pintarem la verda i una de groga,
                        //ja que a la paraula solució només hi ha dues 'B's i això dona entendre que efectivament
                        //la paraula solució només té dues 'B's.
    UnsortedArrayMapping pistesDescobertes; //mapa on anam guardant les pistes que anam descobrint de cada lletre
    HashMap<String, String> diccionari = new HashMap<String, String>(); //diccionari de paraules catalanes
    TreeMap<String,String> arbre= new TreeMap();//arbre amb les solucios posibles en base a les restriccions

    // Variables de construcció de la interfície
    public static String grayColor = "#D9E1E8"; //color caselles
    public static String ColorCursor="#FCBA03"; //color taronja per cursor
    private int widthDisplay;   //amplaria de la pantalla
    private int heightDisplay; //altura de la pantalla
    private boolean acabat; // acabat = true si hem trobat la paraula solució
    public static final String EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE "; //missatges per a la finestra nova
    public static final String EXTRA_MESSAGE2 = "com.example.myfirstapp.MESSAGE2 ";
    public static final String EXTRA_MESSAGE3 = "com.example.myfirstapp.MESSAGE3 ";
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
        acabat = false;
        crearGraella(); //cream la graella de les lletres
        llegirdiccionari(); //llegim les paraules del diccionari
        paraulasolucio(); //cream una paraula solució
        iniciarConjuntLletres(); //iniciam els mappings que utilitzarem de les lletres
        crearTeclat2(); //cream el teclat
    }

    //metode per generar la paraula solucio
    private void paraulasolucio(){
        //generació paraula aleatoria
        Random ran = new Random();
        int numero = ran.nextInt(numeroparaules);
        //cerca de la paraula
        Iterator iter = diccionari.entrySet().iterator();
        for (int i = 0; i < numero; i++) {
            if(iter.hasNext()){
                Map.Entry entry = (Map.Entry) iter.next();
                //obtencio de la paraula solucio
                paraulasolucio = (String) entry.getValue();
                palabrasolucioaccent = (String)entry.getKey();
            }
        }
    }
    //metode per crear la graella de textviews
    private void crearGraella() {
        ConstraintLayout constraintLayout = findViewById(R.id.layout);
        // Definir les característiques del "pinzell"
        gd.setCornerRadius(5);
        gd.setStroke(3, Color.parseColor(grayColor));
        int separacionPantalla=90;
        int separacion=20;
        //definim caracteristiques d'amplitud i altura dels textviews de la graella
        int textViewWidth=((widthDisplay-separacionPantalla*2-separacion*(lengthWord-1))/lengthWord);
        int textViewHeigh=((heightDisplay-separacionPantalla-850)/maxTry);
        // Crear un TextView
        for(int i=0; i<maxTry;i++){
            for (int j=0;j<lengthWord;j++){
                TextView textView = new TextView(this);
                textView.setText("");
                //tractament del cursor
                if(i==0 &&j==0){
                    gd2.setCornerRadius(5);
                    gd2.setStroke(3, Color.parseColor(ColorCursor));
                    textView.setBackground(gd2);
                }else{
                    textView.setBackground(gd);
                }
                String fila_columna=j+""+i;
                textView.setId(Integer.parseInt(fila_columna));
                textView.setWidth(textViewWidth);
                textView.setHeight(textViewHeigh);
                // Posicionam el TextView
                textView.setX(j*textViewWidth+separacionPantalla+separacion*j);
                textView.setY(i*textViewHeigh+separacionPantalla+separacion*i);
                textView.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
                textView.setTextSize(30);
                // Afegir el TextView al layout
                constraintLayout.addView(textView);
            }
        }
    }
    //metode per crear el teclat
    private void crearTeclat2(){
        //constants dels botons
        ConstraintLayout constraintLayout = findViewById(R.id.layout);
        int buttonWidth = 103;
        int buttonHeight = 90;
        int separació_x = 15;
        ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);
        params.height = buttonHeight;
        params.width = buttonWidth;
        float altura = heightDisplay -50;
        float amplaria = widthDisplay;
        //recorregut per creació dels botons
        Iterator it = lletresSolucio.iterator();
        for(int i = 0; i<3 ;i++){
            amplaria = widthDisplay;
            for(int j =0;j <9;j++){
                Button boto = new Button(this);
                if(it.hasNext()){
                UnsortedArrayMapping.Pair p = (UnsortedArrayMapping.Pair) it.next();
                boto.setText(p.getKey().toString());
                //tractament per establir id en un futur i poder pintar de color
                char letra =p.getKey().toString().charAt(0);
                int codigoAscii=(int)letra;
                boto.setId(codigoAscii);
                }else{
                    boto.setText("");
                }
                //estetica dels botons
                boto.setLayoutParams(params);
                boto.setBackgroundColor(Color.parseColor(grayColor));
                //posicionament del botons
                boto.setX(amplaria - (buttonWidth+separació_x));
                boto.setY(altura - (buttonHeight + separació_x));
                amplaria = boto.getX();
                constraintLayout.addView(boto);
                boto.setOnClickListener(this::onClick);
            }
            altura = altura - (buttonHeight + separació_x);
        }
        //obtencio de parametres
        float mig = widthDisplay/2;
        buttonWidth = 180;
        params = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);
        params.height = buttonHeight;
        params.width = buttonWidth;

        //creació botó de borrar
        Button boto = new Button(this);
        boto.setText("Esborrar");
        boto.setLayoutParams(params);
        boto.setBackgroundColor(Color.parseColor(grayColor));
        //posicionament del boto
        boto.setX(mig-separació_x-buttonWidth);
        boto.setY(altura- (separació_x*2) -buttonHeight);
        constraintLayout.addView(boto);
        boto.setOnClickListener(this::onClick);

        //creació botó de enviar
        boto = new Button(this);
        boto.setText("Enviar");
        boto.setLayoutParams(params);
        boto.setBackgroundColor(Color.parseColor(grayColor));
         //posicionament del boto
        boto.setX(mig+separació_x);
        boto.setY(altura- (separació_x*2) -buttonHeight);
        constraintLayout.addView(boto);

        boto.setOnClickListener(this::onClick);

        TextView paraula_sol = new TextView(this);
        paraula_sol.setText(paraulasolucio);

        paraula_sol.setId(Integer.valueOf(127).intValue());
        paraula_sol.setX(widthDisplay/2);
        paraula_sol.setY(300+heightDisplay/2);

        paraula_sol.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
        constraintLayout.addView(paraula_sol);
        //creació text view per saber el nombre de solucions posibles
        TextView combinacions = new TextView(this);
        String s = "Hi ha "+num_combinacions+" solucions possibles";
        combinacions.setText(s);
        combinacions.setId(Integer.valueOf(126).intValue());
        //posicionamet de textview
        combinacions.setX(-200+widthDisplay/2);
        combinacions.setY(350+heightDisplay/2);
        constraintLayout.addView(combinacions);

    }
    //metode onclick per comprovar sihi ha click
    public void onClick(View v) {
        //obtencio de boto pulsat
        Button boto = (Button) v;
        String lletra = boto.getText().toString();
        TextView textactual=null;
        String id= x+""+y;
        //comprobacions logiques del joc
        if((lletra != "Enviar")&&(lletra != "Esborrar")&&(x!= lengthWord)){
            textactual = findViewById ( Integer . valueOf ( id ) . intValue () );
            //tractament del cursor dels texts views
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
            //tractament de longitud
            if(x != lengthWord){
                Context context = getApplicationContext();
                CharSequence mostra = "LLetres Insuficients";
                int duration = Toast.LENGTH_LONG;

                Toast toast = Toast.makeText(context,mostra,duration);
                toast.show();
            }else {
                //tractament del cursor y obtencio de la paraula enviada
                id = (x - 1) + "" + y;
                textactual = findViewById(Integer.valueOf(id).intValue());
                textactual.setBackground(gd);
                TextView auxiliar;
                for(int x =0;x<lengthWord;x++){
                    //obetencio de la paraula enviada
                    auxiliar = findViewById(Integer.valueOf(x+""+y).intValue());
                    palabraEnviada += (String) auxiliar.getText();
                }
                //comprobació de la paraula enviada
                comprobacio();
                //tractament cursor
                x = 0;
                y += 1;
                id = x + "" + y;
                TextView textaux = findViewById(Integer.valueOf(id).intValue());
                textaux.setBackground(gd2);
            }
        }else if((lletra == "Esborrar")){
            //tractament del cursor
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
        }else if((lletra == "Enviar")&&(y == maxTry-1)){
            //comprobació de paraula enviada
            if(x != lengthWord){
                Context context = getApplicationContext();
                CharSequence mostra = "LLetres Insuficients";
                int duration = Toast.LENGTH_LONG;

                Toast toast = Toast.makeText(context,mostra,duration);
                toast.show();
            }else { //cas darrera fila
                //tractament cursor
                id = (x - 1) + "" + y;
                textactual = findViewById(Integer.valueOf(id).intValue());
                textactual.setBackground(gd);
                //tractament paraula enviada
                TextView auxiliar;
                for(int x =0;x<lengthWord;x++){
                    auxiliar = findViewById(Integer.valueOf(x+""+y).intValue());
                    //obtencio paraula solucio
                    palabraEnviada += (String) auxiliar.getText();
                }
                //comprobació de la paraula enviada
                comprobacio();
                //si acabat mostrar finestra final
                if(!acabat){
                    newWindow();
                }
            }
        }
    }
    //metode per inicialitzar els mappings amb les seves variables
    private void iniciarConjuntLletres(){
        //abecedari
        String abecedari ="ÇZYXWVUTRSRQPONMLKJIHGFEDCBA";
        String minuscula = abecedari.toLowerCase();
        //Iniciar mapping de les lletres
        lletresSolucio = new UnsortedArrayMapping<String,UnsortedLinkedListSet<Integer>>(abecedari.length());
        lletresCorrectes = new UnsortedArrayMapping<String,UnsortedLinkedListSet<Integer>>(abecedari.length());
        pistesDescobertes = new UnsortedArrayMapping<String,UnsortedLinkedListSet<Integer>>(abecedari.length());
        //incialització de les llistes
        for (int i=0;i< abecedari.length();i++){
            UnsortedLinkedListSet<Integer> llistaPosicions= new UnsortedLinkedListSet<Integer>();
            UnsortedLinkedListSet<Integer> llistaPosicions1= new UnsortedLinkedListSet<Integer>();
            UnsortedLinkedListSet<Integer> llistaPosicions2= new UnsortedLinkedListSet<Integer>();
            //Bucle per afegir la posició corresponent de les lletres que es troben a sa paraula solucio
            for (int j = 0; j < paraulasolucio.length(); j++) {
                if (paraulasolucio.charAt(j)==minuscula.charAt(i)){
                    llistaPosicions.add(j+1); //Posicio de la lletra incial ==1 (Pot ser se tendra que cambiar)
                }
            }
            lletresSolucio.put(abecedari.charAt(i),llistaPosicions);
            lletresCorrectes.put(abecedari.charAt(i),llistaPosicions1);
            pistesDescobertes.put(abecedari.charAt(i),llistaPosicions2);
        }
    }

    //al mapping de lletres correctes només ens interessa guardar les posicions de les lletres verdes
    //i grogues cada vegada que pitjam enviar, per tant, després d'haver pitjat enviar i haver pintat
    //ja les lletres buidarem el mapping perquè no s'acumulin posicions, ja que això per aquest cas no ens
    //interessa.
    private void reiniciar_lletresCorrectes(){
        String abecedari ="ÇZYXWVUTRSRQPONMLKJIHGFEDCBA";
        lletresCorrectes = new UnsortedArrayMapping<String,UnsortedLinkedListSet<Integer>>(abecedari.length());
        for (int i=0;i< abecedari.length();i++){
            UnsortedLinkedListSet<Integer> llistaPosicions= new UnsortedLinkedListSet<Integer>();
            lletresCorrectes.put(abecedari.charAt(i),llistaPosicions);
        }

    }
    //metode per llegir les paraules del diccionari
    private void llegirdiccionari(){
        //declarcions del reader y stream per llegir el fitxer
        InputStream is = getResources().openRawResource(R.raw.paraules);
        BufferedReader buffer=null;
        try{
            buffer = new BufferedReader(new InputStreamReader(is)) ;
            String linia = buffer.readLine();
            //bucle de lectura de paraules
            while(linia!=null){
                String [] paraules = linia.split(";");
                if(paraules[0].length()== lengthWord){
                    //ficam les paraules al hashing y a l'arbre
                    diccionari.put(paraules[0],paraules[1]);
                    arbre.put(paraules[0],paraules[1]);
                    numeroparaules++;
                }
                linia = buffer.readLine();
            }

        }catch(IOException error){
            System.out.println(error.toString());
        }finally{
            try{
                num_combinacions = arbre.size();
                buffer.close();
            }catch(Exception error){
                System.out.println(error.toString());
            }
        }
    }

    private void comprobacio(){
        //Si la paraula no existeix
        String minuscula = palabraEnviada.toLowerCase();
        if(minuscula.equals(paraulasolucio)){
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
            acabat = true;
            newWindow();
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
                    TreeMap<String,String> auxiliar = new TreeMap<>();


                    id = i+""+y;
                    UnsortedLinkedListSet<Integer> aux = (UnsortedLinkedListSet<Integer>) lletresSolucio.get(palabraEnviada.charAt(i));

                    if (aux.isEmpty()){
                        TextView textaux = findViewById(Integer.valueOf(id).intValue());
                        textaux.setBackgroundColor(Color.GRAY);
                        //pintar el botó de vermell
                        int codigoAscii=(int)palabraEnviada.charAt(i);
                        Button boto = findViewById(Integer.valueOf(codigoAscii).intValue());
                        boto.setTextColor(Color.RED);
                        afegir_pista(palabraEnviada.charAt(i),posicioIncorrecte);

                        Set<Map.Entry<String,String>> setMapping = arbre.entrySet();
                        Iterator itArbre = setMapping.iterator();

                        while(itArbre.hasNext()){
                            Map.Entry<String, String> entry = (Map.Entry<String, String>) itArbre.next();
                            String value = entry.getValue();
                            String str = ""+palabraEnviada.charAt(i);
                            if(!value.contains(str.toLowerCase())){
                                auxiliar.put((String) entry.getKey(), (String) entry.getValue());
                            }
                        }

                        arbre = auxiliar;

                        num_combinacions = arbre.size();
                        TextView combinacions = findViewById(Integer.valueOf(126).intValue());
                        String s = "Hi ha "+num_combinacions+" solucions possibles";
                        combinacions.setText(s);

                    }
                    if(aux.contains(i+1)){

                        //Aquí afegim al mapping de lletres correctes la posició
                        UnsortedLinkedListSet<Integer> llistaPosicions= new UnsortedLinkedListSet<Integer>();
                        llistaPosicions = (UnsortedLinkedListSet<Integer>) lletresCorrectes.get(palabraEnviada.charAt(i));
                        llistaPosicions.add(i+1);
                        lletresCorrectes.put(palabraEnviada.charAt(i),llistaPosicions);
                        int codigoAscii=(int)palabraEnviada.charAt(i);
                        Button boto = findViewById(Integer.valueOf(codigoAscii).intValue());
                        boto.setTextColor(Color.GREEN);
                        //cridam al mètode afegir pista
                        afegir_pista(palabraEnviada.charAt(i),i+1);

                        //actualitzam l'arbre de solucions possibles
                        Set<Map.Entry<String,String>> setMapping = arbre.entrySet();
                        Iterator itArbre = setMapping.iterator();
                        while(itArbre.hasNext()){
                            Map.Entry<String, String> entry = (Map.Entry<String, String>) itArbre.next();
                            String value = entry.getValue();
                            String str = ""+palabraEnviada.charAt(i);
                            if(value.charAt(i)==palabraEnviada.toLowerCase().charAt(i)){
                                auxiliar.put((String) entry.getKey(), (String) entry.getValue());
                            }
                        }

                        arbre = auxiliar;
                        num_combinacions = arbre.size();
                        TextView combinacions = findViewById(Integer.valueOf(126).intValue());
                        String s = "Hi ha "+num_combinacions+" solucions possibles";
                        combinacions.setText(s);

                        //Verd TO DO
                    }
                    if (!aux.isEmpty() && !aux.contains(i+1)){
                        //afegim la posició al mapping de lletres Correctes
                        UnsortedLinkedListSet<Integer> llistaPosicions= new UnsortedLinkedListSet<Integer>();
                        llistaPosicions = (UnsortedLinkedListSet<Integer>) lletresCorrectes.get(palabraEnviada.charAt(i));
                        llistaPosicions.add(i+1);
                        lletresCorrectes.put(palabraEnviada.charAt(i),llistaPosicions);

                        //cridam al mètode afegirpista
                        afegir_pista(palabraEnviada.charAt(i),-1*(i+1));


                        //actualitzam l'arbre de solucions possibles
                        Set<Map.Entry<String,String>> setMapping = arbre.entrySet();
                        Iterator itArbre = setMapping.iterator();
                        while(itArbre.hasNext()){
                            Map.Entry<String, String> entry = (Map.Entry<String, String>) itArbre.next();
                            String value = entry.getValue();
                            String str = ""+palabraEnviada.charAt(i);
                            if(value.contains(str.toLowerCase())&& value.charAt(i)!=palabraEnviada.toLowerCase().charAt(i)){
                                auxiliar.put((String) entry.getKey(), (String) entry.getValue());
                            }
                        }

                        arbre = auxiliar;
                        num_combinacions = arbre.size();
                        TextView combinacions = findViewById(Integer.valueOf(126).intValue());
                        String s = "Hi ha "+num_combinacions+" solucions possibles";
                        combinacions.setText(s);
                    }
                }

                //A partir d'aqui ens encarregarem de pintar les lletres grogues i verdes
                //Hem utilitzat les normes del wordle
                //Si a la paraula solució i ha dues A's i a la paraula enviar hem posat 3 A's
                // una a la posició correcte i dues a posicions equivocades
                //llavors la A de la posició correcte es pinta de verd
                //però de les altres A's en pintarem la primera de groc i la segona de negre
                //això es fa per donar a entendre que a la paraula solució només hi ha dues A's
                //Les posicions de la paraula enviada les hem guardades a lletresCorrectes.
                //ara bé, només hem guardat les lletres que son verdes o grogues, ja que les
                //grises no tenen conflicte, sempre son grises.

                Iterator i = lletresCorrectes.iterator();

                while(i.hasNext()){
                    UnsortedArrayMapping.Pair pair = (UnsortedArrayMapping.Pair) i.next();
                    String lletra = pair.getKey().toString();
                    UnsortedLinkedListSet<Integer> nostre = (UnsortedLinkedListSet<Integer>) pair.getValue();

                    if(!nostre.isEmpty()){ //si no hem escrit aquesta lletra (no té posicions)
                        ArrayList<Integer> verds = new ArrayList<>();
                        ArrayList<Integer> grocs = new ArrayList<>();
                        UnsortedLinkedListSet<Integer> teclat = (UnsortedLinkedListSet<Integer>) lletresSolucio.get(lletra.charAt(0));

                        //cercam li la posició de la lletra actual està dins la solució
                        Iterator llista = nostre.iterator();
                        while(llista.hasNext()){
                            Integer posicio = (Integer) llista.next();
                            if(teclat.contains(posicio)){
                                verds.add(posicio);
                            }else{
                                grocs.add(posicio);
                            }
                        }

                        int numero_verds = verds.size();
                        int numero_grocs = grocs.size();
                        int total = teclat.getNumero(); //numero total d'aquesta lletra a la paraula solució

                        //si el numero de lletres verdes i grogues passa el total d'aquesta lletra
                        //de la paraula solució pintarem totes les verdes, però no totes les grogues

                        if(numero_verds+numero_grocs>total){
                            for(int j=0;j<verds.size();j++){
                                String ide = (verds.get(j)-1)+""+y;
                                TextView textaux = findViewById(Integer.valueOf(ide).intValue());
                                textaux.setBackgroundColor(Color.GREEN);
                            }

                            for(int k =0;k<grocs.size();k++){
                                if((k+1)<=total-numero_verds){
                                    String ide = (grocs.get(k)-1)+""+y;
                                    TextView textaux = findViewById(Integer.valueOf(ide).intValue());
                                    textaux.setBackgroundColor(Color.YELLOW);
                                }else{
                                    String ide = (grocs.get(k)-1)+""+y;
                                    TextView textaux = findViewById(Integer.valueOf(ide).intValue());
                                    textaux.setBackgroundColor(Color.GRAY);
                                }
                            }
                        }else{ //d'altra banda ho pintam tot
                            for(int j=0;j<verds.size();j++){
                                String ide = (verds.get(j)-1)+""+y;
                                TextView textaux = findViewById(Integer.valueOf(ide).intValue());
                                textaux.setBackgroundColor(Color.GREEN);
                            }

                            for(int k =0;k<grocs.size();k++){
                                String ide = (grocs.get(k)-1)+""+y;
                                TextView textaux = findViewById(Integer.valueOf(ide).intValue());
                                textaux.setBackgroundColor(Color.YELLOW);
                            }
                        }
                    }
                }

                palabraEnviada = "";
                reiniciar_lletresCorrectes(); //reiniciam el mapping cada cop
            }
        }
    }
    //metode que ens permet afegir pistes que es van descobrint
    private void afegir_pista(char lletra, int posicio){
        UnsortedLinkedListSet<Integer> llistaPosicions1 = (UnsortedLinkedListSet<Integer>) pistesDescobertes.get(lletra);
        if(posicio!=posicioIncorrecte){
            llistaPosicions1.add(posicio);
        }else{
            llistaPosicions1.add(posicioIncorrecte);
        }

        pistesDescobertes.put(lletra,llistaPosicions1);

    }
    //comunicacio amb la finestra de victoria/derrota
    public void newWindow() {
        //variables per passar restriccions y paraules possibles
        String restriccions = "Restriccions: ";
        String paraules_disponibles = "Paraules Possibles: ";
        //iteradors
        Iterator it = pistesDescobertes.iterator();
        Set<Map.Entry<String,String>> setMapping = arbre.entrySet();
        Iterator itArbre = setMapping.iterator();
        //tractamet per obtenir restriccions y paraules possibles
        if(!acabat){
            while(it.hasNext()){
                UnsortedArrayMapping.Pair pair = (UnsortedArrayMapping.Pair) it.next();
                String lletra = pair.getKey().toString();
                UnsortedLinkedListSet<Integer> llistaPosicions = (UnsortedLinkedListSet<Integer>) pair.getValue();
                if(!llistaPosicions.isEmpty()){
                    Iterator i = llistaPosicions.iterator();
                    while(i.hasNext()){
                        int posicio = (int) i.next();
                        if(posicio == posicioIncorrecte){
                            restriccions += "no ha de contenir la "+lletra+",";
                        }else if(posicio<0){
                            restriccions+= "conté la "+lletra+" però no a la posició "+-1*posicio+",";
                        }else{
                            restriccions+= "conté la "+lletra+" a la posició "+posicio+",";
                        }
                    }
                }

            }
            //bucle d'obtencio de paraules possibles
            while(itArbre.hasNext()){
                Map.Entry<String,String> entry = (Map.Entry<String,String>) itArbre.next();
                paraules_disponibles += entry.getKey() + ", ";
            }
        }else{
            restriccions=null;
            paraules_disponibles=null;
        }
        //eviar missatges
        Intent intent = new Intent(this, MainActivity2.class);
        intent.putExtra(EXTRA_MESSAGE,restriccions) ;
        intent.putExtra(EXTRA_MESSAGE2,paraules_disponibles) ;
        intent.putExtra(EXTRA_MESSAGE3,paraulasolucio) ;
        startActivity(intent) ;
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
