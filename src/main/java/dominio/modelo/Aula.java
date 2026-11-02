package dominio.modelo;
import java.io.Serializable;
/**
 *
 * @author inici4rsesi0n
 */
public class Aula implements Serializable {
    private static final long serialVersionUID = 1L;
    private String nombre;
    private int capacidad;
    private String ubicacion;
    private String tipo;
    private ModalidadAula modalidad;
    protected Aula() {
}
    public Aula(String nombre, int capacidad, String ubicacion, String tipo) {
        this(nombre, capacidad, ubicacion, tipo, ModalidadAula.PRESENCIAL);
    
}
    public Aula(String nombre, int capacidad, String ubicacion, String tipo, ModalidadAula modalidad) {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre del aula no puede ser nulo o vacío");
        
}
        if (capacidad <= 0) {
            throw new IllegalArgumentException("La capacidad del aula debe ser mayor a 0");
        
}
        if (ubicacion == null || ubicacion.isBlank()) {
            throw new IllegalArgumentException("La ubicación del aula no puede ser nula o vacía");
        
}
        if (tipo == null || tipo.isBlank()) {
            throw new IllegalArgumentException("El tipo de aula no puede ser nulo o vacío");
        
}
        this.nombre = nombre;
        this.capacidad = capacidad;
        this.ubicacion = ubicacion;
        this.tipo = tipo;
        this.modalidad = (modalidad != null) ? modalidad : ModalidadAula.PRESENCIAL;
    
}
    public String getNombre() { return nombre; 
}
    public void setNombre(String nombre) { this.nombre = nombre; 
}
    public int getCapacidad() { return capacidad; 
}
    public void setCapacidad(int capacidad) { this.capacidad = capacidad; 
}
    public String getUbicacion() { return ubicacion; 
}
    public void setUbicacion(String ubicacion) { this.ubicacion = ubicacion; 
}
    public String getTipo() { return tipo; 
}
    public void setTipo(String tipo) { this.tipo = tipo; 
}
    public ModalidadAula getModalidad() { return modalidad; 
}
    public void setModalidad(ModalidadAula modalidad) { this.modalidad = (modalidad != null) ? modalidad : ModalidadAula.PRESENCIAL; 
}
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Aula)) return false;
        Aula aula = (Aula) o;
        return nombre != null && nombre.equalsIgnoreCase(aula.nombre);
    
}
    @Override
    public int hashCode() {
        return nombre != null ? nombre.toLowerCase().hashCode() : 0;
    
}
    @Override
    public String toString() {
        return "Aula [nombre=" + nombre + ", capacidad=" + capacidad + ", tipo=" + tipo + ", modalidad=" + modalidad + "]";
    
}

}
