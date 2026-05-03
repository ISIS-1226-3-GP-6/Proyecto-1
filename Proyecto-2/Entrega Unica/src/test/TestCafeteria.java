package test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import cafeteria.Bebida;
import cafeteria.Comida;
import cafeteria.Platillo;
import cafeteria.TicketNuevoPlatillo;
import reservacion.Reserva;
import usuarios.Cliente;

public class TestCafeteria {

	private Cliente ana;
	private Comida hamburguesa;
	private Bebida cerveza;
	private Bebida cafe;

	@BeforeEach
	public void setUp() {
		ana = new Cliente("ana", "1234", 0);
		hamburguesa = new Comida(25000, false);
		cerveza = new Bebida(12000, true, true, false);
		cafe = new Bebida(7000, true, false, true);
	}

	@Test
	public void testPlatilloDatosYSettersDesdeSubclase() {
		Platillo platillo = hamburguesa;

		assertEquals(25000, platillo.getPrecio());
		assertFalse(platillo.isAprobado());

		platillo.setPrecio(28000);
		platillo.setAprobado(true);

		assertEquals(28000, platillo.getPrecio());
		assertTrue(platillo.isAprobado());
	}

	@Test
	public void testComidaAlergenosConstructorListaNula() {
		Comida comida = new Comida(18000, true, null);

		assertTrue(comida.getAlergenos().isEmpty());
		assertFalse(comida.contieneAlergeno("gluten"));
	}

	@Test
	public void testComidaAgregarAlergenoSinDuplicados() {
		hamburguesa.agregarAlergeno("gluten");
		hamburguesa.agregarAlergeno("gluten");
		hamburguesa.agregarAlergeno("lactosa");

		assertEquals(2, hamburguesa.getAlergenos().size());
		assertTrue(hamburguesa.contieneAlergeno("gluten"));
		assertTrue(hamburguesa.contieneAlergeno("lactosa"));
		assertFalse(hamburguesa.contieneAlergeno("mani"));
	}

	@Test
	public void testComidaSetAlergenos() {
		List<String> alergenos = new ArrayList<>();
		alergenos.add("mani");
		alergenos.add("soya");

		hamburguesa.setAlergenos(alergenos);

		assertSame(alergenos, hamburguesa.getAlergenos());
		assertTrue(hamburguesa.contieneAlergeno("mani"));
		assertEquals(2, hamburguesa.getAlergenos().size());
	}

	@Test
	public void testBebidaDatosYSetters() {
		assertTrue(cerveza.isAlcoholica());
		assertFalse(cerveza.isCaliente());

		cerveza.setAlcoholica(false);
		cerveza.setCaliente(true);

		assertFalse(cerveza.isAlcoholica());
		assertTrue(cerveza.isCaliente());
	}

	@Test
	public void testBebidaAlcoholicaNoPermitidaConMenores() {
		Reserva reservaConMenores = new Reserva(4, true, false, 1, ana);
		Reserva reservaSinMenores = new Reserva(4, false, false, 2, ana);

		assertFalse(cerveza.esPermitidaEn(reservaConMenores));
		assertTrue(cerveza.esPermitidaEn(reservaSinMenores));
		assertTrue(cafe.esPermitidaEn(reservaConMenores));
	}

	@Test
	public void testTicketNuevoPlatilloDatosYSetters() {
		TicketNuevoPlatillo ticket = new TicketNuevoPlatillo(hamburguesa);

		assertFalse(ticket.isAprobado());
		assertSame(hamburguesa, ticket.getPlatillo());

		ticket.setAprobado(true);
		ticket.setPlatillo(cafe);

		assertTrue(ticket.isAprobado());
		assertSame(cafe, ticket.getPlatillo());
	}

	@Test
	public void testTicketNuevoPlatilloAprobarApruebaTicketYPlatillo() {
		TicketNuevoPlatillo ticket = new TicketNuevoPlatillo(hamburguesa);

		ticket.aprobar();

		assertTrue(ticket.isAprobado());
		assertTrue(hamburguesa.isAprobado());
	}

	@Test
	public void testTicketNuevoPlatilloRechazarRechazaTicketYPlatillo() {
		TicketNuevoPlatillo ticket = new TicketNuevoPlatillo(cafe);
		ticket.aprobar();

		ticket.rechazar();

		assertFalse(ticket.isAprobado());
		assertFalse(cafe.isAprobado());
	}

	@Test
	public void testTicketNuevoPlatilloConPlatilloNuloNoFalla() {
		TicketNuevoPlatillo ticket = new TicketNuevoPlatillo(null);

		ticket.aprobar();
		assertTrue(ticket.isAprobado());

		ticket.rechazar();
		assertFalse(ticket.isAprobado());
	}
}
