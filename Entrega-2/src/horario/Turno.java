package horario;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import usuarios.Empleado;

public class Turno implements Serializable {

	private static final long serialVersionUID = 1L;

	private String diaSemana;
	private List<Empleado> empleados;
	
	public Turno(String diaSemana) {
        this.diaSemana = diaSemana;
        this.empleados = new ArrayList<>();
    }
	
	public String getDiaSemana() {
		return diaSemana;
	}
	public void setDiaSemana(String nuevo) {
		this.diaSemana=nuevo;
	}
	public List<Empleado> getEmpleados() {
        return empleados;
    }
    public void setEmpleados(List<Empleado> empleados) {
        this.empleados = empleados;
    }
    public void agregarEmpleado(Empleado e) {
    	this.empleados.add(e);
    }
    

}
