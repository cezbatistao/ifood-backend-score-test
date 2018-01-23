package ifood.score.infrastructure.service.order;

import ifood.score.infrastructure.service.relevance.OrderRelevanceCalculateScoreScheduler;
import ifood.score.service.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@Profile("!test")
public class OrderExpiratedDateVerifyScheduler {

    private static Logger log = LoggerFactory.getLogger(OrderExpiratedDateVerifyScheduler.class);

    private OrderService orderService;

    @Scheduled(cron = "${cron.calculate.score:0 0/20 * * * ?}")
    public void checkoutFakeOrder() {
        log.info("Procurando Pedidos com data de confirmação com mais de um mês para marcar como EXPIRADO.");
        Mono.just(orderService.markOrdersAsExpired()).then();
    }
}
