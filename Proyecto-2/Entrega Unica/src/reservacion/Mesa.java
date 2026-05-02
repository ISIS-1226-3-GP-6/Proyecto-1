package reservacion;

import java.io.Serializable;

public class Mesa implements Serializable {

	private static final long serialVersionUID = 1L;

	private int capacidad;
	private int id;
	private boolean ocupada;
	
	public Mesa(int capacidad, int id) {
		this.capacidad=capacidad;
		this.id = id;
		this.ocupada=false;
	}

	public void ocupar() {
		this.ocupada=true;
	}
	public void desocupar() {
		this.ocupada=false;
	}


	
	public int getCapacidad() {
		return capacidad;
	}
	public void setCapacidad(int nuevo) {
		this.capacidad=nuevo;
	}
	public boolean sePuedeSentar(int num) {
	    return num <= capacidad && !ocupada;
	}
	
	public int getId() {
		return id;
	}

}
