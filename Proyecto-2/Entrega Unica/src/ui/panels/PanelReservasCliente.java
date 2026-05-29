package ui.panels;

import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.*;

import control.Cafe;
import reservacion.Reserva;
import usuarios.Cliente;

public class PanelReservasCliente extends JPanel {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Cafe cafe;
    private String loginCliente;
    private String passwordCliente;
    
    private JSpinner spnPersonas;
    private JCheckBox chkMenores;
    private JCheckBox chkNinos;
    private JTextArea txtEstado;
    
    public PanelReservasCliente(Cafe cafe, String login, String password) {
        this.cafe = cafe;
        this.loginCliente = login;
        this.passwordCliente = password;
        setLayout(new GridBagLayout());
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Número de personas
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(new JLabel("Número de personas:"), gbc);
        
        spnPersonas = new JSpinner(new SpinnerNumberModel(1, 1, 20, 1));
        gbc.gridx = 1;
        add(spnPersonas, gbc);
        
        // Menores
        gbc.gridx = 0;
        gbc.gridy = 1;
        add(new JLabel("¿Hay menores de edad?"), gbc);
        chkMenores = new JCheckBox();
        gbc.gridx = 1;
        add(chkMenores, gbc);
        
        // Niños
        gbc.gridx = 0;
        gbc.gridy = 2;
        add(new JLabel("¿Hay niños (menores de 5 años)?"), gbc);
        chkNinos = new JCheckBox();
        gbc.gridx = 1;
        add(chkNinos, gbc);
        
        // Botón crear reserva
        JButton btnCrear = new JButton("Crear Reserva");
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        btnCrear.addActionListener(e -> crearReserva());
        add(btnCrear, gbc);
        
        // Área de estado
        txtEstado = new JTextArea(10, 30);
        txtEstado.setEditable(false);
        JScrollPane scroll = new JScrollPane(txtEstado);
        gbc.gridy = 4;
        add(scroll, gbc);
        
        actualizarEstado();
    }
    
    private void crearReserva() {
        int personas = (int) spnPersonas.getValue();
        boolean menores = chkMenores.isSelected();
        boolean ninos = chkNinos.isSelected();
        
        boolean ok = cafe.crearReservacion(loginCliente, passwordCliente, personas, menores, ninos);
        if (ok) {
            JOptionPane.showMessageDialog(this, "Reserva creada exitosamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            actualizarEstado();
        } else {
            JOptionPane.showMessageDialog(this, "Error al crear reserva. Capacidad excedida o sin mesas disponibles.", 
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void actualizarEstado() {
        txtEstado.setText("");
        // Buscar reserva activa del cliente
        Reserva activa = cafe.getReservas().stream()
                .filter(r -> r.getCliente().getLogin().equals(loginCliente) && !r.isTerminada())
                .findFirst()
                .orElse(null);
        
        if (activa != null) {
            txtEstado.append("=== RESERVA ACTIVA ===\n");
            txtEstado.append("Mesa: " + activa.getMesaId() + "\n");
            txtEstado.append("Personas: " + activa.getNumPersonas() + "\n");
            txtEstado.append("Menores: " + (activa.isHayMenores() ? "Sí" : "No") + "\n");
            txtEstado.append("Niños: " + (activa.isHayNinos() ? "Sí" : "No") + "\n");
            txtEstado.append("Préstamos activos: " + activa.getPrestamosActivos().size() + "/2\n");
        } else {
            txtEstado.append("No tienes una reserva activa.\n");
            txtEstado.append("Crea una reserva para acceder a los juegos en préstamo.");
        }
    }
}