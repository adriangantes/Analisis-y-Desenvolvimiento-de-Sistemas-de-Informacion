package org.irlab.model.services;

import org.irlab.model.entities.Horario;

import java.util.List;

public interface HorarioService {
    Long crearHorario(Horario horario);
    List<Horario> consultarHorarios();
    List<Horario> consultarHorarioAlumno(String dni);
    List<Horario> consultarHorarioAlumnoPorDia(String dni, String dia);
    void asignarAlumnoAHorario(Long horarioId, String dniAlumno);

}
