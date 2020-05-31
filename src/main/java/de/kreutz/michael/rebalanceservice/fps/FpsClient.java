package de.kreutz.michael.rebalanceservice.fps;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class FpsClient {

    private final WebClient webClient;

    public FpsClient(
            final WebClient.Builder webClientBuilder,
            @Value("${fps.baseUrl}") final String baseUrl
    ) {
        this.webClient = webClientBuilder.baseUrl(baseUrl).build();
    }

    public Optional<CustomerPortfolio> getPortfolioOf(final long customerId) {
        log.info("Get portfolio of customer with id {}.", customerId);
        try {
            final CustomerPortfolio customerPortfolio = webClient.get()
                    .uri("/customer/{customerId}", customerId).accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .bodyToMono(CustomerPortfolio.class)
                    .block();
            return Optional.ofNullable(customerPortfolio);
        } catch (WebClientResponseException e) {
            log.error("Failed to fetch portfolio from fps for customer with id {} due to: ", customerId, e);
            return Optional.empty();
        }
    }

    public void execute(final List<CustomerPortfolio> batchOfTrades) {
        log.info("Execute trades {}.", batchOfTrades);
        try {
            webClient.post()
                    .uri("/execute")
                    .bodyValue(batchOfTrades)
                    .retrieve()
                    .bodyToMono(Void.class)
                    .block();
            log.info("Successfully executed batch of trades {}.", batchOfTrades);
        } catch (WebClientResponseException e) {
            log.error("Failed to execute batch of trades {} due to: ", batchOfTrades, e);
        }
    }
}
