package cafeteria;

import reservacion.Reserva;

public class Bebida extends Platillo {

	private static final long serialVersionUID = 1L;

	private boolean esAlcoholica;
	private boolean esCaliente;
	
	public Bebida(double precio, boolean aprobado, boolean esAlcoholica, boolean esCaliente) {
		super(precio, aprobado);
		this.esAlcoholica=esAlcoholica;
		this.esCaliente=esCaliente;
	}
	
	public boolean isAlcoholica() {
		return esAlcoholica;
	}
	public void setAlcoholica(boolean nuevo) {
		this.esAlcoholica=nuevo;
	}
	public boolean isCaliente() {
		return esCaliente;
	}
	public void setCaliente(boolean nuevo) {
		this.esCaliente=nuevo;
	}
	public boolean esPermitidaEn(Reserva reserva) {
        if (reserva.isHayMenores() && this.esAlcoholica) {
            return false;
        }
        return true;
    }

}
