package org.irlab.model.entities;

import jakarta.persistence.*;

import java.util.List;
import java.util.Objects;
import java.util.Random;

@Entity
@Table(name="Alumnos")
public class Alumno {

    @Id
    @Column (unique = true, nullable = false)
    private String dni;

    @Column (nullable = false)
    private String nombre;

    @Column (nullable = false)
    private String apellido1;

    @Column
    private String apellido2;

    @Column
    private List<Integer> cursoId;

    @Column (unique = true, nullable = false)
    private String email;

    @Column (nullable = false)
    private String password;

    @ManyToMany
    @JoinTable(
            name = "alumno_horario",
            joinColumns = @JoinColumn(name = "alumno_dni"),
            inverseJoinColumns = @JoinColumn(name = "horario_id")
    )
    private List<Horario> horarios;

    @PrePersist
    private void generarPassword() {
        if (this.password == null || this.password.isEmpty()) {
            this.password = generarPasswordAleatoria();
        }
    }

    private String generarPasswordAleatoria() {
        int longitud = 10;
        String caracteres = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < longitud; i++) {
            sb.append(caracteres.charAt(random.nextInt(caracteres.length())));
        }
        return sb.toString();
    }

    public Alumno() {}

    public Alumno (String dni, String nombre, String apellido1) {
        this.dni = dni;
        this.nombre = nombre;
        this.apellido1 = apellido1;

        int hash = Math.abs(Objects.hash(dni)) % 1_000_000;
        this.email = nombre + apellido1 + String.format("%06d", hash) + "@universalis.com";

        this.password = generarPasswordAleatoria();
    }

    public Alumno (String dni, String nombre, String apellido1, String apellido2) {
        this(dni, nombre, apellido1);
        this.apellido2 = apellido2;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido1() {
        return apellido1;
    }

    public void setApellido1(String apellido1) {
        this.apellido1 = apellido1;
    }

    public String getApellido2() {
        return apellido2;
    }

    public void setApellido2(String apellido2) {
        this.apellido2 = apellido2;
    }

    public List<Integer> getCursoId() {
        return cursoId;
    }

    public void setCursoId(List<Integer> cursoId) {
        this.cursoId = cursoId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "Alumno{" + "dni=" + dni +
                ", nombre=" + nombre +
                ", apellido1=" + apellido1 +
                ", apellido2=" + apellido2 +
                ", cursoId=" + cursoId.toString() +
                ", email=" + email +
                ", password=" + password +
                '}';
    }

    public List<Horario> getHorarios() {
        return horarios;
    }

    public void setHorarios(List<Horario> horarios) {
        this.horarios = horarios;
    }
}
