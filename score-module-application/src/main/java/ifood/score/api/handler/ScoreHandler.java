package ifood.score.api.handler;

import ifood.score.domain.model.ScoreCategory;
import ifood.score.domain.model.ScoreMenuItem;
import ifood.score.service.ScoreService;
import ifood.score.service.score.Type;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.NoSuchElementException;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;

@Component
public class ScoreHandler {

    private static final String PARAMETER_VALUE = "value";
    private static final String MESSAGE_ERROR_VALUE_PARAM_INVALID = "Path parâmetro value inválido [%s].";

    private ScoreService scoreService;

    @Autowired
    public ScoreHandler(ScoreService scoreService) {
        this.scoreService = scoreService;
    }

    public Mono<ServerResponse> menuItemAbove(ServerRequest request) {
        String value = request.pathVariable(PARAMETER_VALUE);
        Double scoreAbove = extractScoreParam(value);
        if (scoreAbove == null) {
            return ServerResponse.badRequest()
                    .body(Mono.just(String.format(MESSAGE_ERROR_VALUE_PARAM_INVALID, value)), String.class);
        }

        return ServerResponse.ok().contentType(APPLICATION_JSON_UTF8).body(scoreService.findFirstScoreMenuItemByScoreAndType(scoreAbove, Type.ABOVE), ScoreMenuItem.class);
    }

    public Mono<ServerResponse> menuItemBelow(ServerRequest request) {
        String value = request.pathVariable(PARAMETER_VALUE);
        Double scoreBelow = extractScoreParam(value);
        if (scoreBelow == null) {
            return ServerResponse.badRequest()
                    .body(Mono.just(String.format(MESSAGE_ERROR_VALUE_PARAM_INVALID, value)), String.class);
        }

        return ServerResponse.ok().contentType(APPLICATION_JSON_UTF8).body(scoreService.findFirstScoreMenuItemByScoreAndType(scoreBelow, Type.BELOW), ScoreMenuItem.class);
    }

    public Mono<ServerResponse> categoryAbove(ServerRequest request) {
        String value = request.pathVariable(PARAMETER_VALUE);
        Double scoreAbove = extractScoreParam(value);
        if (scoreAbove == null) {
            return ServerResponse.badRequest()
                    .body(Mono.just(String.format(MESSAGE_ERROR_VALUE_PARAM_INVALID, value)), String.class);
        }

        return ServerResponse.ok().contentType(APPLICATION_JSON_UTF8).body(scoreService.findFirstScoreCategoryByScoreAndType(scoreAbove, Type.ABOVE), ScoreCategory.class);
    }

    public Mono<ServerResponse> categoryBelow(ServerRequest request) {
        String value = request.pathVariable(PARAMETER_VALUE);
        Double scoreBelow = extractScoreParam(value);
        if (scoreBelow == null) {
            return ServerResponse.badRequest()
                    .body(Mono.just(String.format(MESSAGE_ERROR_VALUE_PARAM_INVALID, value)), String.class);
        }

        return ServerResponse.ok().contentType(APPLICATION_JSON_UTF8).body(scoreService.findFirstScoreCategoryByScoreAndType(scoreBelow, Type.BELOW), ScoreCategory.class);
    }

    protected Double extractScoreParam(String value) {
        try {
            BigDecimal scoreBigDecimal = new BigDecimal(value);

            if (scoreBigDecimal.doubleValue() < 0) {
                return null;
            }

            return scoreBigDecimal.setScale(2, RoundingMode.HALF_UP).doubleValue();
        } catch (IllegalArgumentException | NoSuchElementException e) {
            return null;
        }
    }
}
