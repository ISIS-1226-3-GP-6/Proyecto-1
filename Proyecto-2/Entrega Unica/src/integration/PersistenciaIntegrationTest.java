package integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import control.Cafe;
import juego.JuegoDeMesa;
import juego.JuegoFisico;
import juego.Torneo;

class PersistenciaIntegrationTest {

	private Path archivoTemporal;

	@BeforeEach
	void setUp() throws IOException {
		archivoTemporal = Files.createTempFile("persistencia-integration", ".txt");
		Files.deleteIfExists(archivoTemporal);
	}

	@AfterEach
	void cleanUp() throws IOException {
		Files.deleteIfExists(archivoTemporal);
	}

	@Test
	void persistenciaGuardaYCargaUsuariosReservasCatalogosYTorneos() {
		// Arrange
		Cafe cafe = new Cafe(archivoTemporal.toString(), 12, "admin", "clave");
		cafe.crearMesa(4);
		cafe.crearCliente("ana", "1234");
		cafe.crearReservacion("ana", "1234", 2, false, false);
		cafe.crearMesero("admin", "clave", "mario", "pass");
		cafe.agregarJuegoCatalogo("admin", "clave", "Catan", 1995, "Kosmos", "Estrategia",
				false, true, true, 2, 4, 120000);
		JuegoDeMesa catan = cafe.getCatalogoJuegos().iterator().next();
		cafe.agregarJuegoCompra(new JuegoFisico("nuevo", false, catan));
		cafe.crearTorneo("admin", "clave", true, 100, 25000, 5, "Catan");

		// Act
		boolean guardado = cafe.save();
		Cafe cafeCargado = new Cafe(archivoTemporal.toString());

		// Assert
		assertTrue(guardado);
		assertTrue(cafeCargado.esAdmin("admin", "clave"));
		assertNotNull(cafeCargado.autenticarEmpleado("mario", "pass"));
		assertEquals(2, cafeCargado.getUsuarios().size());
		assertEquals(1, cafeCargado.getReservas().size());
		assertEquals(1, cafeCargado.getCatalogoJuegos().size());
		assertEquals(1, cafeCargado.getCatalogoCompra().size());
		assertEquals(1, cafeCargado.getTorneosActivos().size());
		assertFalse(cafeCargado.iniciarSesion("ana", "mal"));

		Torneo torneo = cafeCargado.getTorneosActivos().get(0);
		assertEquals("Catan", torneo.getJuego().getNombre());
	}
}
