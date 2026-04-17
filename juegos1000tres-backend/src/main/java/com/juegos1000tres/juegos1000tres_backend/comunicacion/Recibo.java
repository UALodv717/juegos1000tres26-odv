package com.juegos1000tres.juegos1000tres_backend.comunicacion;

public interface Recibo<PAYLOAD> {

    Recibo<PAYLOAD> conEvento(String comando, Evento<PAYLOAD> evento);

    void procesar(PAYLOAD payload, ContextoEvento contexto);

    Class<PAYLOAD> getClasePayload();
}