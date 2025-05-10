package org.irlab.model.daos;

import org.irlab.model.entities.Alumno;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class AlumnoDaoTest extends DaoTestBase{

    @Test
    public void testCreateAndFindByDni() {
        Alumno a = new Alumno();
        a.setDni("12345678A");
        a.setNombre("Mario");
        a.setApellido1("Sánchez");
        a.setEmail("mario@example.com");
        a.setPassword("clave123");

        AlumnoDao.create(em, a);
        em.flush();

        Optional<Alumno> found = AlumnoDao.findByDni(em, "12345678A");
        assertTrue(found.isPresent());
        assertEquals("Mario", found.get().getNombre());
    }

    @Test
    public void testUpdateAlumno() {
        Alumno a = new Alumno();
        a.setDni("87654321Z");
        a.setNombre("Lucía");
        a.setApellido1("Martínez");
        a.setEmail("lucia@example.com");
        a.setPassword("pass");

        AlumnoDao.create(em, a);
        em.flush();

        a.setNombre("Lucía Actualizada");
        AlumnoDao.update(em, a);
        em.flush();

        Alumno updated = AlumnoDao.findByDni(em, "87654321Z").get();
        assertEquals("Lucía Actualizada", updated.getNombre());
    }

    @Test
    public void testFindAllAlumnos() {
        List<Alumno> all = AlumnoDao.findAll(em);
        assertNotNull(all);
    }
}
