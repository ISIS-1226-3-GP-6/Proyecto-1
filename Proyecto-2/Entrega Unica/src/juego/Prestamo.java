package juego;
import java.io.Serializable;
import java.util.Date;

import reservacion.Reserva;
import usuarios.Cliente;
import usuarios.Usuario;

public class Prestamo implements Serializable {

	private static final long serialVersionUID = 1L;

	private Date fecha;
	private boolean terminado;
	private JuegoFisico juego;
	private Usuario usuario;
	private Reserva reserva;
	
	public Prestamo(Date fecha, JuegoFisico juego, Usuario usuario, Reserva reserva) {
        this.fecha = fecha;
        this.juego = juego;
        this.terminado = false;
        if (usuario == null || (usuario instanceof Cliente && reserva == null))
        	throw new RuntimeException("Creacion de prestamo invalido");
        this.usuario = usuario;
        this.reserva = reserva;
    }

    public Date getFecha(){
        return fecha;
    }
    public void setFecha(Date fecha){
        this.fecha = fecha;
    }
    public boolean isTerminado(){
        return terminado;
    }
    public void setTerminado(boolean terminado){
        this.terminado = terminado;
    }
    public JuegoFisico getJuego(){
        return juego;
    }
    public void setJuego(JuegoFisico juego){
        this.juego = juego;
    }
    public void finalizar() {
        this.terminado = true;
        if (this.juego != null) {
            this.juego.devolver();
        }
    }

	public Usuario getUsuario() {
		return usuario;
	}

	public Reserva getReserva() {
		return reserva;
	}

}
