package ui.panels;

import java.awt.BorderLayout;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import control.Cafe;
import usuarios.Mesero;

public class PanelJuegosMesero extends JPanel {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Cafe cafe;
    private Mesero mesero;
    private JTable tablaJuegos;
    private DefaultTableModel modeloTabla;
    
    public PanelJuegosMesero(Cafe cafe, Mesero mesero) {
        this.cafe = cafe;
        this.mesero = mesero;
        setLayout(new BorderLayout());
        
        String[] columnas = {"Juego", "¿Puede Explicar?"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tablaJuegos = new JTable(modeloTabla);
        JScrollPane scroll = new JScrollPane(tablaJuegos);
        add(scroll, BorderLayout.CENTER);
        
        JButton btnRefrescar = new JButton("Refrescar");
        btnRefrescar.addActionListener(e -> actualizarTabla());
        add(btnRefrescar, BorderLayout.SOUTH);
        
        actualizarTabla();
        
        JTextArea txtInfo = new JTextArea(4, 40);
        txtInfo.setEditable(false);
        txtInfo.setText(
            "Los juegos difíciles requieren que un mesero capacitado\n" +
            "explique las reglas a los clientes.\n\n" +
            "Si un juego no aparece en tu lista, no podrás explicarlo."
        );
        add(new JScrollPane(txtInfo), BorderLayout.NORTH);
    }
    
    private void actualizarTabla() {
        modeloTabla.setRowCount(0);
        List<String> conocidos = mesero.getDificilesConocidos();
        
        for (var juego : cafe.getCatalogoJuegos()) {
            if (juego.getDificultad()) {
                boolean puede = conocidos.contains(juego.getNombre());
                Object[] fila = {juego.getNombre(), puede ? "Sí" : "No"};
                modeloTabla.addRow(fila);
            }
        }
    }
}