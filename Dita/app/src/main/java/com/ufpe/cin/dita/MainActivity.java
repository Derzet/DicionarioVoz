package com.ufpe.cin.dita;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private final int REQ_CODE_SPEECH_INPUT = 100;
    private String site ="http://dicionario-aberto.net/search-json/";
    private TextView texto;
    private ImageView image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.pesquisar:

                ImageView imagemView = (ImageView) findViewById(R.id.image);
                String ImageName = String.valueOf(imagemView.getTag());
                if(ImageName == "conclusao"){

                    TextView text = (TextView) findViewById(R.id.resultado);
                    text.setText("");
                    imagemView.setImageResource(R.drawable.pensando);
                }
                procurar();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void procurar(){
        TextView texto = (TextView) findViewById(R.id.resultado);
        promptSpeechInput();
    }

    /**
     * Showing google speech input dialog
     * */
    private void promptSpeechInput() {

        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, getString(R.string.speech_prompt));

        try {

            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {

            Toast.makeText(getApplicationContext(), getString(R.string.speech_not_supported), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Receiving speech input
     * */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        texto = (TextView) findViewById(R.id.resultado);
        image = (ImageView) findViewById(R.id.image);
        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);


                    site ="http://dicionario-aberto.net/search-json/";
                    site +=result.get(0);

                   JSONTask jsontask = new JSONTask(texto, image);
                   jsontask.execute(site);
                }
                break;
            }

        }
    }
}
