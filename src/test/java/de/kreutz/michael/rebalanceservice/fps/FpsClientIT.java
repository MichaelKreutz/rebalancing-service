package de.kreutz.michael.rebalanceservice.fps;

import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Optional;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class FpsClientIT {

    @Autowired
    private FpsClient fpsClient;

    private WireMockServer wireMockServer;

    @BeforeEach
    public void beforeEach() {
        this.wireMockServer = new WireMockServer(8081);
        this.wireMockServer.start();
    }

    @AfterEach
    public void afterEach() {
        this.wireMockServer.stop();
    }

    @Test
    void getPortfolioOfReturns200() {
        this.wireMockServer.stubFor(
                get(urlEqualTo("/customer/1"))
                        .willReturn(aResponse()
                                .withStatus(200)
                                .withHeader("Content-Type", "application/json")
                                .withBody("{\"customerId\": 1, \"stocks\": 6700, \"bonds\": 1200, \"cash\": 400}")
                        ));

        final Optional<CustomerPortfolio> customerPortfolio = fpsClient.getPortfolioOf(1L);

        wireMockServer.verify(1, getRequestedFor(urlEqualTo("/customer/1")));
        final CustomerPortfolio expectedPortfolio = CustomerPortfolio.builder()
                .customerId(1L)
                .stocks(6700)
                .bonds(1200)
                .cash(400)
                .build();
        assertThat(customerPortfolio).isPresent();
        assertThat(customerPortfolio.get()).isEqualTo(expectedPortfolio);
    }

    @Test
    void getPortfolioOfReturns500() {
        this.wireMockServer.stubFor(
                get(urlEqualTo("/customer/1"))
                        .willReturn(aResponse()
                                .withStatus(500)
                                .withHeader("Content-Type", "application/json")
                        ));

        final Optional<CustomerPortfolio> customerPortfolio = fpsClient.getPortfolioOf(1L);

        wireMockServer.verify(1, getRequestedFor(urlEqualTo("/customer/1")));
        assertThat(customerPortfolio).isNotPresent();
    }

    @Test
    void executeReturns201() {
        this.wireMockServer.stubFor(
                post(urlEqualTo("/execute"))
                        .withRequestBody(equalToJson("[" +
                                "   {" +
                                "      \"customerId\":1," +
                                "      \"stocks\":70," +
                                "      \"bonds\":40," +
                                "      \"cash\":-30" +
                                "   }," +
                                "   {" +
                                "      \"customerId\":2," +
                                "      \"stocks\":170," +
                                "      \"bonds\":-30," +
                                "      \"cash\":-10" +
                                "   }" +
                                "]".trim()))
                        .willReturn(aResponse()
                                .withStatus(201)
                                .withHeader("Content-Type", "application/json")
                        ));

        final List<CustomerPortfolio> trades = asList(
                CustomerPortfolio.builder().customerId(1).stocks(70).bonds(40).cash(-30).build(),
                CustomerPortfolio.builder().customerId(2).stocks(170).bonds(-30).cash(-10).build()
        );
        fpsClient.execute(trades);

        wireMockServer.verify(1, postRequestedFor(urlEqualTo("/execute")));
    }

    @Test
    void executeReturns500() {
        this.wireMockServer.stubFor(
                post(urlEqualTo("/execute"))
                        .withRequestBody(equalToJson("[" +
                                "   {" +
                                "      \"customerId\":1," +
                                "      \"stocks\":70," +
                                "      \"bonds\":40," +
                                "      \"cash\":-30" +
                                "   }," +
                                "   {" +
                                "      \"customerId\":2," +
                                "      \"stocks\":170," +
                                "      \"bonds\":-30," +
                                "      \"cash\":-10" +
                                "   }" +
                                "]".trim()))
                        .willReturn(aResponse()
                                .withStatus(500)
                                .withHeader("Content-Type", "application/json")
                        ));

        final List<CustomerPortfolio> trades = asList(
                CustomerPortfolio.builder().customerId(1).stocks(70).bonds(40).cash(-30).build(),
                CustomerPortfolio.builder().customerId(2).stocks(170).bonds(-30).cash(-10).build()
        );
        fpsClient.execute(trades);

        wireMockServer.verify(1, postRequestedFor(urlEqualTo("/execute")));
    }

}
