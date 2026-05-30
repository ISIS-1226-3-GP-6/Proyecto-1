package ui.panels;

import java.awt.BorderLayout;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import control.Cafe;
import horario.Turno;
import usuarios.Empleado;

public class PanelTurnosEmpleado extends JPanel {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Cafe cafe;
    private Empleado empleado;
    private JTable tablaTurnos;
    private DefaultTableModel modeloTabla;
    private JTextArea txtSolicitud;
    
    public PanelTurnosEmpleado(Cafe cafe, Empleado empleado) {
        this.cafe = cafe;
        this.empleado = empleado;
        setLayout(new BorderLayout());
        
        String[] columnas = {"Día", "Empleados en turno"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tablaTurnos = new JTable(modeloTabla);
        JScrollPane scrollTabla = new JScrollPane(tablaTurnos);
        scrollTabla.setBorder(BorderFactory.createTitledBorder("Horario Semanal"));
        add(scrollTabla, BorderLayout.CENTER);
        
        JPanel panelSolicitud = new JPanel(new BorderLayout()); 
        panelSolicitud.setBorder(BorderFactory.createTitledBorder("Solicitar Cambio de Turno"));
        
        txtSolicitud = new JTextArea(5, 40);
        txtSolicitud.setEditable(false);
        
        JPanel panelBotones = new JPanel();
        
        JButton btnRefrescar = new JButton("Refrescar Turnos");
        btnRefrescar.addActionListener(e -> actualizarTurnos());
        
        JButton btnSolicitarCambio = new JButton("Solicitar Cambio de Turno");
        btnSolicitarCambio.addActionListener(e -> solicitarCambioTurno());
        
        panelBotones.add(btnRefrescar);
        panelBotones.add(btnSolicitarCambio);
        
        panelSolicitud.add(new JScrollPane(txtSolicitud), BorderLayout.CENTER);
        panelSolicitud.add(panelBotones, BorderLayout.SOUTH);
        
        add(panelSolicitud, BorderLayout.SOUTH);
        
        actualizarTurnos();
        actualizarInfoTurnos();
    }
    
    private void actualizarTurnos() {
        modeloTabla.setRowCount(0);
        
        var turnosPorDia = cafe.getHorario().getTurnosPorDia();
        for (String dia : turnosPorDia.keySet()) {
            Turno turno = turnosPorDia.get(dia);
            boolean enTurno = turno.getEmpleados().contains(empleado);
            String marcador = enTurno ? "✓ " : "";
            Object[] fila = {marcador + dia, turno.getEmpleados().size()};
            modeloTabla.addRow(fila);
        }
    }
    
    private void actualizarInfoTurnos() {
        var turnosPorDia = cafe.getHorario().getTurnosPorDia();
        StringBuilder sb = new StringBuilder();
        sb.append("=== TUS TURNOS ===\n");
        
        boolean tieneTurno = false;
        for (String dia : turnosPorDia.keySet()) {
            Turno turno = turnosPorDia.get(dia);
            if (turno.getEmpleados().contains(empleado)) {
                sb.append("- ").append(dia).append("\n");
                tieneTurno = true;
            }
        }
        
        if (!tieneTurno) {
            sb.append("No tienes turnos asignados.\n");
        }
        
        sb.append("\n=== CÓMO SOLICITAR UN CAMBIO ===\n");
        sb.append("1. Selecciona el día de tu turno actual\n");
        sb.append("2. Selecciona el día del turno deseado\n");
        sb.append("3. Indica si es intercambio con otro empleado\n");
        sb.append("4. Espera la aprobación del administrador\n");
        
        txtSolicitud.setText(sb.toString());
    }
    
    private void solicitarCambioTurno() {
        var turnosPorDia = cafe.getHorario().getTurnosPorDia();
        if (turnosPorDia.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No hay turnos configurados.", 
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        String[] dias = turnosPorDia.keySet().toArray(new String[0]);
        
        JComboBox<String> cbTurnoActual = new JComboBox<>(dias);
        JComboBox<String> cbTurnoDeseado = new JComboBox<>(dias);
        JCheckBox chkIntercambio = new JCheckBox("Es intercambio con otro empleado");
        JTextField txtOtroEmpleado = new JTextField(15);
        txtOtroEmpleado.setEnabled(false);
        
        chkIntercambio.addActionListener(e -> txtOtroEmpleado.setEnabled(chkIntercambio.isSelected()));
        
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(new JLabel("Turno actual:"));
        panel.add(cbTurnoActual);
        panel.add(Box.createVerticalStrut(10));
        panel.add(new JLabel("Turno deseado:"));
        panel.add(cbTurnoDeseado);
        panel.add(Box.createVerticalStrut(10));
        panel.add(chkIntercambio);
        panel.add(new JLabel("Login del otro empleado:"));
        panel.add(txtOtroEmpleado);
        
        int result = JOptionPane.showConfirmDialog(this, panel, "Solicitar Cambio de Turno", 
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        
        if (result == JOptionPane.OK_OPTION) {
            String diaActual = (String) cbTurnoActual.getSelectedItem();
            String diaDeseado = (String) cbTurnoDeseado.getSelectedItem();
            Turno turnoActual = turnosPorDia.get(diaActual);
            Turno turnoDeseado = turnosPorDia.get(diaDeseado);
            
            String loginSecundario = chkIntercambio.isSelected() ? txtOtroEmpleado.getText() : null;
            
            var ticket = cafe.solicitarCambioTurno(
                    empleado.getLogin(), empleado.getPassword(),
                    turnoActual, turnoDeseado, loginSecundario);
            
            if (ticket != null) {
                JOptionPane.showMessageDialog(this, 
                        "Solicitud enviada al administrador.\nEstado: " + ticket.getEstado(),
                        "Éxito", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, 
                        "Error al crear la solicitud.\nVerifique los datos ingresados.",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}