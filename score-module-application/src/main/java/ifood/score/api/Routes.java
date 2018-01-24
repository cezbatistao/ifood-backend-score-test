package ifood.score.api;

import ifood.score.api.handler.EchoHandler;
import ifood.score.api.handler.OrderHandler;
import ifood.score.api.handler.ScoreHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;
import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Component
public class Routes {

    @Bean
    public RouterFunction<ServerResponse> monoRouterFunction(EchoHandler echoHandler, ScoreHandler scoreHandler, OrderHandler orderHandler) {
        return route(POST("/echo"), echoHandler::echo)
                .andRoute(GET("/orders/{orderUuid}").and(accept(APPLICATION_JSON_UTF8)), orderHandler::get)
                .andRoute(GET("/orders/{orderUuid}/relevances").and(accept(APPLICATION_JSON_UTF8)), orderHandler::getRelevances)
                .andRoute(GET("/menu-item/score/{value}/above").and(accept(APPLICATION_JSON_UTF8)), scoreHandler::menuItemAbove)
                .andRoute(GET("/menu-item/score/{value}/below").and(accept(APPLICATION_JSON_UTF8)), scoreHandler::menuItemBelow)
                .andRoute(GET("/category/score/{value}/above").and(accept(APPLICATION_JSON_UTF8)), scoreHandler::categoryAbove)
                .andRoute(GET("/category/score/{value}/below").and(accept(APPLICATION_JSON_UTF8)), scoreHandler::categoryBelow);
    }
}
