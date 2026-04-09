package usuarios;
import java.util.ArrayList;
import java.util.List;

import juego.JuegoDeMesa;

public class Cliente extends Usuario {
	private int puntosFidelidad;
	private List<JuegoDeMesa> juegosFavoritos;
	
	public Cliente(String login, String password, int puntosFidelidad) {
		super(login, password);
		this.puntosFidelidad = puntosFidelidad;
        this.juegosFavoritos = new ArrayList<>();
	}
	
	 public int getPuntosFidelidad(){
	        return puntosFidelidad;
	    }
	    public void setPuntosFidelidad(int puntosFidelidad){
	        this.puntosFidelidad = puntosFidelidad;
	    }
	    public List<JuegoDeMesa> getJuegosFavoritos(){
	        return juegosFavoritos;
	    }
	    public void setJuegosFavoritos(List<JuegoDeMesa> juegosFavoritos){
	        this.juegosFavoritos = juegosFavoritos;
	    }
	    public void agregarPuntos(double nuevo) {
	    	int puntosGanados = (int)(nuevo*0.01);
	    	this.puntosFidelidad= this.puntosFidelidad + puntosGanados;
	    }
	    public void usarPuntos(int cant) {
	    	if (cant <= 0) {
	    		System.out.println("Cantidad inválida");
	            return;
	        }
	        if (cant > this.puntosFidelidad) {
	        	System.out.println("Puntos insuficientes");
	            return;
	        }
	        this.puntosFidelidad = this.puntosFidelidad - cant;
	    }
	    public void agregarJuegoFav(JuegoDeMesa fav) {
	    	if (fav == null) {
	            return;
	        }
	        if (!this.juegosFavoritos.contains(fav)) {
	            this.juegosFavoritos.add(fav);
	        }
	    }
}
