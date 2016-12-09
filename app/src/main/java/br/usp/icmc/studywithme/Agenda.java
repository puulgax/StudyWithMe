package br.usp.icmc.studywithme;

import android.content.Intent;
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
    Grupo groupSelected;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agenda);
        loadAgenda(getIntent().getIntExtra("idUsuario",-1));

        final ListView lv = (ListView) findViewById(R.id.agenda_listview);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                groupSelected = (Grupo) lv.getAdapter().getItem(position);
                loadLocal(groupSelected.getId());
            }
        });
    }

    private class ThreadLocal extends Thread {
        private Handler handler;
        private String groupID;

        public ThreadLocal(Handler handler, String groupID) {
            this.handler = handler;
            this.groupID = groupID;
        }

        public void run() {
            ConexaoBanco conexao = new ConexaoBanco();
            String local = conexao.loadLocal(groupID);
            Message msg = new Message();
            msg.obj = local;
            handler.sendMessage(msg);
        }
    }

    private void loadLocal(final String groupID) {
        Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                String str = (String) msg.obj;
                String[] grp = {groupSelected.getId(), groupSelected.getMateria(),
                        groupSelected.getDia(), groupSelected.getHora()};
                Intent intent = new Intent(getApplicationContext(), InfoGrupo.class);
                intent.putExtra("lat", str);;
                intent.putExtra("gdt", grp);
                intent.putExtra("idUsuario", getIntent().getIntExtra("idUsuario",-1));
                startActivity(intent);
            }
        };
        ThreadLocal tl = new ThreadLocal(handler, groupID);
        tl.start();
    }

    private class ThreadAgenda extends Thread{
        private Handler handler;
        private int idUsuario;

        public ThreadAgenda (Handler handler, int idUsuario) {
            this.handler = handler;
            this.idUsuario = idUsuario;
        }
        public void run() {
            ConexaoBanco conexao = new ConexaoBanco();
            ArrayList<Grupo> valores = conexao.loadAgenda(idUsuario);
            Message msg = new Message();
            msg.obj = valores;
            handler.sendMessage(msg);
        }
    }


    private void loadAgenda(int idUsuario){
        Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                ArrayList<Grupo> array = (ArrayList<Grupo>) msg.obj;
                ListView lv = (ListView) findViewById(R.id.agenda_listview);
                GroupRowAdapter adapter = new GroupRowAdapter(getApplicationContext(), array);
                lv.setAdapter(adapter);
            }
        };
        ThreadAgenda tg = new ThreadAgenda(handler, idUsuario);
        tg.start();
    }
}
