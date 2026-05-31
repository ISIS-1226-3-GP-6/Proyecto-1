package ui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

import control.Cafe;
import ui.panels.PanelAdministradorJuegos;
import ui.panels.PanelAdministradorInventario;
import ui.panels.PanelAdministradorTickets;
import ui.panels.PanelAdministradorTorneos;
import ui.panels.PanelAdministradorCatalogo;
import ui.panels.PanelAdministradorEmpleados;
import ui.panels.PanelAdministradorGraficas;

public class VentanaAdministrador extends JFrame {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Cafe cafe;
    private String loginAdmin;
    
    private JPanel panelContenido;
    private CardLayout cardLayout;
    private PanelAdministradorCatalogo panelCatalogo;

    private PanelAdministradorJuegos panelJuegos;
    private PanelAdministradorInventario panelInventario;
    private PanelAdministradorTickets panelTickets;
    private PanelAdministradorGraficas panelGraficas;
    private JPanel panelTorneos;
    private JPanel panelEmpleados;
    
    public VentanaAdministrador(Cafe cafe) {
        this.cafe = cafe;
        setTitle("Café - Administrador");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1100, 750);
        setLocationRelativeTo(null);
        
        initComponents();
        
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent e) {
            	cafe.save();
            	cafe.cerrarSesion();
            }
        });
        
        setVisible(true);
    }
    
    private void initComponents() {
        setLayout(new BorderLayout());
        
        // Panel superior
        JPanel panelSuperior = new JPanel(new BorderLayout());
        panelSuperior.setBackground(new Color(50, 50, 80));
        panelSuperior.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        JLabel lblTitulo = new JLabel("Café - Panel de Administrador");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitulo.setForeground(Color.WHITE);
        panelSuperior.add(lblTitulo, BorderLayout.WEST);
        
        JButton btnCerrarSesion = new JButton("Cerrar Sesión");
        btnCerrarSesion.addActionListener(e -> cerrarSesion());
        panelSuperior.add(btnCerrarSesion, BorderLayout.EAST);
        
        add(panelSuperior, BorderLayout.NORTH);
        
        // Panel izquierdo (menú)
        JPanel panelMenu = crearPanelMenu();
        add(panelMenu, BorderLayout.WEST);
        
        // Panel central
        cardLayout = new CardLayout();
        panelContenido = new JPanel(cardLayout);
        
        panelCatalogo = new PanelAdministradorCatalogo(cafe);        // Ver catálogo
        panelJuegos = new PanelAdministradorJuegos(cafe);            // Agregar juego
        panelInventario = new PanelAdministradorInventario(cafe);    // Mover, reparar, marcar desaparecido
        panelTickets = new PanelAdministradorTickets(cafe);          // Aprobar/rechazar tickets
        panelTorneos = new PanelAdministradorTorneos(cafe);          // Crear, asignar, eliminar torneos
        panelEmpleados = new PanelAdministradorEmpleados(cafe);      // Registrar empleado
        panelGraficas = new PanelAdministradorGraficas(cafe);
        
        panelContenido.add(panelCatalogo, "catalogo");
        panelContenido.add(panelJuegos, "juegos");
        panelContenido.add(panelInventario, "inventario");
        panelContenido.add(panelTickets, "tickets");
        panelContenido.add(panelTorneos, "torneos");
        panelContenido.add(panelEmpleados, "empleados");
        panelContenido.add(panelGraficas, "graficas");
        
        add(panelContenido, BorderLayout.CENTER);
        
        cardLayout.show(panelContenido, "juegos");
    }
    
    private JPanel crearPanelMenu() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        panel.setPreferredSize(new Dimension(220, 0));
        panel.setBackground(new Color(240, 240, 250));
        
        String[][] opciones = {
            {"Gestionar Juegos", "juegos"},
            {"Gestionar Inventario", "inventario"},
            {"Resolver Tickets", "tickets"},
            {"Gestionar Torneos", "torneos"},
            {"Gestionar Empleados", "empleados"},
            {"Ver Graficas", "graficas"}
        };
        
        for (String[] opcion : opciones) {
            JButton btn = new JButton(opcion[0]);
            btn.setAlignmentX(Component.CENTER_ALIGNMENT);
            btn.setMaximumSize(new Dimension(200, 40));
            btn.addActionListener(e -> cardLayout.show(panelContenido, opcion[1]));
            panel.add(btn);
            panel.add(Box.createRigidArea(new Dimension(0, 10)));
        }
        
        return panel;
    }
    
    
    private void cerrarSesion() {
    	cafe.save();
        cafe.cerrarSesion();
        dispose();
        new VentanaPrincipal(cafe).setVisible(true);
    }
}