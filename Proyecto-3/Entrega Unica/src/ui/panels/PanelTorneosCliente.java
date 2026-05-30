package ui.panels;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import control.Cafe;
import juego.Torneo;
import usuarios.Cliente;

public class PanelTorneosCliente extends JPanel {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Cafe cafe;
    private Cliente cliente;
    private JTable tablaTorneos;
    private DefaultTableModel modeloTabla;
    private JTextArea txtDetalle;
    
    public PanelTorneosCliente(Cafe cafe, Cliente cliente) {
        this.cafe = cafe;
        this.cliente = cliente;
        setLayout(new BorderLayout());
        
        String[] columnas = {"Juego", "Tipo", "Bono", "Costo", "Cupos Disponibles"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tablaTorneos = new JTable(modeloTabla);
        JScrollPane scroll = new JScrollPane(tablaTorneos);
        add(scroll, BorderLayout.CENTER);
        
        JPanel panelBotones = new JPanel(new GridLayout(1, 3, 10, 10));
        
        JButton btnConsultar = new JButton("Consultar Torneos");
        btnConsultar.addActionListener(e -> actualizarTabla());
        
        JButton btnInscribir = new JButton("Inscribirse");
        btnInscribir.addActionListener(e -> inscribirTorneo());
        
        JButton btnDesinscribir = new JButton("Desinscribirse");
        btnDesinscribir.addActionListener(e -> desinscribirTorneo());
        
        panelBotones.add(btnConsultar);
        panelBotones.add(btnInscribir);
        panelBotones.add(btnDesinscribir);
        
        add(panelBotones, BorderLayout.NORTH);
        
        txtDetalle = new JTextArea(6, 40);
        txtDetalle.setEditable(false);
        add(new JScrollPane(txtDetalle), BorderLayout.SOUTH);
        
        actualizarTabla();
        actualizarDetalle();
    }
    
    private void actualizarTabla() {
        modeloTabla.setRowCount(0);
        List<Torneo> torneos = cafe.getTorneos();
        
        for (Torneo t : torneos) {
            int cuposRestantes = t.getCupos() - t.getCuposTaken();
            Object[] fila = {
                t.getJuego() != null ? t.getJuego().getNombre() : "Sin juego",
                t.isEsCompetitivo() ? "Competitivo" : "Amistoso",
                t.getBono(),
                "$" + t.getCostoEntrada(),
                cuposRestantes
            };
            modeloTabla.addRow(fila);
        }
    }
    
    private void actualizarDetalle() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== INFORMACIÓN ===\n\n");
        sb.append("Los torneos amistosos otorgan premios en puntos de fidelidad.\n");
        sb.append("Los torneos competitivos tienen premios físicos.\n\n");
        sb.append("Puedes inscribir hasta 3 participantes por torneo.\n");
        sb.append("Los fanáticos (juego en favoritos) tienen cupos prioritarios (20%).\n\n");
        sb.append("Si ya estás inscrito, puedes desinscribirte.\n");
        
        sb.append("\n=== MIS INSCRIPCIONES ===\n"); // mostrar torneos
        boolean tieneInscripciones = false;
        for (Torneo t : cafe.getTorneos()) {
            if (t.getUsuarios().contains(cliente)) {
                sb.append("- ").append(t.getJuego().getNombre()).append("\n");
                tieneInscripciones = true;
            }
        }
        if (!tieneInscripciones) {
            sb.append("No estás inscrito en ningún torneo.\n");
        }
        
        txtDetalle.setText(sb.toString());
    }
    
    private void inscribirTorneo() {
        int fila = tablaTorneos.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un torneo.");
            return;
        }
        
        Torneo torneo = cafe.getTorneos().get(fila);
        
        String input = JOptionPane.showInputDialog(this, 
            "Número de participantes (máximo 3):", "Inscribir", JOptionPane.QUESTION_MESSAGE);
        
        if (input == null) return;
        
        try {
            int participantes = Integer.parseInt(input);
            if (participantes < 1 || participantes > 3) {
                JOptionPane.showMessageDialog(this, "Número inválido. Debe ser entre 1 y 3.");
                return;
            }
            
            boolean ok = torneo.inscribir(cliente, participantes);
            if (ok) {
                JOptionPane.showMessageDialog(this, "Inscripción exitosa.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                actualizarTabla();
                actualizarDetalle();
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Error: cupos insuficientes o ya estás inscrito.", 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Ingrese un número válido.");
        }
    }
    
    private void desinscribirTorneo() {
        int fila = tablaTorneos.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un torneo.");
            return;
        }
        
        Torneo torneo = cafe.getTorneos().get(fila);
        
        if (!torneo.getUsuarios().contains(cliente)) {
            JOptionPane.showMessageDialog(this, "No estás inscrito en este torneo.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "¿Desinscribirte del torneo de " + torneo.getJuego().getNombre() + "?", 
            "Confirmar", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            boolean ok = torneo.desinscribir(cliente);
            if (ok) {
                JOptionPane.showMessageDialog(this, "Desinscripción exitosa.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                actualizarTabla();
                actualizarDetalle();
            } else {
                JOptionPane.showMessageDialog(this, "Error al desinscribir.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}