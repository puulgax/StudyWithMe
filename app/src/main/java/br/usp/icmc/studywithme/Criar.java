package br.usp.icmc.studywithme;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.ArrayList;

import static android.provider.LiveFolders.INTENT;
import static android.provider.Telephony.Mms.Part.TEXT;
import static br.usp.icmc.studywithme.R.id.activity_criar;

public class Criar extends AppCompatActivity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_criar);
        loadDisciplinas();
    }

    private class ThreadDisciplina extends Thread{
        private Handler handler;

        public ThreadDisciplina(Handler handler) {
            this.handler = handler;
        }
        public void run() {
            ConexaoBanco conexao = new ConexaoBanco();
            ArrayList<String> valores = conexao.loadDisciplinas();
            Message msg = new Message();
            msg.obj = valores;
            handler.sendMessage(msg);

        };
    }

    private void loadDisciplinas(){
        Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                ArrayList<String> array = (ArrayList<String>) msg.obj;
                Spinner s = (Spinner) findViewById(R.id.criar_disciplina_list);
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(),
                        android.R.layout.simple_spinner_item, array);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                s.setAdapter(adapter);

            }
        };
        ThreadDisciplina td = new ThreadDisciplina(handler);
        td.start();
    }

}
