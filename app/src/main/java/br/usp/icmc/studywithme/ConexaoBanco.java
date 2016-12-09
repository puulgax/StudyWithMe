package br.usp.icmc.studywithme;

import android.net.ParseException;
import android.os.AsyncTask;
import android.util.Log;
import br.usp.icmc.studywithme.classes.Grupo;


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
	
	public int criaGrupos(String disciplina,String materia,String data,String horario,String location,int usuario) {
        int retorno;
        List<NameValuePair> myArgs = new ArrayList<NameValuePair>();
        myArgs.add(new BasicNameValuePair("createGrupos", disciplina));
        myArgs.add(new BasicNameValuePair("cMateria", materia));
        myArgs.add(new BasicNameValuePair("cData", data));
        myArgs.add(new BasicNameValuePair("cHorario", horario));
        myArgs.add(new BasicNameValuePair("cLocation", location));
        myArgs.add(new BasicNameValuePair("cUser", String.valueOf(usuario)));
        retorno = executaArgumentosCriar(myArgs);
        return retorno;
    }

    public ArrayList<Grupo> loadGrupos(String nomeDisciplina) {
        ArrayList<Grupo> retorno = new ArrayList<Grupo>();
        List<NameValuePair> myArgs = new ArrayList<NameValuePair>();
        myArgs.add(new BasicNameValuePair("getGrupos", nomeDisciplina));
        JSONArray json = executaArgumentos(myArgs);
        if(json == null) return retorno;
        for(int i = 0; i < json.length(); i++){
            try {
                JSONObject obj = json.getJSONObject(i);
                retorno.add(new Grupo(obj.get("id").toString(), obj.get("materia").toString(), obj.get("data").toString(), obj.get("horario").toString()));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return retorno;
    }

    //TODO: arrumar loadAgenda para ler grupos que participa
    public ArrayList<Grupo> loadAgenda(int idUsuario) {
        ArrayList<Grupo> retorno = new ArrayList<Grupo>();
        List<NameValuePair> myArgs = new ArrayList<NameValuePair>();
        myArgs.add(new BasicNameValuePair("getGrupoParticipante", String.valueOf(idUsuario)));
        JSONArray json = executaArgumentos(myArgs);
        if(json == null) return retorno;
        for(int i = 0; i < json.length(); i++){
            try {
                JSONObject obj = json.getJSONObject(i);
                retorno.add(new Grupo(obj.get("id").toString(), obj.get("materia").toString(), obj.get("data").toString(), obj.get("horario").toString(),obj.get("disciplina").toString()));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return retorno;
    }

    public int verificaLogin(String idFb, String nome) {
        List<NameValuePair> myArgs = new ArrayList<NameValuePair>();
        myArgs.add(new BasicNameValuePair("getUsuario", idFb));
        myArgs.add(new BasicNameValuePair("uNome", nome));
        int resposta = executaArgumentosCriar(myArgs);
        return resposta;
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
	
	private int executaArgumentosCriar(List<NameValuePair> myArgs) {
        String responseText = null;
        int resposta = 0;
        try {
            post.setEntity(new UrlEncodedFormEntity(myArgs));
            HttpResponse myResponse = client.execute(post);
            responseText = EntityUtils.toString(myResponse.getEntity());
            resposta = Integer.parseInt(responseText);
        } catch (ParseException e) {
            e.printStackTrace();
            Log.i("Parse Exception", e + "");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return resposta;
    }
}