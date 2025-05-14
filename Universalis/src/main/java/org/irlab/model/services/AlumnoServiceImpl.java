package org.irlab.model.services;

import jakarta.persistence.EntityManager;
import org.irlab.common.AppEntityManagerFactory;
import org.irlab.model.daos.AlumnoDao;
import org.irlab.model.entities.Alumno;
import org.irlab.model.exceptions.UserAlreadyExistsException;
import org.irlab.model.exceptions.InvalidInputException;

import java.util.List;

public class AlumnoServiceImpl implements AlumnoService {

    @Override
    public Alumno altaAlumno(String dni, String nombre, String apellido1, String apellido2)
            throws UserAlreadyExistsException, InvalidInputException {

        if (dni == null || nombre == null || apellido1 == null) {
            throw new InvalidInputException("Faltan datos obligatorios");
        }

        EntityManager em = AppEntityManagerFactory.getInstance().createEntityManager();
        Alumno alumno = null;
        try {
            em.getTransaction().begin();

            // CORRECCIÓN: Usar isPresent() para verificar si el Optional contiene un valor
            if (AlumnoDao.findByDni(em, dni).isPresent()) {
                throw new UserAlreadyExistsException("Ya existe un alumno con ese DNI o email.");
            }

            alumno = new Alumno(dni, nombre, apellido1, apellido2);

            AlumnoDao.create(em, alumno);
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
        return alumno;
    }

    public List<Alumno> consultarAlumnos(String filtro, String valor, String orden, boolean ascendente) {
        EntityManager em = AppEntityManagerFactory.getInstance().createEntityManager();
        try {
            if (filtro != null && valor != null) {
                return AlumnoDao.findByFilter(em, filtro, valor);
            } else {
                return AlumnoDao.findAllOrdered(em, orden != null ? orden : "apellido1", ascendente);
            }
        } finally {
            em.close();
        }
    }


}