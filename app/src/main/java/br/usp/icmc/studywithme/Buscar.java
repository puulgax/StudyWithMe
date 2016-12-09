package br.usp.icmc.studywithme;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;

import br.usp.icmc.studywithme.classes.Grupo;
import br.usp.icmc.studywithme.classes.GroupRowAdapter;

import java.util.ArrayList;

public class Buscar extends AppCompatActivity {
    private Grupo groupSelected;
    private Integer idUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buscar);
        idUsuario = getIntent().getIntExtra("idUsuario", -1);

        final SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        final android.widget.SearchView searchView = (android.widget.SearchView) findViewById(R.id.search);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setQueryHint(getResources().getString(R.string.search_hint));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                loadGrupos(query);
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                //loadGrupos(newText);
                return false;
            }
        });

        final ListView lv = (ListView) findViewById(R.id.buscar_listview);
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
                intent.putExtra("idUsuario", idUsuario);
                startActivity(intent);
            }
        };
        ThreadLocal tl = new ThreadLocal(handler, groupID);
        tl.start();
    }

    private class ThreadGrupos extends Thread{
        private Handler handler;
        private String nomeDisciplina;

        public ThreadGrupos (Handler handler, String nomeDisciplina) {
            this.handler = handler;
            this.nomeDisciplina = nomeDisciplina;
        }
        public void run() {
            ConexaoBanco conexao = new ConexaoBanco();
            ArrayList<Grupo> valores = conexao.loadGrupos(nomeDisciplina, idUsuario);
            Message msg = new Message();
            msg.obj = valores;
            handler.sendMessage(msg);
        }
    }

    private void loadGrupos(String nomeDisciplina){
        Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                ArrayList<Grupo> array = (ArrayList<Grupo>) msg.obj;
                ListView lv = (ListView) findViewById(R.id.buscar_listview);
                GroupRowAdapter adapter = new GroupRowAdapter(getApplicationContext(), array);
                lv.setAdapter(adapter);

            }
        };
        ThreadGrupos tg = new ThreadGrupos(handler, nomeDisciplina);
        tg.start();
    }

}
