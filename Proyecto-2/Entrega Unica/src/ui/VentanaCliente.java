package ui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

import control.Cafe;
import ui.panels.*;
import reservacion.Reserva;
import usuarios.Cliente;
import usuarios.Usuario;

public class VentanaCliente extends JFrame {
    private static final long serialVersionUID = 1L;
    private Cafe cafe;
    private Cliente cliente;
    private Reserva reservaActiva;
    
    private CardLayout cardLayoutPrincipal;  // Para alternar entre login y menú principal
    private CardLayout cardLayoutContenido;  // Para alternar entre paneles del menú
    private JPanel panelContenido;
    private JLabel lblEstado;
    private JPanel panelLogin;
    private JPanel panelMenuPrincipal;
    
    private PanelReservasCliente panelReservas;
    private PanelCatalogoCliente panelCatalogo;
    private PanelPrestamosCliente panelPrestamos;
    private PanelComprasCliente panelCompras;
    private PanelTorneosCliente panelTorneos;
    private PanelPerfilCliente panelPerfil;
    
    public VentanaCliente(Cafe cafe) {
        this.cafe = cafe;
        
        setTitle("Café - Cliente");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1100, 750);
        setLocationRelativeTo(null);
        
        // Usar CardLayout para alternar entre login y menú principal
        cardLayoutPrincipal = new CardLayout();
        setLayout(cardLayoutPrincipal);
        
        // Panel de login/registro
        panelLogin = crearPanelLogin();
        add(panelLogin, "login");
        
        // Panel del menú principal (se mostrará después del login)
        panelMenuPrincipal = new JPanel(new BorderLayout());
        add(panelMenuPrincipal, "principal");
        
        // Mostrar panel de login primero
        cardLayoutPrincipal.show(getContentPane(), "login");
        
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                cafe.save();
            }
        });
        
        setVisible(true);
    }
    
    private JPanel crearPanelLogin() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(50, 50, 80));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        
        // Título
        JLabel lblTitulo = new JLabel("Bienvenido al Café");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 24));
        lblTitulo.setForeground(Color.WHITE);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(lblTitulo, gbc);
        
        // Login
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        JLabel lblLogin = new JLabel("Login:");
        lblLogin.setForeground(Color.WHITE);
        panel.add(lblLogin, gbc);
        
        JTextField txtLogin = new JTextField(15);
        gbc.gridx = 1;
        panel.add(txtLogin, gbc);
        
        // Password
        gbc.gridx = 0;
        gbc.gridy = 2;
        JLabel lblPassword = new JLabel("Contraseña:");
        lblPassword.setForeground(Color.WHITE);
        panel.add(lblPassword, gbc);
        
        JPasswordField txtPassword = new JPasswordField(15);
        gbc.gridx = 1;
        panel.add(txtPassword, gbc);
        
        // Botones
        JPanel panelBotones = new JPanel();
        panelBotones.setOpaque(false);
        
        JButton btnIniciarSesion = new JButton("Iniciar Sesión");
        btnIniciarSesion.addActionListener(e -> {
            String login = txtLogin.getText().trim();
            String password = new String(txtPassword.getPassword());
            
            if (login.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(panel, "Complete todos los campos.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (cafe.iniciarSesion(login, password) && cafe.getUsuarioActivo() instanceof Cliente) {
                this.cliente = (Cliente) cafe.getUsuarioActivo();
                inicializarMenuPrincipal();
                cardLayoutPrincipal.show(getContentPane(), "principal");
                setTitle("Café - Cliente: " + cliente.getLogin());
            } else {
                JOptionPane.showMessageDialog(panel, "Login o contraseña incorrectos.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        JButton btnRegistrarse = new JButton("Registrarse");
        btnRegistrarse.addActionListener(e -> {
            String login = txtLogin.getText().trim();
            String password = new String(txtPassword.getPassword());
            
            if (login.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(panel, "Complete todos los campos.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            boolean existe = cafe.getUsuarios().stream()
                    .anyMatch(u -> u.getLogin().equals(login));
            
            if (existe) {
                JOptionPane.showMessageDialog(panel, "El login ya existe. Elija otro.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            Cliente nuevo = new Cliente(login, password, 0);
            cafe.getUsuarios().add(nuevo);
            JOptionPane.showMessageDialog(panel, "Registro exitoso. Ahora inicia sesión.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            txtLogin.setText("");
            txtPassword.setText("");
        });
        
        panelBotones.add(btnIniciarSesion);
        panelBotones.add(btnRegistrarse);
        
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        panel.add(panelBotones, gbc);
        
        return panel;
    }
    
    private void inicializarMenuPrincipal() {
        panelMenuPrincipal.removeAll();
        panelMenuPrincipal.setLayout(new BorderLayout());
        
        // Panel superior
        JPanel panelSuperior = new JPanel(new BorderLayout());
        panelSuperior.setBackground(new Color(50, 50, 80));
        panelSuperior.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        JLabel lblTitulo = new JLabel("Café - Panel de Cliente");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitulo.setForeground(Color.WHITE);
        panelSuperior.add(lblTitulo, BorderLayout.WEST);
        
        JButton btnCerrarSesion = new JButton("Cerrar Sesión");
        btnCerrarSesion.addActionListener(e -> cerrarSesion());
        panelSuperior.add(btnCerrarSesion, BorderLayout.EAST);
        
        panelMenuPrincipal.add(panelSuperior, BorderLayout.NORTH);
        
        // Panel izquierdo (menú)
        JPanel panelMenu = crearPanelMenu();
        panelMenuPrincipal.add(panelMenu, BorderLayout.WEST);
        
        // Panel central con CardLayout para las diferentes vistas
        cardLayoutContenido = new CardLayout();  // CardLayout separado para el contenido
        panelContenido = new JPanel(cardLayoutContenido);
        
        // Crear paneles
        panelReservas = new PanelReservasCliente(cafe, cliente.getLogin(), cliente.getPassword());
        panelCatalogo = new PanelCatalogoCliente(cafe);
        panelPrestamos = new PanelPrestamosCliente(cafe, cliente.getLogin(), cliente.getPassword());
        panelCompras = new PanelComprasCliente(cafe, cliente.getLogin(), cliente.getPassword());
        panelTorneos = new PanelTorneosCliente(cafe, cliente);
        panelPerfil = new PanelPerfilCliente(cafe, cliente);
        
        panelContenido.add(panelReservas, "reservas");
        panelContenido.add(panelCatalogo, "catalogo");
        panelContenido.add(panelPrestamos, "prestamos");
        panelContenido.add(panelCompras, "compras");
        panelContenido.add(panelTorneos, "torneos");
        panelContenido.add(panelPerfil, "perfil");
        
        panelMenuPrincipal.add(panelContenido, BorderLayout.CENTER);
        
        // Panel inferior de estado
        JPanel panelInferior = new JPanel(new BorderLayout());
        panelInferior.setBorder(BorderFactory.createTitledBorder("Estado del Cliente"));
        lblEstado = new JLabel();
        actualizarEstado();
        panelInferior.add(lblEstado, BorderLayout.WEST);
        
        JButton btnRefrescarEstado = new JButton("Refrescar");
        btnRefrescarEstado.addActionListener(e -> actualizarEstado());
        panelInferior.add(btnRefrescarEstado, BorderLayout.EAST);
        
        panelMenuPrincipal.add(panelInferior, BorderLayout.SOUTH);
        
        // Mostrar panel de reservas por defecto
        cardLayoutContenido.show(panelContenido, "reservas");
        
        panelMenuPrincipal.revalidate();
        panelMenuPrincipal.repaint();
    }
    
    private JPanel crearPanelMenu() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        panel.setPreferredSize(new Dimension(220, 0));
        panel.setBackground(new Color(240, 240, 250));
        
        String[][] opciones = {
            {" Crear Reserva", "reservas"},
            {" Ver Catálogo", "catalogo"},
            {" Préstamos", "prestamos"},
            {" Compras", "compras"},
            {" Torneos", "torneos"},
            {" Mi Perfil", "perfil"}
        };
        
        for (String[] opcion : opciones) {
            JButton btn = new JButton(opcion[0]);
            btn.setAlignmentX(Component.CENTER_ALIGNMENT);
            btn.setMaximumSize(new Dimension(200, 35));
            String destino = opcion[1];
            btn.addActionListener(e -> {
                cardLayoutContenido.show(panelContenido, destino);
                actualizarEstado();
                actualizarReservaActiva();
                
                if (destino.equals("prestamos")) {
                    panelPrestamos.setReservaActiva(reservaActiva);
                }
                if (destino.equals("compras")) {
                    panelCompras.setReservaActiva(reservaActiva);
                }
                if (destino.equals("perfil")) {
                    panelPerfil.setReservaActiva(reservaActiva);
                }
            });
            panel.add(btn);
            panel.add(Box.createRigidArea(new Dimension(0, 10)));
        }
        
        return panel;
    }
    
    private void actualizarReservaActiva() {
        reservaActiva = cafe.getReservas().stream()
                .filter(r -> r.getCliente().getLogin().equals(cliente.getLogin()) && !r.isTerminada())
                .findFirst()
                .orElse(null);
    }
    
    private void actualizarEstado() {
        if (cliente == null) return;
        
        actualizarReservaActiva();
        
        String estadoTexto = "Puntos: " + cliente.getPuntosFidelidad() + " | ";
        if (reservaActiva != null) {
            estadoTexto += "Reserva activa: Mesa " + reservaActiva.getMesaId() + 
                          " | Personas: " + reservaActiva.getNumPersonas() +
                          " | Préstamos: " + reservaActiva.getPrestamosActivos().size() + "/2";
        } else {
            estadoTexto += "No hay reserva activa";
        }
        
        lblEstado.setText(estadoTexto);
    }
    
    private void cerrarSesion() {
        cafe.save();
        cafe.cerrarSesion();
        dispose();
        new VentanaPrincipal(cafe).setVisible(true);
    }
}