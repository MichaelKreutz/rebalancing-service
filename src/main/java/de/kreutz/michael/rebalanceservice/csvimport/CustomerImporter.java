package de.kreutz.michael.rebalanceservice.csvimport;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Service;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class CustomerImporter implements CsvImporter<Customer> {

    private static final String CUSTOMER_ID = "customerId";
    private static final String EMAIL = "email";
    private static final String DATE_OF_BIRTH = "dateOfBirth";
    private static final String RISK_LEVEL = "riskLevel";
    private static final String RETIREMENT_AGE = "retirementAge";
    private static final String[] HEADERS = {CUSTOMER_ID, EMAIL, DATE_OF_BIRTH, RISK_LEVEL, RETIREMENT_AGE};

    public List<Customer> fromCsv(final String pathToCsv) throws IOException {
        log.info("Start reading from file {}.", pathToCsv);
        try (final Reader in = new FileReader(pathToCsv)) {
            final Iterable<CSVRecord> records = CSVFormat.DEFAULT
                    .withHeader(HEADERS)
                    .withFirstRecordAsHeader()
                    .parse(in);
            final List<Customer> customers = new ArrayList<>();
            for (CSVRecord record : records) {
                final Customer customer = Customer.builder()
                        .customerId(Long.parseLong(record.get(CUSTOMER_ID)))
                        .dateOfBirth(LocalDate.parse(record.get(DATE_OF_BIRTH)))
                        .email(record.get(EMAIL))
                        .riskLevel(Integer.parseInt(record.get(RISK_LEVEL)))
                        .retirementAge(Integer.parseInt(record.get(RETIREMENT_AGE)))
                        .build();
                customers.add(customer);
            }
            log.info("Read customers {} from file.", customers);
            return customers;
        }
    }
}
