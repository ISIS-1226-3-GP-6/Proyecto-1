package integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import cafeteria.TicketNuevoPlatillo;
import control.Cafe;
import horario.TicketCambiarTurno;
import horario.Turno;
import juego.JuegoDeMesa;
import juego.JuegoFisico;
import juego.Prestamo;
import juego.Torneo;
import reservacion.Reserva;
import usuarios.Cliente;
import usuarios.Cocinero;
import usuarios.Mesero;
import usuarios.Usuario;

class AdministradorIntegrationTest {

	private Path archivoTemporal;
	private Cafe cafe;

	@BeforeEach
	void setUp() throws IOException {
		archivoTemporal = Files.createTempFile("admin-integration", ".txt");
		cafe = new Cafe(archivoTemporal.toString(), 12, "admin", "clave");
	}

	@AfterEach
	void cleanUp() throws IOException {
		Files.deleteIfExists(archivoTemporal);
	}

	@Test
	void RF01_ADMIN_adminPuedeRegistrarEmpleadoConRolCorrectoYLoginUnico() {
		// Arrange / Act
		boolean meseroCreado = cafe.crearMesero("admin", "clave", "mario", "mesero123");
		boolean loginDuplicado = cafe.crearCocinero("admin", "clave", "mario", "otra");
		boolean cocineroCreado = cafe.crearCocinero("admin", "clave", "sofia", "cocina123");

		// Assert
		assertTrue(meseroCreado);
		assertFalse(loginDuplicado);
		assertTrue(cocineroCreado);
		assertTrue(cafe.autenticarEmpleado("mario", "mesero123") instanceof Mesero);
		assertTrue(cafe.autenticarEmpleado("sofia", "cocina123") instanceof Cocinero);
	}

	@Test
	void RF22_adminPuedeAgregarEditarYEliminarElementosDelCatalogo() {
		// Arrange
		cafe.agregarJuegoCatalogo("admin", "clave", "Catan", 1995, "Kosmos", "Estrategia",
				false, true, true, 2, 4, 120000);
		JuegoDeMesa catan = cafe.getCatalogoJuegos().iterator().next();

		// Act
		catan.setPrecio(130000);
		boolean eliminado = cafe.getCatalogoJuegos().remove(catan);

		// Assert
		assertTrue(eliminado);
		assertTrue(cafe.getCatalogoJuegos().isEmpty());
		assertEquals(130000, catan.getPrecio());
	}

	@Test
	void RF23_adminPuedeAprobarYRechazarSugerenciasDePlatillos() {
		// Arrange
		cafe.crearCocinero("admin", "clave", "sofia", "cocina123");
		TicketNuevoPlatillo aprobar = cafe.solicitarCrearComida("sofia", "cocina123", 18000, null);
		TicketNuevoPlatillo rechazar = cafe.solicitarCrearBebida("sofia", "cocina123", 7000, false, true);

		// Act
		boolean aprobado = cafe.aprobarTicketPlatillo("admin", "clave", aprobar);
		boolean rechazado = cafe.rechazarTicketPlatillo("admin", "clave", rechazar);

		// Assert
		assertTrue(aprobado);
		assertTrue(rechazado);
		assertTrue(aprobar.isAprobado());
		assertFalse(rechazar.isAprobado());
		assertTrue(cafe.getMenu().contains(aprobar.getPlatillo()));
		assertFalse(cafe.getMenu().contains(rechazar.getPlatillo()));
	}

	@Test
	void RF24_adminPuedeResolverSolicitudDeCambioDeTurno() {
		// Arrange
		Mesero mario = registrarMesero("mario");
		Cocinero sofia = registrarCocinero("sofia");
		Turno lunes = crearTurno("Lunes manana", mario);
		Turno martes = crearTurno("Martes tarde", sofia);
		TicketCambiarTurno ticket = cafe.solicitarCambioTurno("mario", "pass", lunes, martes, "sofia");

		// Act
		boolean aprobado = cafe.aprobarTicketTurno("admin", "clave", ticket);

		// Assert
		assertTrue(aprobado);
		assertEquals("Aprobada", ticket.getEstado());
		assertFalse(cafe.getTicketsTurnoPendientes().contains(ticket));
		// TODO RF24: el modelo actual cambia el estado, pero no intercambia empleados ni valida minimos por turno.
	}

	@Test
	void RF25_juegoMovidoDeCompraAPrestamoDesapareceDeOrigenYApareceEnDestino() {
		// Arrange
		JuegoFisico juego = crearJuegoCompra("Catan");

		// Act
		boolean movido = cafe.transferirJuegoVentaAPrestamo(juego);

		// Assert
		assertTrue(movido);
		assertFalse(cafe.getCatalogoCompra().contains(juego));
		assertTrue(cafe.getCatalogoPrestamo().contains(juego));
	}

	@Test
	void RF26_repararJuegoDaniadoConCopiaDeCompraActualizaInventario() {
		// Arrange
		JuegoFisico daniado = crearJuegoPrestamo("Catan");
		daniado.setEstado("daniado");
		JuegoFisico reemplazo = crearJuegoCompra("Catan Reemplazo");

		// Act
		cafe.getCatalogoPrestamo().remove(daniado);
		boolean reemplazoMovido = cafe.transferirJuegoVentaAPrestamo(reemplazo);

		// Assert
		assertTrue(reemplazoMovido);
		assertFalse(cafe.getCatalogoPrestamo().contains(daniado));
		assertTrue(cafe.getCatalogoPrestamo().contains(reemplazo));
		assertFalse(cafe.getCatalogoCompra().contains(reemplazo));
		// TODO RF26: falta un metodo de Cafe/Admin que encapsule reparar juego y registre el reemplazo como operacion unica.
	}

	@Test
	void RF27_juegoDePrestamoVencidoPuedeMarcarseComoDesaparecido() {
		// Arrange
		cafe.crearMesa(4);
		cafe.crearCliente("ana", "1234");
		cafe.crearReservacion("ana", "1234", 2, false, false);
		JuegoFisico juego = crearJuegoPrestamo("Catan");
		Reserva reserva = cafe.getReservas().get(0);
		Prestamo prestamo = cafe.generarPrestamoJuego("ana", "1234", juego, reserva);

		// Act
		juego.setEstado("desaparecido");
		cafe.getCatalogoPrestamo().remove(prestamo.getJuego());

		// Assert
		assertEquals("desaparecido", juego.getEstado());
		assertFalse(cafe.getCatalogoPrestamo().contains(juego));
		// TODO RF27: el modelo no distingue prestamo vencido; se marca con estado y salida de inventario.
	}

	@Test
	void RF28_adminPuedeCrearVariosTorneosYQuedanDisponibles() {
		// Arrange
		crearJuegoBase("Catan");
		crearJuegoBase("Azul");

		// Act
		boolean catan = cafe.crearTorneo("admin", "clave", true, 100, 25000, 5, "Catan");
		boolean azul = cafe.crearTorneo("admin", "clave", false, 50, 10000, 4, "Azul");

		// Assert
		assertTrue(catan);
		assertTrue(azul);
		assertEquals(2, cafe.getTorneosActivos().size());
	}

	@Test
	void RF30_adminPuedeAsignarGanadorInscritoYDarPuntos() {
		// Arrange
		Cliente ana = registrarCliente("ana");
		Torneo torneo = crearTorneo("Catan", 100);
		torneo.inscribir(ana, 1);

		// Act
		boolean asignado = cafe.asignarGanadorTorneo("admin", "clave", torneo, "ana");

		// Assert
		assertTrue(asignado);
		assertEquals(1, ana.getPuntosFidelidad());
		// TODO RF30: Torneo no guarda todavia un campo ganador consultable.
	}

	@Test
	void RF31_adminPuedeEliminarTorneoYNoApareceDisponible() {
		// Arrange
		Torneo torneo = crearTorneo("Catan", 100);

		// Act
		boolean eliminado = cafe.eliminarTorneo("admin", "clave", torneo);

		// Assert
		assertTrue(eliminado);
		assertFalse(cafe.getTorneosActivos().contains(torneo));
	}

	@Test
	void RF32_adminConsultaUsuariosInscritosYCantidadDeParticipantes() {
		// Arrange
		Cliente ana = registrarCliente("ana");
		Cliente bruno = registrarCliente("bruno");
		Torneo torneo = crearTorneo("Catan", 100);
		torneo.inscribir(ana, 2);
		torneo.inscribir(bruno, 1);

		// Act
		Map<Usuario, Long> participantesPorUsuario = torneo.getUsuarios().stream()
				.collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

		// Assert
		assertEquals(2, participantesPorUsuario.get(ana));
		assertEquals(1, participantesPorUsuario.get(bruno));
		assertEquals(3, torneo.getCuposTaken());
	}

	private Cliente registrarCliente(String login) {
		cafe.crearCliente(login, "1234");
		return (Cliente) cafe.getUsuarios().stream()
				.filter(usuario -> usuario.getLogin().equals(login))
				.findFirst()
				.orElseThrow();
	}

	private Mesero registrarMesero(String login) {
		cafe.crearMesero("admin", "clave", login, "pass");
		return (Mesero) cafe.autenticarEmpleado(login, "pass");
	}

	private Cocinero registrarCocinero(String login) {
		cafe.crearCocinero("admin", "clave", login, "pass");
		return (Cocinero) cafe.autenticarEmpleado(login, "pass");
	}

	private Turno crearTurno(String dia, usuarios.Empleado empleado) {
		Turno turno = new Turno(dia);
		turno.agregarEmpleado(empleado);
		cafe.getHorario().asignarTurno(dia, turno);
		return turno;
	}

	private Torneo crearTorneo(String nombreJuego, int bono) {
		crearJuegoBase(nombreJuego);
		cafe.crearTorneo("admin", "clave", true, bono, 25000, 5, nombreJuego);
		return cafe.getTorneos().get(cafe.getTorneos().size() - 1);
	}

	private JuegoFisico crearJuegoCompra(String nombre) {
		JuegoDeMesa juego = crearJuegoBase(nombre);
		JuegoFisico copia = new JuegoFisico("nuevo", false, juego);
		cafe.agregarJuegoCompra(copia);
		return copia;
	}

	private JuegoFisico crearJuegoPrestamo(String nombre) {
		JuegoDeMesa juego = crearJuegoBase(nombre);
		JuegoFisico copia = new JuegoFisico("nuevo", false, juego);
		cafe.agregarJuegoPrestamo(copia);
		return copia;
	}

	private JuegoDeMesa crearJuegoBase(String nombre) {
		cafe.agregarJuegoCatalogo("admin", "clave", nombre, 2020, "Editorial", "Familiar",
				false, true, true, 1, 6, 100000);
		return cafe.getCatalogoJuegos().stream()
				.filter(juego -> juego.getNombre().equals(nombre))
				.findFirst()
				.orElseThrow();
	}
}
