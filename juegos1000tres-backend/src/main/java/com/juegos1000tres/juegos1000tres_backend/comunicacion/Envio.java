package com.juegos1000tres.juegos1000tres_backend.comunicacion;

public abstract class Envio<PAYLOAD> {

    public abstract PAYLOAD traducirEnviableAFormato(Enviable enviable);

    public abstract Class<PAYLOAD> getClasePayload();
}