package de.kreutz.michael.rebalanceservice.csvimport;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDate;

@Value
@Builder
public class Customer {
    long customerId;
    String email;
    LocalDate dateOfBirth;
    int riskLevel;
    int retirementAge;
}
