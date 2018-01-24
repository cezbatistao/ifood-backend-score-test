package ifood.score.infrastructure.service.relevance;

import ifood.score.service.OrderRelevanceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import reactor.core.scheduler.Schedulers;

@Service
@Profile("!test")
public class OrderRelevanceCalculateScoreScheduler {

    private static Logger log = LoggerFactory.getLogger(OrderRelevanceCalculateScoreScheduler.class);

    private OrderRelevanceService orderRelevanceService;

    @Autowired
    public OrderRelevanceCalculateScoreScheduler(OrderRelevanceService orderRelevanceService) {
        this.orderRelevanceService = orderRelevanceService;
    }

    @Scheduled(cron = "${cron.calculate.score:0 0/30 * * * ?}")
    public void calculateAllScore() {
        log.info("Gerando os Scores de Itens de Menu e Categorias.");
        orderRelevanceService.calculateScore().subscribe();
    }
}
