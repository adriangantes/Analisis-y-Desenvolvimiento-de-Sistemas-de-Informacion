package org.irlab.model.services;

import org.irlab.model.entities.Alumno;
import org.irlab.model.exceptions.UserAlreadyExistsException;
import org.irlab.model.exceptions.InvalidInputException;

import java.util.List;

public interface AlumnoService {
    Alumno altaAlumno(String dni, String nombre, String apellido1, String apellido2)
            throws UserAlreadyExistsException, InvalidInputException;

    List<Alumno> consultarAlumnos(String filtro, String valor, String orden, boolean ascendente);




}
