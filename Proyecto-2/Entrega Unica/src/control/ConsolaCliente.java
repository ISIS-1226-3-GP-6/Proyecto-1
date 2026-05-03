package control;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

import cafeteria.Platillo;
import compras.CompraJuegoMesa;
import compras.CompraPlatillo;
import juego.JuegoDeMesa;
import juego.JuegoFisico;
import juego.Prestamo;
import juego.Torneo;
import reservacion.Reserva;
import usuarios.Cliente;
import usuarios.Usuario;

public class ConsolaCliente {

    private Cafe sistema;
    private Scanner scanner;
    private String loginCliente;
    private String passwordCliente;
    private Cliente clienteAutenticado;
    private Reserva reservaActiva;

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

            Usuario user = sistema.getUsuarios().stream()
                    .filter(u -> u.getLogin().equals(login) && u.autenticacion(password))
                    .findFirst()
                    .orElse(null);

            if (user instanceof Cliente) {
                this.loginCliente = login;
                this.passwordCliente = password;
                this.clienteAutenticado = (Cliente) user;
                return true;
            }
            System.out.println("Credenciales incorrectas.");
        }
        return false;
    }

    private void registrarse() {
        System.out.print("Nuevo login: ");
        String login = scanner.nextLine();
        System.out.print("Contrasena: ");
        String pass = scanner.nextLine();

        boolean existe = sistema.getUsuarios().stream()
                .anyMatch(u -> u.getLogin().equals(login));

        if (existe) {
            System.out.println("Error: el login ya existe.");
            return;
        }

        Cliente nuevo = new Cliente(login, pass, 0);
        sistema.getUsuarios().add(nuevo);
        System.out.println("Registro exitoso. Ahora inicia sesion.");
        ejecutar();
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
            System.out.println("14. Ver mis puntos y favoritos");
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
                case 3 : seleccionarJuegos(); break;
                case 4 : solicitarPrestamo(); break;
                case 5 : devolverJuegos(); break;
                case 6 : finalizarReserva(); break;
                case 7 : comprarJuego(); break;
                case 8 : comprarPlatillo(); break;
                case 9 : usarPuntos(); break;
                case 10 : consultarTorneos(); break;
                case 11 : inscribirTorneo(); break;
                case 12 : desinscribirTorneo(); break;
                case 13 : agregarFavorito(); break;
                case 14 : verEstadoCliente(); break;
                case 0 : System.out.println("Saliendo..."); break;
                default : System.out.println("Opcion invalida");
            }
        }
    }

    private void crearReserva() {
        if (reservaActiva != null && !reservaActiva.isTerminada()) {
            System.out.println("Ya tienes una reserva activa. Finalizala primero.");
            return;
        }

        System.out.print("Numero de personas: ");
        int personas = scanner.nextInt();
        scanner.nextLine();
        System.out.print("Hay menores? (true/false): ");
        boolean menores = scanner.nextBoolean();
        System.out.print("Hay ninos? (true/false): ");
        boolean ninos = scanner.nextBoolean();
        scanner.nextLine();

        boolean ok = sistema.crearReservacion(loginCliente, passwordCliente, personas, menores, ninos);
        if (ok) {
            // Buscar la reserva recien creada
            reservaActiva = sistema.getReservas().stream()
                    .filter(r -> r.getCliente().getLogin().equals(loginCliente) && !r.isTerminada())
                    .findFirst()
                    .orElse(null);
            System.out.println("Reserva creada.");
        } else {
            System.out.println("Error: capacidad excedida o sin mesas disponibles.");
        }
    }

    private void verCatalogo() {
        List<JuegoFisico> prestamo = sistema.getCatalogoPrestamo();
        if (prestamo.isEmpty()) {
            System.out.println("No hay juegos disponibles para prestamo.");
            return;
        }
        System.out.println("\n--- JUEGOS DISPONIBLES PARA PRESTAMO ---");
        for (JuegoFisico j : prestamo) {
            String disponible = j.isOcupado() ? "No disponible" : "Disponible";
            System.out.println(j.getJuegoBase().getNombre() + " | " + disponible);
        }
    }

    private void seleccionarJuegos() {
        if (reservaActiva == null || reservaActiva.isTerminada()) {
            System.out.println("No tienes una reserva activa. Crea una primero.");
            return;
        }

        System.out.print("Nombre del juego 1: ");
        String j1 = scanner.nextLine();
        System.out.print("Nombre del juego 2 (dejar vacio si no): ");
        String j2 = scanner.nextLine();

        List<JuegoFisico> seleccionados = new ArrayList<>();
        JuegoFisico juego1 = buscarJuegoPorNombre(j1);
        if (juego1 != null && !juego1.isOcupado()) {
            seleccionados.add(juego1);
        } else {
            System.out.println("Juego no disponible: " + j1);
            return;
        }

        if (!j2.isBlank()) {
            JuegoFisico juego2 = buscarJuegoPorNombre(j2);
            if (juego2 != null && !juego2.isOcupado()) {
                seleccionados.add(juego2);
            } else {
                System.out.println("Juego no disponible: " + j2);
                return;
            }
        }

        if (seleccionados.size() > 2) {
            System.out.println("Solo puedes seleccionar hasta 2 juegos.");
            return;
        }

        // Guardar seleccion en algun lado (usando un atributo temporal o directamente el prestamo)
        System.out.println("Juegos seleccionados. Ahora puedes solicitar el prestamo.");
    }

    private JuegoFisico buscarJuegoPorNombre(String nombre) {
        return sistema.getCatalogoPrestamo().stream()
                .filter(j -> j.getJuegoBase().getNombre().equalsIgnoreCase(nombre) && !j.isOcupado())
                .findFirst()
                .orElse(null);
    }

    private void solicitarPrestamo() {
        if (reservaActiva == null || reservaActiva.isTerminada()) {
            System.out.println("No tienes una reserva activa.");
            return;
        }

        System.out.print("Nombre del juego a solicitar: ");
        String nombre = scanner.nextLine();
        JuegoFisico juego = buscarJuegoPorNombre(nombre);

        if (juego == null) {
            System.out.println("Juego no disponible.");
            return;
        }

        Prestamo prestamo = sistema.generarPrestamoJuego(loginCliente, passwordCliente, juego, reservaActiva);
        if (prestamo != null) {
            System.out.println("Prestamo solicitado exitosamente.");
        } else {
            System.out.println("Error al solicitar prestamo.");
        }
    }

    private void devolverJuegos() {
        if (reservaActiva == null || reservaActiva.isTerminada()) {
            System.out.println("No tienes una reserva activa.");
            return;
        }

        if (reservaActiva.getPrestamosActivos().isEmpty()) {
            System.out.println("No tienes prestamos activos.");
            return;
        }

        for (Prestamo p : reservaActiva.getPrestamosActivos()) {
            p.finalizar();
        }
        System.out.println("Juegos devueltos.");
    }

    private void finalizarReserva() {
        if (reservaActiva == null || reservaActiva.isTerminada()) {
            System.out.println("No tienes una reserva activa.");
            return;
        }

        if (!reservaActiva.getPrestamosActivos().isEmpty()) {
            System.out.println("Error: aun tienes juegos sin devolver.");
            return;
        }

        reservaActiva.cerrarReserva();
        reservaActiva = null;
        System.out.println("Reserva finalizada.");
    }

    private void comprarJuego() {
        List<JuegoFisico> compra = sistema.getCatalogoCompra();
        if (compra.isEmpty()) {
            System.out.println("No hay juegos disponibles para compra.");
            return;
        }

        System.out.println("\n--- JUEGOS EN VENTA ---");
        for (int i = 0; i < compra.size(); i++) {
            System.out.println(i + ". " + compra.get(i).getJuegoBase().getNombre() + " | $" + compra.get(i).getJuegoBase().getPrecio());
        }
        System.out.print("Seleccione indice: ");
        int idx = scanner.nextInt();
        scanner.nextLine();

        if (idx < 0 || idx >= compra.size()) {
            System.out.println("Indice invalido.");
            return;
        }

        JuegoFisico juego = compra.get(idx);
        List<JuegoFisico> lista = new ArrayList<>();
        lista.add(juego);

        CompraJuegoMesa compraRealizada = sistema.generarCompraJuegos(loginCliente, passwordCliente, lista, 0);
        if (compraRealizada != null) {
            System.out.println("Compra realizada. Total: $" + compraRealizada.calcularTotal());
            System.out.println("Se agregaron puntos de fidelidad.");
        } else {
            System.out.println("Error en la compra.");
        }
    }

    private void comprarPlatillo() {
        if (reservaActiva == null || reservaActiva.isTerminada()) {
            System.out.println("No tienes una reserva activa. Crea una primero.");
            return;
        }

        var menu = sistema.getMenu();
        if (menu.isEmpty()) {
            System.out.println("No hay platillos en el menu.");
            return;
        }

        System.out.println("\n--- MENU ---");
        List<Platillo> listaMenu = new ArrayList<>(menu);
        for (int i = 0; i < listaMenu.size(); i++) {
            System.out.println(i + ". " + listaMenu.get(i).getClass().getSimpleName() + " | $" + listaMenu.get(i).getPrecio());
        }
        System.out.print("Seleccione indice: ");
        int idx = scanner.nextInt();
        scanner.nextLine();

        if (idx < 0 || idx >= listaMenu.size()) {
            System.out.println("Indice invalido.");
            return;
        }

        List<Platillo> pedido = new ArrayList<>();
        pedido.add(listaMenu.get(idx));

        System.out.print("Cantidad: ");
        int cantidad = scanner.nextInt();
        scanner.nextLine();

        for (int i = 1; i < cantidad; i++) {
            pedido.add(listaMenu.get(idx));
        }

        CompraPlatillo compra = sistema.generarCompraPlatillos(loginCliente, passwordCliente, reservaActiva, pedido, 0);
        if (compra != null) {
            System.out.println("Compra realizada. Total: $" + compra.calcularTotal());
        } else {
            System.out.println("Error en la compra (restriccion de edad o juego de accion).");
        }
    }

    private void usarPuntos() {
        System.out.println("Puntos disponibles: " + clienteAutenticado.getPuntosFidelidad());
        System.out.print("Puntos a usar: ");
        int puntos = scanner.nextInt();
        scanner.nextLine();
        clienteAutenticado.usarPuntos(puntos);
        System.out.println("Descuento aplicado. Puntos restantes: " + clienteAutenticado.getPuntosFidelidad());
    }

    private void consultarTorneos() {
        List<Torneo> torneos = sistema.getTorneos();
        if (torneos == null || torneos.isEmpty()) {
            System.out.println("No hay torneos activos.");
            return;
        }
        System.out.println("\n--- TORNEOS ACTIVOS ---");
        for (Torneo t : torneos) {
            int cuposRestantes = t.getCupos() - t.getCuposTaken();
            System.out.println("Juego: " + (t.getJuego() != null ? t.getJuego().getNombre() : "Sin juego")
                    + " | Competitivo: " + t.isEsCompetitivo()
                    + " | Bono: " + t.getBono()
                    + " | Cupos: " + cuposRestantes + "/" + t.getCupos());
        }
    }

    private void inscribirTorneo() {
        List<Torneo> torneos = sistema.getTorneos();
        if (torneos == null || torneos.isEmpty()) {
            System.out.println("No hay torneos disponibles.");
            return;
        }

        for (int i = 0; i < torneos.size(); i++) {
            System.out.println(i + ". " + (torneos.get(i).getJuego() != null ? torneos.get(i).getJuego().getNombre() : "Sin juego"));
        }
        System.out.print("Seleccione torneo: ");
        int idx = scanner.nextInt();
        scanner.nextLine();

        if (idx < 0 || idx >= torneos.size()) {
            System.out.println("Indice invalido.");
            return;
        }

        System.out.print("Numero de participantes (max 3): ");
        int participantes = scanner.nextInt();
        scanner.nextLine();

        Torneo torneo = torneos.get(idx);
        boolean ok = torneo.inscribir(clienteAutenticado, participantes);
        if (ok) {
            System.out.println("Inscripcion exitosa.");
        } else {
            System.out.println("Error: cupos insuficientes o ya estas inscrito.");
        }
    }

    private void desinscribirTorneo() {
        List<Torneo> torneos = sistema.getTorneos();
        if (torneos == null || torneos.isEmpty()) {
            System.out.println("No hay torneos.");
            return;
        }

        for (int i = 0; i < torneos.size(); i++) {
            System.out.println(i + ". " + (torneos.get(i).getJuego() != null ? torneos.get(i).getJuego().getNombre() : "Sin juego"));
        }
        System.out.print("Seleccione torneo: ");
        int idx = scanner.nextInt();
        scanner.nextLine();

        if (idx < 0 || idx >= torneos.size()) {
            System.out.println("Indice invalido.");
            return;
        }

        Torneo torneo = torneos.get(idx);
        boolean ok = torneo.desinscribir(clienteAutenticado);
        if (ok) {
            System.out.println("Desinscripcion exitosa.");
        } else {
            System.out.println("Error: no estabas inscrito.");
        }
    }

    private void agregarFavorito() {
        var juegos = sistema.getCatalogoJuegos();
        if (juegos.isEmpty()) {
            System.out.println("No hay juegos en el catalogo.");
            return;
        }

        List<JuegoDeMesa> lista = new ArrayList<>(juegos);
        for (int i = 0; i < lista.size(); i++) {
            System.out.println(i + ". " + lista.get(i).getNombre());
        }
        System.out.print("Seleccione juego: ");
        int idx = scanner.nextInt();
        scanner.nextLine();

        if (idx < 0 || idx >= lista.size()) {
            System.out.println("Indice invalido.");
            return;
        }

        clienteAutenticado.agregarJuegoFav(lista.get(idx));
        System.out.println("Juego agregado a favoritos.");
    }

    private void verEstadoCliente() {
        System.out.println("\n--- MIS DATOS ---");
        System.out.println("Login: " + clienteAutenticado.getLogin());
        System.out.println("Puntos de fidelidad: " + clienteAutenticado.getPuntosFidelidad());
        System.out.println("Juegos favoritos: " + clienteAutenticado.getJuegosFavoritos().stream()
                .map(JuegoDeMesa::getNombre)
                .collect(Collectors.joining(", ")));
        if (reservaActiva != null && !reservaActiva.isTerminada()) {
            System.out.println("Reserva activa: " + reservaActiva.getNumPersonas() + " personas, mesa " + reservaActiva.getMesaId());
            System.out.println("Prestamos activos: " + reservaActiva.getPrestamosActivos().size());
        } else {
            System.out.println("No tienes reserva activa.");
        }
    }
}