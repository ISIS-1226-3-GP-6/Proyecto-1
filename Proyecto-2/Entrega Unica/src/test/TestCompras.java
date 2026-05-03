package test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import cafeteria.Bebida;
import cafeteria.Comida;
import cafeteria.Platillo;
import compras.CompraJuegoMesa;
import compras.CompraPlatillo;
import juego.JuegoDeMesa;
import juego.JuegoFisico;

public class TestCompras {

	private Comida hamburguesa;
	private Bebida limonada;
	private JuegoDeMesa catan;
	private JuegoDeMesa azul;
	private JuegoFisico copiaCatan;
	private JuegoFisico copiaAzul;

	@BeforeEach
	public void setUp() {
		hamburguesa = new Comida(25000, true);
		limonada = new Bebida(8000, true, false, false);
		catan = new JuegoDeMesa("Catan", 1995, "Kosmos", "Estrategia", false, true, true, 3, 4, 120000);
		azul = new JuegoDeMesa("Azul", 2017, "Plan B Games", "Abstracto", false, true, true, 2, 4, 90000);
		copiaCatan = new JuegoFisico("nuevo", false, catan);
		copiaAzul = new JuegoFisico("nuevo", false, azul);
	}

	@Test
	public void testCompraPlatilloConstructorDatosYSetters() {
		LocalDateTime fecha = LocalDateTime.of(2026, 5, 2, 10, 30);
		CompraPlatillo compra = new CompraPlatillo(10, 0, fecha);

		assertEquals(10, compra.getDescuento());
		assertEquals(0, compra.getTotal());
		assertSame(fecha, compra.getFechaHora());
		assertTrue(compra.getPlatillos().isEmpty());

		LocalDateTime nuevaFecha = LocalDateTime.of(2026, 5, 3, 12, 0);
		compra.setDescuento(15);
		compra.setFechaHora(nuevaFecha);

		assertEquals(15, compra.getDescuento());
		assertSame(nuevaFecha, compra.getFechaHora());
	}

	@Test
	public void testCompraPlatilloDefaultInicializaFechaYLista() {
		CompraPlatillo compra = new CompraPlatillo();

		assertEquals(0, compra.getDescuento());
		assertEquals(0, compra.getTotal());
		assertTrue(compra.getPlatillos().isEmpty());
		assertTrue(compra.getFechaHora() != null);
	}

	@Test
	public void testCompraPlatilloAgregaPlatillosYCalculaTotalConDescuento() {
		CompraPlatillo compra = new CompraPlatillo(10, 0, LocalDateTime.now());

		compra.agregarPlatillo(hamburguesa);
		compra.agregarPlatillo(limonada);

		assertEquals(2, compra.getPlatillos().size());
		assertTrue(compra.getPlatillos().contains(hamburguesa));
		assertEquals(29700, compra.calcularTotal());
		assertEquals(29700, compra.getTotal());
	}

	@Test
	public void testCompraPlatilloSinProductosTotalCero() {
		CompraPlatillo compra = new CompraPlatillo(25, 100, LocalDateTime.now());

		assertEquals(0, compra.calcularTotal());
		assertEquals(0, compra.getTotal());
	}

	@Test
	public void testCompraJuegoMesaConstructorDatosYSetters() {
		LocalDateTime fecha = LocalDateTime.of(2026, 5, 2, 15, 45);
		CompraJuegoMesa compra = new CompraJuegoMesa(5, 0, fecha);

		assertEquals(5, compra.getDescuento());
		assertEquals(0, compra.getTotal());
		assertSame(fecha, compra.getFechaHora());
		assertTrue(compra.getJuegos().isEmpty());

		compra.setDescuento(20);
		compra.setFechaHora(fecha.plusDays(1));

		assertEquals(20, compra.getDescuento());
		assertEquals(fecha.plusDays(1), compra.getFechaHora());
	}

	@Test
	public void testCompraJuegoMesaAgregaJuegosEIgnoraNulos() {
		CompraJuegoMesa compra = new CompraJuegoMesa();

		compra.agregarJuego(copiaCatan);
		compra.agregarJuego(null);

		assertEquals(1, compra.getJuegos().size());
		assertTrue(compra.getJuegos().contains(copiaCatan));
	}

	@Test
	public void testCompraJuegoMesaSetJuegos() {
		CompraJuegoMesa compra = new CompraJuegoMesa();
		List<JuegoFisico> juegos = new ArrayList<>();
		juegos.add(copiaCatan);
		juegos.add(copiaAzul);

		compra.setJuegos(juegos);

		assertSame(juegos, compra.getJuegos());
		assertEquals(2, compra.getJuegos().size());
	}

	@Test
	public void testCompraJuegoMesaCalculaTotalConDescuento() {
		CompraJuegoMesa compra = new CompraJuegoMesa(10, 0, LocalDateTime.now());

		compra.agregarJuego(copiaCatan);
		compra.agregarJuego(copiaAzul);

		assertEquals(189000, compra.calcularTotal());
	}

	@Test
	public void testCompraJuegoMesaSinJuegosTotalCero() {
		CompraJuegoMesa compra = new CompraJuegoMesa(15, 200, LocalDateTime.now());

		assertEquals(0, compra.calcularTotal());
	}

	@Test
	public void testPlatillosUsadosEnComprasMantienenDatos() {
		Platillo platillo = hamburguesa;

		assertEquals(25000, platillo.getPrecio());
		assertTrue(platillo.isAprobado());

		platillo.setPrecio(27000);
		platillo.setAprobado(false);

		assertEquals(27000, platillo.getPrecio());
		assertFalse(platillo.isAprobado());
	}
}
