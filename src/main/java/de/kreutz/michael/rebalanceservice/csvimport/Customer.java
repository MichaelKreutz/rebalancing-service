package de.kreutz.michael.rebalanceservice.csvimport;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDate;
import java.time.Period;

@Value
@Builder
public class Customer {
    long customerId;
    String email;
    LocalDate dateOfBirth;
    int riskLevel;
    int retirementAge;

    public int calcYearsToRetirement() {
        final int age = calcAge();
        return retirementAge - age;
    }

    private int calcAge() {
        return Period.between(dateOfBirth, LocalDate.now()).getYears();
    }
}
