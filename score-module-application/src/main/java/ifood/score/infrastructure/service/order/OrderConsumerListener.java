package ifood.score.infrastructure.service.order;

import ifood.score.service.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;
import reactor.core.scheduler.Schedulers;

import java.util.UUID;

import static ifood.score.infrastructure.service.order.OrderCheckoutMock.CANCEL_ORDER_QUEUE;
import static ifood.score.infrastructure.service.order.OrderCheckoutMock.CHECKOUT_ORDER_QUEUE;

@Component
@Profile("!test")
public class OrderConsumerListener {

    private static Logger log = LoggerFactory.getLogger(OrderConsumerListener.class);

    private OrderService orderService;

    @Autowired
    public OrderConsumerListener(OrderService orderService) {
        this.orderService = orderService;
    }

    @JmsListener(destination = CHECKOUT_ORDER_QUEUE, containerFactory = "containerFactory")
    public void receiveFakeOrder(Order order) {
        log.info("Pedido recebido {}.", order);
        orderService.save(order).subscribeOn(Schedulers.single());
    }

    @JmsListener(destination = CANCEL_ORDER_QUEUE, containerFactory = "containerFactory")
    public void receiveCancelFakeOrder(UUID orderUuid) {
        log.info("Pedido com ID [{}] cancelado.", orderUuid);
        orderService.cancel(orderUuid).subscribeOn(Schedulers.single());
    }
}
