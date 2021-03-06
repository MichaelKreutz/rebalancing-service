package de.kreutz.michael.rebalanceservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class RebalancingApplication {

    public static void main(String[] args) {
        SpringApplication.run(RebalancingApplication.class, args);
    }

}
