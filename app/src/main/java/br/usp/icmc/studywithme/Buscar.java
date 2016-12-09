package br.usp.icmc.studywithme;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import org.w3c.dom.Text;

import br.usp.icmc.studywithme.classes.Grupo;
import br.usp.icmc.studywithme.classes.GroupRowAdapter;

import java.util.ArrayList;

public class Buscar extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buscar);

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

    private class ThreadGrupos extends Thread{
        private Handler handler;
        private String nomeDisciplina;

        public ThreadGrupos (Handler handler, String nomeDisciplina) {
            this.handler = handler;
            this.nomeDisciplina = nomeDisciplina;
        }
        public void run() {
            ConexaoBanco conexao = new ConexaoBanco();
            ArrayList<Grupo> valores = conexao.loadGrupos(nomeDisciplina);
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
