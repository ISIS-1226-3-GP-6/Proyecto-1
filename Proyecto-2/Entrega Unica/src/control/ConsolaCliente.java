package control;

import java.util.Scanner;

public class ConsolaCliente {

    private Cafe sistema;
    private Scanner scanner;
    private String loginCliente;
    private String passwordCliente;

    public ConsolaCliente(Cafe sistema) {
        this.sistema = sistema;
        this.scanner = new Scanner(System.in);
    }

    public static void main(String[] args) {
        Cafe cafe = new Cafe("escenario1.txt");
        ConsolaCliente consola = new ConsolaCliente(cafe);
        consola.ejecutar();
        cafe.save();
        consola.scanner.close();
    }

    public void ejecutar() {
        System.out.println("===== CLIENTE =====");
        System.out.println("1. Iniciar sesion");
        System.out.println("2. Registrarse");
        System.out.print("Opcion: ");
        int op = scanner.nextInt();
        scanner.nextLine();

        if (op == 1) {
            boolean autenticado = loginCliente();
            if (!autenticado) {
                System.out.println("Error de autenticacion.");
                return;
            }
        } else if (op == 2) {
            registrarse();
            return;
        } else {
            System.out.println("Opcion invalida.");
            return;
        }

        System.out.println("Bienvenido cliente " + loginCliente);
        menu();
    }

    private boolean loginCliente() {
        for (int i = 0; i < 3; i++) {
            System.out.print("Login: ");
            String login = scanner.nextLine();
            System.out.print("Password: ");
            String password = scanner.nextLine();
            if (sistema.esCliente(login, password)) {
                this.loginCliente = login;
                this.passwordCliente = password;
                return true;
            }
            System.out.println("Credenciales incorrectas. Intentos restantes: " + (2 - i));
        }
        return false;
    }

    private void registrarse() {
        System.out.print("Nuevo login: ");
        String login = scanner.nextLine();
        System.out.print("Contrasena: ");
        String pass = scanner.nextLine();
        boolean ok = sistema.registrarCliente(login, pass);
        if (ok) {
            System.out.println("Registro exitoso. Ahora inicia sesion.");
            ejecutar();
        } else {
            System.out.println("Error: el login ya existe.");
        }
    }

    private void menu() {
        int opcion = -1;
        while (opcion != 0) {
            System.out.println("\n--- MENU CLIENTE ---");
            System.out.println("1. Crear reserva");
            System.out.println("2. Ver catalogo de juegos");
            System.out.println("3. Seleccionar juegos para prestamo");
            System.out.println("4. Solicitar prestamo");
            System.out.println("5. Devolver juegos");
            System.out.println("6. Finalizar reserva");
            System.out.println("7. Comprar juego");
            System.out.println("8. Comprar platillo");
            System.out.println("9. Usar puntos de fidelidad");
            System.out.println("10. Consultar torneos");
            System.out.println("11. Inscribirse a torneo");
            System.out.println("12. Desinscribirse de torneo");
            System.out.println("13. Agregar juego a favoritos");
            System.out.println("0. Salir");
            System.out.print("Opcion: ");

            if (scanner.hasNextInt()) {
                opcion = scanner.nextInt();
                scanner.nextLine();
            } else {
                System.out.println("Entrada invalida");
                scanner.nextLine();
                continue;
            }

            switch (opcion) {
                case 1: crearReserva(); break;
                case 2: verCatalogo(); break;
                case 3: seleccionarJuegos(); break;
                case 4: solicitarPrestamo(); break;
                case 5: devolverJuegos(); break;
                case 6: finalizarReserva(); break;
                case 7: comprarJuego(); break;
                case 8: comprarPlatillo(); break;
                case 9: usarPuntos(); break;
                case 10: consultarTorneos(); break;
                case 11: inscribirTorneo(); break;
                case 12: desinscribirTorneo(); break;
                case 13: agregarFavorito(); break;
                case 0: System.out.println("Saliendo..."); break;
                default: System.out.println("Opcion invalida");
            }
        }
    }

    private void crearReserva() {
        System.out.print("Numero de personas: ");
        int personas = scanner.nextInt();
        scanner.nextLine();
        System.out.print("Hay menores? (true/false): ");
        boolean menores = scanner.nextBoolean();
        System.out.print("Hay ninos? (true/false): ");
        boolean ninos = scanner.nextBoolean();
        scanner.nextLine();
        boolean ok = sistema.crearReserva(loginCliente, passwordCliente, personas, menores, ninos);
        if (ok) {
            System.out.println("Reserva creada.");
        } else {
            System.out.println("Error al crear reserva (capacidad excedida o sin mesas disponibles).");
        }
    }

    private void verCatalogo() {
        var juegos = sistema.getCatalogoPrestamo();
        if (juegos.isEmpty()) {
            System.out.println("No hay juegos disponibles.");
            return;
        }
        System.out.println("--- JUEGOS DISPONIBLES PARA PRESTAMO ---");
        for (var j : juegos) {
            String disponible = j.isOcupado() ? "No disponible" : "Disponible";
            System.out.println(j.getJuegoBase().getNombre() + " | " + disponible);
        }
    }

    private void seleccionarJuegos() {
        System.out.print("Nombre del juego 1: ");
        String j1 = scanner.nextLine();
        System.out.print("Nombre del juego 2 (dejar vacio si no): ");
        String j2 = scanner.nextLine();
        boolean ok = sistema.seleccionarJuegosPrestamo(loginCliente, passwordCliente, j1, j2);
        if (ok) {
            System.out.println("Juegos seleccionados.");
        } else {
            System.out.println("Error en la seleccion.");
        }
    }

    private void solicitarPrestamo() {
        boolean ok = sistema.solicitarPrestamo(loginCliente, passwordCliente);
        if (ok) {
            System.out.println("Prestamo solicitado exitosamente.");
        } else {
            System.out.println("Error en el prestamo.");
        }
    }

    private void devolverJuegos() {
        boolean ok = sistema.devolverJuegos(loginCliente, passwordCliente);
        if (ok) {
            System.out.println("Juegos devueltos.");
        } else {
            System.out.println("Error al devolver juegos.");
        }
    }

    private void finalizarReserva() {
        boolean ok = sistema.finalizarReserva(loginCliente, passwordCliente);
        if (ok) {
            System.out.println("Reserva finalizada.");
        } else {
            System.out.println("Error: aun hay prestamos activos o pagos pendientes.");
        }
    }

    private void comprarJuego() {
        sistema.mostrarCatalogoCompra();
        System.out.print("Nombre del juego: ");
        String juego = scanner.nextLine();
        boolean ok = sistema.comprarJuego(loginCliente, passwordCliente, juego);
        if (ok) {
            System.out.println("Compra realizada. Se agregaron puntos de fidelidad.");
        } else {
            System.out.println("Error en la compra.");
        }
    }

    private void comprarPlatillo() {
        sistema.mostrarMenu();
        System.out.print("Nombre del platillo: ");
        String platillo = scanner.nextLine();
        System.out.print("Cantidad: ");
        int cantidad = scanner.nextInt();
        scanner.nextLine();
        boolean ok = sistema.comprarPlatillo(loginCliente, passwordCliente, platillo, cantidad);
        if (ok) {
            System.out.println("Compra realizada.");
        } else {
            System.out.println("Error en la compra.");
        }
    }

    private void usarPuntos() {
        System.out.print("Puntos a usar: ");
        int puntos = scanner.nextInt();
        scanner.nextLine();
        boolean ok = sistema.usarPuntosFidelidad(loginCliente, passwordCliente, puntos);
        if (ok) {
            System.out.println("Descuento aplicado.");
        } else {
            System.out.println("Error: puntos insuficientes.");
        }
    }

    private void consultarTorneos() {
        var torneos = sistema.getTorneosActivos();
        if (torneos.isEmpty()) {
            System.out.println("No hay torneos activos.");
            return;
        }
        System.out.println("--- TORNEOS ACTIVOS ---");
        for (var t : torneos) {
            int cuposRestantes = t.getCupos() - t.getCuposTaken();
            System.out.println("Juego: " + t.getJuego().getNombre() +
                    " | Competitivo: " + t.isEsCompetitivo() +
                    " | Bono: " + t.getBono() +
                    " | Cupos disponibles: " + cuposRestantes);
        }
    }

    private void inscribirTorneo() {
        System.out.print("Nombre del juego del torneo: ");
        String juegoNombre = scanner.nextLine();
        System.out.print("Numero de participantes (max 3): ");
        int participantes = scanner.nextInt();
        scanner.nextLine();
        boolean ok = sistema.inscribirTorneo(loginCliente, passwordCliente, juegoNombre, participantes);
        if (ok) {
            System.out.println("Inscripcion exitosa.");
        } else {
            System.out.println("Error en la inscripcion.");
        }
    }

    private void desinscribirTorneo() {
        System.out.print("Nombre del juego del torneo: ");
        String juegoNombre = scanner.nextLine();
        boolean ok = sistema.desinscribirTorneo(loginCliente, passwordCliente, juegoNombre);
        if (ok) {
            System.out.println("Desinscripcion exitosa.");
        } else {
            System.out.println("Error en la desinscripcion.");
        }
    }

    private void agregarFavorito() {
        System.out.print("Nombre del juego favorito: ");
        String juego = scanner.nextLine();
        boolean ok = sistema.agregarJuegoFavorito(loginCliente, passwordCliente, juego);
        if (ok) {
            System.out.println("Juego agregado a favoritos.");
        } else {
            System.out.println("Error: el juego no existe.");
        }
    }
}