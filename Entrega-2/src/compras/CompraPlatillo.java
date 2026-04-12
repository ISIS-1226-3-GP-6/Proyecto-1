package compras;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CompraPlatillo extends Compra {
    
    private List<Platillo> platillos;
    
    public CompraPlatillo(double descuento, double total, LocalDateTime fechaHora) {
        super(descuento, total, fechaHora);
        this.platillos = new ArrayList<>();
    }
    
    public CompraPlatillo() {
        this(0, 0, LocalDateTime.now());
    }
    
    public List<Platillo> getPlatillos() {
        return platillos;
    }
    
    public void setPlatillos(List<Platillo> platillos) {
        this.platillos = platillos;
    }
    
    public void agregarPlatillo(Platillo platillo) {
        if (platillo != null) {
            this.platillos.add(platillo);
        }
    }
    
    @Override 
    public double calcularTotal() {
        double subtotal = 0;
        for (Platillo p : platillos) {
            subtotal += p.getPrecio();
        }
        double descuentoAplicado = subtotal * (descuento / 100);
        return subtotal - descuentoAplicado;
    }
    
}