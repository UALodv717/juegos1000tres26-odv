package com.juegos1000tres.juegos1000tres_backend.modelos;

public abstract class Juego {

    private final int numeroJugadores;
    private final boolean necesitaPantalla;
    private final Conexion conexionJugadores;
    private final Conexion conexionPantalla;

    protected Juego(int numeroJugadores, boolean necesitaPantalla, Conexion conexionJugadores, Conexion conexionPantalla) {
        if (numeroJugadores <= 0) {
            throw new IllegalArgumentException("El numero de jugadores debe ser mayor que cero");
        }

        this.numeroJugadores = numeroJugadores;
        this.necesitaPantalla = necesitaPantalla;
        this.conexionJugadores = conexionJugadores;
        this.conexionPantalla = conexionPantalla;
    }

    public int getNumeroJugadores() {
        return numeroJugadores;
    }

    public boolean isNecesitaPantalla() {
        return necesitaPantalla;
    }

    public abstract void iniciar();

    public abstract void terminar();
}
