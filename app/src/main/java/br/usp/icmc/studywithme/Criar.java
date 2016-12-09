package br.usp.icmc.studywithme;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

import br.usp.icmc.studywithme.R.*;

public class Criar extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap googleMap;
    private LatLng latlgn;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_criar);
        loadDisciplinas();
        findViewById(id.criar_materia_text).requestFocus();
        final Button btnCriar = (Button) findViewById(id.btnCriar);
        btnCriar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Spinner s = (Spinner) findViewById(R.id.criar_disciplina_list);
                criaGrupo(s.getSelectedItem().toString());
            }
        });

        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return; //error
        }
        else {
            this.googleMap.setMyLocationEnabled(true);
            LatLng local = new LatLng(-22.007515,-47.894383);
            Marker m = this.googleMap.addMarker(new MarkerOptions().position(local).title("Study Here").draggable(true));
            this.googleMap.moveCamera(CameraUpdateFactory.newLatLng(local));
            this.googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(local.latitude, local.longitude), 12.0f));
            this.googleMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
                @Override
                public void onMarkerDragStart(Marker marker) {}
                @Override
                public void onMarkerDrag(Marker marker) {}
                @Override
                public void onMarkerDragEnd(Marker marker) {
                    updateMarker(marker);
                }
            });
        }
    }
    private void updateMarker(Marker marker){
        latlgn = marker.getPosition();
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
                        layout.spinner_item, array);
                adapter.setDropDownViewResource(layout.spinner_dropdown_item);
                s.setAdapter(adapter);

            }
        };
        ThreadDisciplina td = new ThreadDisciplina(handler);
        td.start();
    }

    private class ThreadGrupos extends Thread{
        private Handler handler;
        private String materia,data,horario,disciplina,location;

        public ThreadGrupos(Handler handler,String disciplina,String materia,String data,String horario,String location) {
            this.handler = handler;
            this.disciplina = disciplina;
            this.materia = materia;
            this.data = data;
            this.horario = horario;
            this.location = location;
        }
        public void run() {
            ConexaoBanco conexao = new ConexaoBanco();
            int retorno= conexao.criaGrupos(disciplina,materia,data,horario,location);
            Message msg = new Message();
            msg.obj = retorno;
            handler.sendMessage(msg);

        };
    }

    private void criaGrupo(String nomeDisciplina){
        Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                int retorno = (int) msg.obj;
                AlertDialog.Builder builder = new AlertDialog.Builder(Criar.this);
                if(retorno == 1) {
                    EditText data = (EditText) findViewById(id.criar_data_text);
                    EditText horario = (EditText) findViewById(id.criar_hora_text);
                    EditText materia = (EditText) findViewById(id.criar_materia_text);
                    data.getText().clear();
                    horario.getText().clear();
                    materia.getText().clear();
                    builder.setMessage("   Grupo criado!\n")
                            .setTitle("Grupo criado com sucesso");

                }
                else{
                    builder.setMessage("Algo de errado aconteceu!")
                            .setTitle("Erro");
                }
                AlertDialog dialog = builder.create();
                dialog.show();

            }
        };
        Spinner s = (Spinner) findViewById(R.id.criar_disciplina_list);
        EditText data = (EditText) findViewById(id.criar_data_text);
        EditText horario = (EditText) findViewById(id.criar_hora_text);
        EditText materia = (EditText) findViewById(id.criar_materia_text);
        String location = latlgn.latitude+","+latlgn.longitude;
        ThreadGrupos tg = new ThreadGrupos(handler,s.getSelectedItem().toString(),materia.getText().toString(),data.getText().toString(),horario.getText().toString(),location);
        tg.start();
    }

}
