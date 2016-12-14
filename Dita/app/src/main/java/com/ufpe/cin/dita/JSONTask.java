package com.ufpe.cin.dita;

import android.os.AsyncTask;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Matheus on 01/11/2016.
 */

public class JSONTask extends AsyncTask<String,String,String> {

    public String resultado = "";
    TextView text = null;
    ImageView image = null;

    public JSONTask(TextView text, ImageView image){

        this.text = text;
        this.image = image;
    }

    @Override
    protected String doInBackground(String... urls) {


        HttpURLConnection connection = null;
        BufferedReader reader = null;
        try {
            URL url = new URL(urls[0]);
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();
            InputStream stream = connection.getInputStream();
            reader = new BufferedReader(new InputStreamReader(stream));
            StringBuffer buffer = new StringBuffer();
            String line = "";
            while ((line = reader.readLine()) != null) {

                buffer.append(line);

            }
            String finalJSON = buffer.toString();

            JSONObject parentObject0 = new  JSONObject(finalJSON);
            JSONObject parentObject = parentObject0.getJSONObject("entry");
            JSONArray parentArray = parentObject.getJSONArray("sense");
            //parentObject.getJSONArray("sense");

            StringBuffer bufferFinal = new StringBuffer();


            JSONObject finalObject = parentArray.getJSONObject(0);
            String id =  parentObject.getString("@id");
            String gramGrp= " "+finalObject.getString("gramGrp");
            String definition = " "+finalObject.getString("def");
            bufferFinal.append(id);
            bufferFinal.append(gramGrp);
            bufferFinal.append(definition);
            // id+" "+gramGrp+" "+definition
            //resultado = bufferFinal.toString();
            return bufferFinal.toString();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }

            try {
                if (reader != null) reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        //  textView.setText(result);

        this.text.setText(result);
        this.image.setImageResource(R.drawable.conclusao);
        this.image.setTag("conclusao");

    }
}