package br.usp.icmc.studywithme;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import br.usp.icmc.studywithme.classes.GroupRowAdapter;
import br.usp.icmc.studywithme.classes.Grupo;

public class Agenda extends AppCompatActivity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agenda);
        loadAgenda("Calculo I");//TODO: nomeDisciplina -> UserID

        final ListView lv = (ListView) findViewById(R.id.agenda_listview);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Grupo g = (Grupo) lv.getAdapter().getItem(position);
                setContentView(R.layout.info_grupo);
                TextView tv = (TextView) findViewById(R.id.info_text_disciplina);
                tv.setText(g.getDisciplina());
                tv = (TextView) findViewById(R.id.info_text_materia);
                tv.setText(g.getMateria());
                tv = (TextView) findViewById(R.id.info_text_data);
                tv.setText(g.getDia());
                tv = (TextView) findViewById(R.id.info_text_hora);
                tv.setText(g.getHora());
                //TODO: read rest of info
            }
        });
    }

    private class ThreadAgenda extends Thread{
        private Handler handler;
        private String nomeDisciplina;//TODO: nomeDisciplina -> UserID

        public ThreadAgenda (Handler handler, String nomeDisciplina) {//TODO: nomeDisciplina -> UserID
            this.handler = handler;
            this.nomeDisciplina = nomeDisciplina;//TODO: nomeDisciplina -> UserID
        }
        public void run() {
            ConexaoBanco conexao = new ConexaoBanco();
            ArrayList<Grupo> valores = conexao.loadAgenda(nomeDisciplina); //TODO:nomeDisciplina -> UserID
            Message msg = new Message();
            msg.obj = valores;
            handler.sendMessage(msg);
        }
    }


    private void loadAgenda(String nomeDisciplina){//TODO: nomeDisciplina -> UserID
        Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                ArrayList<Grupo> array = (ArrayList<Grupo>) msg.obj;
                ListView lv = (ListView) findViewById(R.id.agenda_listview);
                GroupRowAdapter adapter = new GroupRowAdapter(getApplicationContext(), array);
                lv.setAdapter(adapter);

            }
        };
        ThreadAgenda tg = new ThreadAgenda(handler, nomeDisciplina); //TODO: nomeDisciplina -> UserID
        tg.start();
    }
}
