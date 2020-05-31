package de.kreutz.michael.rebalanceservice.rebalance;

import com.google.common.collect.Lists;
import de.kreutz.michael.rebalanceservice.csvimport.Customer;
import de.kreutz.michael.rebalanceservice.csvimport.CustomerImporter;
import de.kreutz.michael.rebalanceservice.csvimport.Strategy;
import de.kreutz.michael.rebalanceservice.csvimport.StrategyImporter;
import de.kreutz.michael.rebalanceservice.fps.CustomerPortfolio;
import de.kreutz.michael.rebalanceservice.fps.FpsClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class RebalancingService {

    private final CustomerImporter customerImporter;
    private final StrategyImporter strategyImporter;
    private final FpsClient fpsClient;
    private final int batchSize;
    private final String pathToCustomerCsv;
    private final String pathToStrategyCsv;

    public RebalancingService(
            final CustomerImporter customerImporter,
            final StrategyImporter strategyImporter,
            final FpsClient fpsClient,
            @Value("${fps.batchSize}") final int batchSize,
            @Value("${csvImport.customer}") final String pathToCustomerCsv,
            @Value("${csvImport.strategy}") final String pathToStrategyCsv
    ) {
        this.customerImporter = customerImporter;
        this.strategyImporter = strategyImporter;
        this.fpsClient = fpsClient;
        this.batchSize = batchSize;
        this.pathToCustomerCsv = pathToCustomerCsv;
        this.pathToStrategyCsv = pathToStrategyCsv;
    }

    @Async
    public void rebalance() {
        final List<Customer> customers = customerImporter.fromCsv(pathToCustomerCsv);
        final List<Strategy> strategies = strategyImporter.fromCsv(pathToStrategyCsv);

        final List<CustomerPortfolio> trades = calcTrades(customers, strategies);
        final List<List<CustomerPortfolio>> partitionedTrades = Lists.partition(trades, batchSize);
        log.info("Partitioned all trades into {} batches with maximum batch size {}.", partitionedTrades.size(), batchSize);
        for (List<CustomerPortfolio> partition : partitionedTrades) {
            fpsClient.execute(partition);
        }
    }

    private List<CustomerPortfolio> calcTrades(List<Customer> customers, List<Strategy> strategies) {
        final List<CustomerPortfolio> trades = new ArrayList<>();
        for (final Customer customer : customers) {
            final Strategy strategy = strategies.stream()
                    .filter(strat -> strat.isApplicableTo(customer.getRiskLevel(), customer.calcYearsToRetirement()))
                    .findAny()
                    .orElseGet(() -> {
                        log.warn("No strategy applicable to customer {}.", customer);
                        return Strategy.DEFAULT_STRATEGY;
                    });
            log.debug("Associate strategy {} with customer {}.", strategy, customer);

            final Optional<CustomerPortfolio> customerPortfolio = fpsClient.getPortfolioOf(customer.getCustomerId());
            if (customerPortfolio.isPresent()) {
                final CustomerPortfolio trade = calcRebalancing(customerPortfolio.get(), strategy);
                if (isAnythingToBeTraded(trade)) {
                    log.debug("Calculated trade for customer with {}: {}", customer, trade);
                    trades.add(trade);
                } else {
                    log.info("Target values already reached. Skip trade of customer {}.", customer);
                }
            }
        }
        return trades;
    }

    private boolean isAnythingToBeTraded(CustomerPortfolio trade) {
        return trade.getStocks() != 0 || trade.getBonds() != 0 || trade.getCash() != 0;
    }

    private CustomerPortfolio calcRebalancing(final CustomerPortfolio customerPortfolio, final Strategy strategy) {
        final long totalValue = customerPortfolio.calcTotalValue();
        final long targetBonds = calcTargetValue(totalValue, strategy.getBondsPercentage());
        final long targetCash = calcTargetValue(totalValue, strategy.getCashPercentage());
        final long targetStocks = calcTargetValue(totalValue, strategy.getStocksPercentage());
        return CustomerPortfolio.builder()
                .customerId(customerPortfolio.getCustomerId())
                .bonds(targetBonds - customerPortfolio.getBonds())
                .cash(targetCash - customerPortfolio.getCash())
                .stocks(targetStocks - customerPortfolio.getStocks())
                .build();

    }

    private long calcTargetValue(long totalValue, int percentage) {
        return totalValue * percentage / 100;
    }
}
