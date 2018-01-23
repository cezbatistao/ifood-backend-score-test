package ifood.score.infrastructure.service.relevance;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@Profile("!test")
public class OrderRelevanceCalculateScoreScheduler {

    private static Logger log = LoggerFactory.getLogger(OrderRelevanceCalculateScoreScheduler.class);

    @Scheduled(cron = "${cron.calculate.score:0 0/30 * * * ?}")
    public void checkoutFakeOrder() {
        log.info("***************************************** Calculando.......");
    }
}
