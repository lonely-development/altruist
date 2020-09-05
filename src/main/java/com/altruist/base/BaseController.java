package com.altruist.base;

import com.altruist.IdDto;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.UUID;

public abstract class BaseController {

    @NotNull
    protected ResponseEntity<IdDto> buildCreatedResponse(
            HttpServletRequest httpServletRequest, UUID accountId)
            throws URISyntaxException {
        return ResponseEntity.created(
                new URI(httpServletRequest.getRequestURL() + "/" +
                        accountId.toString()))
                             .body(new IdDto(accountId));
    }
}
