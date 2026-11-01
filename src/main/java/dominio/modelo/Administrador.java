package dominio.modelo;
/**
 *
 * @author inici4rsesi0n
 */
public class Administrador extends Usuario {
    private static final long serialVersionUID = 1L;

    protected Administrador() {
        super();
    }
    public Administrador(String codigo, String hashContrasena, String dni,
                         String nombre, String apellido, int edad) {
        super(codigo, hashContrasena, dni, nombre, apellido, edad, Rol.ADMINISTRADOR);
    }
    @Override
    public String toString() {
        return "Administrador [" + super.toString() + "]";
    }
}
