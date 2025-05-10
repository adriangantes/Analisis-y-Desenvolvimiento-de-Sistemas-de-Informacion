package org.irlab.model.entities;

import jakarta.persistence.*;

import java.sql.Time;
import java.util.List;

@Entity
@Table(name="Horarios")
public class Horario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column (nullable = false)
    private String diaSemana;

    @Column (nullable = false)
    private Time horaEntrada;

    @Column (nullable = false)
    private Time horaSalida;

    @Column (nullable = false)
    private String asignatura;

    @Column (nullable = false)
    private String aula;

    @Column (nullable = false)
    private String profesor;

    @ManyToMany(mappedBy = "horarios")
    private List<Alumno> alumnos;

    public Horario() {}

    public Horario(String diaSemana, Time horaEntrada, Time horaSalida,
                   String asignatura, String aula, String profesor) {
        this.diaSemana = diaSemana;
        this.horaEntrada = horaEntrada;
        this.horaSalida = horaSalida;
        this.asignatura = asignatura;
        this.aula = aula;
        this.profesor = profesor;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDiaSemana() {
        return diaSemana;
    }

    public void setDiaSemana(String diaSemana) {
        this.diaSemana = diaSemana;
    }

    public Time getHoraEntrada() {
        return horaEntrada;
    }

    public void setHoraEntrada(Time horaEntrada) {
        this.horaEntrada = horaEntrada;
    }

    public Time getHoraSalida() {
        return horaSalida;
    }

    public void setHoraSalida(Time horaSalida) {
        this.horaSalida = horaSalida;
    }

    public String getAsignatura() {
        return asignatura;
    }

    public void setAsignatura(String asignatura) {
        this.asignatura = asignatura;
    }

    public String getAula() {
        return aula;
    }

    public void setAula(String aula) {
        this.aula = aula;
    }

    public String getProfesor() {
        return profesor;
    }

    public void setProfesor(String profesor) {
        this.profesor = profesor;
    }

    @Override
    public String toString() {
        return "Horario{" + "id=" + id +
                ", diaSemana=" + diaSemana +
                ", horaEntrada=" + horaEntrada +
                ", horaSalida=" + horaSalida +
                ", asignatura=" + asignatura +
                ", aula=" + aula +
                ", profesor=" + profesor +
                '}';
    }

    public List<Alumno> getAlumnos() {
        return alumnos;
    }

    public void setAlumnos(List<Alumno> alumnos) {
        this.alumnos = alumnos;
    }
}
