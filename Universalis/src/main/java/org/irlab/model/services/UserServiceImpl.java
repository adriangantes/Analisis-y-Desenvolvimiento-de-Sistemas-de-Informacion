/**
 * Copyright 2022-2025 Information Retrieval Lab
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

package org.irlab.model.services;

import java.util.Locale;
import java.util.Optional;

import javax.annotation.Nonnull;

import org.irlab.common.AppEntityManagerFactory;
import org.irlab.model.daos.RoleDao;
import org.irlab.model.daos.UserDao;
import org.irlab.model.entities.Role;
import org.irlab.model.entities.User;
import org.irlab.model.exceptions.RoleNotFoundException;
import org.irlab.model.exceptions.UserAlreadyExistsException;
import org.irlab.model.exceptions.UserNotFoundException;

import com.google.common.base.Preconditions;

/**
 * Implementation of the user service facade
 */
public class UserServiceImpl implements UserService {

    private static final String DEFAULT_GREETING = "Hello";
    private static final String DEFAULT_ROLE_NAME = "user";

    private static final String MESSAGE_FORMAT = "%s, %s!";

    public UserServiceImpl() throws RoleNotFoundException {
        try (var em = AppEntityManagerFactory.getInstance().createEntityManager()) {
            RoleDao.findByName(em, DEFAULT_ROLE_NAME)
                    .orElseThrow(() -> new RoleNotFoundException(DEFAULT_ROLE_NAME));
        }
    }

    /* Constructor for tests, not a good practice */
    UserServiceImpl(boolean createDefault) {
        try (var em = AppEntityManagerFactory.getInstance().createEntityManager()) {
            var role = new Role(DEFAULT_ROLE_NAME);
            try {
                em.getTransaction().begin();
                em.persist(role);
                em.getTransaction().commit();
            } catch (Exception e) {
                em.getTransaction().rollback();
                throw e;
            }
        }
    }

    @Override
    @Nonnull
    public String greet(@Nonnull String name) {
        Preconditions.checkNotNull(name, "name cannot be null");

        try (var em = AppEntityManagerFactory.getInstance().createEntityManager()) {
            Optional<User> maybeUser = UserDao.findByName(em, name);
            String greeting = maybeUser.map(User::getGreeting).orElse(DEFAULT_GREETING);

            return String.format(Locale.ENGLISH, MESSAGE_FORMAT, greeting, name);
        }
    }

    @Override
    public void setUserGreeting(@Nonnull String name, @Nonnull String greeting)
            throws UserNotFoundException {
        Preconditions.checkNotNull(name, "name cannot be null");
        Preconditions.checkNotNull(greeting, "greeting cannot be null");

        try (var em = AppEntityManagerFactory.getInstance().createEntityManager()) {
            User user = UserDao.findByName(em, name).map(u -> {
                u.setGreeting(greeting);
                return u;
            }).orElseThrow(() -> new UserNotFoundException(String.format("with name %s", name)));

            try {
                em.getTransaction().begin();
                UserDao.update(em, user);
                em.getTransaction().commit();
            } catch (Exception e) {
                em.getTransaction().rollback();
                throw e;
            }
        }

    }

    @Override
    public void createUser(@Nonnull String name)
            throws UserAlreadyExistsException, RoleNotFoundException {
        createUser(name, DEFAULT_ROLE_NAME);
    }

    @Override
    public void createUser(@Nonnull String name, @Nonnull String role)
            throws UserAlreadyExistsException, RoleNotFoundException {
        try (var em = AppEntityManagerFactory.getInstance().createEntityManager()) {
            Role r = RoleDao.findByName(em, role)
                    .orElseThrow(() -> new RoleNotFoundException(role));
            var maybeUser = UserDao.findByName(em, name);
            if (maybeUser.isPresent()) {
                throw new UserAlreadyExistsException(name);
            } else {
                User user = new User(name, DEFAULT_GREETING, r);
                try {
                    em.getTransaction().begin();
                    em.persist(user);
                    em.getTransaction().commit();
                } catch (Exception e) {
                    em.getTransaction().rollback();
                    throw e;
                }
            }
        }
    }

}
