package cafeteria;

public abstract class Platillo {
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
