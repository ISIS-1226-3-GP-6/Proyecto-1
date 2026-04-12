package usuarios;

public class Administrador extends Usuario {
    
    public Administrador(String login, String password) {
        super(login, password);
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
