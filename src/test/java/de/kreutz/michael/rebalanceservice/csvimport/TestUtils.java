package de.kreutz.michael.rebalanceservice.csvimport;

import lombok.experimental.UtilityClass;

import java.time.LocalDate;

@UtilityClass
public class TestUtils {

    public String findPathOfResource(final String resourceName) {
        return TestUtils.class.getResource(resourceName).getPath();
    }

    public Customer createSally() {
        return Customer.builder()
                .customerId(2)
                .dateOfBirth(LocalDate.parse("1978-05-01"))
                .email("sally@gmail.com")
                .retirementAge(67)
                .riskLevel(8)
                .build();
    }

    public Customer createBob() {
        return Customer.builder()
                .customerId(1)
                .dateOfBirth(LocalDate.parse("1961-04-29"))
                .email("bob@bob.com")
                .retirementAge(65)
                .riskLevel(3)
                .build();
    }
}
