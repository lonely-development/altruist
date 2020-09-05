package com.altruist.account

import com.altruist.trade.TradeService
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@WebMvcTest(controllers = [AccountController])
class AccountControllerTest extends Specification {

    @Autowired
    MockMvc mvc

    @Autowired
    ObjectMapper objectMapper

    @Autowired
    AccountService mockAccountService

    @Autowired
    TradeService mockTradeService

    def "Should accept account requests and ignore set id"() {
        given: "an account request"
        AccountDto request = new AccountDto(
                id: UUID.randomUUID(),
                username: "username123",
                email: "email@example.com",
                address: null
        )

        AccountDto expectedRequest = new AccountDto(
                id: null, // id should be ignored when writing
                username: request.username,
                email: request.email,
                address: null
        )

        UUID expectedId = UUID.randomUUID()

        when: "the request is submitted"
        ResultActions results = mvc.perform(post("/accounts")
                .accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )

        then: "the request is processed"
        1 * mockAccountService.createAccount(expectedRequest) >> expectedId

        and: "a Created response is returned"
        results.andExpect(status().isCreated())

        and: "the order ID is returned"
        results.andExpect(header().exists("Location"))
                .andExpect(header().string("Location", containsString("/accounts/$expectedId")))
        results.andExpect(content().json("""{"id":"$expectedId"}"""))
    }

    @TestConfiguration
    static class TestConfig {
        DetachedMockFactory factory = new DetachedMockFactory()

        @Bean
        AccountService accountService() {
            factory.Mock(AccountService)
        }

        @Bean
        TradeService tradeService() {
            factory.Mock(TradeService)
        }
    }
}
