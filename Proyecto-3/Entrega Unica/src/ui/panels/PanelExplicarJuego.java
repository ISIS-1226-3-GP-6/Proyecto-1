package ui.panels;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import javax.swing.*;

import control.Cafe;
import juego.JuegoDeMesa;
import usuarios.Mesero;

public class PanelExplicarJuego extends JPanel {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Cafe cafe;
    private Mesero mesero;
    private JComboBox<String> cbJuegos;
    private JTextArea txtResultado;
    
    public PanelExplicarJuego(Cafe cafe, Mesero mesero) {
        this.cafe = cafe;
        this.mesero = mesero;
        setLayout(new BorderLayout());
        
        JPanel panelSeleccion = new JPanel(new GridLayout(2, 2, 10, 10));
        
        panelSeleccion.add(new JLabel("Juego difícil a explicar:"));
        cbJuegos = new JComboBox<>();
        
        for (var juego : cafe.getCatalogoJuegos()) {
            if (juego.getDificultad()) {
                cbJuegos.addItem(juego.getNombre());
            }
        }
        
        panelSeleccion.add(cbJuegos);
        
        JButton btnVerificar = new JButton("Verificar si puedo explicar");
        panelSeleccion.add(btnVerificar);
        panelSeleccion.add(new JLabel()); 
        
        add(panelSeleccion, BorderLayout.NORTH);
        
        txtResultado = new JTextArea(6, 40);
        txtResultado.setEditable(false);
        txtResultado.setFont(new java.awt.Font("Monospaced", java.awt.Font.PLAIN, 12));
        add(new JScrollPane(txtResultado), BorderLayout.CENTER);
        
        btnVerificar.addActionListener(e -> verificarExplicacion());
        
        txtResultado.setText(
            "=== EXPLICAR JUEGO A CLIENTE ===\n\n" +
            "Selecciona un juego difícil de la lista y presiona el botón\n" +
            "para verificar si puedes explicarlo.\n\n" +
            "Si conoces el juego, puedes acercarte a la mesa del cliente\n" +
            "y explicar las reglas."
        );
    }
    
    private void verificarExplicacion() {
        String juegoNombre = (String) cbJuegos.getSelectedItem();
        if (juegoNombre == null) {
            txtResultado.setText("No hay juegos difíciles disponibles.");
            return;
        }
        
        JuegoDeMesa juego = cafe.getCatalogoJuegos().stream()
                .filter(j -> j.getNombre().equals(juegoNombre))
                .findFirst()
                .orElse(null);
        
        if (juego == null) {
            txtResultado.setText("Juego no encontrado.");
            return;
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append("=== RESULTADO ===\n\n");
        sb.append("Juego: ").append(juego.getNombre()).append("\n");
        
        if (mesero.puedeExplicar(juego)) {
            sb.append(" PUEDES explicar este juego.\n\n");
            sb.append("Acércate a la mesa del cliente y ofrece tu ayuda\n");
            sb.append("para explicar las reglas del juego.");
        } else {
            sb.append(" NO PUEDES explicar este juego.\n\n");
            sb.append("No tienes este juego en tu lista de conocidos.\n");
            sb.append("Pide al administrador que te capacite en este juego.");
        }
        
        txtResultado.setText(sb.toString());
    }
}