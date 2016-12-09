package br.usp.icmc.studywithme;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONException;
import org.json.JSONObject;

public class Inicio extends AppCompatActivity {
    CallbackManager callbackManager;
    private AccessTokenTracker accessTokenTracker;
    int idUsuario = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        AppEventsLogger.activateApp(this);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        LoginButton loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions("public_profile");
        //LoginManager.getInstance().logOut();
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                AccessToken accessToken = loginResult.getAccessToken();
                Profile profile = Profile.getCurrentProfile();

                // Facebook Email address
                GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(
                                    JSONObject object,
                                    GraphResponse response) {
                                try {
                                    criaUsuario(object.getString("id"),object.getString("name"));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email,gender, birthday");
                request.setParameters(parameters);
                request.executeAsync();

            }
            @Override
            public void onCancel() {
                LoginManager.getInstance().logOut();
            }

            @Override
            public void onError(FacebookException error) {

            }
        });
        Profile profile = Profile.getCurrentProfile().getCurrentProfile();
        if (profile != null) {
            criaUsuario(profile.getId(),profile.getName());
        }
        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
                if (currentAccessToken == null)
                    idUsuario = 0;
            }
        };
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    public void criaUsuario(String id, String nome){
        Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                int id = (int) msg.obj;
                if(id != 0){
                    idUsuario = id;
                }
            }
        };
            ThreadLogin tl = new ThreadLogin(handler,id,nome);
            tl.start();
    }

    private class ThreadLogin extends Thread{
        private Handler handler;
        private String idFb, nome;

        public ThreadLogin(Handler handler,String idFb, String nome) {
            this.handler = handler;
            this.idFb = idFb;
            this.nome = nome;
        }
        public void run() {
            ConexaoBanco conexao = new ConexaoBanco();
            int retorno= conexao.verificaLogin(idFb,nome);
            Message msg = new Message();
            msg.obj = retorno;
            handler.sendMessage(msg);

        };
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        Intent intent = null;
        if (id == R.id.action_criar)
            intent = new Intent(this, Criar.class);


        else if (id == R.id.action_buscar)
            intent = new Intent(this, Buscar.class);
        else if (id == R.id.action_notif)
            intent = new Intent(this, Notificacoes.class);
        else if (id == R.id.action_agenda)
            intent = new Intent(this, Agenda.class);
        else if (id == R.id.action_perfil)
            intent = new Intent(this, Perfil.class);
        if(idUsuario == 0) {
            AlertDialog.Builder builder = new AlertDialog.Builder(Inicio.this);
            builder.setMessage("Você precisa logar primeiro!\n")
                    .setTitle("Erro");
            AlertDialog dialog = builder.create();
            dialog.show();
        }
        else {
            intent.putExtra("idUsuario", idUsuario);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
}
