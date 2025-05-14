/**
 * Copyright 2023-2024 Information Retrieval Lab
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.irlab.model.daos;

import java.util.List;
import java.util.Optional;

import org.irlab.model.entities.Role;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;

public class RoleDao {

    private RoleDao() {}

    /**
     * Find role by name
     *
     * @param em       the entity manager
     * @param roleName the role name
     * @return the role or an empty optional if it does not exists
     */
    public static Optional<Role> findByName(EntityManager em, String roleName) {
        TypedQuery<Role> q = em.createQuery("SELECT r FROM Role r WHERE r.roleName = :name",
                Role.class);
        q.setParameter("name", roleName);
        try {
            Role r = q.getSingleResult();
            return Optional.of(r);
        } catch (NoResultException ex) {
            return Optional.empty();
        }
    }

    /**
     * Get all roles
     *
     * @param em the entity manager
     * @return all the roles
     */
    public static List<Role> getAll(EntityManager em) {
        return em.createQuery("SELECT r FROM Role r", Role.class).getResultList();
    }
}
