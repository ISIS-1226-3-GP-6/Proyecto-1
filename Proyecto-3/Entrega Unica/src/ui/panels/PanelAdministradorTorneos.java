package ui.panels;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import control.Cafe;
import juego.JuegoDeMesa;
import juego.Torneo;

public class PanelAdministradorTorneos extends JPanel {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Cafe cafe;
    private JTable tablaTorneos;
    private DefaultTableModel modeloTabla;
    private JTextArea txtInscripciones;
    
    public PanelAdministradorTorneos(Cafe cafe) {
        this.cafe = cafe;
        setLayout(new BorderLayout());
        
        String[] columnas = {"Juego", "Tipo", "Bono", "Costo", "Cupos", "Inscritos"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tablaTorneos = new JTable(modeloTabla);
        JScrollPane scrollTabla = new JScrollPane(tablaTorneos);
        add(scrollTabla, BorderLayout.CENTER);
        
        JPanel panelBotones = new JPanel(new GridLayout(2, 2, 10, 10));
        
        JButton btnCrear = new JButton("Crear Torneo");
        btnCrear.addActionListener(e -> crearTorneo());
        
        JButton btnAsignarGanador = new JButton("Asignar Ganador");
        btnAsignarGanador.addActionListener(e -> asignarGanador());
        
        JButton btnEliminar = new JButton("Eliminar Torneo");
        btnEliminar.addActionListener(e -> eliminarTorneo());
        
        JButton btnConsultar = new JButton("Consultar Inscripciones");
        btnConsultar.addActionListener(e -> consultarInscripciones());
        
        panelBotones.add(btnCrear);
        panelBotones.add(btnAsignarGanador);
        panelBotones.add(btnEliminar);
        panelBotones.add(btnConsultar);
        
        add(panelBotones, BorderLayout.NORTH);
        
        txtInscripciones = new JTextArea(8, 40);
        txtInscripciones.setEditable(false);
        JScrollPane scrollInscripciones = new JScrollPane(txtInscripciones);
        scrollInscripciones.setBorder(BorderFactory.createTitledBorder("Detalle de Inscripciones"));
        add(scrollInscripciones, BorderLayout.SOUTH);
        
        actualizarTabla();
    }
    
    private void actualizarTabla() {
        modeloTabla.setRowCount(0);
        for (Torneo t : cafe.getTorneos()) {
            Object[] fila = {
                t.getJuego() != null ? t.getJuego().getNombre() : "Sin juego",
                t.isEsCompetitivo() ? "Competitivo" : "Amistoso",
                t.getBono(),
                "$" + t.getCostoEntrada(),
                t.getCupos(),
                t.getCuposTaken()
            };
            modeloTabla.addRow(fila);
        }
        txtInscripciones.setText("");
    }
    
    private void crearTorneo() {
        JPanel panel = new JPanel(new GridLayout(0, 2, 5, 5));
        
        JCheckBox chkCompetitivo = new JCheckBox();
        JTextField txtBono = new JTextField();
        JTextField txtCosto = new JTextField();
        JTextField txtCupos = new JTextField();
        JComboBox<String> cbJuegos = new JComboBox<>();
        
        for (JuegoDeMesa j : cafe.getCatalogoJuegos()) {
            cbJuegos.addItem(j.getNombre());
        }
        
        panel.add(new JLabel("Competitivo:"));
        panel.add(chkCompetitivo);
        panel.add(new JLabel("Bono en puntos:"));
        panel.add(txtBono);
        panel.add(new JLabel("Costo entrada:"));
        panel.add(txtCosto);
        panel.add(new JLabel("Cupos totales:"));
        panel.add(txtCupos);
        panel.add(new JLabel("Juego asociado:"));
        panel.add(cbJuegos);
        
        int result = JOptionPane.showConfirmDialog(this, panel, "Crear Torneo", 
                JOptionPane.OK_CANCEL_OPTION);
        
        if (result == JOptionPane.OK_OPTION) {
            try {
                boolean esCompetitivo = chkCompetitivo.isSelected();
                int bono = Integer.parseInt(txtBono.getText());
                double costo = Double.parseDouble(txtCosto.getText());
                int cupos = Integer.parseInt(txtCupos.getText());
                String juegoNombre = (String) cbJuegos.getSelectedItem();
                
                boolean ok = cafe.crearTorneo("password", "admin", esCompetitivo, bono, costo, cupos, juegoNombre);
                
                if (ok) {
                    JOptionPane.showMessageDialog(this, "Torneo creado exitosamente.");
                    actualizarTabla();
                } else {
                    JOptionPane.showMessageDialog(this, "Error al crear torneo.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Datos numéricos inválidos.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void asignarGanador() {
        int fila = tablaTorneos.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un torneo.");
            return;
        }
        
        Torneo torneo = cafe.getTorneos().get(fila);
        
        String ganador = JOptionPane.showInputDialog(this, "Login del ganador:");
        if (ganador != null && !ganador.trim().isEmpty()) {
            boolean ok = cafe.asignarGanadorTorneo("password", "admin", torneo, ganador.trim());
            if (ok) {
                JOptionPane.showMessageDialog(this, "Ganador asignado exitosamente.");
                actualizarTabla();
            } else {
                JOptionPane.showMessageDialog(this, "Error: cliente no existe o no está inscrito.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void eliminarTorneo() {
        int fila = tablaTorneos.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un torneo.");
            return;
        }
        
        Torneo torneo = cafe.getTorneos().get(fila);
        
        int confirm = JOptionPane.showConfirmDialog(this, 
                "¿Eliminar torneo de " + torneo.getJuego().getNombre() + "?", 
                "Confirmar", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            boolean ok = cafe.eliminarTorneo("password", "admin", torneo);
            if (ok) {
                JOptionPane.showMessageDialog(this, "Torneo eliminado.");
                actualizarTabla();
            } else {
                JOptionPane.showMessageDialog(this, "Error al eliminar torneo.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void consultarInscripciones() {
        int fila = tablaTorneos.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un torneo.");
            return;
        }
        
        Torneo torneo = cafe.getTorneos().get(fila);
        
        StringBuilder sb = new StringBuilder();
        sb.append("=== TORNEO DE ").append(torneo.getJuego().getNombre()).append(" ===\n\n");
        sb.append("Cupos totales: ").append(torneo.getCupos()).append("\n");
        sb.append("Cupos ocupados: ").append(torneo.getCuposTaken()).append("\n");
        sb.append("Cupos prioritarios usados: ").append(torneo.getCuposPrioritariosTaken()).append("\n\n");
        sb.append("=== USUARIOS INSCRITOS ===\n");
        
        if (torneo.getUsuarios().isEmpty()) {
            sb.append("No hay usuarios inscritos.\n");
        } else {
            for (var u : torneo.getUsuarios()) {
                sb.append("- ").append(u.getLogin()).append("\n");
            }
        }
        
        txtInscripciones.setText(sb.toString());
    }
}