package integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import cafeteria.Bebida;
import cafeteria.Platillo;
import compras.CompraJuegoMesa;
import compras.CompraPlatillo;
import control.Cafe;
import juego.JuegoDeMesa;
import juego.JuegoFisico;
import juego.Prestamo;
import reservacion.Reserva;
import usuarios.Cliente;

class ClienteIntegrationTest {

	private Path archivoTemporal;
	private Cafe cafe;

	@BeforeEach
	void setUp() throws IOException {
		archivoTemporal = Files.createTempFile("cliente-integration", ".txt");
		cafe = new Cafe(archivoTemporal.toString(), 8, "admin", "clave");
		cafe.crearMesa(4);
		cafe.crearMesa(4);
	}

	@AfterEach
	void cleanUp() throws IOException {
		Files.deleteIfExists(archivoTemporal);
	}

	@Test
	void RF01_CLIENTE_clientePuedeRegistrarseYLoginDebeSerUnico() {
		// Arrange / Act
		boolean registrado = cafe.crearCliente("ana", "1234");
		boolean duplicado = cafe.crearCliente("ana", "otra");

		// Assert
		assertTrue(registrado);
		assertFalse(duplicado);
		assertEquals(1, cafe.getUsuarios().size());
	}

	@Test
	void RF04_clientePuedeCrearReservaConMesaDisponible() {
		// Arrange
		cafe.crearCliente("ana", "1234");

		// Act
		boolean creada = cafe.crearReservacion("ana", "1234", 4, true, false);

		// Assert
		assertTrue(creada);
		assertEquals(1, cafe.getReservas().size());
		assertEquals("ana", cafe.getReservas().get(0).getCliente().getLogin());
		assertFalse(cafe.getMesas().get(0).sePuedeSentar(1));
	}

	@Test
	void RF04_noSeCreaReservaSiNoHayCapacidadDisponible() {
		// Arrange
		cafe.crearCliente("ana", "1234");

		// Act
		boolean creada = cafe.crearReservacion("ana", "1234", 9, false, false);

		// Assert
		assertFalse(creada);
		assertTrue(cafe.getReservas().isEmpty());
	}

	@Test
	void RF05_catalogoDevuelveJuegosCompatiblesConLaReserva() {
		// Arrange
		Reserva reserva = crearReservaActiva(4, true, true);
		JuegoFisico compatible = crearJuegoPrestamo("Catan", true, true, 2, 4, false);
		JuegoFisico noAptoNinos = crearJuegoPrestamo("Terraforming Mars", false, true, 1, 5, false);
		JuegoFisico sinCopiaDisponible = crearJuegoPrestamo("Azul", true, true, 2, 4, true);

		// Act
		List<JuegoFisico> catalogo = cafe.consultarJuegosPrestamoCompatibles(reserva);

		// Assert
		assertTrue(catalogo.contains(compatible));
		assertFalse(catalogo.contains(noAptoNinos));
		assertFalse(catalogo.contains(sinCopiaDisponible));
	}

	@Test
	void RF06_clientePuedeSeleccionarHastaDosJuegosDisponibles() {
		// Arrange
		Reserva reserva = crearReservaActiva(4, false, false);
		JuegoFisico catan = crearJuegoPrestamo("Catan", true, true, 2, 4, false);
		JuegoFisico azul = crearJuegoPrestamo("Azul", true, true, 2, 4, false);
		JuegoFisico carcassonne = crearJuegoPrestamo("Carcassonne", true, true, 2, 5, false);

		// Act
		Prestamo prestamo1 = cafe.generarPrestamoJuego("ana", "1234", catan, reserva);
		Prestamo prestamo2 = cafe.generarPrestamoJuego("ana", "1234", azul, reserva);
		Prestamo prestamo3 = cafe.generarPrestamoJuego("ana", "1234", carcassonne, reserva);

		// Assert
		assertNotNull(prestamo1);
		assertNotNull(prestamo2);
		assertNull(prestamo3);
		assertEquals(2, reserva.getPrestamosActivos().size());
	}

	@Test
	void RF07_prestamoValidoSeRegistraYJuegoQuedaOcupado() {
		// Arrange
		Reserva reserva = crearReservaActiva(4, false, false);
		JuegoFisico catan = crearJuegoPrestamo("Catan", true, true, 2, 4, false);

		// Act
		Prestamo prestamo = cafe.generarPrestamoJuego("ana", "1234", catan, reserva);

		// Assert
		assertNotNull(prestamo);
		assertTrue(catan.isOcupado());
		assertTrue(cafe.getHistorialPrestamos().contains(prestamo));
		assertTrue(reserva.getPrestamosActivos().contains(prestamo));
	}

	@Test
	void RF08_clientePuedeDevolverJuegoPrestado() {
		// Arrange
		Reserva reserva = crearReservaActiva(4, false, false);
		JuegoFisico catan = crearJuegoPrestamo("Catan", true, true, 2, 4, false);
		Prestamo prestamo = cafe.generarPrestamoJuego("ana", "1234", catan, reserva);

		// Act
		prestamo.finalizar();

		// Assert
		assertTrue(prestamo.isTerminado());
		assertFalse(catan.isOcupado());
	}

	@Test
	void RF10_clientePuedeComprarJuegoDisponibleYElInventarioSeReduce() {
		// Arrange
		registrarClienteBasico();
		JuegoFisico juegoCompra = crearJuegoCompra("Catan", 120000);

		// Act
		CompraJuegoMesa compra = cafe.generarCompraJuegos("ana", "1234", List.of(juegoCompra), 0);

		// Assert
		assertNotNull(compra);
		assertEquals(120000, compra.calcularTotal());
		assertFalse(cafe.getCatalogoCompra().contains(juegoCompra));
		assertTrue(cafe.getCompras().contains(compra));
	}

	@Test
	void RF11_clientePuedeComprarPlatillosValidosDuranteSuReserva() {
		// Arrange
		Reserva reserva = crearReservaActiva(2, false, false);
		cafe.crearBebida("admin", "clave", 10000, false, true);
		Platillo bebida = cafe.getMenu().iterator().next();

		// Act
		CompraPlatillo compra = cafe.generarCompraPlatillos("ana", "1234", reserva, List.of(bebida), 0);

		// Assert
		assertNotNull(compra);
		assertEquals(10000, compra.calcularTotal());
		assertTrue(cafe.getCompras().contains(compra));
	}

	@Test
	void RF11_compraPlatilloFallaSiBebidaAlcoholicaNoCumpleRestriccionEdad() {
		// Arrange
		Reserva reserva = crearReservaActiva(2, true, false);
		cafe.crearBebida("admin", "clave", 10000, true, false);
		Bebida cerveza = (Bebida) cafe.getMenu().iterator().next();

		// Act
		CompraPlatillo compra = cafe.generarCompraPlatillos("ana", "1234", reserva, List.of(cerveza), 0);

		// Assert
		assertNull(compra);
	}

	@Test
	void RF12_clienteConPuntosPuedeAplicarlosAUnaCompra() {
		// Arrange
		Cliente cliente = registrarClienteBasico();
		cliente.setPuntosFidelidad(50);

		// Act
		cliente.usarPuntos(20);

		// Assert
		assertEquals(30, cliente.getPuntosFidelidad());
		// TODO RF12: falta un metodo en Cafe/Compra para aplicar puntos como descuento monetario a una compra concreta.
	}

	@Test
	void RF29_clientePuedeAgregarJuegoAFavoritosSinDuplicarlo() {
		// Arrange
		Cliente cliente = registrarClienteBasico();
		JuegoFisico juego = crearJuegoPrestamo("Catan", true, true, 2, 4, false);
		JuegoDeMesa catan = juego.getJuegoBase();

		// Act
		cliente.agregarJuegoFav(catan);
		cliente.agregarJuegoFav(catan);

		// Assert
		assertEquals(1, cliente.getJuegosFavoritos().size());
		assertSame(catan, cliente.getJuegosFavoritos().get(0));
	}

	private Cliente registrarClienteBasico() {
		cafe.crearCliente("ana", "1234");
		return (Cliente) cafe.getUsuarios().stream()
				.filter(usuario -> usuario.getLogin().equals("ana"))
				.findFirst()
				.orElseThrow();
	}

	private Reserva crearReservaActiva(int personas, boolean hayMenores, boolean hayNinos) {
		registrarClienteBasico();
		cafe.crearReservacion("ana", "1234", personas, hayMenores, hayNinos);
		return cafe.getReservas().get(0);
	}

	private JuegoFisico crearJuegoPrestamo(String nombre, boolean puedenNinos, boolean puedenJovenes,
			int minJugadores, int maxJugadores, boolean ocupado) {
		JuegoDeMesa juego = crearJuegoBase(nombre, puedenNinos, puedenJovenes, minJugadores, maxJugadores, 100000);
		JuegoFisico copia = new JuegoFisico("nuevo", ocupado, juego);
		cafe.agregarJuegoPrestamo(copia);
		return copia;
	}

	private JuegoFisico crearJuegoCompra(String nombre, double precio) {
		JuegoDeMesa juego = crearJuegoBase(nombre, true, true, 2, 4, precio);
		JuegoFisico copia = new JuegoFisico("nuevo", false, juego);
		cafe.agregarJuegoCompra(copia);
		return copia;
	}

	private JuegoDeMesa crearJuegoBase(String nombre, boolean puedenNinos, boolean puedenJovenes,
			int minJugadores, int maxJugadores, double precio) {
		cafe.agregarJuegoCatalogo("admin", "clave", nombre, 2020, "Editorial", "Familiar",
				false, puedenNinos, puedenJovenes, minJugadores, maxJugadores, precio);
		return cafe.getCatalogoJuegos().stream()
				.filter(juego -> juego.getNombre().equals(nombre))
				.findFirst()
				.orElseThrow();
	}
}
