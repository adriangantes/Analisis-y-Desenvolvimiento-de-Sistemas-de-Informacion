package org.irlab.model.daos;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class DaoTestBase {

    protected EntityManagerFactory emf;
    protected EntityManager em;

    @BeforeAll
    public void setupEntityManagerFactory() {
        emf = Persistence.createEntityManagerFactory("test-persistence-unit");
    }

    @BeforeEach
    public void initEntityManager() {
        em = emf.createEntityManager();
        em.getTransaction().begin();
    }

    @AfterEach
    public void closeEntityManager() {
        if (em.getTransaction().isActive()) em.getTransaction().rollback();
        em.close();
    }

    @AfterAll
    public void closeEntityManagerFactory() {
        emf.close();
    }
}
