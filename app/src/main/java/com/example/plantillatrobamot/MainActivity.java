package com.example.plantillatrobamot;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    // Variables de lògica del joc
    private int lengthWord = 5;
    private int maxTry = 6;
    private int x = 0;
    private int y = 0;
    private int prova =0;
    // Variables de construcció de la interfície
    public static String grayColor = "#D9E1E8";
    public static String ColorCursor="#FCBA03";
    private int widthDisplay;
    private int heightDisplay;
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
        crearGraella();
        crearTeclat2();
        //prova();
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

        String [] abecedari = {"A","B","C","D","E","F","G","H","I","J",
                "K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z","Ç"};

        float altura = heightDisplay -50;
        float amplaria = widthDisplay;
        int contador = 26;

        for(int i = 0; i<3 ;i++){
            amplaria = widthDisplay;
            for(int j =0;j <9;j++){
                Button boto = new Button(this);
                boto.setText(abecedari[contador]);
                boto.setLayoutParams(params);
                boto.setBackgroundColor(Color.parseColor(grayColor));
                boto.setX(amplaria - (buttonWidth+separació_x));
                boto.setY(altura - (buttonHeight + separació_x));
                amplaria = boto.getX();
                constraintLayout.addView(boto);
                boto.setOnClickListener(this::onClick);
                contador--;
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

    }
        public void onClick(View v) {
            Button boto = (Button) v;
            String lletra = boto.getText().toString();
            int xaux=0;
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
                    xaux=x;
                    xaux--;
                    id = xaux+""+y;
                    textactual = findViewById(Integer.valueOf(id).intValue());
                    textactual.setBackground(gd2);
                }
            }else if((lletra == "Enviar")&&(y != maxTry)&&(x==lengthWord)){
                x--;
                id=x+""+y;
                textactual = findViewById(Integer.valueOf(id).intValue());
                textactual.setBackground(gd);
                x =0;
                y +=1;
                id=x+""+y;
                TextView textaux = findViewById(Integer.valueOf(id).intValue());
                textaux.setBackground(gd2);
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