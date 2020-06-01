package de.kreutz.michael.rebalanceservice.rebalance;

import de.kreutz.michael.rebalanceservice.csvimport.Customer;
import de.kreutz.michael.rebalanceservice.csvimport.CustomerImporter;
import de.kreutz.michael.rebalanceservice.csvimport.StrategyImporter;
import de.kreutz.michael.rebalanceservice.csvimport.TestUtils;
import de.kreutz.michael.rebalanceservice.fps.CustomerPortfolio;
import de.kreutz.michael.rebalanceservice.fps.FpsClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
class RebalancingServiceTest {

    @Mock
    private CustomerImporter customerImporter;
    @Mock
    private StrategyImporter strategyImporter;
    @Mock
    private FpsClient fpsClient;

    private RebalancingService rebalancingService;

    @Test
    void rebalanceToApplicableStrategy() {
        rebalancingService = new RebalancingService(customerImporter, strategyImporter, fpsClient, 1, "pathToCustomers.csv", "pathToStrategy.csv");
        doReturn(singletonList(TestUtils.createBobClone(1L))).when(customerImporter).fromCsv(anyString());
        doReturn(singletonList(TestUtils.createStrategyForBob())).when(strategyImporter).fromCsv(anyString());
        doReturn(Optional.of(CustomerPortfolio.builder().stocks(550).bonds(50).cash(400).build())).when(fpsClient).getPortfolioOf(1L);

        rebalancingService.rebalance();

        ArgumentCaptor<List<CustomerPortfolio>> tradesCaptor = ArgumentCaptor.forClass(List.class);
        verify(customerImporter).fromCsv(anyString());
        verify(strategyImporter).fromCsv(anyString());
        verify(fpsClient).execute(tradesCaptor.capture());
        verify(fpsClient).getPortfolioOf(any(Long.class));
        verifyNoMoreInteractions(customerImporter, strategyImporter, fpsClient);
        final List<CustomerPortfolio> trades = tradesCaptor.getValue();
        assertThat(trades).hasSize(1);
        final CustomerPortfolio expectedTrade = CustomerPortfolio.builder().stocks(-450).bonds(650).cash(-200).build();
        assertThat(trades.get(0)).isEqualTo(expectedTrade);
    }

    @Test
    void noRebalanceIfTargetValueIsAlreadyReached() {
        rebalancingService = new RebalancingService(customerImporter, strategyImporter, fpsClient, 1, "pathToCustomers.csv", "pathToStrategy.csv");
        doReturn(singletonList(TestUtils.createBobClone(1L))).when(customerImporter).fromCsv(anyString());
        doReturn(singletonList(TestUtils.createStrategyForBob())).when(strategyImporter).fromCsv(anyString());
        doReturn(Optional.of(CustomerPortfolio.builder().build())).when(fpsClient).getPortfolioOf(1L);

        rebalancingService.rebalance();

        verify(customerImporter).fromCsv(anyString());
        verify(strategyImporter).fromCsv(anyString());
        verify(fpsClient, never()).execute(any());
        verify(fpsClient).getPortfolioOf(any(Long.class));
        verifyNoMoreInteractions(customerImporter, strategyImporter, fpsClient);
    }

    @Test
    void rebalanceToOnlyCashIfStrategyIsMissing() {
        rebalancingService = new RebalancingService(customerImporter, strategyImporter, fpsClient, 1, "pathToCustomers.csv", "pathToStrategy.csv");
        doReturn(singletonList(TestUtils.createBobClone(1L))).when(customerImporter).fromCsv(anyString());
        doReturn(singletonList(TestUtils.createFirstStrategy())).when(strategyImporter).fromCsv(anyString());
        doReturn(Optional.of(CustomerPortfolio.builder().stocks(100).bonds(50).cash(30).build())).when(fpsClient).getPortfolioOf(1L);

        rebalancingService.rebalance();

        ArgumentCaptor<List<CustomerPortfolio>> tradesCaptor = ArgumentCaptor.forClass(List.class);
        verify(customerImporter).fromCsv(anyString());
        verify(strategyImporter).fromCsv(anyString());
        verify(fpsClient).execute(tradesCaptor.capture());
        verify(fpsClient).getPortfolioOf(any(Long.class));
        verifyNoMoreInteractions(customerImporter, strategyImporter, fpsClient);
        final List<CustomerPortfolio> trades = tradesCaptor.getValue();
        assertThat(trades).hasSize(1);
        final CustomerPortfolio expectedTrade = CustomerPortfolio.builder().stocks(-100).bonds(-50).cash(150).build();
        assertThat(trades.get(0)).isEqualTo(expectedTrade);
    }

    @Test
    void rebalanceToMoreCashIfTargetValuesNotReachable() {
        rebalancingService = new RebalancingService(customerImporter, strategyImporter, fpsClient, 1, "pathToCustomers.csv", "pathToStrategy.csv");
        doReturn(singletonList(TestUtils.createBobClone(1L))).when(customerImporter).fromCsv(anyString());
        doReturn(singletonList(TestUtils.createStrategyForBob())).when(strategyImporter).fromCsv(anyString());
        doReturn(Optional.of(CustomerPortfolio.builder().stocks(3).bonds(3).cash(3).build())).when(fpsClient).getPortfolioOf(1L);

        rebalancingService.rebalance();

        ArgumentCaptor<List<CustomerPortfolio>> tradesCaptor = ArgumentCaptor.forClass(List.class);
        verify(customerImporter).fromCsv(anyString());
        verify(strategyImporter).fromCsv(anyString());
        verify(fpsClient).execute(tradesCaptor.capture());
        verify(fpsClient).getPortfolioOf(any(Long.class));
        verifyNoMoreInteractions(customerImporter, strategyImporter, fpsClient);
        final List<CustomerPortfolio> trades = tradesCaptor.getValue();
        assertThat(trades).hasSize(1);
        final CustomerPortfolio expectedTrade = CustomerPortfolio.builder().stocks(-3).bonds(3).cash(0).build();
        assertThat(trades.get(0)).isEqualTo(expectedTrade);
    }

    @Test
    void partitionTradesIntoBatches() {
        rebalancingService = new RebalancingService(customerImporter, strategyImporter, fpsClient, 3, "pathToCustomers.csv", "pathToStrategy.csv");
        final List<Customer> customers = IntStream.range(0, 10).mapToObj(TestUtils::createBobClone).collect(Collectors.toList());
        doReturn(customers).when(customerImporter).fromCsv(anyString());
        doReturn(singletonList(TestUtils.createStrategyForBob())).when(strategyImporter).fromCsv(anyString());
        doReturn(Optional.of(CustomerPortfolio.builder().cash(10).build())).when(fpsClient).getPortfolioOf(any(Long.class));

        rebalancingService.rebalance();

        verify(customerImporter).fromCsv(anyString());
        verify(strategyImporter).fromCsv(anyString());
        verify(fpsClient, times(4)).execute(any());
        verify(fpsClient, times(10)).getPortfolioOf(any(Long.class));
        verifyNoMoreInteractions(customerImporter, strategyImporter, fpsClient);
    }
}
