package usuarios;

public abstract class Usuario {
	private String login;
	private String password;
	
	public Usuario(String login, String password) {
		this.login=login;
		this.password=password;
	}
	
	public String getLogin() {
		return login;
	}
	public void setLogin(String nuevo) {
		this.login=nuevo;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String nuevo) {
		this.password=nuevo;
	}
	public boolean autenticacion(String password) {
	    if (password == null) {
	        return false;
	    }
	    if (this.password.equals(password)) {
	        return true;
	    } else {
	        return false;
	    }
	}

}
