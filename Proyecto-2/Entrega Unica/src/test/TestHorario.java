package test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import horario.HorarioSemanal;
import horario.TicketCambiarTurno;
import horario.Turno;
import usuarios.Cocinero;
import usuarios.Empleado;
import usuarios.Mesero;

public class TestHorario {

	private Mesero mesero;
	private Cocinero cocinero;
	private Turno lunes;
	private Turno martes;
	private HorarioSemanal horario;

	@BeforeEach
	public void setUp() {
		mesero = new Mesero("mario", "mesero123");
		cocinero = new Cocinero("carlos", "cocinero123");
		lunes = new Turno("Lunes manana");
		martes = new Turno("Martes tarde");
		horario = new HorarioSemanal();
	}

	@Test
	public void testTurnoDatosYEmpleados() {
		assertEquals("Lunes manana", lunes.getDiaSemana());
		assertTrue(lunes.getEmpleados().isEmpty());

		lunes.agregarEmpleado(mesero);
		lunes.agregarEmpleado(cocinero);

		assertEquals(2, lunes.getEmpleados().size());
		assertTrue(lunes.getEmpleados().contains(mesero));
		assertTrue(lunes.getEmpleados().contains(cocinero));

		lunes.setDiaSemana("Lunes tarde");
		assertEquals("Lunes tarde", lunes.getDiaSemana());
	}

	@Test
	public void testTurnoSetEmpleados() {
		List<Empleado> empleados = new ArrayList<>();
		empleados.add(mesero);

		lunes.setEmpleados(empleados);

		assertSame(empleados, lunes.getEmpleados());
		assertEquals(1, lunes.getEmpleados().size());
	}

	@Test
	public void testHorarioSemanalInicialYAsignarTurno() {
		assertTrue(horario.getTurnosPorDia().isEmpty());

		horario.asignarTurno("lunes", lunes);

		assertTrue(horario.tieneTurno("lunes"));
		assertSame(lunes, horario.obtenerTurno("lunes"));
		assertEquals(1, horario.getTurnosPorDia().size());
	}

	@Test
	public void testHorarioSemanalNoAsignaDiaOTurnoNulo() {
		horario.asignarTurno(null, lunes);
		horario.asignarTurno("martes", null);

		assertTrue(horario.getTurnosPorDia().isEmpty());
		assertFalse(horario.tieneTurno("martes"));
		assertNull(horario.obtenerTurno("martes"));
	}

	@Test
	public void testHorarioSemanalEliminarTurno() {
		horario.asignarTurno("lunes", lunes);

		horario.eliminarTurno("lunes");

		assertFalse(horario.tieneTurno("lunes"));
		assertNull(horario.obtenerTurno("lunes"));
		assertTrue(horario.getTurnosPorDia().isEmpty());
	}

	@Test
	public void testHorarioSemanalSetTurnosPorDia() {
		Map<String, Turno> turnos = new HashMap<>();
		turnos.put("martes", martes);

		horario.setTurnosPorDia(turnos);

		assertSame(turnos, horario.getTurnosPorDia());
		assertTrue(horario.tieneTurno("martes"));
		assertSame(martes, horario.obtenerTurno("martes"));
	}

	@Test
	public void testTicketCambiarTurnoDatosYSetters() {
		TicketCambiarTurno ticket = new TicketCambiarTurno(mesero, lunes, martes, cocinero);
		Turno miercoles = new Turno("Miercoles noche");
		Cocinero otroCocinero = new Cocinero("sofia", "pass");

		assertSame(mesero, ticket.getEmpleadoPrincipal());
		assertSame(lunes, ticket.getTurnoInicial());
		assertSame(martes, ticket.getTurnoFinal());
		assertSame(cocinero, ticket.getEmpleadoSecundario());
		assertNull(ticket.getEstado());

		ticket.setEmpleadoPrincipal(cocinero);
		ticket.setEmpleadoSecundario(otroCocinero);
		ticket.setTurnoInicial(martes);
		ticket.setTurnoFinal(miercoles);
		ticket.setEstado("Pendiente");

		assertSame(cocinero, ticket.getEmpleadoPrincipal());
		assertSame(otroCocinero, ticket.getEmpleadoSecundario());
		assertSame(martes, ticket.getTurnoInicial());
		assertSame(miercoles, ticket.getTurnoFinal());
		assertEquals("Pendiente", ticket.getEstado());
	}

	@Test
	public void testTicketCambiarTurnoAprobarYRechazar() {
		TicketCambiarTurno ticket = new TicketCambiarTurno(mesero, lunes, martes, cocinero);

		ticket.aprobar();
		assertEquals("Aprobada", ticket.getEstado());

		ticket.rechazar();
		assertEquals("Rechazada", ticket.getEstado());
	}
}
