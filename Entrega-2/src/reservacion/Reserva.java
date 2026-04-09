package reservacion;
import java.util.HashSet;
import java.util.Set;

import juego.Prestamo;

public class Reserva {
	private int numPersonas;
    private boolean hayMenores;
    private boolean hayNinos;
    private boolean terminada;
    private int mesaId;
    private Set<Prestamo> prestamosActivos;
    
    public Reserva(int numPersonas, boolean hayMenores, boolean hayNinos, int mesaId) {
        this.numPersonas = numPersonas;
        this.hayMenores = hayMenores;
        this.hayNinos = hayNinos;
        this.mesaId = mesaId;
        this.terminada = false;
        this.prestamosActivos = new HashSet<>();
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
    public void setPrestamosActivos(Set<Prestamo> prestamosActivos){
        this.prestamosActivos = prestamosActivos;
    }
    public void agregarPrestamo(Prestamo p) {
    	if (p == null) {
            return;
        }
        if (this.terminada) {
            return;
        }
        this.prestamosActivos.add(p);
    }
    public void cerrarReserva() {
    	this.terminada = true;
        for (Prestamo p : prestamosActivos) {
            if (p != null) {
                p.finalizar();
            }
        }
    }
	
}
