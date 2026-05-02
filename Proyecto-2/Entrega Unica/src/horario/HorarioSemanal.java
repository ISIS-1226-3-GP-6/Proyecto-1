package horario;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class HorarioSemanal implements Serializable {

    private static final long serialVersionUID = 1L;
    
    private Map<String, Turno> turnosPorDia;
    
    public HorarioSemanal() {
        this.turnosPorDia = new HashMap<>();
    }
    
    public Map<String, Turno> getTurnosPorDia() {
        return turnosPorDia;
    }
    
    public void setTurnosPorDia(Map<String, Turno> turnosPorDia) {
        this.turnosPorDia = turnosPorDia;
    }
    
    public void asignarTurno(String dia, Turno turno) {
        if (dia != null && turno != null) {
            turnosPorDia.put(dia, turno);
        }
    }
    
    public Turno obtenerTurno(String dia) {
        return turnosPorDia.get(dia);
    }
    
    public boolean tieneTurno(String dia) {
        return turnosPorDia.containsKey(dia);
    }
    
    public void eliminarTurno(String dia) {
        turnosPorDia.remove(dia);
    }

}
