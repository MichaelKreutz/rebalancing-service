package de.kreutz.michael.rebalanceservice.csvimport;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

public interface CsvImporter<T> {

    List<T> fromCsv(String pathToCsvFile) throws IOException;
}
