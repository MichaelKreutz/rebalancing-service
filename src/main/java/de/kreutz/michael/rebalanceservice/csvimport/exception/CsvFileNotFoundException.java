package de.kreutz.michael.rebalanceservice.csvimport.exception;

import java.io.FileNotFoundException;

public class CsvFileNotFoundException extends RuntimeException {

    public CsvFileNotFoundException(final String message, final FileNotFoundException exception) {
        super(message, exception);
    }
}
