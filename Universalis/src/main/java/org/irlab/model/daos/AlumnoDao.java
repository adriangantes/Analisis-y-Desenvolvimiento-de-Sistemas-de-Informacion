package org.irlab.model.daos;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.NonUniqueResultException;
import jakarta.persistence.TypedQuery;
import org.irlab.model.entities.Alumno;

import java.util.List;
import java.util.Optional;

public class AlumnoDao {

    private AlumnoDao () {}

    public static void create(EntityManager em, Alumno alumno) {
        em.persist(alumno);
    }

    public static void update(EntityManager em, Alumno alumno) {
        Optional<Alumno> alum = findByDni(em, alumno.getDni());

        if (alum.isPresent()) {
            Alumno update = alum.get();
            update.setNombre(alumno.getNombre());
            update.setApellido1(alumno.getApellido1());
            update.setApellido2(alumno.getApellido2());
            update.setPassword(alumno.getPassword());
            update.setHorarios(alumno.getHorarios());

            em.persist(update);
        }else{
            throw new EntityNotFoundException("Alumno no encontrado con DNI: " + alumno.getDni());
        }
    }

    public static Optional<Alumno> findByDni (EntityManager em, String dni) {
        TypedQuery<Alumno> q = em.createQuery("SELECT a FROM Alumno a WHERE a.dni = :dni", Alumno.class);
        q.setParameter("dni", dni);
        List<Alumno> queryResult = q.getResultList();
        if (queryResult.size() > 1) {
            throw new NonUniqueResultException();
        } else if (queryResult.size() == 1) {
            return Optional.of(queryResult.get(0));
        } else {
            return Optional.empty();
        }
    }

    public static Optional<Alumno> findByEmail (EntityManager em, String email) {
        TypedQuery<Alumno> q = em.createQuery("SELECT a FROM Alumno a WHERE a.email = :email", Alumno.class);
        q.setParameter("email", email);
        List<Alumno> queryResult = q.getResultList();
        if (queryResult.size() > 1) {
            throw new NonUniqueResultException();
        } else if (queryResult.size() == 1) {
            return Optional.of(queryResult.get(0));
        } else {
            return Optional.empty();
        }
    }

    public static List<Alumno> findAll (EntityManager em) {
        return em.createQuery("SELECT a FROM Alumno a", Alumno.class).getResultList();
    }

    public static List<Alumno> findAllOrdered(EntityManager em, String orderBy, boolean ascending) {
        String queryStr = "SELECT a FROM Alumno a ORDER BY a." + orderBy +
                (ascending ? " DESC" : " ASC");
        return em.createQuery(queryStr, Alumno.class).getResultList();
    }

    public static List<Alumno> findByFilter(EntityManager em, String filter, String value) {
        String queryStr = "SELECT a FROM Alumno a WHERE a." + filter + " LIKE :value";
        return em.createQuery(queryStr, Alumno.class)
                .setParameter("value", "%" + value + "%")
                .getResultList();
    }
}
