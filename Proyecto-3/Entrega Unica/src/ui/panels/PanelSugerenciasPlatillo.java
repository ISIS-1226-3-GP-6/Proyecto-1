package ui.panels;

import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;

import cafeteria.TicketNuevoPlatillo;
import control.Cafe;
import usuarios.Empleado;

public class PanelSugerenciasPlatillo extends JPanel {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Cafe cafe;
    private Empleado empleado;
    
    private JTextField txtNombre;
    private JSpinner spnPrecio;
    private JComboBox<String> cbTipo;
    private JPanel panelOpcionesExtra;
    private JCheckBox chkAlcoholica;
    private JCheckBox chkCaliente;
    private JTextField txtAlergenos;
    
    public PanelSugerenciasPlatillo(Cafe cafe, Empleado empleado) {
        this.cafe = cafe;
        this.empleado = empleado;
        setLayout(new GridBagLayout());
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(new JLabel("Nombre del platillo:"), gbc);
        txtNombre = new JTextField(20);
        gbc.gridx = 1;
        add(txtNombre, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 1;
        add(new JLabel("Precio sugerido:"), gbc);
        spnPrecio = new JSpinner(new SpinnerNumberModel(5000, 500, 100000, 500));
        gbc.gridx = 1;
        add(spnPrecio, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 2;
        add(new JLabel("Tipo:"), gbc);
        cbTipo = new JComboBox<>(new String[]{"Comida", "Bebida"});
        cbTipo.addActionListener(e -> actualizarOpcionesExtra());
        gbc.gridx = 1;
        add(cbTipo, gbc);
        
        panelOpcionesExtra = new JPanel();
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        add(panelOpcionesExtra, gbc);
        
        JButton btnEnviar = new JButton("Enviar Sugerencia");
        btnEnviar.addActionListener(e -> enviarSugerencia());
        gbc.gridy = 4;
        add(btnEnviar, gbc);
        
        JTextArea txtInfo = new JTextArea(5, 30);
        txtInfo.setEditable(false);
        txtInfo.setText("=== INFORMACIÓN ===\n\n" +
                "Las sugerencias de platillos serán revisadas por el administrador.\n" +
                "Una vez aprobadas, aparecerán en el menú del café.\n\n" +
                "Para bebidas: indica si es alcohólica o caliente.\n" +
                "Para comidas: indica los alérgenos (separados por coma).");
        
        gbc.gridy = 5;
        add(new JScrollPane(txtInfo), gbc);
        
        actualizarOpcionesExtra();
    }
    
    private void actualizarOpcionesExtra() {
        panelOpcionesExtra.removeAll();
        
        if (cbTipo.getSelectedItem().equals("Bebida")) {
            chkAlcoholica = new JCheckBox("Alcohólica");
            chkCaliente = new JCheckBox("Caliente");
            panelOpcionesExtra.add(chkAlcoholica);
            panelOpcionesExtra.add(chkCaliente);
        } else {
            txtAlergenos = new JTextField(20);
            panelOpcionesExtra.add(new JLabel("Alérgenos (separados por coma):"));
            panelOpcionesExtra.add(txtAlergenos);
        }
        
        panelOpcionesExtra.revalidate();
        panelOpcionesExtra.repaint();
    }
    
    private void enviarSugerencia() {
        String nombre = txtNombre.getText().trim();
        double precio = ((Number) spnPrecio.getValue()).doubleValue();
        
        if (nombre.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor ingrese un nombre.", 
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        TicketNuevoPlatillo ticket = null;
        
        if (cbTipo.getSelectedItem().equals("Bebida")) {
            boolean alcoholica = chkAlcoholica.isSelected();
            boolean caliente = chkCaliente.isSelected();
            ticket = cafe.solicitarCrearBebida(empleado.getLogin(), empleado.getPassword(), 
                    precio, alcoholica, caliente);
        } else {
            String alergenosStr = txtAlergenos.getText().trim();
            List<String> alergenos = alergenosStr.isEmpty() ? new ArrayList<>() 
                    : List.of(alergenosStr.split(","));
            ticket = cafe.solicitarCrearComida(empleado.getLogin(), empleado.getPassword(), 
                    precio, alergenos);
        }
        
        if (ticket != null) {
            JOptionPane.showMessageDialog(this, 
                    "Sugerencia enviada al administrador.\nEstado: Pendiente de aprobación.",
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);
            limpiarFormulario();
        } else {
            JOptionPane.showMessageDialog(this, 
                    "Error al enviar la sugerencia.", 
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void limpiarFormulario() {
        txtNombre.setText("");
        spnPrecio.setValue(5000);
        if (cbTipo.getSelectedItem().equals("Bebida")) {
            chkAlcoholica.setSelected(false);
            chkCaliente.setSelected(false);
        } else {
            txtAlergenos.setText("");
        }
    }
}