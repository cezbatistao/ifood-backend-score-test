package ifood.score.infrastructure.service.order;

import ifood.score.mock.generator.order.OrderPicker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.IntStream;

import static ifood.score.mock.generator.RandomishPicker._int;

@Service
@Profile("!test")
public class OrderCheckoutMock {

	static final String CHECKOUT_ORDER_QUEUE = "checkout-order";
	static final String CANCEL_ORDER_QUEUE = "cancel-order";

	@Autowired
	JmsTemplate jmsTemplate;

	private ConcurrentLinkedQueue<UUID> cancellantionQueue = new ConcurrentLinkedQueue<>();

	private static OrderPicker picker = new OrderPicker();

	@Scheduled(fixedRate = 3 * 1000)
	public void checkoutFakeOrder() {
		IntStream.rangeClosed(1, _int(2, 12)).forEach(t -> {
			Order order = picker.pick();
			if (_int(0, 20) % 20 == 0) {
				cancellantionQueue.add(order.getUuid());
			}
			jmsTemplate.convertAndSend(CHECKOUT_ORDER_QUEUE, order);
		});
	}

	@Scheduled(fixedRate = 30 * 1000)
	public void cancelFakeOrder() {
		IntStream.range(1, _int(2, cancellantionQueue.size() > 2 ? cancellantionQueue.size() : 2)).forEach(t -> {
			UUID orderUuid = cancellantionQueue.poll();
			if (orderUuid != null) {
				jmsTemplate.convertAndSend(CANCEL_ORDER_QUEUE, orderUuid);
			}
		});
	}
}
