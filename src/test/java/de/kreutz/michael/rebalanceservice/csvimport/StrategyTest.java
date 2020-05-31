package de.kreutz.michael.rebalanceservice.csvimport;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class StrategyTest {

    @Test
    void isApplicable() {
        final Strategy strategy = TestUtils.createStrategyForBob();

        final boolean isApplicable = strategy.isApplicableTo(3, 14);

        assertThat(isApplicable).isTrue();
    }

    @Test
    void isNotApplicable() {
        final Strategy strategy = TestUtils.createFirstStrategy();

        final boolean isApplicable = strategy.isApplicableTo(3, 14);

        assertThat(isApplicable).isFalse();

    }
}
