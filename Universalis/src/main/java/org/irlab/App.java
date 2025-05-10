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

package org.irlab;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Time;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

import org.irlab.common.AppEntityManagerFactory;
import org.irlab.model.entities.Alumno;
import org.irlab.model.entities.Horario;
import org.irlab.model.exceptions.InvalidInputException;
import org.irlab.model.exceptions.RoleNotFoundException;
import org.irlab.model.exceptions.UserAlreadyExistsException;
import org.irlab.model.exceptions.UserNotFoundException;
import org.irlab.model.services.*;

import jakarta.persistence.EntityManager;

public class App {

    private enum Command {
        EXIT, ALTA_ALUMNO, LISTAR_ALUMNO, CONSULTAR_HORARIOS_ALUMNO, CONSULTAR_HORARIOS
    }

    private static final int CORRECT_SHUTDOWN = 50000;

    private static UserService userService = null;

    private static RoleService roleService = null;

    private static AlumnoService alumnoService = null;
    private static HorarioService horarioService = null;
    private static Scanner scanner = null;

    private static void init() {
        try (EntityManager em = AppEntityManagerFactory.getInstance().createEntityManager()) {}
        try {
            userService = new UserServiceImpl();
            roleService = new RoleServiceImpl();
            alumnoService = new AlumnoServiceImpl();
            horarioService = new HorarioServiceImpl();
        } catch (RoleNotFoundException e) {
            System.out.println(
                    """
                            Could not find the default role in the database.

                            This usually means the database has not been populated. The schema has now been created if
                            it didn't exists already. You can now populate the database with:

                              mvn sql:execute

                            Have a good day!
                            """);
            System.exit(1);
        }
    }

    private static void shutdown() throws SQLException {
        AppEntityManagerFactory.close();

        try {
            DriverManager.getConnection("jdbc:derby:;shutdown=true");
        } catch (SQLException e) {
            if (e.getErrorCode() != CORRECT_SHUTDOWN) {
                throw e;
            }
        }
    }

    private static Command getCommand() {
        System.out.println("Choose an option:");
        System.out.println("  1) Dar de alta al alumno");
        System.out.println("  2) Consultar lista de alumnos");
        System.out.println("  3) Consultar horario alumno");
        System.out.println("  4) Gestionar horarios");
        System.out.println();
        System.out.println("  q) Exit");
        System.out.println();
        while (true) {
            System.out.print("Option: ");
            String input = scanner.nextLine();
            if (input.isEmpty()) {
                System.out.println("An option needs to be introduced");
            } else if (input.length() > 1) {
                System.err.println(input + " is not a valid option");
            } else {
                switch (input.charAt(0)) {
                    case '1': return Command.ALTA_ALUMNO;

                    case '2': return Command.LISTAR_ALUMNO;

                    case '3': return Command.CONSULTAR_HORARIOS_ALUMNO;

                    case '4': return Command.CONSULTAR_HORARIOS;

                    case 'q': return Command.EXIT;
                    default: System.out.println(input + " is not a valid option");
                }
            }
        }
    }

    private static @Nonnull String readInput(String message, String errorMessage) {
        String result = null;
        while (result == null) {
            System.out.print(message);
            String input = scanner.nextLine();
            if (input.isEmpty()) {
                System.out.println(errorMessage);
            } else {
                result = input;
            }
        }
        return result;
    }

    private static void greetUser() {
        String userName = readInput("User name: ", "You must supply a user name");
        String greetingMessage = userService.greet(userName);
        System.out.println(greetingMessage);
    }

    private static void changeGreeting() {
        String userName = readInput("User name: ", "You must supply a user name");
        String newGreeting = readInput("Greeting message: ",
                "You must supply a new greeting message");
        try {
            userService.setUserGreeting(userName, newGreeting);
            System.out.println("User greeting changed");
        } catch (UserNotFoundException e) {
            System.out.println(
                    String.format("Greeting could not be changed, due to the following error:\n%s",
                            e.getMessage()));
        }

    }

    private static @Nonnull String askForRole() {
        String roleList = roleService.getAvailableRoleNames().stream()
                .map(name -> "  - " + name + "\n").collect(Collectors.joining(""));
        String prompt = "Select a role for the user. Available roles are:\n" + roleList
                + "User role: ";
        return readInput(prompt, "You must supply a role name");
    }

    private static void createUser() {
        String userName = readInput("User name: ", "You must supply a user name");
        String roleName = askForRole();
        try {
            userService.createUser(userName, roleName);
            System.out.println("User created");
        } catch (UserAlreadyExistsException e) {
            System.out.println("User not created: a user with that name already exists.");
        } catch (RoleNotFoundException e) {
            System.out.println("User not created: invalid role");
        }
    }

    private static void altaAlumno() {
        String dni = readInput("DNI: ", "El DNI es obligatorio");
        String nombre = readInput("Nombre: ", "El nombre es obligatorio");
        String apellido1 = readInput("Primer apellido: ", "Obligatorio");
        String apellido2 = readInput("Segundo apellido: ", "Puede estar vacío");
        //String email = readInput("Email: ", "El email es obligatorio");

        AlumnoService alumnoService = new AlumnoServiceImpl();

        try {
            Alumno a = alumnoService.altaAlumno(dni, nombre, apellido1, apellido2);
            System.out.println("\nAlumno dado de alta correctamente");
            System.out.println("\tEmail: " + a.getEmail());
            System.out.println("\tPassword: " + a.getPassword());
        } catch (UserAlreadyExistsException | InvalidInputException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void listarAlumnos() {
        if (alumnoService == null) {
            System.out.println("Error: Servicio de alumnos no disponible");
            return;
        }
        try {
            System.out.println("\n--- LISTADO DE ALUMNOS ---");
            System.out.println("Filtrar por (dejar en blanco para no filtrar):");
            System.out.print("Campo (dni, nombre, apellido1, apellido2, email): ");
            String filtro = scanner.nextLine();

            String valor = "";
            if (!filtro.isEmpty()) {
                System.out.print("Valor a buscar: ");
                valor = scanner.nextLine();
            }

            System.out.print("Ordenar por (default: apellido1): ");
            String orden = scanner.nextLine();
            if (orden.isEmpty()) orden = "apellido1";

            System.out.print("Orden ascendente (s/n)? ");
            boolean ascendente = scanner.nextLine().equalsIgnoreCase("s");


            List<Alumno> alumnos = alumnoService.consultarAlumnos(
                    filtro.isEmpty() ? null : filtro,
                    valor.isEmpty() ? null : valor,
                    orden,
                    ascendente);

            System.out.println("\nRESULTADOS:");
            System.out.printf("%-10s %-15s %-15s %-15s %-20s%n",
                    "DNI", "Nombre", "Primer Apellido", "Segundo Apellido", "Email");

            for (Alumno a : alumnos) {
                System.out.printf("%-10s %-15s %-15s %-15s %-20s%n",
                        a.getDni(), a.getNombre(), a.getApellido1(),
                        a.getApellido2(), a.getEmail());
            }
        } catch (Exception e){
            System.out.println("Error al listar alumnos: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void consultarHorarioAlumno() {
        System.out.print("\nIntroduce DNI del alumno: ");
        String dni = scanner.nextLine();

        System.out.print("Filtrar por día (Lunes-Martes-Miércoles-Jueves-Viernes): ");
        String dia = scanner.nextLine();

        List<Horario> horarios;
        if (dia.isEmpty()) {
            horarios = horarioService.consultarHorarioAlumno(dni);
        } else {
            horarios = horarioService.consultarHorarioAlumnoPorDia(dni, dia);
        }

        if (horarios.isEmpty()) {
            System.out.println("No se encontraron horarios para este alumno.");
            return;
        }

        System.out.println("\n--- HORARIO DEL ALUMNO ---");
        System.out.printf("%-10s %-10s %-10s %-20s %-10s %-20s%n",
                "Día", "Entrada", "Salida", "Asignatura", "Aula", "Profesor");

        for (Horario h : horarios) {
            System.out.printf("%-10s %-10s %-10s %-20s %-10s %-20s%n",
                    h.getDiaSemana(),
                    h.getHoraEntrada().toString().substring(0, 5), // Formato HH:mm
                    h.getHoraSalida().toString().substring(0, 5),
                    h.getAsignatura(),
                    h.getAula(),
                    h.getProfesor());
        }
    }

    private static void consultarHorarios() {

        while(true){
            List<Horario> horarios = horarioService.consultarHorarios();

            System.out.println("\n--- HORARIOS ---");

            if (horarios.isEmpty()) {
                System.out.println("No hay horarios disponibles.");
                return;
            } else {
                System.out.printf("%-10s %-10s %-10s %-10s %-20s %-10s %-20s%n",
                        "ID", "Día", "Entrada", "Salida", "Asignatura", "Aula", "Profesor");

                for (Horario h : horarios) {
                    System.out.printf("%-10s %-10s %-10s %-10s %-20s %-10s %-20s%n",
                            h.getId(),
                            h.getDiaSemana(),
                            h.getHoraEntrada().toString().substring(0, 5), // Formato HH:mm
                            h.getHoraSalida().toString().substring(0, 5),
                            h.getAsignatura(),
                            h.getAula(),
                            h.getProfesor());
                }
            }

            label:
            while(true) {
                System.out.println();
                System.out.println("Choose an option:");
                System.out.println("  e) exit (go back to main menu)");
                System.out.println("  1) create new schedule");
                System.out.println("  2) add student to schedule\n");
                System.out.print("Option: ");
                String input = scanner.nextLine();

                switch (input) {
                    case "1":
                        crearHorario();
                        break label;
                    case "2":
                        addAlumnoHorario();
                        break label;
                    case "e":
                        return;
                    default:
                        System.out.println(input + " is not a valid option");
                        break;
                }
            }
        }
    }

    private static void crearHorario() {
        System.out.println("\n--- CREAR NUEVO HORARIO ---");

        // Datos básicos del horario
        System.out.print("Día de la semana (Lunes-Martes-...): ");
        String dia = scanner.nextLine();

        System.out.print("Hora de entrada (HH:MM): ");
        Time horaEntrada = Time.valueOf(scanner.nextLine() + ":00");

        System.out.print("Hora de salida (HH:MM): ");
        Time horaSalida = Time.valueOf(scanner.nextLine() + ":00");

        System.out.print("Asignatura: ");
        String asignatura = scanner.nextLine();

        System.out.print("Aula: ");
        String aula = scanner.nextLine();

        System.out.print("Profesor: ");
        String profesor = scanner.nextLine();

        // Crear el horario
        Horario horario = new Horario(dia, horaEntrada, horaSalida, asignatura, aula, profesor);
        Long horarioId = horarioService.crearHorario(horario);

        // Asignar alumnos
        System.out.println("\n--- ASIGNAR ALUMNOS ---");
        addAlumno(horarioId);

        System.out.println("Horario creado con ID " + horarioId + " y alumnos asignados correctamente");
    }

    private static void addAlumno(Long horarioId){

        while (true) {
            System.out.print("Introduce DNI del alumno a asignar (o 'fin' para terminar): ");
            String dni = scanner.nextLine();

            if (dni.equalsIgnoreCase("fin")) {
                break;
            }

            try {
                horarioService.asignarAlumnoAHorario(horarioId, dni);
                System.out.println("Alumno asignado correctamente");
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    public static void addAlumnoHorario () {

        System.out.println("\n--- SELECCIONAR HORARIO ---");
        System.out.print("ID del horario: ");
        Long horarioId = Long.valueOf(scanner.nextLine());

        System.out.println("\n--- ASIGNAR ALUMNOS ---");
        addAlumno(horarioId);
    }

    public static void main(String[] args) throws SQLException {
        init();
        boolean exit = false;
        scanner = new Scanner(System.in);
        while (!exit) {
            System.out.println();
            Command command = getCommand();
            switch (command) {
                case ALTA_ALUMNO -> altaAlumno();
                case LISTAR_ALUMNO -> listarAlumnos();
                case CONSULTAR_HORARIOS_ALUMNO -> consultarHorarioAlumno();
                case CONSULTAR_HORARIOS -> consultarHorarios();
                case EXIT -> exit = true;
            }
        }

        shutdown();
    }
}
