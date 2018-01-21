package ifood.score.api.handler;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
public class OrderHandler {

    public Mono<ServerResponse> orders(ServerRequest request) {
        return ServerResponse.ok().body(Mono.just("AHHHHHHHHHHH"), String.class);
    }
}
