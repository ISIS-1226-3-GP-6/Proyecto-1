package reservacion;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import juego.Prestamo;
import usuarios.Cliente;

public class Reserva implements Serializable {

	private static final long serialVersionUID = 1L;

	private int numPersonas;
    private boolean hayMenores;
    private boolean hayNinos;
    private boolean terminada;
    private int mesaId;
    private Set<Prestamo> prestamosActivos;
    private Cliente cliente;
    
    public Reserva(int numPersonas, boolean hayMenores, boolean hayNinos, int mesaId, Cliente cliente) {
        this.numPersonas = numPersonas;
        this.hayMenores = hayMenores;
        this.hayNinos = hayNinos;
        this.mesaId = mesaId;
        this.terminada = false;
        this.prestamosActivos = new HashSet<>();
        this.cliente = cliente;
    }
    
    public int getNumPersonas(){
        return numPersonas;
    }
    public void setNumPersonas(int numPersonas){
        this.numPersonas = numPersonas;
    }
    public boolean isHayMenores(){
        return hayMenores;
    }
    public void setHayMenores(boolean hayMenores){
        this.hayMenores = hayMenores;
    }
    public boolean isHayNinos(){
        return hayNinos;
    }
    public void setHayNinos(boolean hayNinos){
        this.hayNinos = hayNinos;
    }
    public boolean isTerminada(){
        return terminada;
    }
    public void setTerminada(boolean terminada){
        this.terminada = terminada;
    }
    public int getMesaId(){
        return mesaId;
    }
    public void setMesaId(int mesaId){
        this.mesaId = mesaId;
    }
    public Set<Prestamo> getPrestamosActivos(){
        return prestamosActivos;
    }
    public boolean agregarPrestamo(Prestamo p) {
    	if (p == null) {
            return false;
        }
        if (this.terminada) {
            return false;
        }
        if (prestamosActivos.size() >= 2)
        	return false;
        
        this.prestamosActivos.add(p);
        return true;
    }
    public void cerrarReserva() {
    	this.terminada = true;
        for (Prestamo p : prestamosActivos) {
            if (p != null) {
                p.finalizar();
            }
        }
    }
    
    public Cliente getCliente() {
    	return cliente;
    }
	
}
