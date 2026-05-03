title Client Prestamo Creation Process Requiring Reservation

Cafe -> JuegoDeMesa: <<create>>(nombre, anioPublicacion, empresaMatriz, tipoJuego, esDificil, puedenNinos, puedenJovenes, minJugadores, maxJugadores, precio)

JuegoDeMesa --> Cafe: catalogoJuegos.add(juegoBase)

Cafe -> JuegoFisico: <<create>>("nuevo", false, juegoBase)

JuegoFisico --> Cafe: inventarioVenta.add(juegoFisico)

Cafe -> Cafe: transferirJuegoVentaAPrestamo(juegoFisico)

Cafe --> Cafe: inventarioVenta.remove(juegoFisico)

Cafe --> Cafe: inventarioPrestamo.add(juegoFisico)

Cafe -> Cliente: <<create>>(login, password, 0)

Cliente --> Cafe: usuarios.add(newCliente)

Cafe -> Mesa: <<create>>(capacidad, id)

Mesa --> Cafe: mesas.add(newMesa)

Cafe -> Reserva: <<create>>(numPersonas, hayMenores, hayNinos, mesa.id, newCliente)

Reserva --> Cafe: reservas.add(newReserva)

Cafe -> Mesa: ocupar()

Cliente -> Cafe: generarPrestamoJuego(login, password, juegoFisico, newReserva)

Cafe -> Cafe: autenticarUsuario(login, password)

Cafe -> Cafe: validar juegoFisico in inventarioPrestamo

Cafe -> Cafe: validar !juegoFisico.isOcupado()

Cafe -> Cafe: validar newReserva in reservas

Cafe -> Prestamo: <<create>>(new Date(), juegoFisico, newCliente, newReserva)

Prestamo --> Cafe: historialPrestamos.add(prestamo)

Cafe -> JuegoFisico: prestar()

JuegoFisico --> Cafe: ocupado = true

Cafe -> Reserva: agregarPrestamo(prestamo)

Reserva --> Cafe: prestamosActivos.add(prestamo)

Cafe --> Cliente: return prestamo

note over Cafe,Cliente: If the client has no valid reservation, the prestamo cannot be created.

note over Cafe,JuegoFisico: The game must already be in inventarioPrestamo before the client requests it.

Cafe -> Torneo: <<create>>(juegoBase, tipoTorneo, diaSemana, cuposMaximos, admin)

Torneo --> Cafe: catalogoTorneos.add(torneo)

Usuario -> Cafe: inscribirseTorneo(login, password, torneo)

Cafe -> Cafe: autenticarUsuario(login, password)

Cafe -> Torneo: inscribir(usuario)

Torneo --> Cafe: confirmacion

Cafe --> Usuario: inscripcion exitosa

note over Torneo: Max 3 participantes por usuario y 20% de cupos para fanáticos.

note over Cafe,Torneo: El Cafe mantiene el catalogo de torneos.
