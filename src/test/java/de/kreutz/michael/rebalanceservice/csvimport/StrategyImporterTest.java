package de.kreutz.michael.rebalanceservice.csvimport;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class StrategyImporterTest {

    @Test
    void importStrategyFromCsvHappyCase() throws IOException {
        final StrategyImporter strategyImporter = new StrategyImporter();
        final String pathToFile = TestUtils.findPathOfResource("/csv/strategy.csv");

        final List<Strategy> strategies = strategyImporter.fromCsv(pathToFile);

        assertThat(strategies).isNotNull();
        assertThat(strategies).hasSize(3);
        final Strategy expectedStrategy1 = Strategy.builder()
                .strategyId(1)
                .minRiskLevel(0)
                .maxRiskLevel(3)
                .minYearsToRetirement(20)
                .maxYearsToRetirement(30)
                .stocksPercentage(20)
                .cashPercentage(20)
                .bondsPercentage(60)
                .build();
        assertThat(strategies).contains(expectedStrategy1);
    }
}
