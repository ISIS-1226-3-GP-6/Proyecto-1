package horario;

import usuarios.Empleado;

public class TicketCambiarTurno {
	private Empleado empleadoPrincipal;
	private Turno turnoInicial;
	private Turno turnoFinal;
	private Empleado empleadoSecundario;
	private String estado;
	
	public TicketCambiarTurno(Empleado empleadoPrincipal, Turno turnoInicial, Turno turnoFinal, Empleado empleadoSecundario) {
		this.empleadoPrincipal = empleadoPrincipal;
		this.empleadoSecundario = empleadoSecundario;
		this.turnoInicial = turnoInicial;
		this.turnoFinal = turnoFinal;
	}
	
	public Empleado getEmpleadoPrincipal(){
		return empleadoPrincipal;
	}
	public void setEmpleadoPrincipal(Empleado nuevo){
		this.empleadoPrincipal=nuevo;
	}
	public Turno getTurnoInicial(){
		return turnoInicial;
	}
	public void setTurnoInicial(Turno t){
		this.turnoInicial=t;
	}
	public Turno getTurnoFinal() {
		return turnoFinal;
	}
	public void setTurnoFinal(Turno t){
		this.turnoFinal=t;
	}
	public Empleado getEmpleadoSecundario(){
		return empleadoSecundario;
	}
	public void setEmpleadoSecundario(Empleado nuevo){
		this.empleadoSecundario=nuevo;
	}
	public String getEstado(){
		return estado;
	}
	public void setEstado(String nuevo){
		this.estado=nuevo;
	}
	public void aprobar(){
	    this.estado = "Aprobada";
	    System.out.println("El ticket fue aprobado");
	}
	public void rechazar(){
	    this.estado = "Rechazada";
	    System.out.println("El ticket fue rechazado.");
	}
	
	
	

}
