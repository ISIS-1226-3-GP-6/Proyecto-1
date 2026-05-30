package ui.panels;

import java.awt.BorderLayout;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import control.Cafe;
import cafeteria.TicketNuevoPlatillo;
import horario.TicketCambiarTurno;

public class PanelAdministradorTickets extends JPanel {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Cafe cafe;
    private JTabbedPane tabbedPane;
    private DefaultTableModel modeloTicketsPlatillos;
    private DefaultTableModel modeloTicketsTurnos;
    
    public PanelAdministradorTickets(Cafe cafe) {
        this.cafe = cafe;
        setLayout(new BorderLayout());
        
        tabbedPane = new JTabbedPane();
        
        String[] colsPlatillos = {"Platillo", "Precio", "Estado"};
        modeloTicketsPlatillos = new DefaultTableModel(colsPlatillos, 0);
        JTable tablaPlatillos = new JTable(modeloTicketsPlatillos);
        JScrollPane scrollPlatillos = new JScrollPane(tablaPlatillos);
        
        JPanel panelPlatillos = new JPanel(new BorderLayout());
        panelPlatillos.add(scrollPlatillos, BorderLayout.CENTER);
        
        JButton btnAprobarPlatillo = new JButton("Aprobar Seleccionado");
        btnAprobarPlatillo.addActionListener(e -> aprobarPlatillo(tablaPlatillos.getSelectedRow()));
        
        JButton btnRechazarPlatillo = new JButton("Rechazar Seleccionado");
        btnRechazarPlatillo.addActionListener(e -> rechazarPlatillo(tablaPlatillos.getSelectedRow()));
        
        JPanel panelBotonesPlatillos = new JPanel();
        panelBotonesPlatillos.add(btnAprobarPlatillo);
        panelBotonesPlatillos.add(btnRechazarPlatillo);
        panelPlatillos.add(panelBotonesPlatillos, BorderLayout.SOUTH);
        
        tabbedPane.addTab("Sugerencias de Platillos", panelPlatillos);
        
        String[] colsTurnos = {"Empleado", "Turno Actual", "Turno Deseado", "Intercambio", "Estado"};
        modeloTicketsTurnos = new DefaultTableModel(colsTurnos, 0);
        JTable tablaTurnos = new JTable(modeloTicketsTurnos);
        JScrollPane scrollTurnos = new JScrollPane(tablaTurnos);
        
        JPanel panelTurnos = new JPanel(new BorderLayout());
        panelTurnos.add(scrollTurnos, BorderLayout.CENTER);
        
        JButton btnAprobarTurno = new JButton("Aprobar Seleccionado");
        btnAprobarTurno.addActionListener(e -> aprobarTurno(tablaTurnos.getSelectedRow()));
        
        JButton btnRechazarTurno = new JButton("Rechazar Seleccionado");
        btnRechazarTurno.addActionListener(e -> rechazarTurno(tablaTurnos.getSelectedRow()));
        
        JPanel panelBotonesTurnos = new JPanel();
        panelBotonesTurnos.add(btnAprobarTurno);
        panelBotonesTurnos.add(btnRechazarTurno);
        panelTurnos.add(panelBotonesTurnos, BorderLayout.SOUTH);
        
        tabbedPane.addTab("Cambios de Turno", panelTurnos);
        
        add(tabbedPane, BorderLayout.CENTER);
        
        actualizarTickets();
    }
    
    public void actualizarTickets() {
        modeloTicketsPlatillos.setRowCount(0);
        for (TicketNuevoPlatillo ticket : cafe.getTicketsPlatillosPendientes()) {
            if (ticket != null && ticket.getPlatillo() != null) {
                Object[] fila = {
                    ticket.getPlatillo().getClass().getSimpleName(),
                    "$" + ticket.getPlatillo().getPrecio(),
                    "Pendiente"
                };
                modeloTicketsPlatillos.addRow(fila);
            }
        }
        
        modeloTicketsTurnos.setRowCount(0);
        for (TicketCambiarTurno ticket : cafe.getTicketsTurnoPendientes()) {
            if (ticket != null && ticket.getEmpleadoPrincipal() != null) {
                String intercambio = ticket.getEmpleadoSecundario() != null ? 
                        "Con " + ticket.getEmpleadoSecundario().getLogin() : "General";
                Object[] fila = {
                    ticket.getEmpleadoPrincipal().getLogin(),
                    ticket.getTurnoInicial() != null ? ticket.getTurnoInicial().getDiaSemana() : "N/A",
                    ticket.getTurnoFinal() != null ? ticket.getTurnoFinal().getDiaSemana() : "N/A",
                    intercambio,
                    ticket.getEstado()
                };
                modeloTicketsTurnos.addRow(fila);
            }
        }
    }
    
    private void aprobarPlatillo(int row) {
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un ticket.");
            return;
        }
        
        TicketNuevoPlatillo ticket = cafe.getTicketsPlatillosPendientes().get(row);
        boolean ok = cafe.aprobarTicketPlatillo("password", "admin", ticket);
        
        if (ok) {
            JOptionPane.showMessageDialog(this, "Platillo aprobado y agregado al menú.");
            actualizarTickets();
        } else {
            JOptionPane.showMessageDialog(this, "Error al aprobar.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void rechazarPlatillo(int row) {
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un ticket.");
            return;
        }
        
        TicketNuevoPlatillo ticket = cafe.getTicketsPlatillosPendientes().get(row);
        boolean ok = cafe.rechazarTicketPlatillo("password", "admin", ticket);
        
        if (ok) {
            JOptionPane.showMessageDialog(this, "Platillo rechazado.");
            actualizarTickets();
        } else {
            JOptionPane.showMessageDialog(this, "Error al rechazar.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void aprobarTurno(int row) {
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un ticket.");
            return;
        }
        
        TicketCambiarTurno ticket = cafe.getTicketsTurnoPendientes().get(row);
        boolean ok = cafe.aprobarTicketTurno("password", "admin", ticket);
        
        if (ok) {
            JOptionPane.showMessageDialog(this, "Cambio de turno aprobado.");
            actualizarTickets();
        } else {
            JOptionPane.showMessageDialog(this, "Error al aprobar.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void rechazarTurno(int row) {
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un ticket.");
            return;
        }
        
        TicketCambiarTurno ticket = cafe.getTicketsTurnoPendientes().get(row);
        boolean ok = cafe.rechazarTicketTurno("password", "admin", ticket);
        
        if (ok) {
            JOptionPane.showMessageDialog(this, "Cambio de turno rechazado.");
            actualizarTickets();
        } else {
            JOptionPane.showMessageDialog(this, "Error al rechazar.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}