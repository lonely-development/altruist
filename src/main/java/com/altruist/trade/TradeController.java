package com.altruist.trade;

import com.altruist.IdDto;
import com.altruist.common.View;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.UUID;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * TODO: Controller per object type tend to grow in size and be difficult to maintain.
 * <p/>
 * Once that's the case consider a per command based approach.
 */
@RequiredArgsConstructor
@Slf4j
@RestController
@RequestMapping("/trades")
public class TradeController {

    private final TradeService tradeService;

    @GetMapping(value = "/{id}", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<TradeDto> getById(@PathVariable UUID id) {

        log.info("Received single Trade retrieval [{}].", id);

        return tradeService.getById(id)
                           .map(trade -> ResponseEntity.ok().body(trade))
                           .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping(value = "/account/{accountId}", produces = APPLICATION_JSON_VALUE)
    public List<TradeDto> getByAccount(@PathVariable UUID accountId) {

        log.info("Received Trades retrieval.");
        return tradeService.getByAccountId(accountId);
    }

    @GetMapping(produces = APPLICATION_JSON_VALUE)
    public List<TradeDto> getAll() {

        log.info("Received Trades retrieval.");
        return tradeService.getAll();
    }

    @PostMapping(consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<IdDto> create(
            @JsonView(View.Create.class) @RequestBody @Valid TradeDto tradeDto,
            HttpServletRequest httpServletRequest
    ) throws URISyntaxException {

        log.info("Received Trade creation request [{}].", tradeDto);

        UUID tradeId = tradeService.create(tradeDto);

        return ResponseEntity
                .created(new URI(httpServletRequest
                        .getRequestURL() + "/" + tradeId.toString()))
                .body(new IdDto(tradeId));
    }

    @PatchMapping(path = "/{id}/cancel")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancel(@PathVariable UUID id) {

        log.info("Received Trade cancellation request [{}].", id);
        if (!tradeService.cancel(id)) {
            throw new FailedToCancelTrade();
        }
    }

    @ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Trade not found or status not longer SUBMITTED")
    public static class FailedToCancelTrade extends RuntimeException {
    }

}
