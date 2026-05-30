package ui.panels;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import control.Cafe;
import usuarios.Empleado;
import usuarios.Mesero;
import usuarios.Usuario;
import usuarios.Cocinero;

public class PanelAdministradorEmpleados extends JPanel {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Cafe cafe;
    private JTable tablaEmpleados;
    private DefaultTableModel modeloTabla;
    
    public PanelAdministradorEmpleados(Cafe cafe) {
        this.cafe = cafe;
        setLayout(new BorderLayout());
        
        String[] columnas = {"Login", "Rol", "Tipo"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tablaEmpleados = new JTable(modeloTabla);
        JScrollPane scroll = new JScrollPane(tablaEmpleados);
        add(scroll, BorderLayout.CENTER);
        
        JPanel panelBotones = new JPanel(new GridLayout(1, 2, 10, 10));
        
        JButton btnRegistrar = new JButton("Registrar Empleado");
        btnRegistrar.addActionListener(e -> registrarEmpleado());
        
        JButton btnRefrescar = new JButton("Refrescar");
        btnRefrescar.addActionListener(e -> actualizarTabla());
        
        panelBotones.add(btnRegistrar);
        panelBotones.add(btnRefrescar);
        add(panelBotones, BorderLayout.SOUTH);
        
        actualizarTabla();
    }
    
    private void actualizarTabla() {
        modeloTabla.setRowCount(0);
        for (Usuario u : cafe.getUsuarios()) {
            if (u instanceof Empleado) {
                String rol = (u instanceof Mesero) ? "Mesero" : "Cocinero";
                Object[] fila = {u.getLogin(), rol, u.getClass().getSimpleName()};
                modeloTabla.addRow(fila);
            }
        }
    }
    
    private void registrarEmpleado() {
        JPanel panel = new JPanel(new GridLayout(0, 2, 5, 5));
        
        JTextField txtLogin = new JTextField();
        JPasswordField txtPassword = new JPasswordField();
        JComboBox<String> cbRol = new JComboBox<>(new String[]{"mesero", "cocinero"});
        
        panel.add(new JLabel("Login:"));
        panel.add(txtLogin);
        panel.add(new JLabel("Contraseña:"));
        panel.add(txtPassword);
        panel.add(new JLabel("Rol:"));
        panel.add(cbRol);
        
        int result = JOptionPane.showConfirmDialog(this, panel, "Registrar Empleado", 
                JOptionPane.OK_CANCEL_OPTION);
        
        if (result == JOptionPane.OK_OPTION) {
            String login = txtLogin.getText().trim();
            String password = new String(txtPassword.getPassword());
            String rol = (String) cbRol.getSelectedItem();
            
            if (login.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Login y contraseña son obligatorios.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            boolean existe = cafe.getUsuarios().stream()
                    .anyMatch(u -> u.getLogin().equals(login));
            
            if (existe) {
                JOptionPane.showMessageDialog(this, "El login ya existe.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            Empleado nuevo = null;
            if (rol.equals("mesero")) {
                nuevo = new Mesero(login, password);
            } else {
                nuevo = new Cocinero(login, password);
            }
            
            cafe.getUsuarios().add(nuevo);
            JOptionPane.showMessageDialog(this, "Empleado registrado exitosamente.");
            actualizarTabla();
        }
    }
}