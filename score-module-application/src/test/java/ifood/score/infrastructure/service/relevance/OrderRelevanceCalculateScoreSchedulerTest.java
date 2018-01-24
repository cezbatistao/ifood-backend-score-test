package ifood.score.infrastructure.service.relevance;

import ifood.score.infrastructure.service.order.OrderExpiratedDateVerifyScheduler;
import ifood.score.service.OrderRelevanceService;
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
public class OrderRelevanceCalculateScoreSchedulerTest {

    private OrderRelevanceCalculateScoreScheduler orderRelevanceCalculateScoreScheduler;

    @Mock
    private OrderRelevanceService orderRelevanceService;

    @Before
    public void setup() {
        orderRelevanceCalculateScoreScheduler = new OrderRelevanceCalculateScoreScheduler(orderRelevanceService);
    }

    @Test
    public void testVerifyOrdersHasExpiredServiceIsCalled() {
        when(orderRelevanceService.calculateScore()).thenReturn(Mono.empty());

        orderRelevanceCalculateScoreScheduler.calculateAllScore();

        verify(orderRelevanceService, times(1)).calculateScore();
    }
}
