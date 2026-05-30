package ui.panels;

import java.awt.BorderLayout;
import java.awt.Font;
import javax.swing.*;

import control.Cafe;

public class PanelGenerarDescuento extends JPanel {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Cafe cafe;
    private String loginEmpleado;
    
    public PanelGenerarDescuento(Cafe cafe, String login) {
        this.cafe = cafe;
        this.loginEmpleado = login;
        setLayout(new BorderLayout());
        
        JTextArea txtInfo = new JTextArea();
        txtInfo.setEditable(false);
        txtInfo.setFont(new Font("Monospaced", Font.PLAIN, 14));
        txtInfo.setText(
            "=== GENERAR CÓDIGO DE DESCUENTO (10%) ===\n\n" +
            "Como empleado, puedes generar un código de descuento\n" +
            "para compartir con los clientes.\n\n" +
            "El cliente recibirá un 10% de descuento en su compra.\n" +
            "El código es válido por tiempo limitado.\n"
        );
        
        add(new JScrollPane(txtInfo), BorderLayout.CENTER);
        
        JButton btnGenerar = new JButton("Generar Nuevo Código");
        btnGenerar.addActionListener(e -> generarCodigo());
        add(btnGenerar, BorderLayout.SOUTH);
    }
    
    private void generarCodigo() {
        String codigo = "DESC10_" + loginEmpleado + "_" + System.currentTimeMillis();
        
        JTextArea txtCodigo = new JTextArea(3, 30);
        txtCodigo.setText(codigo);
        txtCodigo.setEditable(false);
        txtCodigo.setFont(new Font("Monospaced", Font.BOLD, 14));
        
        JOptionPane.showMessageDialog(this, 
            new Object[]{"Comparte este código con el cliente:", txtCodigo},
            "Código de Descuento", JOptionPane.INFORMATION_MESSAGE);
    }
}