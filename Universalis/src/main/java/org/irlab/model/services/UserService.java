/**
 * Copyright 2022-2024 Information Retrieval Lab
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

import javax.annotation.Nonnull;

import org.irlab.model.exceptions.RoleNotFoundException;
import org.irlab.model.exceptions.UserAlreadyExistsException;
import org.irlab.model.exceptions.UserNotFoundException;

/**
 * The user service facade
 */
public interface UserService {

    /**
     * Get a string greeting the user
     *
     * @param name the name of the user
     * @return the greeting message
     */
    String greet(@Nonnull String name);

    /**
     * Change the user greeting message
     *
     * @param name     the name of the user
     * @param greeting the greeting message
     */
    void setUserGreeting(@Nonnull String name, @Nonnull String greeting)
            throws UserNotFoundException;

    /**
     * Create user with default role and greeting
     *
     * @param name the name of the user
     * @throws UserAlreadyExistsException if a user with the same name already
     *                                    exists
     */
    void createUser(@Nonnull String name) throws UserAlreadyExistsException, RoleNotFoundException;

    /**
     * Create user with the given role and the default greeting message
     *
     * @param name the name of the user
     * @param role the role of the user
     * @throws UserAlreadyExistsException if a user with the same name already
     *                                    exists
     * @throws RoleNotFoundException      if a role with the given name cannot be
     *                                    foundd
     */
    void createUser(@Nonnull String name, @Nonnull String role)
            throws UserAlreadyExistsException, RoleNotFoundException;
}
