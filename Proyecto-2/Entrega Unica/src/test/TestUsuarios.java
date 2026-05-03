package test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import juego.JuegoDeMesa;
import usuarios.Cliente;
import usuarios.Cocinero;
import usuarios.Empleado;
import usuarios.Mesero;

public class TestUsuarios {

	private Cliente ana;
	private Mesero mesero;
	private Cocinero cocinero;
	private JuegoDeMesa catan;
	private JuegoDeMesa uno;

	@BeforeEach
	public void setUp() {
		ana = new Cliente("ana", "1234", 10);
		mesero = new Mesero("mario", "mesero123");
		cocinero = new Cocinero("carlos", "cocinero123");
		catan = new JuegoDeMesa("Catan", 1995, "Kosmos", "Estrategia", true, true, true, 3, 4, 120000);
		uno = new JuegoDeMesa("Uno", 1971, "Mattel", "Cartas", false, true, true, 2, 10, 30000);
	}

	@Test
	public void testUsuarioDatosYAutenticacion() {
		assertEquals("ana", ana.getLogin());
		assertEquals("1234", ana.getPassword());
		assertTrue(ana.autenticacion("1234"));
		assertFalse(ana.autenticacion("abcd"));
		assertFalse(ana.autenticacion(null));

		ana.setLogin("ana2");
		ana.setPassword("nuevo");

		assertEquals("ana2", ana.getLogin());
		assertEquals("nuevo", ana.getPassword());
		assertTrue(ana.autenticacion("nuevo"));
	}

	@Test
	public void testClientePuntosFidelidad() {
		assertEquals(10, ana.getPuntosFidelidad());

		ana.agregarPuntos(5000);
		assertEquals(60, ana.getPuntosFidelidad());

		ana.usarPuntos(15);
		assertEquals(45, ana.getPuntosFidelidad());

		ana.usarPuntos(0);
		ana.usarPuntos(100);
		assertEquals(45, ana.getPuntosFidelidad());

		ana.setPuntosFidelidad(20);
		assertEquals(20, ana.getPuntosFidelidad());
	}

	@Test
	public void testClienteJuegosFavoritos() {
		assertTrue(ana.getJuegosFavoritos().isEmpty());

		ana.agregarJuegoFav(catan);
		ana.agregarJuegoFav(catan);
		ana.agregarJuegoFav(null);

		assertEquals(1, ana.getJuegosFavoritos().size());
		assertTrue(ana.getJuegosFavoritos().contains(catan));
	}

	@Test
	public void testClienteSetJuegosFavoritos() {
		List<JuegoDeMesa> favoritos = new ArrayList<>();
		favoritos.add(catan);
		favoritos.add(uno);

		ana.setJuegosFavoritos(favoritos);

		assertSame(favoritos, ana.getJuegosFavoritos());
		assertEquals(2, ana.getJuegosFavoritos().size());
	}

	@Test
	public void testMeseroConstructoresYJuegosConocidos() {
		assertTrue(mesero.getDificilesConocidos().isEmpty());

		mesero.agregarJuegoConocido("Catan");
		mesero.agregarJuegoConocido("Catan");

		assertEquals(1, mesero.getDificilesConocidos().size());
		assertTrue(mesero.getDificilesConocidos().contains("Catan"));
	}

	@Test
	public void testMeseroConstructorConListaNulaCreaListaVacia() {
		Mesero meseroSinLista = new Mesero("luisa", "pass", null);

		assertTrue(meseroSinLista.getDificilesConocidos().isEmpty());
	}

	@Test
	public void testMeseroSetDificilesConocidos() {
		List<String> conocidos = new ArrayList<>();
		conocidos.add("Catan");

		mesero.setDificilesConocidos(conocidos);

		assertSame(conocidos, mesero.getDificilesConocidos());
		assertEquals(1, mesero.getDificilesConocidos().size());
	}

	@Test
	public void testMeseroPuedeExplicarSoloJuegosDificilesConocidos() {
		mesero.agregarJuegoConocido("Catan");
		mesero.agregarJuegoConocido("Uno");

		assertTrue(mesero.puedeExplicar(catan));
		assertFalse(mesero.puedeExplicar(uno));
		assertFalse(mesero.puedeExplicar(null));
	}

	@Test
	public void testCocineroEsEmpleadoYAutentica() {
		assertTrue(cocinero instanceof Empleado);
		assertEquals("carlos", cocinero.getLogin());
		assertTrue(cocinero.autenticacion("cocinero123"));
		assertFalse(cocinero.autenticacion("otra"));
	}
}
