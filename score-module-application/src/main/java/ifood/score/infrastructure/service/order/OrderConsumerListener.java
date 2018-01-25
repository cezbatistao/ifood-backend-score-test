package ifood.score.infrastructure.service.order;

import ifood.score.service.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.UUID;

@Component
@Profile("!test")
public class OrderConsumerListener {

    private static Logger log = LoggerFactory.getLogger(OrderConsumerListener.class);

    private OrderService orderService;

    @Autowired
    public OrderConsumerListener(OrderService orderService) {
        this.orderService = orderService;
    }

    @StreamListener
    @Output(OrderProcessor.CHECKOUT_ORDER_OUTPUT)
    public Flux<Order> receiveOrderToCheckout(@Input(OrderProcessor.CHECKOUT_ORDER_INPUT) Flux<Order> order) {
        return orderService.checkout(order.doOnNext(o-> log.info("Pedido recebido {}.", o)));
    }

//    @StreamListener
//    @Output(OrderProcessor.CHECKOUT_ORDER_OUTPUT)
//    public void receiveCancelOrder(UUID orderUuid) {
//        log.info("Pedido com ID [{}] cancelado.", orderUuid);
//        orderService.cancel(orderUuid).subscribe();
//    }

    @StreamListener
    @Output(OrderProcessor.CANCEL_ORDER_OUTPUT)
    public Flux<Void> receiveCancelOrder(@Input(OrderProcessor.CANCEL_ORDER_INPUT) Flux<UUID> orderUuid) {
        return orderUuid.doOnNext(o-> log.info("Pedido com ID [{}] cancelado.", o)).flatMap(i-> orderService.cancel(i));
    }
}
