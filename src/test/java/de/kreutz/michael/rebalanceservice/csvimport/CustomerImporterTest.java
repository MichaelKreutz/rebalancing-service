package de.kreutz.michael.rebalanceservice.csvimport;

import de.kreutz.michael.rebalanceservice.csvimport.exception.CsvFileNotFoundException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CustomerImporterTest {

    @Test
    void importCustomerFromCsvHappyCase() {
        final CustomerImporter customerImporter = new CustomerImporter();
        final String pathToFile = TestUtils.findPathOfResource("/csv/customers.csv");

        final List<Customer> customers = customerImporter.fromCsv(pathToFile);

        assertThat(customers).isNotNull();
        assertThat(customers).hasSize(2);
        final Customer expectedCustomer1 = TestUtils.createBob();
        assertThat(customers).contains(expectedCustomer1);
        final Customer expectedCustomer2 = TestUtils.createSally();
        assertThat(customers).contains(expectedCustomer2);
    }

    @Test
    void importCustomerFromCsvFileNotFound() {
        final CustomerImporter customerImporter = new CustomerImporter();
        final String pathToFile = "file-does-not-exist";

        assertThatThrownBy(() -> customerImporter.fromCsv(pathToFile))
                .isInstanceOf(CsvFileNotFoundException.class);
    }

}
