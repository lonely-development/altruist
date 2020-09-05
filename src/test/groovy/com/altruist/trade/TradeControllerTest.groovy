package com.altruist.trade


import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.ResultActions
import spock.lang.Specification
import spock.mock.DetachedMockFactory

import static org.hamcrest.Matchers.containsString
import static org.springframework.http.MediaType.APPLICATION_JSON
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@WebMvcTest(controllers = [TradeController])
class TradeControllerTest extends Specification {

    @Autowired
    MockMvc mvc

    @Autowired
    ObjectMapper objectMapper

    @Autowired
    TradeService mockTradeService

    def "Should accept trade requests and ignore set id and status"() {
        given: "a trade request"
        TradeDto request = new TradeDto(
                id: UUID.randomUUID(),
                accountId: UUID.randomUUID(),
                quantity: 4,
                price: 1,
                symbol: TradeEntity.Symbol.MSFT,
                side: TradeEntity.Side.BUY,
                status: TradeEntity.Status.COMPLETED
        )

        TradeDto expectedRequest = new TradeDto(
                id: null, // id should be ignored when writing
                accountId: request.accountId,
                quantity: request.quantity,
                price: request.price,
                symbol: request.symbol,
                side: request.side,
                status: null
        )

        UUID expectedId = UUID.randomUUID()

        when: "the request is submitted"
        ResultActions results = mvc.perform(post("/trades")
                .accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )

        then: "the request is processed"
        1 * mockTradeService.create(expectedRequest) >> expectedId

        and: "a Created response is returned"
        results.andExpect(status().isCreated())

        and: "the order ID is returned"
        results.andExpect(header().exists("Location"))
                .andExpect(header().string("Location", containsString("/trades/$expectedId")))
        results.andExpect(content().json("""{"id":"$expectedId"}"""))
    }

    def "Should not accept trade field #field should be greater than zero"() {

        given: "an account missing fields"
        TradeDto request = new TradeDto(
                id: UUID.randomUUID(),
                accountId: UUID.randomUUID(),
                quantity: 4,
                price: 1,
                symbol: TradeEntity.Symbol.MSFT,
                side: TradeEntity.Side.BUY,
                status: TradeEntity.Status.COMPLETED
        )
        request[field] = 0

        when:
        ResultActions results = mvc.perform(post("/trades")
                .accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )

        then:
        results.andExpect(status().isBadRequest())

        where:
        field << ["quantity", "price"]
    }

    def "When cancel operation fails BAD_REQUEST should be returned"() {
        given: "a trade cancel request"
        UUID expectedId = UUID.randomUUID()

        when: "the request is submitted"
        ResultActions results = mvc.perform(patch("/trades/" + expectedId.toString() + "/cancel"))

        then: "the request is processed"
        1 * mockTradeService.cancel(expectedId) >> false

        and: "a Created response is returned"
        results.andExpect(status().isBadRequest())
    }

    @TestConfiguration
    static class TestConfig {
        DetachedMockFactory factory = new DetachedMockFactory()

        @Bean
        TradeService tradeService() {
            factory.Mock(TradeService)
        }
    }
}
