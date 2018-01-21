package ifood.score.service;

import ifood.score.domain.repository.OrderRelevanceRepository;
import ifood.score.domain.repository.OrderRepository;
import ifood.score.infrastructure.service.order.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class OrderService {

    private OrderRepository orderRepository;
    private OrderRelevanceService orderRelevanceService;

    @Autowired
    public OrderService(OrderRepository orderRepository, OrderRelevanceService orderRelevanceService) {
        this.orderRepository = orderRepository;
        this.orderRelevanceService = orderRelevanceService;
    }

    public Mono<Order> save(Order order) {
        Mono<Order> orderMono = orderRepository.save(order);
        return orderRelevanceService.calculateRelevance(orderMono).map(r->order);
//        return orderMono.map(o-> {
//            this.orderRelevanceService.calculateRelevance(o);
//            return o;
//        });
    }
}
