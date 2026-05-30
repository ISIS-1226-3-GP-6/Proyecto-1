package ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import control.Cafe;
import usuarios.Cliente;
import usuarios.Empleado;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class VentanaPrincipal extends JFrame {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Cafe cafe;
    private JPanel panelSeleccion;
    private JPanel panelLogin;
    private CardLayout cardLayout;
    
    public VentanaPrincipal(Cafe cafe) {
        this.cafe = cafe;
        setTitle("Café - Board Game Cafe");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 400);
        setLocationRelativeTo(null);
        
        cardLayout = new CardLayout();
        setLayout(cardLayout);
        
        panelSeleccion = crearPanelSeleccion();
        add(panelSeleccion, "seleccion");
        
        panelLogin = crearPanelLogin();
        add(panelLogin, "login");
        
        cardLayout.show(getContentPane(), "seleccion");
        setVisible(true);
        
        
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                cafe.save();
            }
        });
    }
 
    
    private JPanel crearPanelSeleccion() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(50, 50, 80));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        
        JLabel titulo = new JLabel("Café");
        titulo.setFont(new Font("Arial", Font.BOLD, 24));
        titulo.setForeground(Color.WHITE);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(titulo, gbc);
        
        JButton btnCliente = new JButton(" Cliente");
        btnCliente.setPreferredSize(new Dimension(150, 40));
        btnCliente.addActionListener(e -> {
            new VentanaCliente(cafe).setVisible(true);
            dispose(); 
        });
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        panel.add(btnCliente, gbc);
        
        JButton btnEmpleado = new JButton(" Empleado");
        btnEmpleado.setPreferredSize(new Dimension(150, 40));
        btnEmpleado.addActionListener(e -> mostrarLogin("empleado"));
        gbc.gridx = 1;
        panel.add(btnEmpleado, gbc);
        
        JButton btnAdmin = new JButton(" Administrador");
        btnAdmin.setPreferredSize(new Dimension(150, 40));
        btnAdmin.addActionListener(e -> mostrarLogin("admin"));
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        panel.add(btnAdmin, gbc);
        
        return panel;
    }
    
    
    private JPanel crearPanelLogin() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(50, 50, 80));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        
        JLabel lblLogin = new JLabel("Login:");
        lblLogin.setForeground(Color.WHITE);
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(lblLogin, gbc);
        
        JTextField txtLogin = new JTextField(15);
        gbc.gridx = 1;
        panel.add(txtLogin, gbc);
        
        JLabel lblPassword = new JLabel("Contraseña:");
        lblPassword.setForeground(Color.WHITE);
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(lblPassword, gbc);
        
        JPasswordField txtPassword = new JPasswordField(15);
        gbc.gridx = 1;
        panel.add(txtPassword, gbc);
        
        JButton btnAceptar = new JButton("Aceptar");
        JButton btnCancelar = new JButton("Cancelar");
        
        gbc.gridy = 2;
        gbc.gridx = 0;
        panel.add(btnAceptar, gbc);
        gbc.gridx = 1;
        panel.add(btnCancelar, gbc);
        
        // Acciones
        btnAceptar.addActionListener(e -> {
            String login = txtLogin.getText();
            String password = new String(txtPassword.getPassword());
            autenticarYRol(login, password);
        });
        
        btnCancelar.addActionListener(e -> {
            txtLogin.setText("");
            txtPassword.setText("");
            cardLayout.show(getContentPane(), "seleccion");
        });
        
        return panel;
    }
    
    private String rolSeleccionado;
    
    private void mostrarLogin(String rol) {
        this.rolSeleccionado = rol;
        cardLayout.show(getContentPane(), "login");
    }
    
    private void autenticarYRol(String login, String password) {
        boolean exito = false;
        
        switch (rolSeleccionado) {
            case "cliente":
            	new VentanaCliente(cafe).setVisible(true);
                dispose();
                return;
            case "empleado":
                exito = cafe.iniciarSesion(login, password) && cafe.getUsuarioActivo() instanceof Empleado;
                if (exito) {
                    new VentanaEmpleado(cafe).setVisible(true);
                    dispose();
                }
                break;
            case "admin":
                exito = cafe.esAdmin(login, password);
                if (exito) {
                    new VentanaAdministrador(cafe).setVisible(true);
                    dispose();
                }
                break;
        }
        
        if (!exito) {
            JOptionPane.showMessageDialog(this, "Credenciales incorrectas", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}