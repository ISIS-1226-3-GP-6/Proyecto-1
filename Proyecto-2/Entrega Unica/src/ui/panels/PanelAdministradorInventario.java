package ui.panels;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import control.Cafe;
import juego.JuegoDeMesa;
import juego.JuegoFisico;

public class PanelAdministradorInventario extends JPanel {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Cafe cafe;
    private JTabbedPane tabbedPane;
    private DefaultTableModel modeloTablaVenta;
    private DefaultTableModel modeloTablaPrestamo;
    private JTable tablaVenta;
    private JTable tablaPrestamo;
    
    public PanelAdministradorInventario(Cafe cafe) {
        this.cafe = cafe;
        setLayout(new BorderLayout());
        
        tabbedPane = new JTabbedPane();
        
        // Tab de inventario de venta
        String[] colsVenta = {"Juego", "Estado", "Ocupado", "Precio"};
        modeloTablaVenta = new DefaultTableModel(colsVenta, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tablaVenta = new JTable(modeloTablaVenta);
        JScrollPane scrollVenta = new JScrollPane(tablaVenta);
        
        JPanel panelVenta = new JPanel(new BorderLayout());
        panelVenta.add(scrollVenta, BorderLayout.CENTER);
        panelVenta.add(crearPanelBotonesInventario(false), BorderLayout.SOUTH);
        
        tabbedPane.addTab("Inventario de Venta", panelVenta);
        
        // Tab de inventario de préstamo
        String[] colsPrestamo = {"Juego", "Estado", "Ocupado", "Precio"};
        modeloTablaPrestamo = new DefaultTableModel(colsPrestamo, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tablaPrestamo = new JTable(modeloTablaPrestamo);
        JScrollPane scrollPrestamo = new JScrollPane(tablaPrestamo);
        
        JPanel panelPrestamo = new JPanel(new BorderLayout());
        panelPrestamo.add(scrollPrestamo, BorderLayout.CENTER);
        panelPrestamo.add(crearPanelBotonesInventario(true), BorderLayout.SOUTH);
        
        tabbedPane.addTab("Inventario de Préstamo", panelPrestamo);
        
        add(tabbedPane, BorderLayout.CENTER);
        
        actualizarInventario();
    }
    
    private JPanel crearPanelBotonesInventario(boolean esPrestamo) {
        JPanel panel = new JPanel(new GridLayout(1, 4, 10, 10));
        
        JButton btnMover = new JButton(esPrestamo ? "Mover a Venta" : "Mover a Préstamo");
        btnMover.addActionListener(e -> {
            int fila = esPrestamo ? tablaPrestamo.getSelectedRow() : tablaVenta.getSelectedRow();
            if (fila != -1) {
                moverJuego(esPrestamo, fila);
            } else {
                JOptionPane.showMessageDialog(this, "Seleccione un juego.");
            }
        });
        
        JButton btnReparar = new JButton("Reparar Juego");
        btnReparar.setEnabled(esPrestamo);
        btnReparar.addActionListener(e -> repararJuego());
        
        JButton btnMarcarDesaparecido = new JButton("Marcar Desaparecido");
        btnMarcarDesaparecido.setEnabled(esPrestamo);
        btnMarcarDesaparecido.addActionListener(e -> marcarDesaparecido());
        
        panel.add(btnMover);
        panel.add(btnReparar);
        panel.add(btnMarcarDesaparecido);
        
        JButton btnAgregarCopia = new JButton("Agregar Copia");
        btnAgregarCopia.addActionListener(e -> agregarCopiaJuego());
        
        panel.add(btnMover);
        panel.add(btnReparar);
        panel.add(btnMarcarDesaparecido);
        panel.add(btnAgregarCopia);  
        
        return panel;
    }
    
    private void agregarCopiaJuego() {
        // Seleccionar juego del catálogo
        JComboBox<String> cbJuegos = new JComboBox<>();
        for (JuegoDeMesa j : cafe.getCatalogoJuegos()) {
            cbJuegos.addItem(j.getNombre());
        }
        
        JRadioButton rbVenta = new JRadioButton("Inventario de Venta");
        JRadioButton rbPrestamo = new JRadioButton("Inventario de Préstamo");
        ButtonGroup bg = new ButtonGroup();
        bg.add(rbVenta);
        bg.add(rbPrestamo);
        
        JPanel panel = new JPanel(new GridLayout(0, 1));
        panel.add(new JLabel("Seleccionar juego:"));
        panel.add(cbJuegos);
        panel.add(new JLabel("Destino:"));
        panel.add(rbVenta);
        panel.add(rbPrestamo);
        
        int result = JOptionPane.showConfirmDialog(this, panel, "Agregar Copia de Juego", 
                JOptionPane.OK_CANCEL_OPTION);
        
        if (result == JOptionPane.OK_OPTION) {
            String juegoNombre = (String) cbJuegos.getSelectedItem();
            JuegoDeMesa juegoBase = cafe.getCatalogoJuegos().stream()
                    .filter(j -> j.getNombre().equals(juegoNombre))
                    .findFirst().orElse(null);
            
            if (juegoBase == null) return;
            
            JuegoFisico nuevaCopia = new JuegoFisico("nuevo", false, juegoBase);
            
            if (rbVenta.isSelected()) {
                cafe.getCatalogoCompra().add(nuevaCopia);
                JOptionPane.showMessageDialog(this, "Copia agregada a inventario de VENTA");
            } else if (rbPrestamo.isSelected()) {
                cafe.getCatalogoPrestamo().add(nuevaCopia);
                JOptionPane.showMessageDialog(this, "Copia agregada a inventario de PRÉSTAMO");
            }
            
            actualizarInventario(); // Refrescar tablas
        }
    }
    
    private void actualizarInventario() {
        // Actualizar venta
        modeloTablaVenta.setRowCount(0);
        for (JuegoFisico jf : cafe.getCatalogoCompra()) {
            Object[] fila = {
                jf.getJuegoBase().getNombre(),
                jf.getEstado(),
                jf.isOcupado() ? "Sí" : "No",
                "$" + jf.getJuegoBase().getPrecio()
            };
            modeloTablaVenta.addRow(fila);
        }
        
        // Actualizar préstamo
        modeloTablaPrestamo.setRowCount(0);
        for (JuegoFisico jf : cafe.getCatalogoPrestamo()) {
            Object[] fila = {
                jf.getJuegoBase().getNombre(),
                jf.getEstado(),
                jf.isOcupado() ? "Sí" : "No",
                "$" + jf.getJuegoBase().getPrecio()
            };
            modeloTablaPrestamo.addRow(fila);
        }
    }
    
    private void moverJuego(boolean desdePrestamo, int fila) {
        JuegoFisico juego;
        if (desdePrestamo) {
            juego = cafe.getCatalogoPrestamo().get(fila);
            cafe.getCatalogoPrestamo().remove(juego);
            cafe.getCatalogoCompra().add(juego);
        } else {
            juego = cafe.getCatalogoCompra().get(fila);
            cafe.getCatalogoCompra().remove(juego);
            cafe.getCatalogoPrestamo().add(juego);
        }
        
        JOptionPane.showMessageDialog(this, "Juego movido exitosamente.");
        actualizarInventario();
    }
    
    private void repararJuego() {
        int fila = tablaPrestamo.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un juego dañado.");
            return;
        }
        
        JuegoFisico danado = cafe.getCatalogoPrestamo().get(fila);
        
        // Buscar copia nueva en venta del mismo juego
        List<JuegoFisico> nuevos = cafe.getCatalogoCompra().stream()
                .filter(j -> j.getJuegoBase().equals(danado.getJuegoBase()) 
                        && "nuevo".equals(j.getEstado()))
                .collect(java.util.stream.Collectors.toList());
        
        if (nuevos.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No hay copias nuevas de este juego para reparar.");
            return;
        }
        
        JuegoFisico nuevo = nuevos.get(0);
        cafe.getCatalogoPrestamo().remove(danado);
        cafe.getCatalogoPrestamo().add(nuevo);
        cafe.getCatalogoCompra().remove(nuevo);
        
        JOptionPane.showMessageDialog(this, "Juego reparado exitosamente.");
        actualizarInventario();
    }
    
    private void marcarDesaparecido() {
        int fila = tablaPrestamo.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un juego.");
            return;
        }
        
        JuegoFisico juego = cafe.getCatalogoPrestamo().get(fila);
        juego.setEstado("desaparecido");
        cafe.getCatalogoPrestamo().remove(juego);
        
        JOptionPane.showMessageDialog(this, "Juego marcado como desaparecido.");
        actualizarInventario();
    }
}