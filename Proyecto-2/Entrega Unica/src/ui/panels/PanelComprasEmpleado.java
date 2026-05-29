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

public class PanelComprasEmpleado extends JPanel {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Cafe cafe;
    private String loginEmpleado;
    private String passwordEmpleado;
    private JTabbedPane tabbedPane;
    
    public PanelComprasEmpleado(Cafe cafe, String login, String password) {
        this.cafe = cafe;
        this.loginEmpleado = login;
        this.passwordEmpleado = password;
        setLayout(new BorderLayout());
        
        tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Comprar Juego", crearPanelCompraJuegos());
        tabbedPane.addTab("Comprar Platillo", crearPanelCompraPlatillos());
        
        add(tabbedPane, BorderLayout.CENTER);
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
        
        // Cargar juegos disponibles
        for (JuegoFisico jf : cafe.getCatalogoCompra()) {
            Object[] fila = {false, jf.getJuegoBase().getNombre(), 
                    "$" + jf.getJuegoBase().getPrecio(), jf.getEstado()};
            modeloTabla.addRow(fila);
        }
        
        JScrollPane scroll = new JScrollPane(tabla);
        panel.add(scroll, BorderLayout.CENTER);
        
        JButton btnComprar = new JButton("Comprar Seleccionados (20% descuento)");
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
            
            CompraJuegoMesa compra = cafe.generarCompraJuegos(loginEmpleado, passwordEmpleado, seleccionados, 20);
            if (compra != null) {
                double totalConDescuento = compra.calcularTotal();
                JOptionPane.showMessageDialog(panel, 
                    "Compra realizada con 20% de descuento.\nTotal: $" + totalConDescuento,
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);
                // Refrescar tabla
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
        
        panel.add(btnComprar, BorderLayout.SOUTH);
        return panel;
    }
    
    private JPanel crearPanelCompraPlatillos() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // Panel superior: seleccionar cliente y reserva
        JPanel panelSeleccion = new JPanel(new GridLayout(2, 2, 5, 5));
        JComboBox<String> cbClientes = new JComboBox<>();
        JComboBox<String> cbReservas = new JComboBox<>();
        
        // Cargar clientes con reserva activa
        for (Reserva r : cafe.getReservas()) {
            if (!r.isTerminada()) {
                String clienteLogin = r.getCliente().getLogin();
                if (cbClientes.getItemCount() == 0 || !cbClientes.getItemAt(0).equals(clienteLogin)) {
                    cbClientes.addItem(clienteLogin);
                }
            }
        }
        
        cbClientes.addActionListener(e -> {
            cbReservas.removeAllItems();
            String cliente = (String) cbClientes.getSelectedItem();
            for (Reserva r : cafe.getReservas()) {
                if (!r.isTerminada() && r.getCliente().getLogin().equals(cliente)) {
                    cbReservas.addItem("Mesa " + r.getMesaId() + " - " + r.getNumPersonas() + " personas");
                }
            }
        });
        
        panelSeleccion.add(new JLabel("Cliente:"));
        panelSeleccion.add(cbClientes);
        panelSeleccion.add(new JLabel("Reserva:"));
        panelSeleccion.add(cbReservas);
        
        panel.add(panelSeleccion, BorderLayout.NORTH);
        
        // Panel central: menú de platillos
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
        
        JButton btnComprar = new JButton("Comprar (20% descuento)");
        btnComprar.addActionListener(e -> {
            if (cbClientes.getSelectedItem() == null || cbReservas.getSelectedItem() == null) {
                JOptionPane.showMessageDialog(panel, "Seleccione un cliente y una reserva.");
                return;
            }
            
            String clienteLogin = (String) cbClientes.getSelectedItem();
            Reserva reserva = null;
            for (Reserva r : cafe.getReservas()) {
                if (!r.isTerminada() && r.getCliente().getLogin().equals(clienteLogin)) {
                    reserva = r;
                    break;
                }
            }
            
            if (reserva == null) {
                JOptionPane.showMessageDialog(panel, "Reserva no encontrada.");
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
            
            CompraPlatillo compra = cafe.generarCompraPlatillos(clienteLogin, passwordEmpleado, reserva, seleccionados, 20);
            if (compra != null) {
                JOptionPane.showMessageDialog(panel, 
                    "Compra realizada con 20% de descuento.\nTotal: $" + compra.calcularTotal(),
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(panel, "Error en la compra.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        panel.add(btnComprar, BorderLayout.SOUTH);
        return panel;
    }
}