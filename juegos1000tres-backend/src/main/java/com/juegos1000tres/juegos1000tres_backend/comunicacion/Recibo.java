package com.juegos1000tres.juegos1000tres_backend.comunicacion;

public abstract class Recibo<PAYLOAD> {

    public abstract Recibo<PAYLOAD> conEvento(String comando, Evento<PAYLOAD> evento);

    public abstract void procesar(PAYLOAD payload, ContextoEvento contexto);

    public abstract Class<PAYLOAD> getClasePayload();
}