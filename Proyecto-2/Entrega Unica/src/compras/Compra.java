package compras;

import java.io.Serializable;
import java.time.LocalDateTime;

public abstract class Compra implements Serializable{
    
    protected double descuento;
    protected double total;
    protected LocalDateTime fechaHora;
    
    public Compra(double descuento, double total, LocalDateTime fechaHora) {
        this.descuento = descuento;
        this.total = total;
        this.fechaHora = fechaHora;
    }
    
    public Compra() {
        this(0, 0, LocalDateTime.now());
    }
    
    public double getDescuento() {
        return descuento;
    }
    
    public void setDescuento(double descuento) {
        this.descuento = descuento;
    }
    
    public double getTotal() {
        return total;
    }
    
    public LocalDateTime getFechaHora() {
        return fechaHora;
    }
    
    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }
    
    public abstract double calcularTotal();
    
}