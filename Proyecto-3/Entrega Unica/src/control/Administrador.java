package control;

import java.io.Serializable;

import cafeteria.TicketNuevoPlatillo;
import horario.TicketCambiarTurno;
import juego.JuegoFisico;

public class Administrador implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String login;
	private String password;
	
	public String getLogin() {
		return login;
	}
	public void setLogin(String nuevo) {
		this.login=nuevo;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String nuevo) {
		this.password=nuevo;
	}
	public boolean autenticacion(String password) {
	    if (password == null) {
	        return false;
	    }
	    if (this.password.equals(password)) {
	        return true;
	    } else {
	        return false;
	    }
	}
    
    public Administrador(String login, String password) {
    	this.login=login;
		this.password=password;
    }
    
    public void aprobarTicketTurno(TicketCambiarTurno ticket) {
        if (ticket != null && ticket.getEstado().equals("PENDIENTE")) {
            ticket.aprobar();
        }
    }
    
    public void rechazarTicketTurno(TicketCambiarTurno ticket) {
        if (ticket != null && ticket.getEstado().equals("PENDIENTE")) {
            ticket.rechazar();
        }
    }
    
    public void aprobarPlatillo(TicketNuevoPlatillo ticket) {
        if (ticket != null && !ticket.isAprobado()) {
            ticket.aprobar();
        }
    }
    
    public void rechazarPlatillo(TicketNuevoPlatillo ticket) {
        if (ticket != null && !ticket.isAprobado()) {
            ticket.rechazar();
        }
    }
    
    public void moverJuegoInventario(JuegoFisico juego, Cafe cafe, String destino) {
        if (juego != null && cafe != null) {
            if (destino.equalsIgnoreCase("COMPRA")) {
                cafe.agregarJuegoCompra(juego);
            } else if (destino.equalsIgnoreCase("PRESTAMO")) {
            	
                cafe.agregarJuegoPrestamo(juego);
            }
        }
    }
    
    public void marcarJuegoDesaparecido(JuegoFisico juego, Cafe cafe) {
        if (juego != null && cafe != null) {
            juego.setEstado("desaparecido");
            cafe.getCatalogoPrestamo().remove(juego);
        }
    }

}
