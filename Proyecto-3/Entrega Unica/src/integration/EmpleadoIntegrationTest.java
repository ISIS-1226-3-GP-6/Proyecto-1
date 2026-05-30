package integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import cafeteria.TicketNuevoPlatillo;
import compras.CompraJuegoMesa;
import control.Cafe;
import horario.TicketCambiarTurno;
import horario.Turno;
import juego.JuegoDeMesa;
import juego.JuegoFisico;
import usuarios.Cocinero;
import usuarios.Mesero;

class EmpleadoIntegrationTest {

	private Path archivoTemporal;
	private Cafe cafe;

	@BeforeEach
	void setUp() throws IOException {
		archivoTemporal = Files.createTempFile("empleado-integration", ".txt");
		cafe = new Cafe(archivoTemporal.toString(), 12, "admin", "clave");
	}

	@AfterEach
	void cleanUp() throws IOException {
		Files.deleteIfExists(archivoTemporal);
	}

	@Test
	void RF16_empleadoConTurnosAsignadosPuedeConsultarlos() {
		// Arrange
		Mesero mario = registrarMesero("mario");
		Turno lunes = crearTurno("Lunes 08:00-14:00", mario);

		// Act
		List<Turno> turnos = cafe.consultarTurnosEmpleado("mario", "pass");

		// Assert
		assertEquals(1, turnos.size());
		assertEquals(lunes.getDiaSemana(), turnos.get(0).getDiaSemana());
		assertTrue(turnos.get(0).getEmpleados().contains(mario));
	}

	@Test
	void RF17_empleadoPuedeCrearSolicitudCambioTurnoPendiente() {
		// Arrange
		Mesero mario = registrarMesero("mario");
		Cocinero sofia = registrarCocinero("sofia");
		Turno lunes = crearTurno("Lunes 08:00-14:00", mario);
		Turno martes = crearTurno("Martes 14:00-20:00", sofia);

		// Act
		TicketCambiarTurno ticket = cafe.solicitarCambioTurno("mario", "pass", lunes, martes, "sofia");

		// Assert
		assertNotNull(ticket);
		assertEquals("PENDIENTE", ticket.getEstado());
		assertTrue(cafe.getTicketsTurnoPendientes().contains(ticket));
	}

	@Test
	void RF18_empleadoPuedeSugerirNuevoPlatilloPendiente() {
		// Arrange
		registrarCocinero("sofia");

		// Act
		TicketNuevoPlatillo ticket = cafe.solicitarCrearComida("sofia", "pass", 18000, List.of("gluten"));

		// Assert
		assertNotNull(ticket);
		assertFalse(ticket.isAprobado());
		assertTrue(cafe.getTicketsPlatillosPendientes().contains(ticket));
	}

	@Test
	void RF19_empleadoCompraProductoConDescuentoDelVeintePorCiento() {
		// Arrange
		registrarMesero("mario");
		JuegoFisico juego = crearJuegoCompra("Catan", 100000);

		// Act
		CompraJuegoMesa compra = cafe.generarCompraJuegos("mario", "pass", List.of(juego), 20);

		// Assert
		assertNotNull(compra);
		assertEquals(80000, compra.calcularTotal());
		assertTrue(cafe.getCompras().contains(compra));
	}

	@Test
	void RF20_clientePuedeUsarDescuentoCompartidoDelDiezPorCiento() {
		// Arrange
		registrarMesero("mario");
		cafe.crearCliente("ana", "1234");
		JuegoFisico juego = crearJuegoCompra("Catan", 100000);

		// Act
		CompraJuegoMesa compra = cafe.generarCompraJuegos("ana", "1234", List.of(juego), 10);

		// Assert
		assertNotNull(compra);
		assertEquals(90000, compra.calcularTotal());
		// TODO RF20: falta un modelo de codigo de descuento de un solo uso generado por empleado.
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

	private JuegoFisico crearJuegoCompra(String nombre, double precio) {
		cafe.agregarJuegoCatalogo("admin", "clave", nombre, 2020, "Editorial", "Familiar",
				false, true, true, 1, 6, precio);
		JuegoDeMesa juego = cafe.getCatalogoJuegos().stream()
				.filter(j -> j.getNombre().equals(nombre))
				.findFirst()
				.orElseThrow();
		JuegoFisico copia = new JuegoFisico("nuevo", false, juego);
		cafe.agregarJuegoCompra(copia);
		return copia;
	}
}
