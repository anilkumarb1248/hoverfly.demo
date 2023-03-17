package com.hoverfly.demo.exception;

import org.springframework.web.client.RestClientException;

public class ClientException extends RuntimeException{

    private final Class<?> clientClass;

    public ClientException(RestClientException cause, Class<?> clientClass){
        super(cause);
        this.clientClass = clientClass;
    }

    public Class<?> getClientClass(){
        return clientClass;
    }

    @Override
    public String getMessage(){
        return "Exception in clientClass: "+ clientClass + " with message: " + super.getMessage();
    }

}
