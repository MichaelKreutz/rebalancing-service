package de.kreutz.michael.rebalanceservice.csvimport;

import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CustomerImporterTest {

    @Test
    void importCustomerFromCsvHappyCase() throws IOException {
        final CustomerImporter customerImporter = new CustomerImporter();
        final String pathToFile = findPathOfResource("/csv/customers.csv");

        final List<Customer> customers = customerImporter.fromCsv(pathToFile);

        assertThat(customers).isNotNull();
        assertThat(customers).hasSize(2);
        final Customer expectedCustomer1 = Customer.builder()
                .customerId(1)
                .dateOfBirth(LocalDate.parse("1961-04-29"))
                .email("bob@bob.com")
                .retirementAge(65)
                .riskLevel(3)
                .build();
        assertThat(customers).contains(expectedCustomer1);
        final Customer expectedCustomer2 = Customer.builder()
                .customerId(2)
                .dateOfBirth(LocalDate.parse("1978-05-01"))
                .email("sally@gmail.com")
                .retirementAge(67)
                .riskLevel(8)
                .build();
        assertThat(customers).contains(expectedCustomer2);
    }

    @Test
    void importCustomerFromCsvFileNotFound() {
        final CustomerImporter customerImporter = new CustomerImporter();
        final String pathToFile = "file-does-not-exist";

        assertThatThrownBy(() -> customerImporter.fromCsv(pathToFile))
                .isInstanceOf(FileNotFoundException.class);
    }

    private String findPathOfResource(final String resourceName) {
        return getClass().getResource(resourceName).getPath();
    }

}
