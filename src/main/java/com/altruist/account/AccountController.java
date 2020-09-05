package com.altruist.account;

import com.altruist.IdDto;
import com.altruist.base.BaseController;
import com.altruist.common.View;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.net.URISyntaxException;
import java.util.List;
import java.util.UUID;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * TODO: Controller per object type tend to grow in size and be difficult to maintain.
 * <p/>
 * Once that's the case consider a per command based approach.
 */
@RestController
@RequestMapping("/accounts")
@Slf4j
@RequiredArgsConstructor
public class AccountController extends BaseController {

    private final AccountService accountService;

    @GetMapping(value = "/{id}", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<AccountDto> getById(@PathVariable UUID id) {

        log.info("Received single Account retrieval [{}].", id);
        return accountService.getById(id)
                             .map(account -> ResponseEntity.ok().body(account))
                             .orElseGet(
                                     () -> ResponseEntity.notFound().build());
    }

    @GetMapping(produces = APPLICATION_JSON_VALUE)
    public List<AccountDto> getAll() {

        log.info("Received Accounts retrieval.");
        return accountService.getAll();
    }

    @PostMapping(consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<IdDto> create(
            @JsonView(View.Create.class) @RequestBody @Valid AccountDto accountDto,
            HttpServletRequest httpServletRequest
    ) throws URISyntaxException {

        log.info("Received Account creation request [{}].", accountDto);
        UUID accountId = accountService.createAccount(accountDto);
        return buildCreatedResponse(httpServletRequest, accountId);
    }

}
