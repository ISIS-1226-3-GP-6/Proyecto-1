package control;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Date;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.Scanner;


import cafeteria.Bebida;
import cafeteria.Comida;
import cafeteria.Platillo;
import cafeteria.TicketNuevoPlatillo;
import compras.CompraJuegoMesa;
import compras.CompraPlatillo;
import compras.Compra;
import horario.HorarioSemanal;
import horario.TicketCambiarTurno;
import horario.Turno;
import juego.JuegoDeMesa;
import juego.JuegoFisico;
import juego.Prestamo;
import juego.Torneo;
import reservacion.Mesa;
import reservacion.Reserva;
import usuarios.Cliente;
import usuarios.Cocinero;
import usuarios.Empleado;
import usuarios.Mesero;
import usuarios.Usuario;

public class Cafe implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private Set<JuegoDeMesa> catalogoJuegos;
	private List<JuegoFisico> inventarioVenta;
	private List<JuegoFisico> inventarioPrestamo;
	
	private List<Mesa> mesas;
	private HorarioSemanal horario;
	private List<TicketCambiarTurno> ticketsTurnoPendientes; // pending in doc, needed for implementation
	
	private int capacidadClientes;
	private Set<Platillo> menu;
	private List<TicketNuevoPlatillo> ticketsPlatillosPendientes;
	private List<Prestamo> historialPrestamos;
	private List<Reserva> reservas; // pending in doc
	
	private List<Usuario> usuarios; // Pending in doc

	private List<Compra> compras; // Pending in doc, needed for implementation
	
	private List<Torneo> torneos;
	
	private Administrador admin; // Pending in doc
	private String ioPath; // Path for persistence, not in doc but needed for implementation
	private Usuario usuarioActivo;
	
	
	
	public Cafe(String path, int capacidad, String loginAdmin, String passwordAdmin) {
		this.ioPath = path;
		this.catalogoJuegos = new HashSet<>();
		this.inventarioVenta = new ArrayList<>();
		this.inventarioPrestamo = new ArrayList<>();
		this.mesas = new ArrayList<>();
		this.horario = new HorarioSemanal();
		this.capacidadClientes = capacidad;
		this.menu = new HashSet<>();
		this.ticketsPlatillosPendientes = new ArrayList<>();
		this.ticketsTurnoPendientes = new ArrayList<>();
		this.historialPrestamos = new ArrayList<>();
		this.usuarios = new ArrayList<>();
		this.reservas = new ArrayList<>();
		this.torneos = new ArrayList<>();
		this.compras = new ArrayList<>();
		
		this.admin = new Administrador(loginAdmin, passwordAdmin);
		this.usuarioActivo = null;
	}
	
	public Cafe(String ioPath) {
		this(ioPath, 0, "idc", "idc");
		
		try (ObjectInputStream input = new ObjectInputStream(new FileInputStream(ioPath))) {
			Cafe persistedCafe = (Cafe) input.readObject();
			this.catalogoJuegos = persistedCafe.catalogoJuegos;
			this.inventarioVenta = persistedCafe.inventarioVenta;
			this.inventarioPrestamo = persistedCafe.inventarioPrestamo;
			this.mesas = persistedCafe.mesas;
			this.horario = persistedCafe.horario;
			this.capacidadClientes = persistedCafe.capacidadClientes;
			this.menu = persistedCafe.menu;
			this.ticketsPlatillosPendientes = persistedCafe.ticketsPlatillosPendientes == null
					? new ArrayList<>()
					: persistedCafe.ticketsPlatillosPendientes;
			this.ticketsTurnoPendientes = persistedCafe.ticketsTurnoPendientes == null
					? new ArrayList<>()
					: persistedCafe.ticketsTurnoPendientes;
			this.historialPrestamos = persistedCafe.historialPrestamos == null
					? new ArrayList<>()
					: persistedCafe.historialPrestamos;
			this.usuarios = persistedCafe.usuarios;
			this.reservas = persistedCafe.reservas;
			this.admin = persistedCafe.admin;
			this.ioPath = persistedCafe.ioPath;
			this.torneos = persistedCafe.torneos == null ? new ArrayList<>() : persistedCafe.torneos;
			this.compras = persistedCafe.compras == null ? new ArrayList<>() : persistedCafe.compras;
			this.usuarioActivo = null;
		} catch (IOException | ClassNotFoundException e) {
			throw new RuntimeException("Could not load cafe persistence", e);
		}
	}
	
	
	public boolean save() {
		
		boolean successful = false;
		
		try (ObjectOutputStream output = new ObjectOutputStream(new FileOutputStream(ioPath))) {
			output.writeObject(this);
			successful = true;
		} catch (IOException e) {
			successful = false;
		}
		
		return successful;
	}
	
	public boolean crearCliente(String login, String password) {

		Cliente usuario = new Cliente(login, password, 0);

		if (!(usuarios.stream().anyMatch(u -> u.getLogin().equals(usuario.getLogin())))) {
			return usuarios.add(usuario);
		}

		return false;
	}

	public boolean crearMesero(String loginAdmin, String passwordAdmin, String login, String password) {
		if (!esAdmin(loginAdmin, passwordAdmin) || login == null || password == null
				|| usuarios.stream().anyMatch(u -> u.getLogin().equals(login))) {
			return false;
		}

		return usuarios.add(new Mesero(login, password));
	}

	public boolean crearCocinero(String loginAdmin, String passwordAdmin, String login, String password) {
		if (!esAdmin(loginAdmin, passwordAdmin) || login == null || password == null
				|| usuarios.stream().anyMatch(u -> u.getLogin().equals(login))) {
			return false;
		}

		return usuarios.add(new Cocinero(login, password));
	}

	public boolean iniciarSesion(String login, String password) {
		Usuario usuario = autenticarUsuario(login, password);
		if (usuario == null) {
			return false;
		}

		usuarioActivo = usuario;
		return true;
	}

	public void cerrarSesion() {
		usuarioActivo = null;
	}

	public Usuario getUsuarioActivo() {
		return usuarioActivo;
	}

	public boolean crearReservacion(String login, String password, int numPersonas, boolean hayMenores, boolean hayNinos) {

		Usuario usuarioAutenticado = usuarios.stream()
				.filter(usuario -> usuario instanceof Cliente && usuario.getLogin().equals(login) && usuario.autenticacion(password))
				.findFirst()
				.orElse(null);

		if (usuarioAutenticado == null) {
			return false;
		}

		Mesa mesa = mesas.stream()
				.filter(m -> m.sePuedeSentar(numPersonas))
				.findFirst()
				.orElse(null);

		if (mesa == null) {
			return false;
		}

		mesa.ocupar();

		reservas.add(new Reserva(numPersonas, hayMenores, hayNinos, mesa.getId(), (Cliente) usuarioAutenticado));

		return true;
	}

	public boolean crearMesa(int capacidad) {
		if (capacidad <= 0) {
			return false;
		}

		int capacidadTotalMesas = 0;
		int id = mesas.size();
		for (Mesa mesa : mesas) {
			capacidadTotalMesas += mesa.getCapacidad();
		}

		if (capacidadTotalMesas + capacidad > capacidadClientes) {
			return false;
		}

		return mesas.add(new Mesa(capacidad, id));
	}

	public boolean esAdmin(String login, String password) {
		return admin != null
				&& admin.getLogin().equals(login)
				&& admin.autenticacion(password);
	}

	public Empleado autenticarEmpleado(String login, String password) {
		return usuarios.stream()
				.filter(usuario -> usuario instanceof Empleado
						&& usuario.getLogin().equals(login)
						&& usuario.autenticacion(password))
				.map(usuario -> (Empleado) usuario)
				.findFirst()
				.orElse(null);
	}

	public boolean crearComida(String loginAdmin, String passwordAdmin, double precio, List<String> alergenos) {
		if (!esAdmin(loginAdmin, passwordAdmin) || precio < 0) {
			return false;
		}

		List<String> alergenosPlatillo = alergenos == null ? new ArrayList<>() : new ArrayList<>(alergenos);
		return menu.add(new Comida(precio, true, alergenosPlatillo));
	}

	public boolean crearBebida(String loginAdmin, String passwordAdmin, double precio, boolean esAlcoholica, boolean esCaliente) {
		if (!esAdmin(loginAdmin, passwordAdmin) || precio < 0) {
			return false;
		}

		return menu.add(new Bebida(precio, true, esAlcoholica, esCaliente));
	}

	public TicketNuevoPlatillo solicitarCrearComida(String loginEmpleado, String passwordEmpleado, double precio,
			List<String> alergenos) {
		if (autenticarEmpleado(loginEmpleado, passwordEmpleado) == null || precio < 0) {
			return null;
		}

		List<String> alergenosPlatillo = alergenos == null ? new ArrayList<>() : new ArrayList<>(alergenos);
		TicketNuevoPlatillo ticket = new TicketNuevoPlatillo(new Comida(precio, false, alergenosPlatillo));
		ticketsPlatillosPendientes.add(ticket);
		return ticket;
	}

	public TicketNuevoPlatillo solicitarCrearBebida(String loginEmpleado, String passwordEmpleado, double precio,
			boolean esAlcoholica, boolean esCaliente) {
		if (autenticarEmpleado(loginEmpleado, passwordEmpleado) == null || precio < 0) {
			return null;
		}

		TicketNuevoPlatillo ticket = new TicketNuevoPlatillo(new Bebida(precio, false, esAlcoholica, esCaliente));
		ticketsPlatillosPendientes.add(ticket);
		return ticket;
	}

	public boolean aprobarTicketPlatillo(String loginAdmin, String passwordAdmin, TicketNuevoPlatillo ticket) {
		if (!esAdmin(loginAdmin, passwordAdmin) || ticket == null || !ticketsPlatillosPendientes.contains(ticket)) {
			return false;
		}

		admin.aprobarPlatillo(ticket);
		ticketsPlatillosPendientes.remove(ticket);
		return ticket.getPlatillo() != null && menu.add(ticket.getPlatillo());
	}

	public boolean rechazarTicketPlatillo(String loginAdmin, String passwordAdmin, TicketNuevoPlatillo ticket) {
		if (!esAdmin(loginAdmin, passwordAdmin) || ticket == null || !ticketsPlatillosPendientes.contains(ticket)) {
			return false;
		}

		admin.rechazarPlatillo(ticket);
		return ticketsPlatillosPendientes.remove(ticket);
	}

	public boolean agregarJuegoCatalogo(String loginAdmin, String passwordAdmin, String nombre, int anioPublicacion,
			String empresaMatriz, String tipoJuego, boolean esDificil, boolean puedenNinos, boolean puedenJovenes,
			int minJugadores, int maxJugadores, double precio) {
		if (!esAdmin(loginAdmin, passwordAdmin)
				|| nombre == null || nombre.isBlank()
				|| empresaMatriz == null || empresaMatriz.isBlank()
				|| tipoJuego == null || tipoJuego.isBlank()
				|| anioPublicacion <= 0
				|| minJugadores <= 0
				|| maxJugadores < minJugadores
				|| precio < 0) {
			return false;
		}

		boolean yaExiste = catalogoJuegos.stream().anyMatch(juego ->
				juego.getNombre().equalsIgnoreCase(nombre)
				&& juego.getAnio() == anioPublicacion
				&& juego.getEmpresa().equalsIgnoreCase(empresaMatriz));

		if (yaExiste) {
			return false;
		}

		return catalogoJuegos.add(new JuegoDeMesa(nombre, anioPublicacion, empresaMatriz, tipoJuego, esDificil,
				puedenNinos, puedenJovenes, minJugadores, maxJugadores, precio));
	}

	public boolean comprarJuegoFisico(JuegoDeMesa juego) {
		
		JuegoFisico juegoFisico = new JuegoFisico("nuevo", false, juego);

		return inventarioVenta.add(juegoFisico);
	}

	public boolean agregarJuegoCompra(JuegoFisico juego) {
		if (juego == null || juego.getJuegoBase() == null || !catalogoJuegos.contains(juego.getJuegoBase())) {
			return false;
		}

		return inventarioVenta.add(juego);
	}

	public boolean transferirJuegoVentaAPrestamo(JuegoFisico juego) {
		if (juego == null || !inventarioVenta.contains(juego) || juego.isOcupado()) {
			return false;
		}

		inventarioVenta.remove(juego);
		return agregarJuegoPrestamo(juego);
	}

	public boolean agregarJuegoPrestamo(JuegoFisico juego) {
		if (juego == null || juego.getJuegoBase() == null || !catalogoJuegos.contains(juego.getJuegoBase())) {
			return false;
		}

		return inventarioPrestamo.add(juego);
	}

	public Set<JuegoDeMesa> getCatalogoJuegos() {
		return catalogoJuegos;
	}

	public List<JuegoFisico> getCatalogoCompra() {
		return inventarioVenta;
	}

	public List<JuegoFisico> getCatalogoPrestamo() {
		return inventarioPrestamo;
	}

	public Set<Platillo> getMenu() {
		return menu;
	}
	
	public List<Usuario> getUsuarios() {
		return usuarios;
	}

	public List<Reserva> getReservas() {
	    return reservas;
	}

	public List<Mesa> getMesas() {
		return mesas;
	}

	public List<Compra> getCompras() {
		return compras;
	}

	public List<Torneo> getTorneos() {
	    return torneos;
	}
	
	public HorarioSemanal getHorario() {
	    return horario;
	}

	public List<TicketNuevoPlatillo> getTicketsPlatillosPendientes() {
		return ticketsPlatillosPendientes;
	}
	
	
	
	public boolean crearTorneo(String loginAdmin, String passwordAdmin, boolean esCompetitivo, 
            int bono, double costoEntrada, int cupos, String juegoNombre) {
		if (!esAdmin(loginAdmin, passwordAdmin)) {
			return false;	
		}
		
		JuegoDeMesa juego = catalogoJuegos.stream()
			.filter(j -> j.getNombre().equalsIgnoreCase(juegoNombre))
			.findFirst()
			.orElse(null);
		
		if (juego == null || bono < 0 || costoEntrada < 0 || cupos <= 0) {
			return false;
		}
		
		Torneo torneo = new Torneo(esCompetitivo, bono, costoEntrada, cupos, juego);
			torneos.add(torneo);
			return true;
		}
	
	public List<Torneo> getTorneosActivos() {
	    return torneos.stream()
	            .filter(t -> t.getCuposTaken() < t.getCupos())
	            .collect(Collectors.toList());
	}
	
	public void mostrarInscripcionesTorneo(Torneo torneo) {
	    if (torneo == null) {
	        System.out.println("Torneo no válido.");
	        return;
	    }
	    
	    torneo.verEstatusInscripcion();
	}
	
	public boolean eliminarTorneo(String loginAdmin, String passwordAdmin, Torneo torneo) {
	    if (!esAdmin(loginAdmin, passwordAdmin) || torneo == null || !torneos.contains(torneo)) {
	        return false;
	    }
	    
	    return torneos.remove(torneo);
	}
	
	public boolean asignarGanadorTorneo(String loginAdmin, String passwordAdmin, Torneo torneo, String ganadorLogin) {
	    if (!esAdmin(loginAdmin, passwordAdmin) || torneo == null || !torneos.contains(torneo)) {
	        return false;
	    }
	    
	    Usuario ganador = usuarios.stream()
	            .filter(u -> u.getLogin().equals(ganadorLogin) && u instanceof Cliente)
	            .findFirst()
	            .orElse(null);
	    
	    if (ganador == null || !torneo.getUsuarios().contains(ganador)) {
	        return false;
	    }
	    
	    Cliente clienteGanador = (Cliente) ganador;
	    clienteGanador.agregarPuntos(torneo.getBono());
	    return true;
	}
	
	
	private Usuario autenticarUsuario(String login, String password) {
		return usuarios.stream()
				.filter(usuario -> usuario.getLogin().equals(login) && usuario.autenticacion(password))
				.findFirst()
				.orElse(null);
	}

	public List<Turno> consultarTurnosEmpleado(String login, String password) {
		Empleado empleado = autenticarEmpleado(login, password);
		if (empleado == null) {
			return new ArrayList<>();
		}

		return horario.getTurnosPorDia().values().stream()
				.filter(turno -> turno.getEmpleados().contains(empleado))
				.collect(Collectors.toList());
	}

	public boolean registrarAyudaMesero(String loginMesero, String passwordMesero, JuegoDeMesa juego) {
		Empleado empleado = autenticarEmpleado(loginMesero, passwordMesero);
		if (!(empleado instanceof Mesero) || juego == null) {
			return false;
		}

		return ((Mesero) empleado).puedeExplicar(juego);
	}

	public List<JuegoFisico> consultarJuegosPrestamoCompatibles(Reserva reserva) {
		if (reserva == null || !reservas.contains(reserva)) {
			return new ArrayList<>();
		}

		return inventarioPrestamo.stream()
				.filter(juego -> juego != null && juego.estaDisponible() && juego.getJuegoBase() != null)
				.filter(juego -> juego.getJuegoBase().aptoParaNumJugadores(reserva.getNumPersonas()))
				.filter(juego -> juego.getJuegoBase().esAptoParaEdad(reserva.isHayNinos(), reserva.isHayMenores()))
				.collect(Collectors.toList());
	}

	public CompraJuegoMesa generarCompraJuegos(String login, String password, List<JuegoFisico> juegos, double descuento) {
		Usuario usuario = autenticarUsuario(login, password);
		if (usuario == null || juegos == null || juegos.isEmpty() || descuento < 0) {
			return null;
		}

		CompraJuegoMesa compra = new CompraJuegoMesa();
		compra.setDescuento(descuento);

		for (JuegoFisico juego : juegos) {
			if (juego == null || !inventarioVenta.contains(juego) || juego.getJuegoBase() == null) {
				return null;
			}
			compra.agregarJuego(juego);
		}

		for (JuegoFisico juego : juegos) {
			inventarioVenta.remove(juego);
		}

		double total = compra.calcularTotal();
		if (usuario instanceof Cliente) {
			((Cliente) usuario).agregarPuntos(total);
		}

		compras.add(compra);
		return compra;
	}

	public CompraPlatillo generarCompraPlatillos(String login, String password, Reserva reserva, List<Platillo> platillos, double descuento) {
		Usuario usuario = autenticarUsuario(login, password);
		if (usuario == null || platillos == null || platillos.isEmpty() || descuento < 0 || !(usuario instanceof Cliente) || reserva == null || !reservas.contains(reserva)) {
			return null;
		}

		if (!reserva.getCliente().getLogin().equals(login)) {
			return null;
		}

		if (platillos.stream().anyMatch(platillo -> {
			if (platillo instanceof Bebida) {
				return !((Bebida) platillo).esPermitidaEn(reserva);
			}
			return false;
		})) {
			return null;
		}


		CompraPlatillo compra = new CompraPlatillo();
		compra.setDescuento(descuento);

		for (Platillo platillo : platillos) {
			if (platillo == null || !menu.contains(platillo)) {
				return null;
			}
			compra.agregarPlatillo(platillo);
		}

		double total = compra.calcularTotal();
		if (usuario instanceof Cliente) {
			((Cliente) usuario).agregarPuntos(total);
		}

		compras.add(compra);
		return compra;
	}

	public Prestamo generarPrestamoJuego(String login, String password, JuegoFisico juego, Reserva reserva) {
		Usuario usuario = autenticarUsuario(login, password);
		if (usuario == null || juego == null || !inventarioPrestamo.contains(juego) || juego.isOcupado()) {
			return null;
		}
		if (usuario instanceof Cliente && reserva == null) {
			return null;
		}
		if (reserva != null && !reservas.contains(reserva)) {
			return null;
		}
		if (reserva != null && juego.getJuegoBase() != null
				&& (!juego.getJuegoBase().aptoParaNumJugadores(reserva.getNumPersonas())
						|| !juego.getJuegoBase().esAptoParaEdad(reserva.isHayNinos(), reserva.isHayMenores()))) {
			return null;
		}

		Prestamo prestamo = new Prestamo(new Date(), juego, usuario, reserva);
		juego.prestar();
		historialPrestamos.add(prestamo);

		if (reserva != null && !reserva.agregarPrestamo(prestamo)) {
			prestamo.finalizar();
			historialPrestamos.remove(prestamo);
			return null;
		}

		return prestamo;
	}

	public TicketCambiarTurno solicitarCambioTurno(String loginEmpleadoPrincipal, String passwordEmpleadoPrincipal,
			Turno turnoInicial, Turno turnoFinal, String loginEmpleadoSecundario) {
		Empleado empleadoPrincipal = autenticarEmpleado(loginEmpleadoPrincipal, passwordEmpleadoPrincipal);
		Empleado empleadoSecundario = usuarios.stream()
				.filter(usuario -> usuario instanceof Empleado && usuario.getLogin().equals(loginEmpleadoSecundario))
				.map(usuario -> (Empleado) usuario)
				.findFirst()
				.orElse(null);

		if (empleadoPrincipal == null || empleadoSecundario == null || turnoFinal == null) {
			return null;
		}

		TicketCambiarTurno ticket = new TicketCambiarTurno(empleadoPrincipal, turnoInicial, turnoFinal, empleadoSecundario);
		ticket.setEstado("PENDIENTE");
		ticketsTurnoPendientes.add(ticket);
		return ticket;
	}

	public boolean aprobarTicketTurno(String loginAdmin, String passwordAdmin, TicketCambiarTurno ticket) {
		if (!esAdmin(loginAdmin, passwordAdmin) || ticket == null || !ticketsTurnoPendientes.contains(ticket)) {
			return false;
		}

		admin.aprobarTicketTurno(ticket);
		return ticketsTurnoPendientes.remove(ticket);
	}

	public boolean rechazarTicketTurno(String loginAdmin, String passwordAdmin, TicketCambiarTurno ticket) {
		if (!esAdmin(loginAdmin, passwordAdmin) || ticket == null || !ticketsTurnoPendientes.contains(ticket)) {
			return false;
		}

		admin.rechazarTicketTurno(ticket);
		return ticketsTurnoPendientes.remove(ticket);
	}

	public List<TicketCambiarTurno> getTicketsTurnoPendientes() {
		return ticketsTurnoPendientes;
	}

	public List<Prestamo> getHistorialPrestamos() {
		return historialPrestamos;
	}
	
	private static void imprimirEstado(String titulo, Cafe cafe, CompraPlatillo compraPlatillos,
			CompraJuegoMesa compraJuegos, Prestamo prestamoCliente, Prestamo prestamoEmpleado) {
		System.out.println("========== " + titulo + " ==========");
		System.out.println("Admin: " + cafe.admin.getLogin());
		System.out.println("Usuarios registrados: " + cafe.usuarios.size());
		for (Usuario usuario : cafe.usuarios) {
			String tipo = usuario.getClass().getSimpleName();
			System.out.println(" - " + tipo + ": " + usuario.getLogin());
			if (usuario instanceof Cliente) {
				Cliente cliente = (Cliente) usuario;
				System.out.println("   puntos=" + cliente.getPuntosFidelidad()
						+ ", favoritos=" + cliente.getJuegosFavoritos().size());
			}
		}

		System.out.println("Mesas creadas: " + cafe.mesas.size());
		for (Mesa mesa : cafe.mesas) {
			System.out.println(" - Mesa " + mesa.getId() + " capacidad=" + mesa.getCapacidad());
		}

		System.out.println("Reservas: " + cafe.reservas.size());
		for (Reserva reserva : cafe.reservas) {
			System.out.println(" - Reserva cliente=" + reserva.getCliente().getLogin()
					+ ", mesa=" + reserva.getMesaId()
					+ ", personas=" + reserva.getNumPersonas()
					+ ", menores=" + reserva.isHayMenores()
					+ ", ninos=" + reserva.isHayNinos()
					+ ", prestamosActivos=" + reserva.getPrestamosActivos().size());
		}

		System.out.println("Menu: " + cafe.menu.size());
		for (Platillo platillo : cafe.menu) {
			System.out.println(" - " + platillo.getClass().getSimpleName()
					+ " precio=" + platillo.getPrecio()
					+ ", aprobado=" + platillo.isAprobado());
		}

		System.out.println("Tickets de platillos pendientes: " + cafe.ticketsPlatillosPendientes.size());
		System.out.println("Catalogo juegos: " + cafe.catalogoJuegos.size());
		for (JuegoDeMesa juego : cafe.catalogoJuegos) {
			System.out.println(" - " + juego.getNombre() + " (" + juego.getTipoJuego()
					+ "), precio=" + juego.getPrecio());
		}

		System.out.println("Inventario venta: " + cafe.inventarioVenta.size());
		for (JuegoFisico juego : cafe.inventarioVenta) {
			System.out.println(" - Venta " + juego.getJuegoBase().getNombre()
					+ ", estado=" + juego.getEstado()
					+ ", ocupado=" + juego.isOcupado());
		}

		System.out.println("Inventario prestamo: " + cafe.inventarioPrestamo.size());
		for (JuegoFisico juego : cafe.inventarioPrestamo) {
			System.out.println(" - Prestamo " + juego.getJuegoBase().getNombre()
					+ ", estado=" + juego.getEstado()
					+ ", ocupado=" + juego.isOcupado());
		}

		System.out.println("Historial prestamos: " + cafe.historialPrestamos.size());
		if (prestamoCliente != null) {
			System.out.println(" - Prestamo cliente terminado=" + prestamoCliente.isTerminado());
		}
		if (prestamoEmpleado != null) {
			System.out.println(" - Prestamo empleado terminado=" + prestamoEmpleado.isTerminado());
		}

		System.out.println("Turnos cargados: " + cafe.horario.getTurnosPorDia().size());
		for (String dia : cafe.horario.getTurnosPorDia().keySet()) {
			Turno turno = cafe.horario.getTurnosPorDia().get(dia);
			System.out.println(" - " + dia + " empleados=" + turno.getEmpleados().size());
		}

		System.out.println("Tickets cambio turno pendientes: " + cafe.ticketsTurnoPendientes.size());
		if (compraPlatillos != null) {
			System.out.println("Compra platillos total=" + compraPlatillos.getTotal()
					+ ", items=" + compraPlatillos.getPlatillos().size());
		}
		if (compraJuegos != null) {
			System.out.println("Compra juegos total=" + compraJuegos.calcularTotal()
					+ ", items=" + compraJuegos.getJuegos().size());
		}
		
		List<Torneo> torneos = cafe.getTorneos();
        List<Torneo> torneosActivos = torneos == null ? new ArrayList<>() : torneos.stream()
                .filter(t -> t.getCuposTaken() < t.getCupos())
                .collect(Collectors.toList());
        
        System.out.println("Torneos activos: " + torneosActivos.size());
        for (Torneo t : torneosActivos) {
            int cuposDisponibles = t.getCupos() - t.getCuposTaken();
            System.out.println(" - Juego: " + (t.getJuego() != null ? t.getJuego().getNombre() : "Sin juego")
                    + " | Cupos Disponibles: " + cuposDisponibles + "/" + t.getCupos()
                    + " | Bono: " + t.getBono() + " puntos"
                    + " | Competitivo: " + (t.isEsCompetitivo() ? "Si" : "No")
                    + " | Costo: $" + t.getCostoEntrada());
        }
		System.out.println("====================================");
	}
	
	
	/*	Con esto se hizo el test.txt

	public static void main(String[] args) {
		Cafe c = new Cafe("test.txt", 50, "password", "admin");

		c.crearMesa(4);
		c.crearMesa(6);
		c.crearMesa(8);

		c.crearCliente("ana", "1234");
		c.crearCliente("bruno", "abcd");

		Mesero mesero = new Mesero("mario", "mesero123");
		Cocinero cocinero = new Cocinero("sofia", "cocina123");
		c.usuarios.add(mesero);
		c.usuarios.add(cocinero);

		Turno lunesManana = new Turno("Lunes manana");
		lunesManana.agregarEmpleado(mesero);
		Turno martesTarde = new Turno("Martes tarde");
		martesTarde.agregarEmpleado(cocinero);
		c.horario.asignarTurno("lunes", lunesManana);
		c.horario.asignarTurno("martes", martesTarde);

		c.crearComida("password", "admin", 18000, List.of("gluten", "lactosa"));
		c.crearBebida("password", "admin", 7000, false, true);

		TicketNuevoPlatillo ticketComida = c.solicitarCrearComida("sofia", "cocina123", 22000, List.of("mani"));
		TicketNuevoPlatillo ticketBebida = c.solicitarCrearBebida("mario", "mesero123", 9500, true, false);
		c.aprobarTicketPlatillo("password", "admin", ticketComida);
		c.rechazarTicketPlatillo("password", "admin", ticketBebida);

		c.agregarJuegoCatalogo("password", "admin", "Catan", 1995, "Kosmos", "Estrategia", false, true, true, 3, 4, 120000);
		c.agregarJuegoCatalogo("password", "admin", "Terraforming Mars", 2016, "FryxGames", "Estrategia", true, false, true, 1, 5, 210000);

		JuegoDeMesa catan = c.catalogoJuegos.stream()
				.filter(juego -> juego.getNombre().equals("Catan"))
				.findFirst()
				.orElse(null);
		JuegoDeMesa mars = c.catalogoJuegos.stream()
				.filter(juego -> juego.getNombre().equals("Terraforming Mars"))
				.findFirst()
				.orElse(null);

		c.comprarJuegoFisico(catan);
		c.comprarJuegoFisico(catan);
		c.agregarJuegoCompra(new JuegoFisico("caja abierta", false, mars));
		JuegoFisico juegoPrestable = new JuegoFisico("excelente", false, mars);
		c.agregarJuegoPrestamo(juegoPrestable);
		c.transferirJuegoVentaAPrestamo(c.getCatalogoCompra().get(0));

		Cliente ana = (Cliente) c.usuarios.stream()
				.filter(usuario -> usuario instanceof Cliente && usuario.getLogin().equals("ana"))
				.findFirst()
				.orElse(null);
		Cliente bruno = (Cliente) c.usuarios.stream()
				.filter(usuario -> usuario instanceof Cliente && usuario.getLogin().equals("bruno"))
				.findFirst()
				.orElse(null);
		ana.agregarJuegoFav(catan);
		bruno.agregarJuegoFav(mars);

		c.crearReservacion("ana", "1234", 4, true, true);
		c.crearReservacion("bruno", "abcd", 2, false, false);
		Reserva reservaAna = c.reservas.stream()
				.filter(reserva -> reserva.getCliente().getLogin().equals("ana"))
				.findFirst()
				.orElse(null);

		List<Platillo> pedidoAna = new ArrayList<>();
		pedidoAna.addAll(c.menu);
		CompraPlatillo compraPlatillos = c.generarCompraPlatillos("ana", "1234", reservaAna, pedidoAna, 10);

		List<JuegoFisico> juegosCompra = new ArrayList<>();
		juegosCompra.add(c.getCatalogoCompra().get(0));
		CompraJuegoMesa compraJuegos = c.generarCompraJuegos("bruno", "abcd", juegosCompra, 5);

		JuegoFisico juegoParaCliente = c.getCatalogoPrestamo().get(0);
		JuegoFisico juegoParaEmpleado = c.getCatalogoPrestamo().get(1);
		Prestamo prestamoCliente = c.generarPrestamoJuego("ana", "1234", juegoParaCliente, reservaAna);
		Prestamo prestamoEmpleado = c.generarPrestamoJuego("mario", "mesero123", juegoParaEmpleado, null);

		TicketCambiarTurno ticketTurno = c.solicitarCambioTurno("mario", "mesero123", lunesManana, martesTarde, "sofia");
		c.aprobarTicketTurno("password", "admin", ticketTurno);

		imprimirEstado("ESTADO ANTES DE GUARDAR", c, compraPlatillos, compraJuegos, prestamoCliente, prestamoEmpleado);

		boolean saved = c.save();
		System.out.println("Cafe guardado: " + saved);

		Cafe cafeCargado = new Cafe("test.txt");
		Reserva reservaAnaCargado = cafeCargado.reservas.stream()
				.filter(reserva -> reserva.getCliente().getLogin().equals("ana"))
				.findFirst()
				.orElse(null);
		CompraPlatillo compraPlatillosCargada = cafeCargado.generarCompraPlatillos("ana", "1234",
				reservaAnaCargado, new ArrayList<>(cafeCargado.menu), 0);
		imprimirEstado("ESTADO DESPUES DE CARGAR", cafeCargado, compraPlatillosCargada, null, null, null);
	}
	*/
	public static void main(String[] args) {
		/*Cafe c = new Cafe("escenario1.txt", 50, "password", "admin");
		c.save();*/
		String nombreCafe = "escenario1.txt";
		Cafe cafeCargado = new Cafe(nombreCafe);
		Cafe.imprimirEstado("CAFE CARGADO DESDE escenario1.txt", cafeCargado, null, null, null, null);
		
		
		Scanner scanner = new Scanner(System.in);
        int opcion;
        
        do {
        	System.out.println("\n Bienvenido al cafe "+nombreCafe+"");
            System.out.println("1. Cliente");
            System.out.println("2. Empleado");
            System.out.println("3. Administrador");
            System.out.print("Seleccione una opción: ");
            
            if (scanner.hasNextInt()) {
                opcion = scanner.nextInt();
                scanner.nextLine();
                
                switch (opcion) {
                    case 1:
                        System.out.println("\n- Abriendo consola de Cliente...\n");
                        ConsolaCliente consolaCliente = new ConsolaCliente(cafeCargado);
                        consolaCliente.ejecutar();
                        break;
                    case 2:
                        System.out.println("\n- Abriendo consola de Empleado...\n");
                        ConsolaEmpleado consolaEmpleado = new ConsolaEmpleado(cafeCargado);
                        consolaEmpleado.ejecutar();
                        break;
                    case 3:
                        System.out.println("\n- Abriendo consola de Administrador...\n");
                        ConsolaAdministrador consolaAdmin = new ConsolaAdministrador(cafeCargado);
                        consolaAdmin.ejecutar();
                        break;
                    default:
                        System.out.println("Opción inválida. Intente de nuevo.\n");
                }
            } else {
                System.out.println("Entrada inválida. Ingrese un número.\n");
                scanner.next();
                opcion = -1;
            }
            
        } while (List.of(1,2,3).contains(opcion)== false);
        
        cafeCargado.save();
        scanner.close();
        
        System.out.println("\nSaliendo...\n\nGracias");
    }
	
}
	
	
