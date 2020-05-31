package de.kreutz.michael.rebalanceservice.csvimport;

import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CustomerImporterTest {

    @Test
    void importCustomerFromCsvHappyCase() throws IOException {
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
                .isInstanceOf(FileNotFoundException.class);
    }

}
