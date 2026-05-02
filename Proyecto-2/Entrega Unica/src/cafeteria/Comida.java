package cafeteria;

import java.util.ArrayList;
import java.util.List;

public class Comida extends Platillo {

    private static final long serialVersionUID = 1L;
    
    private List<String> alergenos;
    
    public Comida(double precio, boolean aprobado, List<String> alergenos) {
        super(precio, aprobado);

        if (alergenos != null) {
            this.alergenos = alergenos;
        } else {
            this.alergenos = new ArrayList<>();
        }
    }
    
    public Comida(double precio, boolean aprobado) {
        this(precio, aprobado, new ArrayList<>());
    }
    
    public List<String> getAlergenos() {
        return alergenos;
    }
    
    public void setAlergenos(List<String> alergenos) {
        this.alergenos = alergenos;
    }
    
    public void agregarAlergeno(String alergeno) {
        if (!alergenos.contains(alergeno)) {
            alergenos.add(alergeno);
        }
    }
    
    public boolean contieneAlergeno(String alergeno) {
        return alergenos.contains(alergeno);
    }

}
