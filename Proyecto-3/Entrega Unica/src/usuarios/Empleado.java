package usuarios;

public abstract class Empleado extends Usuario {

	private static final long serialVersionUID = 1L;

	public Empleado(String login, String password) {
		super(login,password);
	}
}
