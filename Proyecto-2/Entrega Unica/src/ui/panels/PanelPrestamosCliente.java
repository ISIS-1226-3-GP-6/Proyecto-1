package ui.panels;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import control.Cafe;
import juego.JuegoFisico;
import juego.Prestamo;
import reservacion.Reserva;

public class PanelPrestamosCliente extends JPanel {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Cafe cafe;
    private String loginCliente;
    private String passwordCliente;
    private Reserva reservaActiva;
    private JTable tablaJuegos;
    private DefaultTableModel modeloTabla;
    private List<JuegoFisico> juegosSeleccionados;
    private JTextArea txtEstado;
    
    public PanelPrestamosCliente(Cafe cafe, String login, String password) {
        this.cafe = cafe;
        this.loginCliente = login;
        this.passwordCliente = password;
        this.juegosSeleccionados = new ArrayList<>();
        setLayout(new BorderLayout());
        
        // Panel de selección de juegos
        String[] columnas = {"Seleccionar", "Juego", "Disponible"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public Class<?> getColumnClass(int column) {
                return column == 0 ? Boolean.class : String.class;
            }
        };
        
        tablaJuegos = new JTable(modeloTabla);
        JScrollPane scroll = new JScrollPane(tablaJuegos);
        add(scroll, BorderLayout.CENTER);
        
        // Panel de botones
        JPanel panelBotones = new JPanel(new GridLayout(1, 3, 10, 10));
        
        JButton btnSeleccionar = new JButton("Seleccionar Juegos");
        btnSeleccionar.addActionListener(e -> seleccionarJuegos());
        
        JButton btnSolicitar = new JButton("Solicitar Préstamo");
        btnSolicitar.addActionListener(e -> solicitarPrestamo());
        
        JButton btnDevolver = new JButton("Devolver Juegos");
        btnDevolver.addActionListener(e -> devolverJuegos());
        
        panelBotones.add(btnSeleccionar);
        panelBotones.add(btnSolicitar);
        panelBotones.add(btnDevolver);
        
        add(panelBotones, BorderLayout.NORTH);
        
        // Área de estado
        txtEstado = new JTextArea(6, 40);
        txtEstado.setEditable(false);
        add(new JScrollPane(txtEstado), BorderLayout.SOUTH);
        
        actualizarCatalogo();
        actualizarEstado();
    }
    
    public void setReservaActiva(Reserva reserva) {
        this.reservaActiva = reserva;
        actualizarEstado();
        actualizarCatalogo();
    }
    
    private void actualizarCatalogo() {
        modeloTabla.setRowCount(0);
        List<JuegoFisico> juegos = cafe.getCatalogoPrestamo();
        
        for (JuegoFisico jf : juegos) {
            boolean disponible = jf.estaDisponible();
            Object[] fila = {false, jf.getJuegoBase().getNombre(), disponible ? "Sí" : "No"};
            modeloTabla.addRow(fila);
        }
    }
    
    private void actualizarEstado() {
        txtEstado.setText("");
        StringBuilder sb = new StringBuilder();
        sb.append("=== ESTADO DE PRÉSTAMOS ===\n\n");
        
        if (reservaActiva == null) {
            sb.append("❌ No tienes una reserva activa.\n");
            sb.append("Crea una reserva antes de solicitar préstamos.\n");
        } else {
            sb.append(" Reserva activa: Mesa ").append(reservaActiva.getMesaId()).append("\n");
            sb.append("Personas: ").append(reservaActiva.getNumPersonas()).append("\n");
            sb.append("Préstamos activos: ").append(reservaActiva.getPrestamosActivos().size()).append("/2\n\n");
            
            if (!reservaActiva.getPrestamosActivos().isEmpty()) {
                sb.append("=== JUEGOS EN PRÉSTAMO ===\n");
                for (Prestamo p : reservaActiva.getPrestamosActivos()) {
                    sb.append("- ").append(p.getJuego().getJuegoBase().getNombre()).append("\n");
                }
            }
        }
        
        txtEstado.setText(sb.toString());
    }
    
    private void seleccionarJuegos() {
        if (reservaActiva == null) {
            JOptionPane.showMessageDialog(this, "No tienes una reserva activa.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        juegosSeleccionados.clear();
        for (int i = 0; i < modeloTabla.getRowCount(); i++) {
            if ((Boolean) modeloTabla.getValueAt(i, 0)) {
                String nombre = (String) modeloTabla.getValueAt(i, 1);
                cafe.getCatalogoPrestamo().stream()
                    .filter(j -> j.getJuegoBase().getNombre().equals(nombre) && j.estaDisponible())
                    .findFirst()
                    .ifPresent(juegosSeleccionados::add);
            }
        }
        
        if (juegosSeleccionados.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Seleccione al menos un juego disponible.");
            return;
        }
        
        if (juegosSeleccionados.size() > 2) {
            JOptionPane.showMessageDialog(this, "Solo puedes seleccionar hasta 2 juegos.");
            juegosSeleccionados.clear();
            return;
        }
        
        JOptionPane.showMessageDialog(this, 
            "Juegos seleccionados:\n" + 
            juegosSeleccionados.stream().map(j -> j.getJuegoBase().getNombre()).reduce((a,b) -> a + "\n" + b).orElse("") +
            "\n\nAhora puedes solicitar el préstamo.",
            "Selección exitosa", JOptionPane.INFORMATION_MESSAGE);
    }
    
    /*private void solicitarPrestamo() {
        if (reservaActiva == null) {
            JOptionPane.showMessageDialog(this, "No tienes una reserva activa.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (juegosSeleccionados.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No has seleccionado ningún juego.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        int prestamosActuales = reservaActiva.getPrestamosActivos().size();
        if (prestamosActuales + juegosSeleccionados.size() > 2) {
            JOptionPane.showMessageDialog(this, 
                "Ya tienes " + prestamosActuales + " préstamo(s) activo(s).\n" +
                "Máximo 2 juegos por reserva.",
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        StringBuilder resultado = new StringBuilder();
        boolean todosExitosos = true;
        
        for (JuegoFisico juego : juegosSeleccionados) {
            Prestamo prestamo = cafe.generarPrestamoJuego(loginCliente, passwordCliente, juego, reservaActiva);
            if (prestamo != null) {
                resultado.append("✅ ").append(juego.getJuegoBase().getNombre()).append("\n");
            } else {
                resultado.append("❌ ").append(juego.getJuegoBase().getNombre()).append(" (error)\n");
                todosExitosos = false;
            }
        }
        
        if (todosExitosos) {
            JOptionPane.showMessageDialog(this, 
                "Préstamo(s) solicitado(s) exitosamente:\n" + resultado.toString(),
                "Éxito", JOptionPane.INFORMATION_MESSAGE);
            juegosSeleccionados.clear();
            actualizarCatalogo();
            actualizarEstado();
        } else {
            JOptionPane.showMessageDialog(this, 
                "Resultado:\n" + resultado.toString(),
                "Parcialmente exitoso", JOptionPane.WARNING_MESSAGE);
        }
    }*/
    
    private void solicitarPrestamo() {
        if (reservaActiva == null) {
            JOptionPane.showMessageDialog(this, "No tienes una reserva activa.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (juegosSeleccionados.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No has seleccionado ningún juego.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        int prestamosActuales = reservaActiva.getPrestamosActivos().size();
        if (prestamosActuales + juegosSeleccionados.size() > 2) {
            JOptionPane.showMessageDialog(this, 
                "Ya tienes " + prestamosActuales + " préstamo(s) activo(s).\n" +
                "Máximo 2 juegos por reserva.",
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        StringBuilder resultado = new StringBuilder();
        boolean todosExitosos = true;
        
        for (JuegoFisico juego : juegosSeleccionados) {
            // Agregar logs para ver qué pasa
            System.out.println("Intentando prestar: " + juego.getJuegoBase().getNombre());
            System.out.println("  - En inventario prestamo? " + cafe.getCatalogoPrestamo().contains(juego));
            System.out.println("  - Disponible? " + juego.estaDisponible());
            System.out.println("  - Reserva activa? " + (reservaActiva != null && !reservaActiva.isTerminada()));
            
            Prestamo prestamo = cafe.generarPrestamoJuego(loginCliente, passwordCliente, juego, reservaActiva);
            if (prestamo != null) {
                resultado.append("✅ ").append(juego.getJuegoBase().getNombre()).append("\n");
            } else {
                resultado.append("❌ ").append(juego.getJuegoBase().getNombre()).append(" (ver consola para detalles)\n");
                todosExitosos = false;
            }
        }
        
        if (todosExitosos) {
            JOptionPane.showMessageDialog(this, 
                "Préstamo(s) solicitado(s) exitosamente:\n" + resultado.toString(),
                "Éxito", JOptionPane.INFORMATION_MESSAGE);
            juegosSeleccionados.clear();
            actualizarCatalogo();
            actualizarEstado();
        } else {
            JOptionPane.showMessageDialog(this, 
                "Error en préstamo:\n" + resultado.toString(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void devolverJuegos() {
        if (reservaActiva == null) {
            JOptionPane.showMessageDialog(this, "No tienes una reserva activa.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (reservaActiva.getPrestamosActivos().isEmpty()) {
            JOptionPane.showMessageDialog(this, "No tienes préstamos activos.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        for (Prestamo p : reservaActiva.getPrestamosActivos()) {
            p.finalizar();
        }
        
        JOptionPane.showMessageDialog(this, "Juegos devueltos exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
        actualizarCatalogo();
        actualizarEstado();
    }
}