package org.irlab.model.daos;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.NonUniqueResultException;
import jakarta.persistence.TypedQuery;
import org.irlab.model.entities.Alumno;
import org.irlab.model.entities.Horario;

import java.util.List;
import java.util.Optional;

public class HorarioDao {

    private HorarioDao () {}

    public static Long create (EntityManager em, Horario horario) {
        em.persist(horario);
        return horario.getId();
    }

    public static void update (EntityManager em, Horario horario) {
        Optional<Horario> res = findById(em, horario.getId());

        if (res.isPresent()) {
            Horario update = res.get();
            update.setDiaSemana(horario.getDiaSemana());
            update.setHoraEntrada(horario.getHoraEntrada());
            update.setHoraSalida(horario.getHoraSalida());
            update.setAsignatura(horario.getAsignatura());
            update.setAula(horario.getAula());
            update.setProfesor(horario.getProfesor());
            update.setAlumnos(horario.getAlumnos());

            em.merge(update);
        } else {
            throw new EntityNotFoundException("Horario no encontrado con ID: " + horario.getId());
        }
    }

    public static Optional<Horario> findById (EntityManager em, Long id){
        TypedQuery<Horario> q = em.createQuery("SELECT a FROM Horario a WHERE a.id = :id", Horario.class);
        q.setParameter("id", id);
        List<Horario> queryResult = q.getResultList();
        if (queryResult.size() > 1) {
            throw new NonUniqueResultException();
        } else if (queryResult.size() == 1) {
            return Optional.of(queryResult.get(0));
        } else {
            return Optional.empty();
        }
    }

    public static List<Horario> findAll (EntityManager em){
        return em.createQuery("SELECT h FROM Horario h", Horario.class).getResultList();
    }

    public static List<Horario> findByAlumno(EntityManager em, String dni) {
        TypedQuery<Horario> query = em.createQuery(
                "SELECT h FROM Horario h JOIN h.alumnos a WHERE a.dni = :dni " +
                        "ORDER BY h.diaSemana, h.horaEntrada", Horario.class);
        query.setParameter("dni", dni);
        return query.getResultList();
    }

    public static List<Horario> findByAlumnoAndDia(EntityManager em, String dni, String dia) {
        TypedQuery<Horario> query = em.createQuery(
                "SELECT h FROM Horario h JOIN h.alumnos a " +
                        "WHERE a.dni = :dni AND h.diaSemana = :dia " +
                        "ORDER BY h.horaEntrada", Horario.class);
        query.setParameter("dni", dni);
        query.setParameter("dia", dia);
        return query.getResultList();
    }

    public static void addAlumnoToHorario(EntityManager em, Long horarioId, String dni) {
        Horario horario = em.find(Horario.class, horarioId);
        Alumno alumno = AlumnoDao.findByDni(em, dni).orElseThrow();

        horario.getAlumnos().add(alumno);
        alumno.getHorarios().add(horario);

        em.merge(horario);
    }
}
