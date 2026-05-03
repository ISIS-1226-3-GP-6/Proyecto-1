package test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Date;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import juego.JuegoDeMesa;
import juego.JuegoFisico;
import juego.Prestamo;
import reservacion.Mesa;
import reservacion.Reserva;
import usuarios.Cliente;

public class TestReservacion {

	private Cliente ana;
	private JuegoDeMesa catan;
	private JuegoFisico copiaCatan;
	private JuegoFisico copiaAzul;
	private JuegoFisico copiaCarcassonne;
	private Reserva reserva;

	@BeforeEach
	public void setUp() {
		ana = new Cliente("ana", "1234", 0);
		catan = new JuegoDeMesa("Catan", 1995, "Kosmos", "Estrategia", false, true, true, 3, 4, 120000);
		copiaCatan = new JuegoFisico("nuevo", false, catan);
		copiaAzul = new JuegoFisico("nuevo", false,
				new JuegoDeMesa("Azul", 2017, "Plan B Games", "Abstracto", false, true, true, 2, 4, 90000));
		copiaCarcassonne = new JuegoFisico("nuevo", false,
				new JuegoDeMesa("Carcassonne", 2000, "Hans im Gluck", "Familiar", false, true, true, 2, 5, 85000));
		reserva = new Reserva(4, true, false, 7, ana);
	}

	@Test
	public void testMesaDatosYCapacidad() {
		Mesa mesa = new Mesa(4, 10);

		assertEquals(4, mesa.getCapacidad());
		assertEquals(10, mesa.getId());
		assertTrue(mesa.sePuedeSentar(4));
		assertFalse(mesa.sePuedeSentar(5));

		mesa.setCapacidad(6);

		assertEquals(6, mesa.getCapacidad());
		assertTrue(mesa.sePuedeSentar(5));
	}

	@Test
	public void testMesaOcuparYDesocupar() {
		Mesa mesa = new Mesa(2, 1);

		assertTrue(mesa.sePuedeSentar(2));

		mesa.ocupar();
		assertFalse(mesa.sePuedeSentar(1));

		mesa.desocupar();
		assertTrue(mesa.sePuedeSentar(1));
	}

	@Test
	public void testReservaDatosInicialesYSetters() {
		assertEquals(4, reserva.getNumPersonas());
		assertTrue(reserva.isHayMenores());
		assertFalse(reserva.isHayNinos());
		assertFalse(reserva.isTerminada());
		assertEquals(7, reserva.getMesaId());
		assertSame(ana, reserva.getCliente());
		assertTrue(reserva.getPrestamosActivos().isEmpty());

		reserva.setNumPersonas(3);
		reserva.setHayMenores(false);
		reserva.setHayNinos(true);
		reserva.setMesaId(8);
		reserva.setTerminada(true);

		assertEquals(3, reserva.getNumPersonas());
		assertFalse(reserva.isHayMenores());
		assertTrue(reserva.isHayNinos());
		assertEquals(8, reserva.getMesaId());
		assertTrue(reserva.isTerminada());
	}

	@Test
	public void testReservaAgregaPrestamoActivo() {
		Prestamo prestamo = new Prestamo(new Date(), copiaCatan, ana, reserva);

		assertTrue(reserva.agregarPrestamo(prestamo));
		assertEquals(1, reserva.getPrestamosActivos().size());
		assertTrue(reserva.getPrestamosActivos().contains(prestamo));
	}

	@Test
	public void testReservaNoAgregaPrestamoNulo() {
		assertFalse(reserva.agregarPrestamo(null));
		assertTrue(reserva.getPrestamosActivos().isEmpty());
	}

	@Test
	public void testReservaNoPermiteMasDeDosPrestamosActivos() {
		Prestamo prestamo1 = new Prestamo(new Date(), copiaCatan, ana, reserva);
		Prestamo prestamo2 = new Prestamo(new Date(), copiaAzul, ana, reserva);
		Prestamo prestamo3 = new Prestamo(new Date(), copiaCarcassonne, ana, reserva);

		assertTrue(reserva.agregarPrestamo(prestamo1));
		assertTrue(reserva.agregarPrestamo(prestamo2));
		assertFalse(reserva.agregarPrestamo(prestamo3));
		assertEquals(2, reserva.getPrestamosActivos().size());
	}

	@Test
	public void testReservaTerminadaNoAgregaPrestamos() {
		reserva.setTerminada(true);
		Prestamo prestamo = new Prestamo(new Date(), copiaCatan, ana, reserva);

		assertFalse(reserva.agregarPrestamo(prestamo));
		assertTrue(reserva.getPrestamosActivos().isEmpty());
	}

	@Test
	public void testCerrarReservaTerminaPrestamosActivos() {
		Prestamo prestamo1 = new Prestamo(new Date(), copiaCatan, ana, reserva);
		Prestamo prestamo2 = new Prestamo(new Date(), copiaAzul, ana, reserva);
		copiaCatan.prestar();
		copiaAzul.prestar();
		reserva.agregarPrestamo(prestamo1);
		reserva.agregarPrestamo(prestamo2);

		reserva.cerrarReserva();

		assertTrue(reserva.isTerminada());
		assertTrue(prestamo1.isTerminado());
		assertTrue(prestamo2.isTerminado());
		assertFalse(copiaCatan.isOcupado());
		assertFalse(copiaAzul.isOcupado());
	}
}
