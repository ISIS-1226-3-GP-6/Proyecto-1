package juego;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import usuarios.Cliente;
import usuarios.Empleado;
import usuarios.Usuario;

public class Torneo implements Serializable {

	private static final long serialVersionUID = 1L;

	private boolean esCompetitivo;
	private int bono;
	private double costoEntrada;
	private int cupos;
	private int cuposTaken;
	private int cuposPrioritariosTaken;
	private List<Usuario> usuarios;
	private JuegoDeMesa juego;

	public Torneo(boolean esCompetitivo, int bono, double costoEntrada, int cupos) {
		this.esCompetitivo = esCompetitivo;
		this.bono = bono;
		this.costoEntrada = costoEntrada;
		this.cupos = cupos;
		this.cuposTaken = 0;
		this.cuposPrioritariosTaken = 0;
		this.usuarios = new ArrayList<>();
	}

	public boolean desinscribir(Usuario usuario) {
		if (usuario == null || !usuarios.contains(usuario)) {
			return false;
		}

		int cuposLiberados = 0;
		for (int i = 0; i < usuarios.size(); i++) {
			if (usuarios.get(i).equals(usuario)) {
				cuposLiberados++;
				usuarios.remove(i);
				i--;
			}
		}

		cuposTaken -= cuposLiberados;

		if (usuario instanceof Cliente && ((Cliente) usuario).getJuegosFavoritos().contains(this.juego)) {
			cuposPrioritariosTaken -= cuposLiberados;
		}

		return true;
	}

	public boolean inscribir(Usuario usuario, int cupos) {

		if (List.of(1, 2, 3).contains(cupos) == false) {
			System.out.println("Cupos inválidos. Solo se permiten hasta 3 cupos.");
			return false;
		}

		if (usuario instanceof Empleado) {
			return inscribirEmpleado((Empleado) usuario, cupos);
		}
		if (usuario instanceof Cliente && cuposPrioritariosTaken < calcularCuposPrioritarios() && ((Cliente) usuario).getJuegosFavoritos().contains(this.juego)) {
			return inscribirPrioritario((Cliente) usuario, cupos);
		}
		if (usuario instanceof Cliente) {
			return inscribirGeneral((Cliente) usuario, cupos);
		}

		return false;
	}

	private boolean inscribirEmpleado(Empleado empleado, int cupos) {
		return registrarUsuario(empleado, cupos);
	}

	private boolean inscribirPrioritario(Cliente cliente, int cupos) {
		int cuposPrioritariosDisponibles = calcularCuposPrioritarios() - cuposPrioritariosTaken;
		if (cupos > cuposPrioritariosDisponibles) {
			return inscribirGeneral(cliente, cupos);
		}

		boolean inscrito = registrarUsuario(cliente, cupos);
		if (inscrito) {
			cuposPrioritariosTaken += cupos;
		}
		return inscrito;
	}

	private boolean inscribirGeneral(Cliente cliente, int cupos) {
		return registrarUsuario(cliente, cupos);
	}

	private int calcularCuposPrioritarios() {
		return (int) Math.ceil(this.cupos * 0.2);
	}

	public void verEstatusInscripcion() {
		System.out.println("Cupos tomados: " + cuposTaken + "/" + cupos);
		System.out.println("Cupos prioritarios tomados: " + cuposPrioritariosTaken + "/" + calcularCuposPrioritarios());
		System.out.println("Usuarios inscritos:");
		for (Usuario usuario : usuarios) {
			System.out.println("- " + usuario.getLogin());
		}
	}

	private boolean registrarUsuario(Usuario usuario, int cupos) {
		if (usuario == null || cuposTaken + cupos > this.cupos || usuarios.contains(usuario)) {
			return false;
		}

		cuposTaken += cupos;

		for (int i = 0; i < cupos; i++) {
			usuarios.add(usuario);
		}

		return true;
	}

	public boolean isEsCompetitivo() {
		return esCompetitivo;
	}

	public int getBono() {
		return bono;
	}

	public double getCostoEntrada() {
		return costoEntrada;
	}

	public int getCupos() {
		return cupos;
	}

	public int getCuposTaken() {
		return cuposTaken;
	}

	public int getCuposPrioritariosTaken() {
		return cuposPrioritariosTaken;
	}

	public List<Usuario> getUsuarios() {
		return usuarios;
	}

	public JuegoDeMesa getJuego() {
		return juego;
	}

	public void setJuego(JuegoDeMesa juego) {
		this.juego = juego;
	}
}
