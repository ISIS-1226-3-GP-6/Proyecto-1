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
import ui.panels.*;
import usuarios.Empleado;
import usuarios.Mesero;
import usuarios.Cocinero;

public class VentanaEmpleado extends JFrame {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Cafe cafe;
    private Empleado empleado;
    private boolean esMesero;
    
    private JPanel panelContenido;
    private CardLayout cardLayout;
    private JLabel lblEstado;
    
    private PanelTurnosEmpleado panelTurnos;
    private PanelSugerenciasPlatillo panelSugerencias;
    private PanelComprasEmpleado panelCompras;
    private PanelGenerarDescuento panelDescuento;
    private PanelJuegosMesero panelJuegosMesero;
    private PanelExplicarJuego panelExplicarJuego;
    
    public VentanaEmpleado(Cafe cafe) {
        this.cafe = cafe;
        this.empleado = (Empleado) cafe.getUsuarioActivo();
        this.esMesero = empleado instanceof Mesero;
        
        setTitle("Café - Empleado: " + empleado.getLogin() + 
                (esMesero ? " (Mesero)" : " (Cocinero)"));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);
        
        initComponents();
        
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                cafe.save();
            }
        });
        
        setVisible(true);
    }
    
    private void initComponents() {
        setLayout(new BorderLayout());
        
        JPanel panelSuperior = new JPanel(new BorderLayout());
        panelSuperior.setBackground(new Color(50, 50, 80));
        panelSuperior.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        JLabel lblTitulo = new JLabel("Café - Panel de Empleado ");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitulo.setForeground(Color.WHITE);
        panelSuperior.add(lblTitulo, BorderLayout.WEST);
        
        JButton btnCerrarSesion = new JButton("Cerrar Sesión");
        btnCerrarSesion.addActionListener(e -> cerrarSesion());
        panelSuperior.add(btnCerrarSesion, BorderLayout.EAST);
        
        add(panelSuperior, BorderLayout.NORTH);
        
        JPanel panelMenu = crearPanelMenu();
        add(panelMenu, BorderLayout.WEST);
        
        cardLayout = new CardLayout();
        panelContenido = new JPanel(cardLayout);
        
        panelTurnos = new PanelTurnosEmpleado(cafe, empleado);
        panelSugerencias = new PanelSugerenciasPlatillo(cafe, empleado);
        panelCompras = new PanelComprasEmpleado(cafe, empleado.getLogin(), empleado.getPassword());
        panelDescuento = new PanelGenerarDescuento(cafe, empleado.getLogin());
        
        panelContenido.add(panelTurnos, "turnos");
        panelContenido.add(panelSugerencias, "sugerencias");
        panelContenido.add(panelCompras, "compras");
        panelContenido.add(panelDescuento, "descuento");
        
        if (esMesero) {
            panelJuegosMesero = new PanelJuegosMesero(cafe, (Mesero) empleado);
            panelExplicarJuego = new PanelExplicarJuego(cafe, (Mesero) empleado);
            panelContenido.add(panelJuegosMesero, "juegosMesero");
            panelContenido.add(panelExplicarJuego, "explicarJuego");
        }
        
        add(panelContenido, BorderLayout.CENTER);
        
        JPanel panelInferior = new JPanel(new BorderLayout());
        panelInferior.setBorder(BorderFactory.createTitledBorder("Información"));
        lblEstado = new JLabel();
        actualizarEstado();
        panelInferior.add(lblEstado, BorderLayout.WEST);
        add(panelInferior, BorderLayout.SOUTH);
        
        cardLayout.show(panelContenido, "turnos");
    }
    
    private JPanel crearPanelMenu() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        panel.setPreferredSize(new Dimension(220, 0));
        panel.setBackground(new Color(240, 240, 250));
        
        String[][] opciones = {
            {" Consultar Turnos", "turnos"},
            {" Sugerir Platillo", "sugerencias"},
            {" Comprar (20% descuento)", "compras"},
            {" Generar Código Descuento (10%)", "descuento"}
        };
        
        for (String[] opcion : opciones) {
            JButton btn = new JButton(opcion[0]);
            btn.setAlignmentX(Component.CENTER_ALIGNMENT);
            btn.setMaximumSize(new Dimension(200, 35));
            btn.addActionListener(e -> cardLayout.show(panelContenido, opcion[1]));
            panel.add(btn);
            panel.add(Box.createRigidArea(new Dimension(0, 10)));
        }
        
        if (esMesero) {
            panel.add(Box.createRigidArea(new Dimension(0, 20)));
            
            JLabel lblMesero = new JLabel("--- MESERO ---");
            lblMesero.setAlignmentX(Component.CENTER_ALIGNMENT);
            panel.add(lblMesero);
            panel.add(Box.createRigidArea(new Dimension(0, 10)));
            
            JButton btnJuegos = new JButton("🎮 Juegos que Conozco");
            btnJuegos.setAlignmentX(Component.CENTER_ALIGNMENT);
            btnJuegos.setMaximumSize(new Dimension(200, 35));
            btnJuegos.addActionListener(e -> cardLayout.show(panelContenido, "juegosMesero"));
            panel.add(btnJuegos);
            panel.add(Box.createRigidArea(new Dimension(0, 10)));
            
            JButton btnExplicar = new JButton("📖 Explicar Juego Difícil");
            btnExplicar.setAlignmentX(Component.CENTER_ALIGNMENT);
            btnExplicar.setMaximumSize(new Dimension(200, 35));
            btnExplicar.addActionListener(e -> cardLayout.show(panelContenido, "explicarJuego"));
            panel.add(btnExplicar);
        }
        
        return panel;
    }
    
    private void actualizarEstado() {
        String texto = "Empleado: " + empleado.getLogin() + " | Rol: " + 
                (esMesero ? "Mesero" : "Cocinero");
        lblEstado.setText(texto);
    }
    
    private void cerrarSesion() {
        cafe.save();
        cafe.cerrarSesion();
        dispose();
        new VentanaPrincipal(cafe).setVisible(true);
    }
}