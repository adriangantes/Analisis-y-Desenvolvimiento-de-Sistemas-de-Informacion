package org.irlab.model.services;

import jakarta.persistence.EntityManager;
import org.irlab.common.AppEntityManagerFactory;
import org.irlab.model.daos.HorarioDao;
import org.irlab.model.entities.Horario;

import java.util.List;

public class HorarioServiceImpl implements HorarioService {

    @Override
    public List<Horario> consultarHorarioAlumno(String dni) {
        EntityManager em = AppEntityManagerFactory.getInstance().createEntityManager();
        try {
            return HorarioDao.findByAlumno(em, dni);
        } finally {
            em.close();
        }
    }

    @Override
    public List<Horario> consultarHorarios() {
        EntityManager em = AppEntityManagerFactory.getInstance().createEntityManager();
        try {
            return HorarioDao.findAll(em);
        } finally {
            em.close();
        }
    }

    @Override
    public List<Horario> consultarHorarioAlumnoPorDia(String dni, String dia) {
        EntityManager em = AppEntityManagerFactory.getInstance().createEntityManager();
        try {
            return HorarioDao.findByAlumnoAndDia(em, dni, dia);
        } finally {
            em.close();
        }
    }

    @Override
    public Long crearHorario(Horario horario) {
        EntityManager em = AppEntityManagerFactory.getInstance().createEntityManager();
        try {
            em.getTransaction().begin();
            HorarioDao.create(em, horario);
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
        return horario.getId();
    }

    @Override
    public void asignarAlumnoAHorario(Long horarioId, String dniAlumno) {
        EntityManager em = AppEntityManagerFactory.getInstance().createEntityManager();
        try {
            em.getTransaction().begin();
            HorarioDao.addAlumnoToHorario(em, horarioId, dniAlumno);
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }
}