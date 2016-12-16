package com.ufpe.cin.dita;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements TextToSpeech.OnInitListener {

    private static final int REQUEST_CHECK = 1;
    private final int REQ_CODE_SPEECH_INPUT = 100;
    private String site = "http://dicionario-aberto.net/search-json/";

    private TextView titulo;
    private TextView texto;

    private ImageView image;
    private ImageView imagem;

    private Spinner mSpLocale;
    private TextToSpeech mSpeech;
    private EditText mEdtText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent checkIntent = new Intent();
        checkIntent.setAction(
                TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);

        startActivityForResult(checkIntent, REQUEST_CHECK);

        // Inicializa objetos
        mSpLocale = (Spinner) findViewById(R.id.spLocale);
        mEdtText = (EditText) findViewById(R.id.edtText);

        // Cria o adapter com o Array de Idiomas
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_spinner_item,
                getResources()
                        .getStringArray(R.array.locale_arrays));

        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item);

        mSpLocale.setAdapter(adapter);

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
                if (ImageName == "conclusao") {

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

    public void procurar() {
        TextView texto = (TextView) findViewById(R.id.resultado);
        promptSpeechInput();
    }

    /**
     * Showing google speech input dialog
     */
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
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CHECK) {
            if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
                // Inicializa o mSpeech passando o
                // contexto (this) e quem implementa a
                // interface OnInitListener (this)
                mSpeech = new TextToSpeech(this, this);
            } else {
                // Não tem os recursos do TTS instaldo
                // Solicita instalação
                Intent intent = new Intent();
                intent.setAction(
                        TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                startActivity(intent);
            }
        }

        titulo = (TextView) findViewById(R.id.textViewTitulo);


        texto = (TextView) findViewById(R.id.resultado);


        image = (ImageView) findViewById(R.id.image);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);


                    site = "http://dicionario-aberto.net/search-json/";

                    titulo.setText(result.get(0));

                    site += result.get(0);

                    JSONTask jsontask = new JSONTask(texto, image, titulo);
                    jsontask.execute(site);
                    mostrarImagem(result.get(0));
                }
                break;
            }

        }
    }

    public void mostrarImagem(String result) {

        imagem = (ImageView) findViewById(R.id.image1);

        String imagemUrl = "gs://dita-653d2.appspot.com/foto/" + result + ".jpg";

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReferenceFromUrl(imagemUrl);

        final long tamanhoMax = 500 * 1024;
        storageRef.getBytes(tamanhoMax).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                // Data for "images/island.jpg" is returns, use this as needed
                Bitmap bm = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                imagem.setImageBitmap(bm);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                imagem.setImageBitmap(null);
            }
        });
    }

    @Override
    public void onInit(int status) {
        if (status != TextToSpeech.SUCCESS) {
            Toast.makeText(this, R.string.falha_na_inicializacao,
                    Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onDestroy() {
        // Shutdown no Speech
        if (mSpeech != null) {
            mSpeech.stop();
            mSpeech.shutdown();
        }
        super.onDestroy();
    }

    public void btnFalarClick(View v) {

        // Configura os recursos específicos de acordo
        // com o idioma selecionado que precisam ser
        // carregados antes do motor começar a falar.
        int result = -1;
        switch (mSpLocale.getSelectedItemPosition()) {
            case 0:
                result = mSpeech.setLanguage(new Locale("pt_BR"));
                break;
            case 1:
                result = mSpeech.setLanguage(Locale.FRENCH);
                break;
            case 2:
                result = mSpeech.setLanguage(new Locale("spa"));
                break;
            default:
                result = mSpeech.setLanguage(Locale.ENGLISH);
                break;
        }

        // Verifica se o dispositivo suporta o idioma selecionado
        if (result == TextToSpeech.LANG_MISSING_DATA
                || result == TextToSpeech.LANG_NOT_SUPPORTED) {
            // Exibe mensagem de erro
            Toast.makeText(this,
                    R.string.idioma_nao_suportado,
                    Toast.LENGTH_LONG).show();
            return;
        }

        String text = texto.getText().toString();

        // Algum texto foi digitado
        if (text.length() == 0) {
            Toast.makeText(this,
                    R.string.digite_o_texto_para_falar,
                    Toast.LENGTH_LONG).show();
        } else {
            mSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null);
        }
    }
}
