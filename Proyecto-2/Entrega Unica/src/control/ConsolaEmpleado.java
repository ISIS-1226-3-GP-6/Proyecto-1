package control;

import java.util.Scanner;

public class ConsolaEmpleado {

    private Cafe sistema;
    private Scanner scanner;
    private String loginEmpleado;
    private String passwordEmpleado;
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
        System.out.println("Bienvenido empleado " + loginEmpleado);
        rolEmpleado = sistema.getRolEmpleado(loginEmpleado, passwordEmpleado);
        menu();
    }

    private boolean loginEmpleado() {
        for (int i = 0; i < 3; i++) {
            System.out.print("Login: ");
            String login = scanner.nextLine();
            System.out.print("Password: ");
            String password = scanner.nextLine();
            if (sistema.esEmpleado(login, password)) {
                this.loginEmpleado = login;
                this.passwordEmpleado = password;
                return true;
            }
            System.out.println("Credenciales incorrectas. Intentos restantes: " + (2 - i));
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
            if ("mesero".equalsIgnoreCase(rolEmpleado)) {
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
                case 1: consultarTurno(); break;
                case 2: solicitarCambioTurno(); break;
                case 3: sugerirPlatillo(); break;
                case 4: comprarConDescuento(); break;
                case 5: generarCodigoDescuento(); break;
                case 6:
                    if ("mesero".equalsIgnoreCase(rolEmpleado)) verJuegosConocidos();
                    else System.out.println("Opcion invalida");
                    break;
                case 7:
                    if ("mesero".equalsIgnoreCase(rolEmpleado)) explicarJuego();
                    else System.out.println("Opcion invalida");
                    break;
                case 0: System.out.println("Saliendo..."); break;
                default: System.out.println("Opcion invalida");
            }
        }
    }

    private void consultarTurno() {
        String horario = sistema.getHorarioEmpleado(loginEmpleado, passwordEmpleado);
        System.out.println(horario);
    }

    private void solicitarCambioTurno() {
        System.out.print("Dia del turno actual: ");
        String diaActual = scanner.nextLine();
        System.out.print("Dia del turno deseado: ");
        String diaDeseado = scanner.nextLine();
        System.out.print("Login del empleado con quien intercambiar (dejar vacio si es cambio general): ");
        String otroEmpleado = scanner.nextLine();

        if (otroEmpleado.isEmpty()) {
            boolean ok = sistema.solicitarCambioTurnoGeneral(loginEmpleado, passwordEmpleado, diaActual, diaDeseado);
            if (ok) {
                System.out.println("Solicitud de cambio general enviada.");
            } else {
                System.out.println("Error al enviar solicitud.");
            }
        } else {
            boolean ok = sistema.solicitarCambioTurnoConEmpleado(loginEmpleado, passwordEmpleado, diaActual, diaDeseado, otroEmpleado);
            if (ok) {
                System.out.println("Solicitud de intercambio enviada.");
            } else {
                System.out.println("Error al enviar solicitud.");
            }
        }
    }

    private void sugerirPlatillo() {
        System.out.print("Nombre del platillo: ");
        String nombre = scanner.nextLine();
        System.out.print("Precio sugerido: ");
        double precio = scanner.nextDouble();
        scanner.nextLine();
        System.out.print("Tipo (bebida/comida): ");
        String tipo = scanner.nextLine();
        boolean ok = sistema.sugerirNuevoPlatillo(loginEmpleado, passwordEmpleado, nombre, precio, tipo);
        if (ok) {
            System.out.println("Sugerencia enviada al administrador.");
        } else {
            System.out.println("Error al enviar sugerencia.");
        }
    }

    private void comprarConDescuento() {
        System.out.println("1. Comprar juego");
        System.out.println("2. Comprar platillo");
        System.out.print("Opcion: ");
        int subop = scanner.nextInt();
        scanner.nextLine();

        if (subop == 1) {
            sistema.mostrarCatalogoCompra();
            System.out.print("Nombre del juego: ");
            String juego = scanner.nextLine();
            boolean ok = sistema.comprarJuegoConDescuentoEmpleado(loginEmpleado, passwordEmpleado, juego);
            if (ok) {
                System.out.println("Compra realizada con 20% de descuento.");
            } else {
                System.out.println("Error en la compra.");
            }
        } else if (subop == 2) {
            sistema.mostrarMenu();
            System.out.print("Nombre del platillo: ");
            String platillo = scanner.nextLine();
            System.out.print("Cantidad: ");
            int cantidad = scanner.nextInt();
            scanner.nextLine();
            boolean ok = sistema.comprarPlatilloConDescuentoEmpleado(loginEmpleado, passwordEmpleado, platillo, cantidad);
            if (ok) {
                System.out.println("Compra realizada con 20% de descuento.");
            } else {
                System.out.println("Error en la compra.");
            }
        } else {
            System.out.println("Opcion invalida.");
        }
    }

    private void generarCodigoDescuento() {
        String codigo = sistema.generarCodigoDescuento(loginEmpleado, passwordEmpleado);
        System.out.println("Codigo de descuento del 10%: " + codigo);
    }

    private void verJuegosConocidos() {
        var juegos = sistema.getJuegosConocidosPorEmpleado(loginEmpleado, passwordEmpleado);
        if (juegos.isEmpty()) {
            System.out.println("No conoces ningun juego dificil.");
        } else {
            System.out.println("Juegos dificiles que conoces:");
            for (String j : juegos) {
                System.out.println("- " + j);
            }
        }
    }

    private void explicarJuego() {
        System.out.print("Nombre del juego dificil que va a explicar: ");
        String juego = scanner.nextLine();
        boolean ok = sistema.explicarJuegoACliente(loginEmpleado, passwordEmpleado, juego);
        if (ok) {
            System.out.println("Has explicado el juego " + juego);
        } else {
            System.out.println("No conoces ese juego o hubo un error.");
        }
    }
}
