package juego;

public class JuegoFisico {
	private String estado;
	private boolean ocupado;
	private JuegoDeMesa juegoBase;
	
	public JuegoFisico(String estado, boolean ocupado, JuegoDeMesa juegoBase) {
        this.estado = estado;
        this.ocupado = ocupado;
        this.juegoBase = juegoBase;
    }
	
	public String getEstado() {
		return estado;
	}
	public void setEstado(String nuevo) {
		this.estado=nuevo;
	}
	public boolean isOcupado() {
		return ocupado;
	}
	public void setOcupado(boolean nuevo) {
		this.ocupado=nuevo;
	}
	public JuegoDeMesa getJuegoBase() {
        return juegoBase;
    }

    public void setJuegoBase(JuegoDeMesa juegoBase) {
        this.juegoBase = juegoBase;
    }
    public void prestar() {
    	if (this.ocupado) {
            System.out.println("El juego ya está prestado");
            return;
        }
        this.ocupado = true;
    }
    public void devolver() {
    	if (!this.ocupado) {
            System.out.println("El juego ya estaba disponible");
            return;
        }
        this.ocupado = false;
    }
    public boolean estaDisponible() {
    	 if (this.ocupado) {
             return false;
         }
         return true;
     }

}
