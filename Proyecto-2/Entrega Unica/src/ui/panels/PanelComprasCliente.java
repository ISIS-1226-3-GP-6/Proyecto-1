package ui.panels;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import control.Cafe;
import compras.CompraJuegoMesa;
import compras.CompraPlatillo;
import juego.JuegoFisico;
import reservacion.Reserva;
import cafeteria.Platillo;

public class PanelComprasCliente extends JPanel {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Cafe cafe;
    private String loginCliente;
    private String passwordCliente;
    private Reserva reservaActiva;
    private JTabbedPane tabbedPane;
    
    public PanelComprasCliente(Cafe cafe, String login, String password) {
        this.cafe = cafe;
        this.loginCliente = login;
        this.passwordCliente = password;
        setLayout(new BorderLayout());
        
        tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Comprar Juego", crearPanelCompraJuegos());
        tabbedPane.addTab("Comprar Platillo", crearPanelCompraPlatillos());
        
        add(tabbedPane, BorderLayout.CENTER);
    }
    
    public void setReservaActiva(Reserva reserva) {
        this.reservaActiva = reserva;
    }
    
    private JPanel crearPanelCompraJuegos() {
        JPanel panel = new JPanel(new BorderLayout());
        
        String[] columnas = {"Seleccionar", "Juego", "Precio", "Estado"};
        DefaultTableModel modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public Class<?> getColumnClass(int column) {
                return column == 0 ? Boolean.class : String.class;
            }
        };
        
        JTable tabla = new JTable(modeloTabla);
        
        for (JuegoFisico jf : cafe.getCatalogoCompra()) {
            Object[] fila = {false, jf.getJuegoBase().getNombre(), 
                    "$" + jf.getJuegoBase().getPrecio(), jf.getEstado()};
            modeloTabla.addRow(fila);
        }
        
        JScrollPane scroll = new JScrollPane(tabla);
        panel.add(scroll, BorderLayout.CENTER);
        
        JPanel panelInferior = new JPanel(new GridLayout(2, 1, 5, 5));
        
        JButton btnComprar = new JButton("Comprar Seleccionados");
        btnComprar.addActionListener(e -> {
            List<JuegoFisico> seleccionados = new ArrayList<>();
            for (int i = 0; i < modeloTabla.getRowCount(); i++) {
                if ((Boolean) modeloTabla.getValueAt(i, 0)) {
                    String nombre = (String) modeloTabla.getValueAt(i, 1);
                    cafe.getCatalogoCompra().stream()
                        .filter(j -> j.getJuegoBase().getNombre().equals(nombre))
                        .findFirst()
                        .ifPresent(seleccionados::add);
                }
            }
            
            if (seleccionados.isEmpty()) {
                JOptionPane.showMessageDialog(panel, "Seleccione al menos un juego.");
                return;
            }
            
            CompraJuegoMesa compra = cafe.generarCompraJuegos(loginCliente, passwordCliente, seleccionados, 0);
            if (compra != null) {
                JOptionPane.showMessageDialog(panel, 
                    "Compra realizada.\nTotal: $" + compra.calcularTotal() +
                    "\nSe agregaron puntos de fidelidad.",
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);
                modeloTabla.setRowCount(0);
                for (JuegoFisico jf : cafe.getCatalogoCompra()) {
                    Object[] fila = {false, jf.getJuegoBase().getNombre(), 
                            "$" + jf.getJuegoBase().getPrecio(), jf.getEstado()};
                    modeloTabla.addRow(fila);
                }
            } else {
                JOptionPane.showMessageDialog(panel, "Error al realizar la compra.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        JTextArea txtInfo = new JTextArea(3, 40);
        txtInfo.setEditable(false);
        txtInfo.setText("Los juegos comprados se descuentan del inventario.\nSe acumulan puntos de fidelidad (1% del valor).");
        
        panelInferior.add(btnComprar);
        panelInferior.add(txtInfo);
        panel.add(panelInferior, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel crearPanelCompraPlatillos() {
        JPanel panel = new JPanel(new BorderLayout());
        
        String[] columnas = {"Seleccionar", "Platillo", "Precio", "Cantidad"};
        DefaultTableModel modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public Class<?> getColumnClass(int column) {
                if (column == 0) return Boolean.class;
                if (column == 3) return Integer.class;
                return String.class;
            }
        };
        
        JTable tabla = new JTable(modeloTabla);
        
        for (Platillo p : cafe.getMenu()) {
            Object[] fila = {false, p.getClass().getSimpleName(), "$" + p.getPrecio(), 1};
            modeloTabla.addRow(fila);
        }
        
        JScrollPane scroll = new JScrollPane(tabla);
        panel.add(scroll, BorderLayout.CENTER);
        
        JPanel panelInferior = new JPanel(new GridLayout(3, 1, 5, 5));
        
        JButton btnComprar = new JButton("Comprar Platillos");
        btnComprar.addActionListener(e -> {
            if (reservaActiva == null) {
                JOptionPane.showMessageDialog(panel, "No tienes una reserva activa.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            List<Platillo> seleccionados = new ArrayList<>();
            for (int i = 0; i < modeloTabla.getRowCount(); i++) {
                if ((Boolean) modeloTabla.getValueAt(i, 0)) {
                    String nombre = (String) modeloTabla.getValueAt(i, 1);
                    int cantidad = (int) modeloTabla.getValueAt(i, 3);
                    for (Platillo p : cafe.getMenu()) {
                        if (p.getClass().getSimpleName().equals(nombre)) {
                            for (int j = 0; j < cantidad; j++) {
                                seleccionados.add(p);
                            }
                            break;
                        }
                    }
                }
            }
            
            if (seleccionados.isEmpty()) {
                JOptionPane.showMessageDialog(panel, "Seleccione al menos un platillo.");
                return;
            }
            
            CompraPlatillo compra = cafe.generarCompraPlatillos(loginCliente, passwordCliente, reservaActiva, seleccionados, 0);
            if (compra != null) {
                JOptionPane.showMessageDialog(panel, 
                    "Compra realizada.\nTotal: $" + compra.calcularTotal() +
                    "\nSe agregaron puntos de fidelidad.",
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(panel, "Error en la compra (restricción de edad o juego de acción).", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        JTextArea txtInfo = new JTextArea(3, 40);
        txtInfo.setEditable(false);
        txtInfo.setText("Para comprar platillos necesitas una reserva activa.\n" +
                "Bebidas alcohólicas: solo para mayores de edad.\n" +
                "Bebidas calientes: no permitidas con juegos de acción.");
        
        panelInferior.add(btnComprar);
        panelInferior.add(txtInfo);
        panel.add(panelInferior, BorderLayout.SOUTH);
        
        return panel;
    }
}