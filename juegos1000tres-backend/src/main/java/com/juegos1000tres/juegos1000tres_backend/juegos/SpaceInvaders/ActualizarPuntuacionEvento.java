package com.juegos1000tres.juegos1000tres_backend.juegos.SpaceInvaders;

import java.util.Objects;
import java.util.UUID;

import com.juegos1000tres.juegos1000tres_backend.comunicacion.ContextoEvento;
import com.juegos1000tres.juegos1000tres_backend.comunicacion.Evento;

public class ActualizarPuntuacionEvento implements Evento<String> {

    private final SpaceInvader juego;

    public ActualizarPuntuacionEvento(SpaceInvader juego) {
        this.juego = Objects.requireNonNull(juego, "El juego es obligatorio");
    }

    @Override
    public void hacer(String payload, ContextoEvento contexto) {
        // Pendiente: parsear payload y enrutar a ejecutarConDatos cuando se conecte la API.
    }

    public void ejecutarConDatos(
            UUID jugadorId,
            String nombreJugador,
            int puntuacionTotal,
            SpaceInvader juegoModelo,
            ContextoEvento contexto) {
        Objects.requireNonNull(juegoModelo, "El modelo de juego es obligatorio");
        Objects.requireNonNull(contexto, "El contexto de evento es obligatorio");

        juegoModelo.registrarJugador(jugadorId, nombreJugador);
        juegoModelo.actualizarPuntuacion(jugadorId, puntuacionTotal);
        contexto.enviar(juegoModelo.crearEstadoEnviable());
    }

    public SpaceInvader getJuego() {
        return juego;
    }
}
