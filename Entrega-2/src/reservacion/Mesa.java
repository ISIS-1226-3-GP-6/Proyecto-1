package reservacion;

public class Mesa {
	private int capacidad;
	
	public Mesa(int capacidad) {
		this.capacidad=capacidad;
	}
	
	public int getCapacidad() {
		return capacidad;
	}
	public void setCapacidad(int nuevo) {
		this.capacidad=nuevo;
	}
	public boolean sePuedeSentar(int num) {
	    return num <= capacidad;
	}

}
