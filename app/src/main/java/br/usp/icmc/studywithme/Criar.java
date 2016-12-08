package br.usp.icmc.studywithme;

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

    private void loadDisciplinas(){
        new Thread(new Runnable() {
            public void run() {
                Spinner s = (Spinner) findViewById(R.id.criar_disciplina_list);
                ConexaoBanco conexao = new ConexaoBanco();
                ArrayList<String> valores = conexao.loadDisciplinas();
                final ArrayList<String> array = valores;
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(),
                                                android.R.layout.simple_spinner_item, array);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                s.setAdapter(adapter);
            }

        }).start();

    }

}
