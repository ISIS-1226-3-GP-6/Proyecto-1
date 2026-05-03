
package control;

import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

import cafeteria.TicketNuevoPlatillo;
import horario.TicketCambiarTurno;
import juego.JuegoFisico;
import juego.Torneo;
import usuarios.Cocinero;
import usuarios.Empleado;
import usuarios.Mesero;

public class ConsolaAdministrador {

    private Cafe sistema;
    private Scanner scanner;
    private String loginAdmin;
    private String passwordAdmin;

    public ConsolaAdministrador(Cafe sistema) {
        this.sistema = sistema;
        this.scanner = new Scanner(System.in);
    }

    public static void main(String[] args) {
        Cafe cafe = new Cafe("escenario1.txt");
        ConsolaAdministrador consola = new ConsolaAdministrador(cafe);
        consola.ejecutar();
        cafe.save();
        consola.scanner.close();
    }

    public void ejecutar() {
        System.out.println("===== ADMINISTRADOR =====");
        boolean autenticado = loginAdmin();
        if (!autenticado) {
            System.out.println("Error de autenticacion.");
            return;
        }
        System.out.println("Bienvenido administrador " + loginAdmin);
        menu();
    }

    private boolean loginAdmin() {
        for (int i = 0; i < 3; i++) {
            System.out.print("Login: ");
            String login = scanner.nextLine();
            System.out.print("Password: ");
            String password = scanner.nextLine();
            if (sistema.esAdmin(login, password)) {
                this.loginAdmin = login;
                this.passwordAdmin = password;
                return true;
            }
            System.out.println("Credenciales incorrectas. Intentos restantes: " + (2 - i));
        }
        return false;
    }

    private void menu() {
        int opcion = -1;
        while (opcion != 0) {
            System.out.println("\n--- MENU ADMINISTRADOR ---");
            System.out.println("1. Ver catalogo de juegos");
            System.out.println("2. Resolver sugerencias de platillos");
            System.out.println("3. Resolver cambios de turno");
            System.out.println("4. Agregar juego al catalogo");
            System.out.println("5. Marcar juego como desaparecido");
            System.out.println("6. Registrar nuevo empleado");
            System.out.println("7. Mover juego entre catalogos");
            System.out.println("8. Reparar juego");
            System.out.println("9. Crear torneo");
            System.out.println("10. Asignar ganador de torneo");
            System.out.println("11. Eliminar torneo");
            System.out.println("12. Consultar inscripciones a torneo");
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
                case 1: verCatalogo(); break;
                case 2: resolverSugerencias(); break;
                case 3: resolverCambiosTurno(); break;
                case 4: agregarJuego(); break;
                case 5: marcarDesaparecido(); break;
                case 6: registrarEmpleado(); break;
                case 7: moverJuegoEntreCatalogos(); break;
                case 8: repararJuego(); break;
                case 9: crearTorneo(); break;
                case 10: asignarGanadorTorneo(); break;
                case 11: eliminarTorneo(); break;
                case 12: consultarInscripcionesTorneo(); break;
                case 0: System.out.println("Saliendo..."); break;
                default: System.out.println("Opcion invalida");
            }
        }
    }

    private void verCatalogo() {
        var juegos = sistema.getCatalogoJuegos();
        if (juegos.isEmpty()) {
            System.out.println("No hay juegos en el catalogo.");
            return;
        }
        System.out.println("\n--- CATALOGO DE JUEGOS ---");
        for (var j : juegos) {
            System.out.println(j.getNombre() + " | " + j.getTipoJuego() + " | $" + j.getPrecio());
        }
    }

    private void resolverSugerencias() {
        List<TicketNuevoPlatillo> tickets = sistema.getTicketsPlatillosPendientes();
        if (tickets.isEmpty()) {
            System.out.println("No hay sugerencias pendientes.");
            return;
        }
        for (int i = 0; i < tickets.size(); i++) {
            System.out.println(i + ". " + tickets.get(i).getPlatillo());
        }
        System.out.print("Seleccione indice: ");
        int index = scanner.nextInt();
        scanner.nextLine();
        if (index < 0 || index >= tickets.size()) {
            System.out.println("Indice invalido.");
            return;
        }
        System.out.print("1. Aprobar | 2. Rechazar: ");
        int opcion = scanner.nextInt();
        scanner.nextLine();
        if (opcion == 1) {
            sistema.aprobarTicketPlatillo(loginAdmin, passwordAdmin, tickets.get(index));
            System.out.println("Platillo aprobado.");
        } else if (opcion == 2) {
            sistema.rechazarTicketPlatillo(loginAdmin, passwordAdmin, tickets.get(index));
            System.out.println("Platillo rechazado.");
        } else {
            System.out.println("Opcion invalida.");
        }
    }

    private void resolverCambiosTurno() {
        List<TicketCambiarTurno> tickets = sistema.getTicketsTurnoPendientes();
        if (tickets.isEmpty()) {
            System.out.println("No hay solicitudes pendientes.");
            return;
        }
        for (int i = 0; i < tickets.size(); i++) {
            System.out.println(i + ". Solicitud de " + tickets.get(i).getEmpleadoPrincipal().getLogin());
        }
        System.out.print("Seleccione indice: ");
        int index = scanner.nextInt();
        scanner.nextLine();
        if (index < 0 || index >= tickets.size()) {
            System.out.println("Indice invalido.");
            return;
        }
        System.out.print("1. Aprobar | 2. Rechazar: ");
        int opcion = scanner.nextInt();
        scanner.nextLine();
        if (opcion == 1) {
            sistema.aprobarTicketTurno(loginAdmin, passwordAdmin, tickets.get(index));
            System.out.println("Cambio aprobado.");
        } else if (opcion == 2) {
            sistema.rechazarTicketTurno(loginAdmin, passwordAdmin, tickets.get(index));
            System.out.println("Cambio rechazado.");
        } else {
            System.out.println("Opcion invalida.");
        }
    }

    private void agregarJuego() {
        System.out.print("Nombre: ");
        String nombre = scanner.nextLine();
        System.out.print("Anio: ");
        int anio = scanner.nextInt();
        scanner.nextLine();
        System.out.print("Empresa: ");
        String empresa = scanner.nextLine();
        System.out.print("Tipo (Carta/Tablero/Accion): ");
        String tipo = scanner.nextLine();
        System.out.print("Es dificil? (true/false): ");
        boolean dificil = scanner.nextBoolean();
        System.out.print("Permite ninos? (true/false): ");
        boolean ninos = scanner.nextBoolean();
        System.out.print("Permite jovenes? (true/false): ");
        boolean jovenes = scanner.nextBoolean();
        System.out.print("Min jugadores: ");
        int min = scanner.nextInt();
        System.out.print("Max jugadores: ");
        int max = scanner.nextInt();
        System.out.print("Precio: ");
        double precio = scanner.nextDouble();
        scanner.nextLine();

        boolean ok = sistema.agregarJuegoCatalogo(loginAdmin, passwordAdmin, nombre, anio, empresa, tipo,
                dificil, ninos, jovenes, min, max, precio);
        if (ok) {
            System.out.println("Juego agregado.");
        } else {
            System.out.println("Error al agregar juego.");
        }
    }


    private void marcarDesaparecido() {
        List<JuegoFisico> prestamo = sistema.getCatalogoPrestamo();
        if (prestamo.isEmpty()) {
            System.out.println("No hay juegos en prestamo.");
            return;
        }
        for (int i = 0; i < prestamo.size(); i++) {
            System.out.println(i + ". " + prestamo.get(i).getJuegoBase().getNombre());
        }
        System.out.print("Seleccione indice: ");
        int idx = scanner.nextInt();
        scanner.nextLine();
        if (idx < 0 || idx >= prestamo.size()) {
            System.out.println("Indice invalido.");
            return;
        }
        JuegoFisico juego = prestamo.get(idx);
        juego.setEstado("desaparecido");
        sistema.getCatalogoPrestamo().remove(juego);
        System.out.println("Juego marcado como desaparecido.");
    }
  
    
    private void registrarEmpleado() {
        System.out.print("Login: ");
        String login = scanner.nextLine();
        System.out.print("Password: ");
        String pass = scanner.nextLine();
        System.out.print("Rol (mesero/cocinero): ");
        String rol = scanner.nextLine();

        boolean existe = sistema.getUsuarios().stream()
                .anyMatch(u -> u.getLogin().equals(login));
        
        if (existe) {
            System.out.println("Error: el login ya existe.");
            return;
        }

        Empleado nuevo = null;
        if (rol.equalsIgnoreCase("mesero")) {
            nuevo = new Mesero(login, pass);
        } else if (rol.equalsIgnoreCase("cocinero")) {
            nuevo = new Cocinero(login, pass);
        } else {
            System.out.println("Rol invalido. Use 'mesero' o 'cocinero'.");
            return;
        }

        // Agregar a la lista de usuarios del cafe
        sistema.getUsuarios().add(nuevo);
        System.out.println("Empleado registrado exitosamente.");
    }

    /*
    private void moverJuegoEntreCatalogos() {
        List<JuegoFisico> compra = sistema.getCatalogoCompra();
        if (compra.isEmpty()) {
            System.out.println("No hay juegos en catalogo de compra.");
            return;
        }
        for (int i = 0; i < compra.size(); i++) {
            System.out.println(i + ". " + compra.get(i).getJuegoBase().getNombre());
        }
        System.out.print("Seleccione indice: ");
        int idx = scanner.nextInt();
        scanner.nextLine();
        if (idx < 0 || idx >= compra.size()) {
            System.out.println("Indice invalido.");
            return;
        }
        JuegoFisico juego = compra.get(idx);
        sistema.getCatalogoCompra().remove(juego);
        sistema.getCatalogoPrestamo().add(juego);
        System.out.println("Juego movido a catalogo de prestamo.");
    }
    */
    
    private void moverJuegoEntreCatalogos() {
        System.out.println("\n--- MOVER JUEGO ENTRE CATALOGOS ---");
        System.out.println("1. Mover de COMPRA a PRESTAMO");
        System.out.println("2. Mover de PRESTAMO a COMPRA");
        System.out.print("Seleccione una opcion: ");
        
        int direccion = scanner.nextInt();
        scanner.nextLine();
        
        if (direccion == 1) {
            List<JuegoFisico> origen = sistema.getCatalogoCompra();
            if (origen.isEmpty()) {
                System.out.println("No hay juegos en el catalogo de COMPRA.");
                return;
            }
            
            System.out.println("\n--- CATALOGO DE COMPRA ---");
            for (int i = 0; i < origen.size(); i++) {
                System.out.println(i + ". " + origen.get(i).getJuegoBase().getNombre() 
                        + " | Estado: " + origen.get(i).getEstado());
            }
            
            System.out.print("\nSeleccione el indice del juego a mover: ");
            int idx = scanner.nextInt();
            scanner.nextLine();
            
            if (idx < 0 || idx >= origen.size()) {
                System.out.println("Indice invalido.");
                return;
            }
            
            JuegoFisico juego = origen.get(idx);
            sistema.getCatalogoCompra().remove(juego);
            sistema.getCatalogoPrestamo().add(juego);
            System.out.println("Juego '" + juego.getJuegoBase().getNombre() + "' movido a catalogo de PRESTAMO.");
            
        } else if (direccion == 2) {
            List<JuegoFisico> origen = sistema.getCatalogoPrestamo();
            if (origen.isEmpty()) {
                System.out.println("No hay juegos en el catalogo de PRESTAMO.");
                return;
            }
            
            System.out.println("\n--- CATALOGO DE PRESTAMO ---");
            for (int i = 0; i < origen.size(); i++) {
                System.out.println(i + ". " + origen.get(i).getJuegoBase().getNombre() 
                        + " | Estado: " + origen.get(i).getEstado()
                        + " | Ocupado: " + (origen.get(i).isOcupado() ? "SI" : "NO"));
            }
            
            System.out.print("\nSeleccione el indice del juego a mover: ");
            int idx = scanner.nextInt();
            scanner.nextLine();
            
            if (idx < 0 || idx >= origen.size()) {
                System.out.println("Indice invalido.");
                return;
            }
            
            JuegoFisico juego = origen.get(idx);
            
            if (juego.isOcupado()) {
                System.out.println("No se puede mover el juego porque actualmente esta prestado.");
                return;
            }
            
            sistema.getCatalogoPrestamo().remove(juego);
            sistema.getCatalogoCompra().add(juego);
            System.out.println("Juego '" + juego.getJuegoBase().getNombre() + "' movido a catalogo de COMPRA.");
            
        } else {
            System.out.println("Opcion invalida.");
        }
    }

    private void repararJuego() {
        List<JuegoFisico> prestamo = sistema.getCatalogoPrestamo();
        List<JuegoFisico> danados = prestamo.stream()
                .filter(j -> j.getEstado().equals("falta una pieza") || j.getEstado().equals("daniado"))
                .collect(Collectors.toList());

        if (danados.isEmpty()) {
            System.out.println("No hay juegos danados en prestamo.");
            return;
        }
        for (int i = 0; i < danados.size(); i++) {
            System.out.println(i + ". " + danados.get(i).getJuegoBase().getNombre());
        }
        System.out.print("Seleccione juego danado: ");
        int idx = scanner.nextInt();
        scanner.nextLine();
        if (idx < 0 || idx >= danados.size()) {
            System.out.println("Indice invalido.");
            return;
        }
        JuegoFisico danado = danados.get(idx);
        List<JuegoFisico> nuevos = sistema.getCatalogoCompra().stream()
                .filter(j -> j.getJuegoBase().equals(danado.getJuegoBase()) && j.getEstado().equals("nuevo"))
                .collect(Collectors.toList());

        if (nuevos.isEmpty()) {
            System.out.println("No hay copias nuevas de este juego en compra para reparar.");
            return;
        }
        JuegoFisico nuevo = nuevos.get(0);
        sistema.getCatalogoPrestamo().remove(danado);
        sistema.getCatalogoPrestamo().add(nuevo);
        sistema.getCatalogoCompra().remove(nuevo);
        System.out.println("Juego reparado.");
    }
    
    

    private void crearTorneo() {
        System.out.print("Es competitivo? (true/false): ");
        boolean esCompetitivo = scanner.nextBoolean();
        scanner.nextLine();
        System.out.print("Bono en puntos: ");
        int bono = scanner.nextInt();
        scanner.nextLine();
        System.out.print("Costo de entrada: ");
        double costoEntrada = scanner.nextDouble();
        scanner.nextLine();
        System.out.print("Cupos totales: ");
        int cupos = scanner.nextInt();
        scanner.nextLine();
        System.out.print("Nombre del juego asociado: ");
        String juegoNombre = scanner.nextLine();

        boolean ok = sistema.crearTorneo(loginAdmin, passwordAdmin, esCompetitivo, bono, costoEntrada, cupos, juegoNombre);
        if (ok) {
            System.out.println("Torneo creado.");
        } else {
            System.out.println("Error al crear torneo.");
        }
    }

    private void asignarGanadorTorneo() {
        List<Torneo> torneos = sistema.getTorneosActivos();
        if (torneos.isEmpty()) {
            System.out.println("No hay torneos activos.");
            return;
        }
        for (int i = 0; i < torneos.size(); i++) {
            System.out.println(i + ". Torneo de " + torneos.get(i).getJuego().getNombre());
        }
        System.out.print("Seleccione torneo: ");
        int index = scanner.nextInt();
        scanner.nextLine();
        System.out.print("Login del ganador: ");
        String ganadorLogin = scanner.nextLine();
        sistema.asignarGanadorTorneo(loginAdmin, passwordAdmin, torneos.get(index), ganadorLogin);
        System.out.println("Ganador asignado.");
    }

    private void eliminarTorneo() {
        List<Torneo> torneos = sistema.getTorneosActivos();
        if (torneos.isEmpty()) {
            System.out.println("No hay torneos activos.");
            return;
        }
        for (int i = 0; i < torneos.size(); i++) {
            System.out.println(i + ". Torneo de " + torneos.get(i).getJuego().getNombre());
        }
        System.out.print("Seleccione torneo a eliminar: ");
        int index = scanner.nextInt();
        scanner.nextLine();
        sistema.eliminarTorneo(loginAdmin, passwordAdmin, torneos.get(index));
        System.out.println("Torneo eliminado.");
    }

    private void consultarInscripcionesTorneo() {
        List<Torneo> torneos = sistema.getTorneosActivos();
        if (torneos.isEmpty()) {
            System.out.println("No hay torneos activos.");
            return;
        }
        for (int i = 0; i < torneos.size(); i++) {
            System.out.println(i + ". Torneo de " + torneos.get(i).getJuego().getNombre());
        }
        System.out.print("Seleccione torneo: ");
        int index = scanner.nextInt();
        scanner.nextLine();
        sistema.mostrarInscripcionesTorneo(torneos.get(index));
    }
}