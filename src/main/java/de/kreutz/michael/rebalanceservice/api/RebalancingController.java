package de.kreutz.michael.rebalanceservice.api;

import de.kreutz.michael.rebalanceservice.rebalance.RebalancingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RebalancingController {

    private final RebalancingService rebalancingService;

    public RebalancingController(final RebalancingService rebalancingService) {
        this.rebalancingService = rebalancingService;
    }

    @PostMapping("/rebalance")
    public ResponseEntity<Void> rebalance() {
        rebalancingService.rebalance();

        return ResponseEntity.accepted().build();
    }
}
