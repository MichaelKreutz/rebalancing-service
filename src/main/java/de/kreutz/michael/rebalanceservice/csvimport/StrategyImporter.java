package de.kreutz.michael.rebalanceservice.csvimport;

import de.kreutz.michael.rebalanceservice.csvimport.exception.CsvFileNotFoundException;
import de.kreutz.michael.rebalanceservice.csvimport.exception.CsvParseException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import static java.lang.String.format;

@Service
@Slf4j
public class StrategyImporter implements CsvImporter<Strategy> {

    private static final String STRATEGY_ID = "strategyId";
    private static final String MIN_RISK_LEVEL = "minRiskLevel";
    private static final String MAX_RISK_LEVEL = "maxRiskLevel";
    private static final String MIN_YEARS_TO_RETIREMENT = "minYearsToRetirement";
    private static final String MAX_YEARS_TO_RETIREMENT = "maxYearsToRetirement";
    private static final String STOCKS_PERCENTAGE = "stocksPercentage";
    private static final String CASH_PERCENTAGE = "cashPercentage";
    private static final String BONDS_PERCENTAGE = "bondsPercentage";
    private static final String[] HEADERS = {STRATEGY_ID, MIN_RISK_LEVEL, MAX_RISK_LEVEL, MIN_YEARS_TO_RETIREMENT,
            MAX_YEARS_TO_RETIREMENT, STOCKS_PERCENTAGE, CASH_PERCENTAGE, BONDS_PERCENTAGE};

    @Override
    public List<Strategy> fromCsv(final String pathToCsv) {
        log.info("Start reading from file {}.", pathToCsv);
        try (final Reader in = new FileReader(pathToCsv)) {
            final Iterable<CSVRecord> records = CSVFormat.DEFAULT
                    .withHeader(HEADERS)
                    .withFirstRecordAsHeader()
                    .parse(in);
            final List<Strategy> strategies = new ArrayList<>();
            for (CSVRecord record : records) {
                final Strategy customer = Strategy.builder()
                        .bondsPercentage(Integer.parseInt(record.get(BONDS_PERCENTAGE)))
                        .cashPercentage(Integer.parseInt(record.get(CASH_PERCENTAGE)))
                        .maxRiskLevel(Integer.parseInt(record.get(MAX_RISK_LEVEL)))
                        .maxYearsToRetirement(Integer.parseInt(record.get(MAX_YEARS_TO_RETIREMENT)))
                        .minRiskLevel(Integer.parseInt(record.get(MIN_RISK_LEVEL)))
                        .minYearsToRetirement(Integer.parseInt(record.get(MIN_YEARS_TO_RETIREMENT)))
                        .stocksPercentage(Integer.parseInt(record.get(STOCKS_PERCENTAGE)))
                        .strategyId(Integer.parseInt(record.get(STRATEGY_ID)))
                        .build();
                strategies.add(customer);
            }
            log.info("Read strategies {} from file.", strategies);
            return strategies;
        } catch (FileNotFoundException e) {
            throw new CsvFileNotFoundException(format("File %s not found.", pathToCsv), e);
        } catch (IOException e) {
            throw new CsvParseException(format("Failed to parse file %s.", pathToCsv), e);
        }
    }
}
