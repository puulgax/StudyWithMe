package br.usp.icmc.studywithme.classes;

import java.util.ArrayList;

public class Grupo {
    private String Disciplina;
    private String Materia;
    private String Dia;
    private String Hora;
    private String id;
    private ArrayList<String> Participantes = new ArrayList<String>();

    public Grupo(String id, String materia, String data, String hora){
        this.id = id;
        this.Materia = materia;
        this.Dia = data;
        this.Hora = hora;
    }
    public String getId(){
        return id;
    }
    public void setId(String id){
        this.id = id;
    }
    public String getDisciplina(){
        return Disciplina;
    }
    public void setDisciplina(String disciplina){
        this.Disciplina = disciplina;
    }
    public String getMateria(){
        return Materia;
    }
    public void setMateria(String materia){
        this.Materia = materia;
    }
    public String getDia(){
        return Dia;
    }
    public void setDia(String dia){
        this.Dia = dia;
    }
    public String getHora(){
        return Hora;
    }
    public void setHora(String hora){
        this.Hora = hora;
    }
    public ArrayList<String> getParticipantes(){
        return Participantes;
    }
    public void setParticipantes(){
        //TODO
    }
}
