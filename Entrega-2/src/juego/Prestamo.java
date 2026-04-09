package juego;
import java.util.Date;

public class Prestamo {
	private Date fecha;
	private boolean terminado;
	private JuegoFisico juego;
	
	public Prestamo(Date fecha, JuegoFisico juego) {
        this.fecha = fecha;
        this.juego = juego;
        this.terminado = false;
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

}
