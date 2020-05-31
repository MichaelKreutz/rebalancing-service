package de.kreutz.michael.rebalanceservice.csvimport;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class StrategyImporterTest {

    @Test
    void importStrategyFromCsvHappyCase() {
        final StrategyImporter strategyImporter = new StrategyImporter();
        final String pathToFile = TestUtils.findPathOfResource("/csv/strategy.csv");

        final List<Strategy> strategies = strategyImporter.fromCsv(pathToFile);

        assertThat(strategies).isNotNull();
        assertThat(strategies).hasSize(3);
        final Strategy expectedStrategy1 = TestUtils.createFirstStrategy();
        assertThat(strategies).contains(expectedStrategy1);
    }

}
