package ifood.score.infrastructure.config.jms;

import ifood.score.infrastructure.service.order.OrderConsumerListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ErrorHandler;

public class OrderConsumerErrorHandler implements ErrorHandler {

    private static Logger log = LoggerFactory.getLogger(OrderConsumerListener.class);

    @Override
    public void handleError(Throwable t) {
        log.warn("Error to consume message on JMS.");
        log.error(t.getCause().getMessage());
    }
}