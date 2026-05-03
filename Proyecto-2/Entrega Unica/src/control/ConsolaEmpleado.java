package control;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

import cafeteria.Platillo;
import cafeteria.TicketNuevoPlatillo;
import compras.CompraJuegoMesa;
import compras.CompraPlatillo;
import horario.TicketCambiarTurno;
import horario.Turno;
import juego.JuegoDeMesa;
import juego.JuegoFisico;
import juego.Torneo;
import reservacion.Reserva;
import usuarios.Cocinero;
import usuarios.Empleado;
import usuarios.Mesero;
import usuarios.Usuario;

public class ConsolaEmpleado {

    private Cafe sistema;
    private Scanner scanner;
    private String loginEmpleado;
    private String passwordEmpleado;
    private Empleado empleadoAutenticado;
    private String rolEmpleado;

    public ConsolaEmpleado(Cafe sistema) {
        this.sistema = sistema;
        this.scanner = new Scanner(System.in);
    }

    public static void main(String[] args) {
        Cafe cafe = new Cafe("escenario1.txt");
        ConsolaEmpleado consola = new ConsolaEmpleado(cafe);
        consola.ejecutar();
        cafe.save();
        consola.scanner.close();
    }

    public void ejecutar() {
        System.out.println("===== EMPLEADO =====");
        boolean autenticado = loginEmpleado();
        if (!autenticado) {
            System.out.println("Error de autenticacion.");
            return;
        }

        if (empleadoAutenticado instanceof Mesero) {
            rolEmpleado = "mesero";
        } else if (empleadoAutenticado instanceof Cocinero) {
            rolEmpleado = "cocinero";
        }

        System.out.println("Bienvenido empleado " + loginEmpleado + " (" + rolEmpleado + ")");
        menu();
    }

    private boolean loginEmpleado() {
        for (int i = 0; i < 3; i++) {
            System.out.print("Login: ");
            String login = scanner.nextLine();
            System.out.print("Password: ");
            String password = scanner.nextLine();

            Usuario user = sistema.getUsuarios().stream()
                    .filter(u -> u.getLogin().equals(login) && u.autenticacion(password))
                    .findFirst()
                    .orElse(null);

            if (user instanceof Empleado) {
                this.loginEmpleado = login;
                this.passwordEmpleado = password;
                this.empleadoAutenticado = (Empleado) user;
                return true;
            }
            System.out.println("Credenciales incorrectas.");
        }
        return false;
    }

    private void menu() {
        int opcion = -1;
        while (opcion != 0) {
            System.out.println("\n--- MENU EMPLEADO ---");
            System.out.println("1. Consultar turno semanal");
            System.out.println("2. Solicitar cambio de turno");
            System.out.println("3. Sugerir nuevo platillo");
            System.out.println("4. Comprar con descuento (20%)");
            System.out.println("5. Generar codigo de descuento para cliente (10%)");
            if (rolEmpleado.equals("mesero")) {
                System.out.println("6. Ver juegos dificiles que conozco");
                System.out.println("7. Explicar un juego a cliente");
            }
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
                case 1 -> consultarTurno();
                case 2 -> solicitarCambioTurno();
                case 3 -> sugerirPlatillo();
                case 4 -> comprarConDescuento();
                case 5 -> generarCodigoDescuento();
                case 6 -> {
                    if (rolEmpleado.equals("mesero")) verJuegosConocidos();
                    else System.out.println("Opcion invalida");
                }
                case 7 -> {
                    if (rolEmpleado.equals("mesero")) explicarJuego();
                    else System.out.println("Opcion invalida");
                }
                case 0 -> System.out.println("Saliendo...");
                default -> System.out.println("Opcion invalida");
            }
        }
    }

    // ================= CONSULTAR TURNO =================
    private void consultarTurno() {
        var turnosPorDia = sistema.getHorario().getTurnosPorDia();
        if (turnosPorDia.isEmpty()) {
            System.out.println("No hay turnos configurados.");
            return;
        }

        System.out.println("\n--- MIS TURNOS ---");
        boolean encontrado = false;
        for (String dia : turnosPorDia.keySet()) {
            Turno turno = turnosPorDia.get(dia);
            if (turno.getEmpleados().contains(empleadoAutenticado)) {
                System.out.println(dia + ": " + turno.getDiaSemana());
                encontrado = true;
            }
        }
        if (!encontrado) {
            System.out.println("No tienes turnos asignados.");
        }
    }

    // ================= SOLICITAR CAMBIO DE TURNO =================
    private void solicitarCambioTurno() {
        var turnosPorDia = sistema.getHorario().getTurnosPorDia();
        if (turnosPorDia.isEmpty()) {
            System.out.println("No hay turnos configurados.");
            return;
        }

        // Mostrar turnos disponibles
        System.out.println("\n--- TURNOS EXISTENTES ---");
        List<String> dias = new ArrayList<>(turnosPorDia.keySet());
        for (int i = 0; i < dias.size(); i++) {
            String dia = dias.get(i);
            Turno t = turnosPorDia.get(dia);
            System.out.println(i + ". " + dia + " - Empleados: " + t.getEmpleados().size());
        }

        System.out.print("Seleccione el dia de su turno actual: ");
        int idxActual = scanner.nextInt();
        scanner.nextLine();
        if (idxActual < 0 || idxActual >= dias.size()) {
            System.out.println("Indice invalido.");
            return;
        }
        String diaActual = dias.get(idxActual);
        Turno turnoActual = turnosPorDia.get(diaActual);

        if (!turnoActual.getEmpleados().contains(empleadoAutenticado)) {
            System.out.println("No estas asignado a ese turno.");
            return;
        }

        System.out.print("Seleccione el dia del turno deseado: ");
        int idxDeseado = scanner.nextInt();
        scanner.nextLine();
        if (idxDeseado < 0 || idxDeseado >= dias.size()) {
            System.out.println("Indice invalido.");
            return;
        }
        String diaDeseado = dias.get(idxDeseado);
        Turno turnoDeseado = turnosPorDia.get(diaDeseado);

        System.out.print("Es intercambio con otro empleado? (true/false): ");
        boolean esIntercambio = scanner.nextBoolean();
        scanner.nextLine();

        String loginSecundario = null;
        if (esIntercambio) {
            System.out.print("Login del empleado con quien intercambiar: ");
            loginSecundario = scanner.nextLine();
        }

        TicketCambiarTurno ticket = sistema.solicitarCambioTurno(
                loginEmpleado, passwordEmpleado,
                turnoActual, turnoDeseado, loginSecundario
        );

        if (ticket != null) {
            System.out.println("Solicitud de cambio de turno enviada. Estado: " + ticket.getEstado());
        } else {
            System.out.println("Error al crear la solicitud.");
        }
    }

    // ================= SUGERIR NUEVO PLATILLO =================
    private void sugerirPlatillo() {
        System.out.print("Tipo (bebida/comida): ");
        String tipo = scanner.nextLine();
        System.out.print("Precio: ");
        double precio = scanner.nextDouble();
        scanner.nextLine();

        TicketNuevoPlatillo ticket = null;
        if (tipo.equalsIgnoreCase("comida")) {
            System.out.print("Alergenos (separados por coma): ");
            String alergenosInput = scanner.nextLine();
            List<String> alergenos = alergenosInput.isEmpty() ? new ArrayList<>()
                    : List.of(alergenosInput.split(","));
            ticket = sistema.solicitarCrearComida(loginEmpleado, passwordEmpleado, precio, alergenos);
        } else if (tipo.equalsIgnoreCase("bebida")) {
            System.out.print("Es alcoholica? (true/false): ");
            boolean alcoholica = scanner.nextBoolean();
            System.out.print("Es caliente? (true/false): ");
            boolean caliente = scanner.nextBoolean();
            scanner.nextLine();
            ticket = sistema.solicitarCrearBebida(loginEmpleado, passwordEmpleado, precio, alcoholica, caliente);
        } else {
            System.out.println("Tipo invalido.");
            return;
        }

        if (ticket != null) {
            System.out.println("Sugerencia enviada al administrador.");
        } else {
            System.out.println("Error al enviar sugerencia.");
        }
    }

    // ================= COMPRAR CON DESCUENTO (20%) =================
    private void comprarConDescuento() {
        System.out.println("1. Comprar juego");
        System.out.println("2. Comprar platillo");
        System.out.print("Opcion: ");
        int subop = scanner.nextInt();
        scanner.nextLine();

        if (subop == 1) {
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

            CompraJuegoMesa compraRealizada = sistema.generarCompraJuegos(loginEmpleado, passwordEmpleado, lista, 20);
            if (compraRealizada != null) {
                System.out.println("Compra realizada con 20% de descuento. Total: $" + compraRealizada.calcularTotal());
            } else {
                System.out.println("Error en la compra.");
            }
        } else if (subop == 2) {
            System.out.println("Para comprar platillos necesitas una reserva activa.");
            System.out.print("Tienes reserva activa? (true/false): ");
            boolean tieneReserva = scanner.nextBoolean();
            scanner.nextLine();
            if (!tieneReserva) {
                System.out.println("No puedes comprar platillos sin reserva.");
                return;
            }
            System.out.print("Login del cliente de la reserva: ");
            String loginCliente = scanner.nextLine();

            Reserva reserva = sistema.getReservas().stream()
                    .filter(r -> r.getCliente().getLogin().equals(loginCliente) && !r.isTerminada())
                    .findFirst()
                    .orElse(null);

            if (reserva == null) {
                System.out.println("No se encontro reserva activa para ese cliente.");
                return;
            }

            var menu = sistema.getMenu();
            if (menu.isEmpty()) {
                System.out.println("No hay platillos en el menu.");
                return;
            }

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

            CompraPlatillo compra = sistema.generarCompraPlatillos(loginCliente, passwordEmpleado, reserva, pedido, 20);
            if (compra != null) {
                System.out.println("Compra realizada con 20% de descuento. Total: $" + compra.calcularTotal());
            } else {
                System.out.println("Error en la compra.");
            }
        } else {
            System.out.println("Opcion invalida.");
        }
    }

    // ================= GENERAR CODIGO DE DESCUENTO (10%) =================
    private void generarCodigoDescuento() {
        // Generar un codigo simple basado en el login y timestamp
        String codigo = "DESC10_" + loginEmpleado + "_" + System.currentTimeMillis();
        System.out.println("Codigo de descuento del 10% para cliente: " + codigo);
        System.out.println("El cliente debe ingresar este codigo al comprar.");
    }

    // ================= VER JUEGOS DIFICILES QUE CONOCE (solo mesero) =================
    private void verJuegosConocidos() {
        if (!(empleadoAutenticado instanceof Mesero)) {
            System.out.println("Solo los meseros pueden ver juegos que conocen.");
            return;
        }
        Mesero mesero = (Mesero) empleadoAutenticado;
        List<String> conocidos = mesero.getDificilesConocidos();
        if (conocidos.isEmpty()) {
            System.out.println("No conoces ningun juego dificil.");
        } else {
            System.out.println("\n--- JUEGOS DIFICILES QUE CONOCES ---");
            for (String juego : conocidos) {
                System.out.println("- " + juego);
            }
        }
    }

    // ================= EXPLICAR JUEGO A CLIENTE (solo mesero) =================
    private void explicarJuego() {
        if (!(empleadoAutenticado instanceof Mesero)) {
            System.out.println("Solo los meseros pueden explicar juegos.");
            return;
        }

        System.out.print("Nombre del juego dificil a explicar: ");
        String juegoNombre = scanner.nextLine();

        JuegoDeMesa juego = sistema.getCatalogoJuegos().stream()
                .filter(j -> j.getNombre().equalsIgnoreCase(juegoNombre))
                .findFirst()
                .orElse(null);

        if (juego == null) {
            System.out.println("Juego no encontrado en el catalogo.");
            return;
        }

        Mesero mesero = (Mesero) empleadoAutenticado;
        if (mesero.puedeExplicar(juego)) {
            System.out.println("Puedes explicar este juego. Acercate a la mesa del cliente.");
        } else {
            System.out.println("No conoces este juego o no es dificil. No puedes explicarlo.");
        }
    }
}