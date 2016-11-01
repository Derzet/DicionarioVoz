package com.ufpe.cin.dita;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    protected TextView wordTextView;
    protected Button buttonView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        wordTextView = (TextView) findViewById(R.id.textF);
        buttonView = (Button) findViewById(R.id.buttonPalavra);


        buttonView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

              JSONTask jsontask = new JSONTask();
                String site = "http://dicionario-aberto.net/search-json/";
                site.concat("arroz");
                jsontask.execute(site);
                wordTextView.setText( jsontask.getResultado());
            }
        });


        };




}
