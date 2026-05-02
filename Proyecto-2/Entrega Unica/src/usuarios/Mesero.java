package usuarios;

import java.util.ArrayList;
import java.util.List;

import juego.JuegoDeMesa;

public class Mesero extends Empleado {

    private static final long serialVersionUID = 1L;
    
    private List<String> dificilesConocidos;
    
    public Mesero(String login, String password, List<String> dificilesConocidos) {
        super(login, password);
        this.dificilesConocidos = dificilesConocidos != null ? dificilesConocidos : new ArrayList<>();
    }
    
    public Mesero(String login, String password) {
        this(login, password, new ArrayList<>());
    }
    
    // Getters y Setters
    public List<String> getDificilesConocidos() {
        return dificilesConocidos;
    }
    
    public void setDificilesConocidos(List<String> dificilesConocidos) {
        this.dificilesConocidos = dificilesConocidos;
    }
    
    public void agregarJuegoConocido(String juegoNombre) {
        if (!dificilesConocidos.contains(juegoNombre)) {
            dificilesConocidos.add(juegoNombre);
        }
    }
    
    public boolean puedeExplicar(JuegoDeMesa juego) {
        return juego != null && juego.getDificultad() && dificilesConocidos.contains(juego.getNombre());
    }

}
