package ui.panels;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import control.Cafe;
import juego.JuegoDeMesa;

public class PanelAdministradorJuegos extends JPanel {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Cafe cafe;
    private JTable tablaJuegos;
    private DefaultTableModel modeloTabla;
    
    public PanelAdministradorJuegos(Cafe cafe) {
        this.cafe = cafe;
        setLayout(new BorderLayout());
        
        String[] columnas = {"Nombre", "Tipo", "Precio", "Min", "Max", "Difícil", "Niños", "Jóvenes"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tablaJuegos = new JTable(modeloTabla);
        JScrollPane scroll = new JScrollPane(tablaJuegos);
        add(scroll, BorderLayout.CENTER);
        
        JPanel panelBotones = new JPanel(new GridLayout(1, 3, 10, 10));
        
        JButton btnRefrescar = new JButton("Refrescar");
        btnRefrescar.addActionListener(e -> actualizarTabla());
        
        JButton btnAgregar = new JButton("Agregar Juego");
        btnAgregar.addActionListener(e -> agregarJuego());
        
        JButton btnEliminar = new JButton("Eliminar Juego");
        btnEliminar.addActionListener(e -> eliminarJuego());
        
        panelBotones.add(btnRefrescar);
        panelBotones.add(btnAgregar);
        panelBotones.add(btnEliminar);
        
        add(panelBotones, BorderLayout.SOUTH);
        
        actualizarTabla();
    }
    
    private void actualizarTabla() {
        modeloTabla.setRowCount(0);
        for (JuegoDeMesa juego : cafe.getCatalogoJuegos()) {
            Object[] fila = {
                juego.getNombre(),
                juego.getTipoJuego(),
                "$" + juego.getPrecio(),
                juego.getMinJugadores(),
                juego.getMaxJugadores(),
                juego.getDificultad() ? "Sí" : "No",
                juego.getNinos() ? "Sí" : "No",
                juego.getJovenes() ? "Sí" : "No"
            };
            modeloTabla.addRow(fila);
        }
    }
    
    private void agregarJuego() {
        JTextField txtNombre = new JTextField();
        JTextField txtAnio = new JTextField();
        JTextField txtEmpresa = new JTextField();
        JComboBox<String> cbTipo = new JComboBox<>(new String[]{"Carta", "Tablero", "Accion"});
        JCheckBox chkDificil = new JCheckBox();
        JCheckBox chkNinos = new JCheckBox();
        JCheckBox chkJovenes = new JCheckBox();
        JTextField txtMin = new JTextField();
        JTextField txtMax = new JTextField();
        JTextField txtPrecio = new JTextField();
        
        JPanel panel = new JPanel(new GridLayout(0, 2, 5, 5));
        panel.add(new JLabel("Nombre:"));
        panel.add(txtNombre);
        panel.add(new JLabel("Año publicación:"));
        panel.add(txtAnio);
        panel.add(new JLabel("Empresa:"));
        panel.add(txtEmpresa);
        panel.add(new JLabel("Tipo:"));
        panel.add(cbTipo);
        panel.add(new JLabel("¿Es difícil?:"));
        panel.add(chkDificil);
        panel.add(new JLabel("¿Permite niños?:"));
        panel.add(chkNinos);
        panel.add(new JLabel("¿Permite jóvenes?:"));
        panel.add(chkJovenes);
        panel.add(new JLabel("Min jugadores:"));
        panel.add(txtMin);
        panel.add(new JLabel("Max jugadores:"));
        panel.add(txtMax);
        panel.add(new JLabel("Precio:"));
        panel.add(txtPrecio);
        
        int result = JOptionPane.showConfirmDialog(this, panel, "Agregar Juego", 
                JOptionPane.OK_CANCEL_OPTION);
        
        if (result == JOptionPane.OK_OPTION) {
            try {
                boolean ok = cafe.agregarJuegoCatalogo(
                    "password", "admin",  
                    txtNombre.getText(),
                    Integer.parseInt(txtAnio.getText()),
                    txtEmpresa.getText(),
                    (String) cbTipo.getSelectedItem(),
                    chkDificil.isSelected(),
                    chkNinos.isSelected(),
                    chkJovenes.isSelected(),
                    Integer.parseInt(txtMin.getText()),
                    Integer.parseInt(txtMax.getText()),
                    Double.parseDouble(txtPrecio.getText())
                );
                
                if (ok) {
                    JOptionPane.showMessageDialog(this, "Juego agregado exitosamente.");
                    actualizarTabla();
                } else {
                    JOptionPane.showMessageDialog(this, "Error al agregar juego.", 
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Datos numéricos inválidos.", 
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void eliminarJuego() {
        int fila = tablaJuegos.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un juego para eliminar.");
            return;
        }
        
        String nombre = (String) modeloTabla.getValueAt(fila, 0);
        int confirm = JOptionPane.showConfirmDialog(this, 
                "¿Eliminar el juego '" + nombre + "'?", "Confirmar", 
                JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            boolean encontrado = cafe.getCatalogoJuegos().removeIf(j -> j.getNombre().equals(nombre));
            if (encontrado) {
                JOptionPane.showMessageDialog(this, "Juego eliminado.");
                actualizarTabla();
            } else {
                JOptionPane.showMessageDialog(this, "Error al eliminar juego.", 
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}