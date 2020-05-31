package de.kreutz.michael.rebalanceservice.fps;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class CustomerPortfolio {

    long customerId;
    long stocks;
    long bonds;
    long cash;

    public long calcTotalValue() {
        return stocks + bonds + cash;
    }
}
