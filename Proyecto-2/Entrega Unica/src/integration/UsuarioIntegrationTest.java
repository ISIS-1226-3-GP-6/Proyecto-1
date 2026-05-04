package integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import control.Cafe;
import juego.JuegoDeMesa;
import juego.Torneo;
import usuarios.Cliente;
import usuarios.Usuario;

class UsuarioIntegrationTest {

	private Path archivoTemporal;
	private Cafe cafe;

	@BeforeEach
	void setUp() throws IOException {
		archivoTemporal = Files.createTempFile("usuario-integration", ".txt");
		cafe = new Cafe(archivoTemporal.toString(), 20, "admin", "clave");
	}

	@AfterEach
	void cleanUp() throws IOException {
		Files.deleteIfExists(archivoTemporal);
	}

	@Test
	void RF02_usuarioRegistradoPuedeIniciarSesionConCredencialesCorrectas() {
		// Arrange
		cafe.crearCliente("ana", "1234");

		// Act
		boolean inicioSesion = cafe.iniciarSesion("ana", "1234");

		// Assert
		assertTrue(inicioSesion);
		assertEquals("ana", cafe.getUsuarioActivo().getLogin());
	}

	@Test
	void RF02_usuarioNoPuedeIniciarSesionConPasswordIncorrecto() {
		// Arrange
		cafe.crearCliente("ana", "1234");

		// Act
		boolean inicioSesion = cafe.iniciarSesion("ana", "mal");

		// Assert
		assertFalse(inicioSesion);
		assertNull(cafe.getUsuarioActivo());
	}

	@Test
	void RF03_usuarioConSesionActivaPuedeCerrarSesion() {
		// Arrange
		cafe.crearCliente("ana", "1234");
		cafe.iniciarSesion("ana", "1234");

		// Act
		cafe.cerrarSesion();

		// Assert
		assertNull(cafe.getUsuarioActivo());
	}

	@Test
	void RF13_usuarioPuedeInscribirseATorneoConCuposDisponibles() {
		// Arrange
		Cliente ana = registrarClienteBasico("ana", "1234");
		Torneo torneo = crearTorneoDisponible(5);

		// Act
		boolean inscrito = torneo.inscribir(ana, 2);

		// Assert
		assertTrue(inscrito);
		assertEquals(2, torneo.getCuposTaken());
		assertTrue(torneo.getUsuarios().contains(ana));
	}

	@Test
	void RF13_usuarioNoPuedeInscribirMasDeTresParticipantes() {
		// Arrange
		Cliente ana = registrarClienteBasico("ana", "1234");
		Torneo torneo = crearTorneoDisponible(5);

		// Act
		boolean inscrito = torneo.inscribir(ana, 4);

		// Assert
		assertFalse(inscrito);
		assertEquals(0, torneo.getCuposTaken());
	}

	@Test
	void RF13_usuarioNoPuedeInscribirseSiNoHayCuposSuficientes() {
		// Arrange
		Cliente ana = registrarClienteBasico("ana", "1234");
		Cliente bruno = registrarClienteBasico("bruno", "abcd");
		Torneo torneo = crearTorneoDisponible(3);

		// Act
		boolean primeraInscripcion = torneo.inscribir(ana, 3);
		boolean segundaInscripcion = torneo.inscribir(bruno, 1);

		// Assert
		assertTrue(primeraInscripcion);
		assertFalse(segundaInscripcion);
		assertEquals(3, torneo.getCuposTaken());
	}

	@Test
	void RF14_usuarioInscritoPuedeDesinscribirseYLiberarCupos() {
		// Arrange
		Cliente ana = registrarClienteBasico("ana", "1234");
		Torneo torneo = crearTorneoDisponible(5);
		torneo.inscribir(ana, 3);

		// Act
		boolean desinscrito = torneo.desinscribir(ana);

		// Assert
		assertTrue(desinscrito);
		assertEquals(0, torneo.getCuposTaken());
		assertFalse(torneo.getUsuarios().contains(ana));
	}

	@Test
	void RF15_sistemaDevuelveTorneosDisponiblesConInformacionBasica() {
		// Arrange
		Torneo torneo = crearTorneoDisponible(5);

		// Act
		Torneo torneoConsultado = cafe.getTorneosActivos().get(0);

		// Assert
		assertSame(torneo, torneoConsultado);
		assertEquals("Catan", torneoConsultado.getJuego().getNombre());
		assertTrue(torneoConsultado.isEsCompetitivo());
		assertEquals(5, torneoConsultado.getCupos());
	}

	private Cliente registrarClienteBasico(String login, String password) {
		cafe.crearCliente(login, password);
		return (Cliente) cafe.getUsuarios().stream()
				.filter(usuario -> usuario.getLogin().equals(login))
				.findFirst()
				.orElseThrow();
	}

	private Torneo crearTorneoDisponible(int cupos) {
		cafe.agregarJuegoCatalogo("admin", "clave", "Catan", 1995, "Kosmos", "Estrategia",
				false, true, true, 2, 4, 120000);
		cafe.crearTorneo("admin", "clave", true, 100, 25000, cupos, "Catan");
		return cafe.getTorneos().get(cafe.getTorneos().size() - 1);
	}
}
