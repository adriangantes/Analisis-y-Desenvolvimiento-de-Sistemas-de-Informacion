package org.irlab.model.daos;

import org.irlab.model.entities.Horario;
import org.junit.jupiter.api.Test;

import java.sql.Time;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class HorarioDaoTest extends DaoTestBase{

    @Test
    public void testCreateAndFindById() {
        Horario h = new Horario();
        h.setDiaSemana("Lunes");
        h.setHoraEntrada(Time.valueOf("09:00:00"));
        h.setHoraSalida(Time.valueOf("10:00:00"));
        h.setAsignatura("Matemáticas");
        h.setAula("101");
        h.setProfesor("Pedro");

        HorarioDao.create(em, h);
        em.flush();

        Optional<Horario> found = HorarioDao.findById(em, h.getId());
        assertTrue(found.isPresent());
        assertEquals("Matemáticas", found.get().getAsignatura());
    }

    @Test
    public void testUpdateHorario() {
        Horario h = new Horario();
        h.setDiaSemana("Martes");
        h.setHoraEntrada(Time.valueOf("11:00:00"));
        h.setHoraSalida(Time.valueOf("12:00:00"));
        h.setAsignatura("Física");
        h.setAula("202");
        h.setProfesor("Laura");

        HorarioDao.create(em, h);
        em.flush();

        h.setAsignatura("Química");
        HorarioDao.update(em, h);
        em.flush();

        Horario updated = HorarioDao.findById(em, h.getId()).get();
        assertEquals("Química", updated.getAsignatura());
    }

    @Test
    public void testFindAllHorarios() {
        List<Horario> all = HorarioDao.findAll(em);
        assertNotNull(all);
    }
}
