package ifood.score.infrastructure.service.order;

import ifood.score.service.OrderService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import reactor.core.publisher.Mono;

import java.util.UUID;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class OrderConsumerListenerTest {

    @Mock
    private OrderService orderService;

    private OrderConsumerListener orderConsumerListener;

    @Before
    public void setup() {
        orderConsumerListener = new OrderConsumerListener(orderService);
    }

    @Test
    public void testVerifyCheckoutOrderServiceIsCalled() {
        Order orderMock = mock(Order.class);
        when(orderService.checkout(orderMock)).thenReturn(Mono.empty());

        orderConsumerListener.receiveOrderToCheckout(orderMock);

        verify(orderService, times(1)).checkout(orderMock);
    }

    @Test
    public void testVerifyCancelOrderServiceIsCalled() {
        UUID orderUuid = UUID.randomUUID();
        when(orderService.cancel(orderUuid)).thenReturn(Mono.empty());

        orderConsumerListener.receiveCancelOrder(orderUuid);

        verify(orderService, times(1)).cancel(orderUuid);
    }
}
