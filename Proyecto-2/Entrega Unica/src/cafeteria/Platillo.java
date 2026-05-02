package cafeteria;

import java.io.Serializable;

public abstract class Platillo implements Serializable {

	private static final long serialVersionUID = 1L;

	private double precio;
	private boolean aprobado;
	
	public Platillo(double precio, boolean aprobado) {
		this.precio=precio;
		this.aprobado=aprobado;
	}
	
	public double getPrecio() {
		return precio;
	}
	public void setPrecio(double nuevo) {
		this.precio=nuevo;
	}
	public boolean isAprobado() {
		return aprobado;
	}
	public void setAprobado(boolean nuevo) {
		this.aprobado=nuevo;
	}

}
