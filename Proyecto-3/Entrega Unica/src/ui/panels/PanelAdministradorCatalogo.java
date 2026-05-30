package ui.panels;

import java.awt.BorderLayout;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import control.Cafe;
import juego.JuegoDeMesa;

public class PanelAdministradorCatalogo extends JPanel {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Cafe cafe;
    private JTable tablaCatalogo;
    private DefaultTableModel modeloTabla;
    
    public PanelAdministradorCatalogo(Cafe cafe) {
        this.cafe = cafe;
        setLayout(new BorderLayout());
        
        String[] columnas = {"Nombre", "Tipo", "Año", "Empresa", "Precio", "Min/Max", "Difícil", "Niños", "Jóvenes"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tablaCatalogo = new JTable(modeloTabla);
        JScrollPane scroll = new JScrollPane(tablaCatalogo);
        add(scroll, BorderLayout.CENTER);
        
        JButton btnRefrescar = new JButton("Refrescar");
        btnRefrescar.addActionListener(e -> actualizarTabla());
        add(btnRefrescar, BorderLayout.SOUTH);
        
        actualizarTabla();
    }
    
    private void actualizarTabla() {
        modeloTabla.setRowCount(0);
        for (JuegoDeMesa j : cafe.getCatalogoJuegos()) {
            Object[] fila = {
                j.getNombre(),
                j.getTipoJuego(),
                j.getAnio(),
                j.getEmpresa(),
                "$" + j.getPrecio(),
                j.getMinJugadores() + "/" + j.getMaxJugadores(),
                j.getDificultad() ? "Sí" : "No",
                j.getNinos() ? "Sí" : "No",
                j.getJovenes() ? "Sí" : "No"
            };
            modeloTabla.addRow(fila);
        }
    }
}