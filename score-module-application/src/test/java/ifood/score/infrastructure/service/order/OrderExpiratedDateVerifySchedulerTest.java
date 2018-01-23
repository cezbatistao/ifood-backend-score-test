package ifood.score.infrastructure.service.order;

import ifood.score.service.OrderService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import reactor.core.publisher.Mono;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class OrderExpiratedDateVerifySchedulerTest {

    private OrderExpiratedDateVerifyScheduler orderExpiratedDateVerifyScheduler;

    @Mock
    private OrderService orderService;

    @Before
    public void setup() {
        orderExpiratedDateVerifyScheduler = new OrderExpiratedDateVerifyScheduler(orderService);
    }

    @Test
    public void testVerifyOrdersHasExpiredServiceIsCalled() {
        when(orderService.markOrdersAsExpired()).thenReturn(Mono.empty());

        orderExpiratedDateVerifyScheduler.checkOrdersExpired();

        verify(orderService, times(1)).markOrdersAsExpired();
    }
}
