package br.usp.icmc.studywithme;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import br.usp.icmc.studywithme.classes.GroupRowAdapter;
import br.usp.icmc.studywithme.classes.Grupo;

import static android.text.TextUtils.concat;

public class InfoGrupo extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap googleMap;
    private LatLng latLng;
    private String gid;
    private String[] strings;
    private Integer idUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_grupo);

        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.info_map);
        mapFragment.getMapAsync(this);
        Intent i = getIntent();
        Bundle extras = i.getExtras();
        latLng = new LatLng(-22.007515,-47.894383);
        if(extras != null) {
            String str = (String) extras.getSerializable("lat");
            latLng = new LatLng(Double.parseDouble(str.split(",")[0]), Double.parseDouble(str.split(",")[1]));
            strings = (String[]) extras.getSerializable("gdt");
            idUsuario = getIntent().getIntExtra("idUsuario",-1);;
        }
        gid = strings[0];
        TextView tv = (TextView) findViewById(R.id.info_text_materia);
        tv.setText(strings[1]);
        tv = (TextView) findViewById(R.id.info_text_data);
        tv.setText(strings[2]);
        tv = (TextView) findViewById(R.id.info_text_hora);
        tv.setText(strings[3]);
        loadPart(gid);

        Button btn = (Button) findViewById(R.id.info_participar);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                participaGrupo(gid, idUsuario.toString());
            }
        });
    }

    private class ThreadGrupos extends Thread{
        private Handler handler;
        private String idGrupo, idUsuario;

        public ThreadGrupos(Handler handler,String idGrupo, String idUsuario) {
            this.handler = handler;
            this.idGrupo = idGrupo;
            this.idUsuario = idUsuario;
        }
        public void run() {
            ConexaoBanco conexao = new ConexaoBanco();
            int retorno= conexao.participaGrupo(idGrupo, idUsuario);
            Message msg = new Message();
            msg.obj = retorno;
            handler.sendMessage(msg);

        };
    }

    private void participaGrupo(String idGrupo, String idUsuario){
        Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                int retorno = (int) msg.obj;
                AlertDialog.Builder builder = new AlertDialog.Builder(InfoGrupo.this);
                if(retorno == 1) {
                    builder.setMessage("   Entrou no grupo!\n")
                            .setTitle("Agora voce participa deste grupo");

                }
                else{
                    builder.setMessage("Algo de errado aconteceu!")
                            .setTitle("Erro");
                }
                AlertDialog dialog = builder.create();
                dialog.show();

            }
        };
        ThreadGrupos tg = new ThreadGrupos(handler, idGrupo, idUsuario);
        tg.start();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return; //error
        }
        else {
            this.googleMap.setMyLocationEnabled(true);
            Marker m = this.googleMap.addMarker(new MarkerOptions().position(latLng).title("Study Here").draggable(false));
            this.googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            this.googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latLng.latitude, latLng.longitude), 12.0f));

        }
    }
    private class ThreadPart extends Thread {
        private Handler handler;
        private String idGrupo;

        public ThreadPart(Handler handler, String idGrupo) {
            this.handler = handler;
            this.idGrupo = idGrupo;
        }

        public void run() {
            ConexaoBanco conexao = new ConexaoBanco();
            ArrayList<String> valores = conexao.loadPart(idGrupo);
            Message msg = new Message();
            msg.obj = valores;
            handler.sendMessage(msg);
        }
    }

    private void loadPart(String idGrupo) {
        Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                ArrayList<String> array = (ArrayList<String>) msg.obj;
                TextView tv = (TextView) findViewById(R.id.info_part);
                String str = "";
                for(int i=0; i < array.size(); i++){
                    str = str + array.get(i) + "\n";
                }
                tv.setText(str);
            }
        };
        ThreadPart tg = new ThreadPart(handler, idGrupo);
        tg.start();
    }
}
