package com.juegos1000tres.juegos1000tres_backend.juegos.SpaceInvaders.pruebas.comunicacion;

import java.util.Objects;

import com.juegos1000tres.juegos1000tres_backend.comunicacion.Enviable;
import com.juegos1000tres.juegos1000tres_backend.comunicacion.Envio;

public final class JsonEnvioPruebas extends Envio<String> {

    @Override
    public String traducirEnviableAFormato(Enviable enviable) {
        Enviable enviableNoNulo = Objects.requireNonNull(enviable, "El enviable es obligatorio");

        Object salida = enviableNoNulo.out();
        if (!(salida instanceof String json)) {
            throw new IllegalArgumentException("JsonEnvioPruebas requiere que Enviable.out() devuelva String");
        }

        return json;
    }

    @Override
    public Class<String> getClasePayload() {
        return String.class;
    }
}
