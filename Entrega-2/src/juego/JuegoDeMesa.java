package juego;

public class JuegoDeMesa {
	
	private String nombre;
	private int anioPublicacion;
	private String empresaMatriz;
	private boolean esDificil;
	private boolean puedenNinos;
	private boolean puedenJovenes;
	private int minJugadores;
	private int maxJugadores;
	private String tipoJuego;
	
	public JuegoDeMesa(String nombre, int anioPublicacion, String empresaMatriz, String tipoJuego, boolean esDificil, boolean puedenNinos, boolean puedenJovenes, int inJugadores, int maxJugadores) {
		this.nombre = nombre;
		this.anioPublicacion = anioPublicacion;
		this.empresaMatriz = empresaMatriz;
		this.tipoJuego = tipoJuego;
		this.esDificil = esDificil;
		this.puedenNinos = puedenNinos;
		this.puedenJovenes = puedenJovenes;
	}

	public String getNombre(){
		return nombre;
	}
	public void setNombre(String nuevo) {
		this.nombre=nuevo;
	}
	public int getAnio() {
		return anioPublicacion;
	}
	public void setAnio(int nuevo) {
		this.anioPublicacion=nuevo;
	}
	public String getEmpresa() {
		return empresaMatriz;
	}
	public void setEmpresa(String nuevo) {
		this.empresaMatriz=nuevo;
	}
	public boolean getNinos() {
		return puedenNinos;
	}
	public void setNinos(boolean nuevo) {
		this.puedenNinos = nuevo;
	}
	public boolean getJovenes() {
		return puedenJovenes;
	}
	public void setJovenes(boolean nuevo) {
		this.puedenJovenes = nuevo;
	}
	public boolean getDificultad() {
		return esDificil;
	}
	public void setDificultad(boolean nuevo) {
		this.esDificil = nuevo;
	}
	public int getMinJugadores() {
		return minJugadores;
	}
	public void setMinJugadores(int nuevo) {
		this.minJugadores=nuevo;
	}
	public int getMaxJugadores() {
		return maxJugadores;
	}
	public void setMaxJugadores(int nuevo) {
		this.maxJugadores=nuevo;
	}
	public String getTipoJuego() {
		return tipoJuego;
	}
	public void setTipoJuego(String nuevo) {
		this.tipoJuego=nuevo;
	}
	public boolean esAptoParaEdad(boolean hayNinos, boolean hayJovenes) {
	    if (hayNinos && !puedenNinos) {
	        return false;
	    }
	    if (hayJovenes && !puedenJovenes) {
	        return false;
	    }
	    return true;
	    }
	public boolean aptoParaNumJugadores(int num) {
	    if (num < minJugadores) {
	        return false;
	    }
	    if (num > maxJugadores) {
	        return false;
	    }
	    return true;
	}
}
