package integration;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import control.Cafe;
import juego.JuegoDeMesa;
import usuarios.Mesero;

class MeseroIntegrationTest {

	private Path archivoTemporal;
	private Cafe cafe;

	@BeforeEach
	void setUp() throws IOException {
		archivoTemporal = Files.createTempFile("mesero-integration", ".txt");
		cafe = new Cafe(archivoTemporal.toString(), 12, "admin", "clave");
	}

	@AfterEach
	void cleanUp() throws IOException {
		Files.deleteIfExists(archivoTemporal);
	}

	@Test
	void RF21_siMeseroConoceJuegoDificilSeRegistraAyuda() {
		// Arrange
		cafe.crearMesero("admin", "clave", "mario", "pass");
		Mesero mario = (Mesero) cafe.autenticarEmpleado("mario", "pass");
		JuegoDeMesa catan = new JuegoDeMesa("Catan", 1995, "Kosmos", "Estrategia",
				true, true, true, 3, 4, 120000);
		mario.agregarJuegoConocido("Catan");

		// Act
		boolean ayuda = cafe.registrarAyudaMesero("mario", "pass", catan);

		// Assert
		assertTrue(ayuda);
	}

	@Test
	void RF21_siNingunMeseroConoceJuegoDificilRetornaAdvertenciaControlada() {
		// Arrange
		cafe.crearMesero("admin", "clave", "mario", "pass");
		JuegoDeMesa catan = new JuegoDeMesa("Catan", 1995, "Kosmos", "Estrategia",
				true, true, true, 3, 4, 120000);

		// Act
		boolean ayuda = cafe.registrarAyudaMesero("mario", "pass", catan);

		// Assert
		assertFalse(ayuda);
		// TODO RF21: falta una entidad/historial de ayudas; por ahora Cafe valida y retorna boolean.
	}
}
