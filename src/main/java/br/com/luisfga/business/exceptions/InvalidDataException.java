package br.com.luisfga.business.exceptions;

public class InvalidDataException extends Exception {
    public InvalidDataException() {}
    public InvalidDataException(Throwable ex) {
        addSuppressed(ex);
    }
}