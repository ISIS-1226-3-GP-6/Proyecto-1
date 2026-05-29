package control;

import javax.swing.SwingUtilities;
import control.Cafe;
import ui.VentanaPrincipal;

public class AplicacionGUI {
    
    public static void main(String[] args) {
        String nombreArchivo = "escenario2.txt";
        Cafe cafe = new Cafe(nombreArchivo);
        
        // Agregar guardar incluso si se cierra la ventana principal
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            cafe.save();
            System.out.println("Datos guardados al cerrar la aplicación.");
        }));
        
        SwingUtilities.invokeLater(() -> {
            new VentanaPrincipal(cafe).setVisible(true);
        });
    }
}