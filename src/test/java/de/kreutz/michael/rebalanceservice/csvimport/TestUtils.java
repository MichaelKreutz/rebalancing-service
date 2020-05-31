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

    public Customer createBobClone(final long customerId) {
        return Customer.builder()
                .customerId(customerId)
                .dateOfBirth(LocalDate.now().minusYears(51))
                .email("bob@bob.com")
                .retirementAge(65)
                .riskLevel(3)
                .build();
    }

    public Strategy createStrategyForBob() {
        return Strategy.builder()
                .strategyId(2)
                .minRiskLevel(0)
                .maxRiskLevel(3)
                .minYearsToRetirement(10)
                .maxYearsToRetirement(20)
                .stocksPercentage(10)
                .cashPercentage(20)
                .bondsPercentage(70)
                .build();
    }

    public Strategy createFirstStrategy() {
        return Strategy.builder()
                .strategyId(1)
                .minRiskLevel(0)
                .maxRiskLevel(3)
                .minYearsToRetirement(20)
                .maxYearsToRetirement(30)
                .stocksPercentage(20)
                .cashPercentage(20)
                .bondsPercentage(60)
                .build();
    }
}
