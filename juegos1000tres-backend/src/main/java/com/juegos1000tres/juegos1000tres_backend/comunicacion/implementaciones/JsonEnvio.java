package com.juegos1000tres.juegos1000tres_backend.comunicacion.implementaciones;

import java.util.Objects;

import com.juegos1000tres.juegos1000tres_backend.comunicacion.Enviable;
import com.juegos1000tres.juegos1000tres_backend.comunicacion.Envio;

public class JsonEnvio implements Envio<String> {

    @Override
    public String traducirEnviableAFormato(Enviable enviable) {
        Enviable enviableNoNulo = Objects.requireNonNull(enviable, "El enviable es obligatorio");
        return enviableNoNulo.toJson();
    }

    @Override
    public Class<String> getClasePayload() {
        return String.class;
    }
}
