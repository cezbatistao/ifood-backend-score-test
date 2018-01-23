package ifood.score.infrastructure.service.order;

import ifood.score.service.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
@Profile("!test")
public class OrderExpiratedDateVerifyScheduler {

    private static Logger log = LoggerFactory.getLogger(OrderExpiratedDateVerifyScheduler.class);

    private OrderService orderService;

    @Autowired
    public OrderExpiratedDateVerifyScheduler(OrderService orderService) {
        this.orderService = orderService;
    }

    @Scheduled(cron = "${cron.check.orders.expired:0 0/20 * * * ?}")
    public void checkOrdersExpired() {
        log.info("Procurando Pedidos com data de confirmação com mais de um mês para marcar como EXPIRADO.");
        Mono.just(orderService.markOrdersAsExpired()).subscribeOn(Schedulers.single());
    }
}
