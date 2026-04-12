package compras;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CompraJuegoMesa extends Compra {
    
    private List<JuegoFisico> juegos;
    
    public CompraJuegoMesa(double descuento, double total, LocalDateTime fechaHora) {
        super(descuento, total, fechaHora);
        this.juegos = new ArrayList<>();
    }
    
    public CompraJuegoMesa() {
        this(0, 0, LocalDateTime.now());
    }
    
    public List<JuegoFisico> getJuegos() {
        return juegos;
    }
    
    public void setJuegos(List<JuegoFisico> juegos) {
        this.juegos = juegos;
    }
    
    public void agregarJuego(JuegoFisico juego) {
        if (juego != null) {
            this.juegos.add(juego);
        }
    }
    
    @Override
    public double calcularTotal() {
        double subtotal = 0;
        for (JuegoFisico j : juegos) {
            subtotal += j.getPrecio(); // creo que no hay atributo de esto jaja
        }
        double descuentoAplicado = subtotal * (descuento / 100);
        return subtotal - descuentoAplicado;
    }

}