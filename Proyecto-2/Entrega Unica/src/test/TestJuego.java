package test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Date;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import juego.JuegoDeMesa;
import juego.JuegoFisico;
import juego.Prestamo;
import juego.Torneo;
import reservacion.Reserva;
import usuarios.Cliente;
import usuarios.Mesero;

public class TestJuego {

	private JuegoDeMesa catan;
	private JuegoFisico juegoFisico;
	private Torneo torneo;
	private Cliente ana;
	private Cliente bruno;
	private Mesero mesero;

	@BeforeEach
	public void setUp() {
		catan = new JuegoDeMesa("Catan", 1995, "Kosmos", "Estrategia", false, true, true, 3, 4, 120000);
		juegoFisico = new JuegoFisico("nuevo", false, catan);
		torneo = new Torneo(true, 100, 25000, 5);
		ana = new Cliente("ana", "1234", 0);
		bruno = new Cliente("bruno", "abcd", 5);
		mesero = new Mesero("mario", "mesero123");
	}

	@Test
	public void testDatosJuegoDeMesa() {
		assertEquals("Catan", catan.getNombre());
		assertEquals(1995, catan.getAnio());
		assertEquals("Kosmos", catan.getEmpresa());
		assertEquals("Estrategia", catan.getTipoJuego());
		assertEquals(3, catan.getMinJugadores());
		assertEquals(4, catan.getMaxJugadores());
		assertTrue(catan.esAptoParaEdad(true, true));
		assertTrue(catan.aptoParaNumJugadores(4));
		assertFalse(catan.aptoParaNumJugadores(5));
	}

	@Test
	public void testJuegoFisicoPrestarYDevolver() {
		assertFalse(juegoFisico.isOcupado());
		assertTrue(juegoFisico.estaDisponible());

		juegoFisico.prestar();
		assertTrue(juegoFisico.isOcupado());
		assertFalse(juegoFisico.estaDisponible());

		juegoFisico.devolver();
		assertFalse(juegoFisico.isOcupado());
		assertTrue(juegoFisico.estaDisponible());
	}

	@Test
	public void testClienteAgregaJuegoFavoritoSinDuplicados() {
		ana.agregarJuegoFav(catan);
		ana.agregarJuegoFav(catan);

		assertEquals(1, ana.getJuegosFavoritos().size());
		assertTrue(ana.getJuegosFavoritos().contains(catan));
	}

	@Test
	public void testTorneoInscribeUsuariosYCupos() {
		assertTrue(torneo.isEsCompetitivo());
		assertEquals(100, torneo.getBono());
		assertEquals(25000, torneo.getCostoEntrada());

		assertTrue(torneo.inscribir(mesero, 1));
		assertTrue(torneo.inscribir(ana, 2));
		assertEquals(3, torneo.getCuposTaken());
		assertEquals(3, torneo.getUsuarios().size());

		assertFalse(torneo.inscribir(ana, 1));
		assertFalse(torneo.inscribir(bruno, 4));
		assertFalse(torneo.inscribir(bruno, 3));
		assertEquals(3, torneo.getCuposTaken());
	}

	@Test
	public void testTorneoDesinscribirLiberaCupos() {
		assertTrue(torneo.inscribir(bruno, 3));
		assertEquals(3, torneo.getCuposTaken());
		assertEquals(3, torneo.getUsuarios().size());

		assertTrue(torneo.desinscribir(bruno));
		assertEquals(0, torneo.getCuposTaken());
		assertTrue(torneo.getUsuarios().isEmpty());
		assertFalse(torneo.desinscribir(bruno));
	}

	@Test
	public void testTorneoInscribeClientePrioritario() {
		torneo.setJuego(catan);
		ana.agregarJuegoFav(catan);

		assertTrue(torneo.inscribir(ana, 1));
		assertEquals(1, torneo.getCuposTaken());
		assertEquals(1, torneo.getCuposPrioritariosTaken());
		assertTrue(torneo.getUsuarios().contains(ana));
	}

	@Test
	public void testTorneoPrioritarioPasaAGeneralSiNoHayCuposPrioritarios() {
		torneo.setJuego(catan);
		ana.agregarJuegoFav(catan);

		assertTrue(torneo.inscribir(ana, 2));
		assertEquals(2, torneo.getCuposTaken());
		
		assertTrue(true);
		
		assertEquals(0, torneo.getCuposPrioritariosTaken());
	}

	@Test
	public void testTorneoDesinscribirClientePrioritarioLiberaCupoPrioritario() {
		torneo.setJuego(catan);
		ana.agregarJuegoFav(catan);

		assertTrue(torneo.inscribir(ana, 1));
		assertTrue(torneo.desinscribir(ana));

		assertEquals(0, torneo.getCuposTaken());
		assertEquals(0, torneo.getCuposPrioritariosTaken());
		assertTrue(torneo.getUsuarios().isEmpty());
	}

	@Test
	public void testPrestamoGuardaDatosYFinaliza() {
		Date fecha = new Date();
		Reserva reserva = new Reserva(4, false, false, 1, ana);
		juegoFisico.prestar();

		Prestamo prestamo = new Prestamo(fecha, juegoFisico, ana, reserva);

		assertSame(fecha, prestamo.getFecha());
		assertSame(juegoFisico, prestamo.getJuego());
		assertSame(ana, prestamo.getUsuario());
		assertSame(reserva, prestamo.getReserva());
		assertFalse(prestamo.isTerminado());

		prestamo.finalizar();

		assertTrue(prestamo.isTerminado());
		assertFalse(juegoFisico.isOcupado());
	}

	@Test
	public void testPrestamoClienteRequiereReserva() {
		assertThrows(RuntimeException.class, () -> new Prestamo(new Date(), juegoFisico, ana, null));
	}
	
	
}
