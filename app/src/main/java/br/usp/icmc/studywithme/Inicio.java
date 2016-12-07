package br.usp.icmc.studywithme;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

public class Inicio extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_criar) {
            Intent intent = new Intent(this, Criar.class);
            startActivity(intent);
        }
        else if (id == R.id.action_buscar) {
            Intent intent = new Intent(this, Buscar.class);
            startActivity(intent);
        }
        else if (id == R.id.action_notif) {
            Intent intent = new Intent(this, Notificacoes.class);
            startActivity(intent);
        }
        else if (id == R.id.action_agenda) {
            Intent intent = new Intent(this, Agenda.class);
            startActivity(intent);
        }
        else if (id == R.id.action_perfil) {
            Intent intent = new Intent(this, Perfil.class);
            startActivity(intent);
        }
        else if (id == R.id.action_logout) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
