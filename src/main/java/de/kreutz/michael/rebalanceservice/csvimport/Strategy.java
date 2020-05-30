package de.kreutz.michael.rebalanceservice.csvimport;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class Strategy {

    public static final Strategy DEFAULT_STRATEGY = Strategy.builder()
            .bondsPercentage(0)
            .stocksPercentage(0)
            .cashPercentage(100)
            .build();

    int strategyId;
    int minRiskLevel;
    int maxRiskLevel;
    int minYearsToRetirement;
    int maxYearsToRetirement;
    int stocksPercentage;
    int cashPercentage;
    int bondsPercentage;

    public boolean isApplicableTo(final int riskLevel, final int yearsToRetirement) {
        return isRiskLevelWithinRange(riskLevel) && isYearsToRetirementWithinRange(yearsToRetirement);
    }

    private boolean isYearsToRetirementWithinRange(final int yearsToRetirement) {
        return minYearsToRetirement <= yearsToRetirement && maxYearsToRetirement >= yearsToRetirement;
    }

    private boolean isRiskLevelWithinRange(final int riskLevel) {
        return minRiskLevel <= riskLevel && maxRiskLevel >= riskLevel;
    }
}
