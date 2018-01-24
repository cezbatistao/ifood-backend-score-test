package ifood.score.api;

import ifood.score.api.handler.EchoHandler;
import ifood.score.api.handler.OrderHandler;
import ifood.score.api.handler.ScoreHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Component
public class Routes {

    @Bean
    public RouterFunction<ServerResponse> monoRouterFunction(EchoHandler echoHandler, ScoreHandler scoreHandler, OrderHandler orderHandler) {
        return route(POST("/echo"), echoHandler::echo)
                .andRoute(GET("/menu-item/score/{value}/above"), scoreHandler::menuItemAbove)
                .andRoute(GET("/menu-item/score/{value}/below"), scoreHandler::menuItemBelow)
                .andRoute(GET("/category/score/{value}/above"), scoreHandler::categoryAbove)
                .andRoute(GET("/category/score/{value}/below"), scoreHandler::categoryBelow);
    }
}
