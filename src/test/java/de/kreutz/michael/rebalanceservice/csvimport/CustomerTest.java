package de.kreutz.michael.rebalanceservice.csvimport;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;

class CustomerTest {

    @Test
    void calcYearsToRetirement() {
        final Customer customer = Customer.builder()
                .retirementAge(65)
                .dateOfBirth(LocalDate.now().minus(20, ChronoUnit.YEARS))
                .build();

        assertThat(customer.calcYearsToRetirement()).isEqualTo(45);
    }
}
