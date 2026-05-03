package test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import cafeteria.TicketNuevoPlatillo;
import control.Cafe;
import juego.JuegoDeMesa;
import juego.JuegoFisico;
import reservacion.Reserva;
import usuarios.Cocinero;
import usuarios.Empleado;
import usuarios.Mesero;
import usuarios.Usuario;

public class TestControl {

	private Path archivoTemporal;
	private Cafe cafe;

	@BeforeEach
	public void setUp() throws IOException {
		archivoTemporal = Files.createTempFile("cafe-control-test", ".txt");
		cafe = new Cafe(archivoTemporal.toString(), 12, "admin", "clave");
	}

	@AfterEach
	public void tearDown() throws IOException {
		Files.deleteIfExists(archivoTemporal);
	}

	@Test
	public void testAutenticacionAdministrador() {
		assertTrue(cafe.esAdmin("admin", "clave"));
		assertFalse(cafe.esAdmin("admin", "otra"));
		assertFalse(cafe.esAdmin("otro", "clave"));
		assertFalse(cafe.esAdmin("admin", null));
	}

	@Test
	public void testRetrieveEmpleadoAutenticadoSoloConCredencialesValidas() {
		Mesero mesero = new Mesero("mario", "mesero123");
		Cocinero cocinero = new Cocinero("sofia", "cocina123");
		agregarUsuario(cafe, mesero);
		agregarUsuario(cafe, cocinero);
		cafe.crearCliente("ana", "1234");

		Empleado empleado = cafe.autenticarEmpleado("mario", "mesero123");

		assertSame(mesero, empleado);
		assertSame(cocinero, cafe.autenticarEmpleado("sofia", "cocina123"));
		assertNull(cafe.autenticarEmpleado("mario", "otra"));
		assertNull(cafe.autenticarEmpleado("ana", "1234"));
	}

	@Test
	public void testRegistroYRutinaDeRetrieveClienteParaReservar() {
		assertTrue(cafe.crearMesa(4));
		assertTrue(cafe.crearCliente("ana", "1234"));
		assertFalse(cafe.crearCliente("ana", "repetida"));

		assertFalse(cafe.crearReservacion("ana", "mal", 2, false, false));
		assertTrue(cafe.crearReservacion("ana", "1234", 2, false, false));

		List<Reserva> reservas = reservas(cafe);
		assertEquals(1, reservas.size());
		assertEquals("ana", reservas.get(0).getCliente().getLogin());
		assertEquals(1, usuarios(cafe).size());
	}

	@Test
	public void testTicketsDePlatilloUsanAutenticacionSegmentada() {
		Cocinero cocinero = new Cocinero("sofia", "cocina123");
		agregarUsuario(cafe, cocinero);

		assertNull(cafe.solicitarCrearComida("sofia", "mal", 22000, List.of("mani")));

		TicketNuevoPlatillo ticket = cafe.solicitarCrearComida("sofia", "cocina123", 22000, List.of("mani"));

		assertNotNull(ticket);
		assertEquals(1, cafe.getTicketsPlatillosPendientes().size());
		assertFalse(cafe.aprobarTicketPlatillo("admin", "mal", ticket));
		assertTrue(cafe.aprobarTicketPlatillo("admin", "clave", ticket));
		assertTrue(ticket.isAprobado());
		assertEquals(0, cafe.getTicketsPlatillosPendientes().size());
		assertEquals(1, cafe.getMenu().size());
	}

	@Test
	public void testPersistenciaGuardaYCargaEstadoImportante() {
		agregarUsuario(cafe, new Mesero("mario", "mesero123"));
		assertTrue(cafe.crearMesa(4));
		assertTrue(cafe.crearCliente("ana", "1234"));
		assertTrue(cafe.crearReservacion("ana", "1234", 2, false, false));
		assertTrue(cafe.crearComida("admin", "clave", 18000, List.of("gluten")));
		assertTrue(cafe.agregarJuegoCatalogo("admin", "clave", "Catan", 1995, "Kosmos", "Estrategia", false,
				true, true, 3, 4, 120000));

		JuegoDeMesa catan = cafe.getCatalogoJuegos().iterator().next();
		assertTrue(cafe.agregarJuegoCompra(new JuegoFisico("nuevo", false, catan)));
		assertTrue(guardar(cafe));

		Cafe cafeCargado = new Cafe(archivoTemporal.toString());

		assertTrue(cafeCargado.esAdmin("admin", "clave"));
		assertNotNull(cafeCargado.autenticarEmpleado("mario", "mesero123"));
		assertEquals(2, usuarios(cafeCargado).size());
		assertEquals(1, reservas(cafeCargado).size());
		assertEquals(1, cafeCargado.getMenu().size());
		assertEquals(1, cafeCargado.getCatalogoJuegos().size());
		assertEquals(1, cafeCargado.getCatalogoCompra().size());
		assertFalse(cafeCargado.crearCliente("ana", "otra"));
	}

	private static boolean guardar(Cafe cafe) {
		try {
			Method save = Cafe.class.getDeclaredMethod("save");
			save.setAccessible(true);
			return (boolean) save.invoke(cafe);
		} catch (ReflectiveOperationException e) {
			throw new AssertionError("No se pudo invocar save", e);
		}
	}

	private static void agregarUsuario(Cafe cafe, Usuario usuario) {
		usuarios(cafe).add(usuario);
	}

	@SuppressWarnings("unchecked")
	private static List<Usuario> usuarios(Cafe cafe) {
		return (List<Usuario>) leerCampo(cafe, "usuarios");
	}

	@SuppressWarnings("unchecked")
	private static List<Reserva> reservas(Cafe cafe) {
		return (List<Reserva>) leerCampo(cafe, "reservas");
	}

	private static Object leerCampo(Cafe cafe, String nombreCampo) {
		try {
			Field campo = Cafe.class.getDeclaredField(nombreCampo);
			campo.setAccessible(true);
			return campo.get(cafe);
		} catch (ReflectiveOperationException e) {
			throw new AssertionError("No se pudo leer " + nombreCampo, e);
		}
	}
}
