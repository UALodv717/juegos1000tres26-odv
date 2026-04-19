package com.juegos1000tres.juegos1000tres_backend.juegos.SpaceInvaders.pruebas.comunicacion;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.juegos1000tres.juegos1000tres_backend.comunicacion.ContextoEvento;
import com.juegos1000tres.juegos1000tres_backend.comunicacion.Evento;
import com.juegos1000tres.juegos1000tres_backend.comunicacion.Recibo;

public final class JsonReciboPruebas extends Recibo<String> {

    private static final Pattern PATRON_COMANDO = Pattern.compile("\\\"comando\\\"\\s*:\\s*\\\"([^\\\"]+)\\\"");

    private final Map<String, Evento<String>> eventosPorComando;

    public JsonReciboPruebas() {
        this(Map.of());
    }

    private JsonReciboPruebas(Map<String, Evento<String>> eventosPorComando) {
        this.eventosPorComando = Map.copyOf(eventosPorComando);
    }

    @Override
    public Recibo<String> conEvento(String comando, Evento<String> evento) {
        String comandoNormalizado = normalizarComando(comando);
        Evento<String> eventoNoNulo = Objects.requireNonNull(evento, "El evento es obligatorio");

        Map<String, Evento<String>> nuevoMapa = new LinkedHashMap<>(this.eventosPorComando);
        nuevoMapa.put(comandoNormalizado, eventoNoNulo);
        return new JsonReciboPruebas(nuevoMapa);
    }

    @Override
    public void procesar(String payload, ContextoEvento contexto) {
        Objects.requireNonNull(contexto, "El contexto de evento es obligatorio");

        if (payload == null || payload.isBlank()) {
            throw new IllegalArgumentException("El payload entrante no puede estar vacio");
        }

        String comando = extraerComando(payload);
        Evento<String> evento = this.eventosPorComando.get(normalizarComando(comando));
        if (evento == null) {
            throw new IllegalArgumentException("No existe un evento registrado para el comando: " + comando);
        }

        evento.hacer(payload, contexto);
    }

    @Override
    public Class<String> getClasePayload() {
        return String.class;
    }

    private String extraerComando(String payload) {
        Matcher matcher = PATRON_COMANDO.matcher(payload);
        if (!matcher.find()) {
            throw new IllegalArgumentException("El payload no incluye el campo comando");
        }

        return matcher.group(1);
    }

    private String normalizarComando(String comando) {
        if (comando == null || comando.isBlank()) {
            throw new IllegalArgumentException("El comando del evento es obligatorio");
        }

        return comando.trim().toLowerCase(Locale.ROOT);
    }
}
