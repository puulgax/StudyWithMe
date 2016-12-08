package br.usp.icmc.studywithme;

import android.net.ParseException;
import android.os.AsyncTask;
import android.util.Log;


import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by gabri_000 on 07/12/2016.
 */

public class ConexaoBanco{

    private HttpPost post;
    private DefaultHttpClient client;

    public ConexaoBanco() {
        client = new DefaultHttpClient();
        post = new HttpPost("http://192.168.1.101/WebService.php");
    }

    public ArrayList<String> loadDisciplinas() {
        ArrayList<String> retorno = new ArrayList<String>();
        List<NameValuePair> myArgs = new ArrayList<NameValuePair>();
        myArgs.add(new BasicNameValuePair("getDisciplina", "getDisciplina"));
        JSONArray json = executaArgumentos(myArgs);
        for(int i = 0; i < json.length(); i++){
            try {
                JSONObject obj = json.getJSONObject(i);
                retorno.add(obj.get("nome").toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return retorno;
    }

    private JSONArray executaArgumentos(List<NameValuePair> myArgs) {
        String responseText = null;
        JSONArray json = null;
        try {
            post.setEntity(new UrlEncodedFormEntity(myArgs));
            HttpResponse myResponse = client.execute(post);
            responseText = EntityUtils.toString(myResponse.getEntity());
            json = new JSONArray(responseText);
        } catch (ParseException e) {
            e.printStackTrace();
            Log.i("Parse Exception", e + "");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }
}