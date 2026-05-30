package ui.panels;

import java.awt.BorderLayout;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import control.Cafe;
import juego.JuegoDeMesa;
import juego.JuegoFisico;

public class PanelCatalogoCliente extends JPanel {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Cafe cafe;
    private JTable tablaJuegos;
    private DefaultTableModel modeloTabla;
    
    public PanelCatalogoCliente(Cafe cafe) {
        this.cafe = cafe;
        setLayout(new BorderLayout());
        
        String[] columnas = {"Nombre", "Tipo", "Min/Max Jugadores", "Precio", "Disponible"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tablaJuegos = new JTable(modeloTabla);
        JScrollPane scrollPane = new JScrollPane(tablaJuegos);
        add(scrollPane, BorderLayout.CENTER);
        
        JButton btnRefrescar = new JButton("Refrescar");
        btnRefrescar.addActionListener(e -> actualizarCatalogo());
        add(btnRefrescar, BorderLayout.SOUTH);
        
        actualizarCatalogo();
    }
    
    private void actualizarCatalogo() {
        modeloTabla.setRowCount(0);
        List<JuegoFisico> juegos = cafe.getCatalogoPrestamo();
        
        for (JuegoFisico jf : juegos) {
            JuegoDeMesa juego = jf.getJuegoBase();
            String disponible = jf.estaDisponible() ? "Sí" : "No";
            Object[] fila = {
                juego.getNombre(),
                juego.getTipoJuego(),
                juego.getMinJugadores() + " - " + juego.getMaxJugadores(),
                "$" + juego.getPrecio(),
                disponible
            };
            modeloTabla.addRow(fila);
        }
    }
}