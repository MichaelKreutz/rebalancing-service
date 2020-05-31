package de.kreutz.michael.rebalanceservice.csvimport.exception;

import java.io.IOException;

public class CsvParseException extends RuntimeException {
    public CsvParseException(final String message, final IOException exception) {
        super(message, exception);
    }
}
