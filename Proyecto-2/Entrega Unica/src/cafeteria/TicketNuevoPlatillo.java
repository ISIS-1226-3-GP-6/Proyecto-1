package cafeteria;

import java.io.Serializable;

public class TicketNuevoPlatillo implements Serializable {
    
    private boolean aprobado;
    private Platillo platillo;
    
    public TicketNuevoPlatillo(Platillo platillo) {
        this.platillo = platillo;
        this.aprobado = false;
    }
    
    public boolean isAprobado() {
        return aprobado;
    }
    
    public void setAprobado(boolean aprobado) {
        this.aprobado = aprobado;
    }
    
    public Platillo getPlatillo() {
        return platillo;
    }
    
    public void setPlatillo(Platillo platillo) {
        this.platillo = platillo;
    }
    
    public void aprobar() {
        this.aprobado = true;
        if (this.platillo != null) {
            this.platillo.setAprobado(true);
        }
    }
    
    public void rechazar() {
        this.aprobado = false;
        if (this.platillo != null) {
            this.platillo.setAprobado(false);
        }
    }
    
}
