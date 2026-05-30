package ui.panels;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Comparator;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import compras.Compra;
import compras.CompraJuegoMesa;
import compras.CompraPlatillo;
import control.Cafe;
import juego.JuegoDeMesa;
import juego.JuegoFisico;
import reservacion.Reserva;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

public class PanelAdministradorGraficas extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final double IVA = 0.19;

	private Cafe cafe;
	private JComboBox<JuegoDeMesa> cbJuegos;
	private JTextField txtFechaReservas;
	private JTextField txtFechaInicioCompras;
	private JPanel panelGraficaJuego;
	private JPanel panelGraficaReservas;
	private JPanel panelGraficaCompras;

	public PanelAdministradorGraficas(Cafe cafe) {
		this.cafe = cafe;
		setLayout(new BorderLayout());

		JPanel panelPrincipal = new JPanel();
		panelPrincipal.setLayout(new BoxLayout(panelPrincipal, BoxLayout.Y_AXIS));
		panelPrincipal.add(crearFormularioJuego());
		panelPrincipal.add(crearFormularioComprasPeriodo());
		panelPrincipal.add(crearFormularioReservasSemana());

		add(new JScrollPane(panelPrincipal), BorderLayout.CENTER);
	}

	private JPanel crearFormularioJuego() {
		JPanel panel = crearSeccion("Grafica de juegos", 420);
		JPanel form = new JPanel(new GridLayout(0, 2, 5, 5));

		cbJuegos = new JComboBox<>();
		cafe.getCatalogoJuegos().stream()
				.sorted(Comparator.comparing(JuegoDeMesa::getNombre))
				.forEach(cbJuegos::addItem);
		cbJuegos.setRenderer(new DefaultListCellRenderer() {
			private static final long serialVersionUID = 1L;

			@Override
			public java.awt.Component getListCellRendererComponent(JList<?> list, Object value, int index,
					boolean isSelected, boolean cellHasFocus) {
				super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
				if (value instanceof JuegoDeMesa) {
					setText(((JuegoDeMesa) value).getNombre());
				}
				return this;
			}
		});

		JButton btnGenerar = new JButton("Generar grafica");
		btnGenerar.addActionListener(e -> generarGraficaJuego());

		form.add(new JLabel("Juego:"));
		form.add(cbJuegos);
		form.add(new JLabel(""));
		form.add(btnGenerar);

		panelGraficaJuego = crearPanelGrafica("Espacio reservado para grafica JFreeChart de juegos.", 320);

		panel.add(form, BorderLayout.NORTH);
		panel.add(panelGraficaJuego, BorderLayout.CENTER);
		return panel;
	}

	private JPanel crearFormularioReservasSemana() {
		JPanel panel = crearSeccion("Grafica de reservas por semana", 420);
		JPanel form = new JPanel(new GridLayout(0, 2, 5, 5));

		txtFechaReservas = new JTextField(LocalDate.now().toString());
		JButton btnGenerar = new JButton("Generar grafica");
		btnGenerar.addActionListener(e -> generarGraficaReservasSemana());

		form.add(new JLabel("Lunes de la semana (yyyy-MM-dd):"));
		form.add(txtFechaReservas);
		form.add(new JLabel(""));
		form.add(btnGenerar);

		panelGraficaReservas = crearPanelGrafica("Espacio reservado para grafica JFreeChart de reservas semanales.", 320);

		panel.add(form, BorderLayout.NORTH);
		panel.add(panelGraficaReservas, BorderLayout.CENTER);
		return panel;
	}

	private JPanel crearFormularioComprasPeriodo() {
		JPanel panel = crearSeccion("Grafica de ventas por periodo", 420);
		JPanel form = new JPanel(new GridLayout(0, 2, 5, 5));

		txtFechaInicioCompras = new JTextField(LocalDate.now().toString());
		JButton btnGenerar = new JButton("Generar grafica");
		btnGenerar.addActionListener(e -> generarGraficaComprasPeriodo());

		form.add(new JLabel("Fecha inicial (yyyy-MM-dd):"));
		form.add(txtFechaInicioCompras);
		form.add(new JLabel(""));
		form.add(btnGenerar);

		panelGraficaCompras = crearPanelGrafica("Espacio reservado para grafica JFreeChart de ventas por periodo.", 320);

		panel.add(form, BorderLayout.NORTH);
		panel.add(panelGraficaCompras, BorderLayout.CENTER);
		return panel;
	}

	private JPanel crearSeccion(String titulo) {
		return crearSeccion(titulo, 230);
	}

	private JPanel crearSeccion(String titulo, int alto) {
		JPanel panel = new JPanel(new BorderLayout(10, 10));
		panel.setBorder(BorderFactory.createTitledBorder(titulo));
		panel.setPreferredSize(new Dimension(760, alto));
		panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, alto));
		return panel;
	}

	private JPanel crearPanelGrafica(String texto) {
		return crearPanelGrafica(texto, 150);
	}

	private JPanel crearPanelGrafica(String texto, int alto) {
		JPanel panel = new JPanel(new BorderLayout());
		panel.setPreferredSize(new Dimension(760, alto));
		panel.setMinimumSize(new Dimension(500, alto));
		panel.setBorder(BorderFactory.createEtchedBorder());
		panel.add(new JLabel(texto, JLabel.CENTER), BorderLayout.CENTER);
		return panel;
	}

	private void generarGraficaJuego() {
		JuegoDeMesa juego = (JuegoDeMesa) cbJuegos.getSelectedItem();
		if (juego == null) {
			JOptionPane.showMessageDialog(this, "No hay juegos disponibles para graficar.");
			return;
		}

		int totalPrestamo = contarCopiasDelJuego(cafe.getCatalogoPrestamo(), juego);
		int totalVenta = contarCopiasDelJuego(cafe.getCatalogoCompra(), juego);

		if (totalPrestamo + totalVenta == 0) {
			JOptionPane.showMessageDialog(this,
					"No hay copias de '" + juego.getNombre() + "' en prestamo o venta para graficar.");
			return;
		}

		ChartPanel grafica = crearGraficaTortaJuegos(juego, totalPrestamo, totalVenta);
		panelGraficaJuego.removeAll();
		panelGraficaJuego.add(grafica, BorderLayout.CENTER);
		panelGraficaJuego.revalidate();
		panelGraficaJuego.repaint();
	}

	private void generarGraficaReservasSemana() {
		try {
			LocalDate inicioSemana = LocalDate.parse(txtFechaReservas.getText().trim());
			if (inicioSemana.getDayOfWeek() != DayOfWeek.MONDAY) {
				JOptionPane.showMessageDialog(this, "La fecha debe ser un lunes.",
						"Error", JOptionPane.ERROR_MESSAGE);
				return;
			}

			LocalDate finSemana = inicioSemana.plusDays(6);

			ChartPanel grafica = crearGraficaLineasReservas(inicioSemana, finSemana);
			panelGraficaReservas.removeAll();
			panelGraficaReservas.add(grafica, BorderLayout.CENTER);
			panelGraficaReservas.revalidate();
			panelGraficaReservas.repaint();
		} catch (DateTimeParseException ex) {
			JOptionPane.showMessageDialog(this, "Ingrese un lunes valido con formato yyyy-MM-dd.",
					"Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	private void generarGraficaComprasPeriodo() {
		try {
			LocalDate inicio = LocalDate.parse(txtFechaInicioCompras.getText().trim());
			LocalDate fin = inicio.plusDays(4);

			ChartPanel grafica = crearGraficaBarrasVentas(inicio, fin);
			panelGraficaCompras.removeAll();
			panelGraficaCompras.add(grafica, BorderLayout.CENTER);
			panelGraficaCompras.revalidate();
			panelGraficaCompras.repaint();
		} catch (DateTimeParseException ex) {
			JOptionPane.showMessageDialog(this, "Ingrese fechas validas con formato yyyy-MM-dd.",
					"Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	private LocalDate obtenerInicioSemana(LocalDate fecha) {
		int desplazamiento = Math.floorMod(fecha.getDayOfWeek().getValue() - DayOfWeek.MONDAY.getValue(), 7);
		return fecha.minusDays(desplazamiento);
	}

	private void mostrarPlaceholder(JPanel panelGrafica, String texto) {
		panelGrafica.removeAll();
		panelGrafica.add(new JLabel(texto, JLabel.CENTER), BorderLayout.CENTER);
		panelGrafica.revalidate();
		panelGrafica.repaint();
	}

	private int contarCopiasDelJuego(java.util.List<JuegoFisico> catalogo, JuegoDeMesa juego) {
		int total = 0;
		for (JuegoFisico copia : catalogo) {
			if (copia != null && esMismoJuego(copia.getJuegoBase(), juego)) {
				total++;
			}
		}
		return total;
	}

	private boolean esMismoJuego(JuegoDeMesa copiaBase, JuegoDeMesa juegoSeleccionado) {
		return copiaBase != null
				&& copiaBase.getNombre().equals(juegoSeleccionado.getNombre())
				&& copiaBase.getAnio() == juegoSeleccionado.getAnio()
				&& copiaBase.getEmpresa().equals(juegoSeleccionado.getEmpresa());
	}

	private ChartPanel crearGraficaTortaJuegos(JuegoDeMesa juego, int totalPrestamo, int totalVenta) {
		DefaultPieDataset dataset = new DefaultPieDataset();
		dataset.setValue("Prestamo", totalPrestamo);
		dataset.setValue("Venta", totalVenta);

		JFreeChart chart = ChartFactory.createPieChart(
				juego.getNombre() + ": prestamo vs venta",
				dataset,
				true,
				true,
				false);

		ChartPanel panel = new ChartPanel(chart);
		panel.setPreferredSize(new Dimension(760, 320));
		panel.setMinimumDrawWidth(480);
		panel.setMinimumDrawHeight(260);
		panel.setMaximumDrawWidth(1200);
		panel.setMaximumDrawHeight(800);
		return panel;
	}

	private ChartPanel crearGraficaBarrasVentas(LocalDate inicio, LocalDate fin) {
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();

		for (int i = 0; i < 5; i++) {
			LocalDate dia = inicio.plusDays(i);
			dataset.addValue(0, "Cafeteria", dia.toString());
			dataset.addValue(0, "Juegos", dia.toString());
		}

		for (Compra compra : cafe.getCompras()) {
			if (compra == null || compra.getFechaHora() == null) {
				continue;
			}

			LocalDate fechaCompra = compra.getFechaHora().toLocalDate();
			if (fechaCompra.isBefore(inicio) || fechaCompra.isAfter(fin)) {
				continue;
			}

			double totalSinImpuestos = descontarImpuestos(compra.calcularTotal());
			String dia = fechaCompra.toString();

			if (compra instanceof CompraPlatillo) {
				dataset.incrementValue(totalSinImpuestos, "Cafeteria", dia);
			} else if (compra instanceof CompraJuegoMesa) {
				dataset.incrementValue(totalSinImpuestos, "Juegos", dia);
			}
		}

		JFreeChart chart = ChartFactory.createBarChart(
				"Ventas sin impuestos: cafeteria vs juegos",
				"Dia",
				"Ventas sin impuestos",
				dataset,
				PlotOrientation.VERTICAL,
				true,
				true,
				false);

		ChartPanel panel = new ChartPanel(chart);
		panel.setPreferredSize(new Dimension(760, 320));
		panel.setMinimumDrawWidth(480);
		panel.setMinimumDrawHeight(260);
		panel.setMaximumDrawWidth(1200);
		panel.setMaximumDrawHeight(800);
		return panel;
	}

	private double descontarImpuestos(double totalConImpuestos) {
		return totalConImpuestos / (1 + IVA);
	}

	private ChartPanel crearGraficaLineasReservas(LocalDate inicioSemana, LocalDate finSemana) {
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();

		for (int i = 0; i < 7; i++) {
			LocalDate dia = inicioSemana.plusDays(i);
			dataset.addValue(contarReservasDia(dia), "Reservas", dia.toString());
		}

		JFreeChart chart = ChartFactory.createLineChart(
				"Evolucion de reservas",
				"Dia",
				"Numero de reservas",
				dataset,
				PlotOrientation.VERTICAL,
				true,
				true,
				false);

		ChartPanel panel = new ChartPanel(chart);
		panel.setPreferredSize(new Dimension(760, 320));
		panel.setMinimumDrawWidth(480);
		panel.setMinimumDrawHeight(260);
		panel.setMaximumDrawWidth(1200);
		panel.setMaximumDrawHeight(800);
		return panel;
	}

	private int contarReservasDia(LocalDate dia) {
		int total = 0;
		for (Reserva reserva : cafe.getReservas()) {
			if (reserva != null && reserva.getFechaHora() != null
					&& reserva.getFechaHora().toLocalDate().equals(dia)) {
				total++;
			}
		}
		return total;
	}

}
