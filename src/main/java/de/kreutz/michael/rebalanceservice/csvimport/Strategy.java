package de.kreutz.michael.rebalanceservice.csvimport;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class Strategy {

    int strategyId;
    int minRiskLevel;
    int maxRiskLevel;
    int minYearsToRetirement;
    int maxYearsToRetirement;
    int stocksPercentage;
    int cashPercentage;
    int bondsPercentage;
}
