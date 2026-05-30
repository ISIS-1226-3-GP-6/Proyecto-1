package ui.panels;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import control.Cafe;
import juego.JuegoDeMesa;
import reservacion.Reserva;
import usuarios.Cliente;

public class PanelPerfilCliente extends JPanel {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Cafe cafe;
    private Cliente cliente;
    private Reserva reservaActiva;
    private JLabel lblPuntos;
    private JLabel lblReserva;
    private JTable tablaFavoritos;
    private DefaultTableModel modeloTabla;
    
    public PanelPerfilCliente(Cafe cafe, Cliente cliente) {
        this.cafe = cafe;
        this.cliente = cliente;
        setLayout(new BorderLayout());	
        
        // info
        
        JPanel panelInfo = new JPanel(new GridLayout(3, 1, 5, 5));
        panelInfo.setBorder(BorderFactory.createTitledBorder("Información del Cliente"));
        
        lblPuntos = new JLabel();
        lblReserva = new JLabel();
        
        panelInfo.add(new JLabel("Login: " + cliente.getLogin()));
        panelInfo.add(lblPuntos);
        panelInfo.add(lblReserva);
        
        add(panelInfo, BorderLayout.NORTH);
        
        // favs
        String[] columnas = {"Juego", "Tipo", "Precio"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tablaFavoritos = new JTable(modeloTabla);
        JScrollPane scroll = new JScrollPane(tablaFavoritos);
        scroll.setBorder(BorderFactory.createTitledBorder("Juegos Favoritos"));
        add(scroll, BorderLayout.CENTER);
        
        JPanel panelBotones = new JPanel(new GridLayout(1, 3, 10, 10));
        
        JButton btnAgregarFav = new JButton("Agregar a Favoritos");
        btnAgregarFav.addActionListener(e -> agregarFavorito());
        
        JButton btnUsarPuntos = new JButton("Usar Puntos");
        btnUsarPuntos.addActionListener(e -> usarPuntos());
        
        JButton btnRefrescar = new JButton("Refrescar");
        btnRefrescar.addActionListener(e -> actualizarPerfil());
        
        panelBotones.add(btnAgregarFav);
        panelBotones.add(btnUsarPuntos);
        panelBotones.add(btnRefrescar);
        
        add(panelBotones, BorderLayout.SOUTH);
        
        actualizarPerfil();
    }
    
    public void setReservaActiva(Reserva reserva) {
        this.reservaActiva = reserva;
        actualizarPerfil();
    }
    
    private void actualizarPerfil() {
        lblPuntos.setText("Puntos de fidelidad: " + cliente.getPuntosFidelidad());
        
        if (reservaActiva != null && !reservaActiva.isTerminada()) {
            lblReserva.setText("Reserva activa: Mesa " + reservaActiva.getMesaId() + 
                    " | Personas: " + reservaActiva.getNumPersonas());
        } else {
            lblReserva.setText("No hay reserva activa");
        }
        
        modeloTabla.setRowCount(0);
        for (JuegoDeMesa juego : cliente.getJuegosFavoritos()) {
            Object[] fila = {juego.getNombre(), juego.getTipoJuego(), "$" + juego.getPrecio()};
            modeloTabla.addRow(fila);
        }
    }
    
    private void agregarFavorito() {
        List<JuegoDeMesa> juegos = new ArrayList<>(cafe.getCatalogoJuegos());
        
        JComboBox<String> cbJuegos = new JComboBox<>();
        for (JuegoDeMesa j : juegos) {
            cbJuegos.addItem(j.getNombre());
        }
        
        int result = JOptionPane.showConfirmDialog(this, cbJuegos, "Seleccionar juego favorito", 
                JOptionPane.OK_CANCEL_OPTION);
        
        if (result == JOptionPane.OK_OPTION) {
            String nombre = (String) cbJuegos.getSelectedItem();
            JuegoDeMesa juego = juegos.stream()
                    .filter(j -> j.getNombre().equals(nombre))
                    .findFirst()
                    .orElse(null);
            
            if (juego != null) {
                cliente.agregarJuegoFav(juego);
                JOptionPane.showMessageDialog(this, "Juego agregado a favoritos.");
                actualizarPerfil();
            }
        }
    }
    
    private void usarPuntos() {
        String input = JOptionPane.showInputDialog(this, 
            "Puntos disponibles: " + cliente.getPuntosFidelidad() + "\nPuntos a usar (1 punto = $1):",
            "Usar Puntos", JOptionPane.QUESTION_MESSAGE);
        
        if (input == null) return;
        
        try {
            int puntos = Integer.parseInt(input);
            if (puntos <= 0) {
                JOptionPane.showMessageDialog(this, "Ingrese una cantidad positiva.");
                return;
            }
            if (puntos > cliente.getPuntosFidelidad()) {
                JOptionPane.showMessageDialog(this, "Puntos insuficientes.");
                return;
            }
            
            cliente.usarPuntos(puntos);
            JOptionPane.showMessageDialog(this, 
                "Se aplicaron $" + puntos + " de descuento.\n" +
                "Puntos restantes: " + cliente.getPuntosFidelidad(),
                "Descuento aplicado", JOptionPane.INFORMATION_MESSAGE);
            actualizarPerfil();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Ingrese un número válido.");
        }
    }
}