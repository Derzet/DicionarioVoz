package com.ufpe.cin.dita;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void procurar(View view){

        //Criar objeto da classe de hitallo (Faltando)
        //Chamar metodo de hitallo (Faltando)
        TextView texto = (TextView) findViewById(R.id.resultado);
        //Print resultado

        JSONTask jsontask = new JSONTask(texto);
        String site = "http://dicionario-aberto.net/search-json/";
        site.concat("arroz");
        jsontask.execute("http://dicionario-aberto.net/search-json/arroz");


        //texto.setText(jsontask.getResultado());
    }
}
