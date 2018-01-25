package ifood.score.infrastructure.service.order;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.SubscribableChannel;

public interface OrderProcessor {

    String CHECKOUT_ORDER_NAMW = "checkout-order";
    String CHECKOUT_ORDER_INPUT = CHECKOUT_ORDER_NAMW;

    @Input
    SubscribableChannel subscribableChannelCheckoutOrder();

    String CHECKOUT_ORDER_OUTPUT = CHECKOUT_ORDER_NAMW;

    @Output("checkout-order")
    MessageChannel messageChannelCheckoutOrder();

    String CANCEL_ORDER_NAME = "cancel-order";
    String CANCEL_ORDER_INPUT = CANCEL_ORDER_NAME;

    @Input
    SubscribableChannel subscribableChannelCancelOrder();

    String CANCEL_ORDER_OUTPUT = CANCEL_ORDER_NAME;

    @Output(CANCEL_ORDER_INPUT)
    MessageChannel messageChannelCancelOrder();
}
