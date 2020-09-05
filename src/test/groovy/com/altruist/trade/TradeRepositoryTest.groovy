package com.altruist.trade

import com.altruist.account.AccountEntity
import com.altruist.account.AccountRepository
import com.altruist.config.DbConfig
import com.altruist.config.RepositoryConfig
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.FilterType
import org.springframework.context.annotation.Import
import org.springframework.stereotype.Repository
import org.springframework.test.annotation.Rollback
import org.springframework.test.context.ActiveProfiles
import spock.lang.Shared
import spock.lang.Specification

import javax.persistence.EntityManager
import javax.persistence.PersistenceException
import javax.validation.ConstraintViolationException

@ActiveProfiles("test")
@DataJpaTest(includeFilters = [@ComponentScan.Filter(type = FilterType.ANNOTATION, value = [Repository])])
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(value = [DbConfig, RepositoryConfig])
@Rollback
class TradeRepositoryTest extends Specification {

    @Autowired
    AccountRepository accountRepository

    @Autowired
    TradeRepository tradeRepository

    @Autowired
    EntityManager entityManager

    @Shared
    TradeEntity trade

    @Shared
    AccountEntity account

    def "Inserts a trade belonging to an existing account"() {
        given: "an existing account"
        account = new AccountEntity(
                username: "username",
                email: "email@email.com",
                address: null
        )
        account = accountRepository.save(account)

        and: "a new trade"
        trade = new TradeEntity(
                accountId: account.getId(),
                quantity: 2,
                price: 10.4,
                symbol: TradeEntity.Symbol.AAPL,
                side: TradeEntity.Side.BUY,
                status: TradeEntity.Status.SUBMITTED
        )

        when:
        trade = tradeRepository.save(trade)

        then: "the trade id is returned"
        trade.getId()
    }

    def "Retrieves a trade by id"() {

        given: "an account trade"
        account = new AccountEntity(
                email: "email3@email.com",
                username: "username2",
                address: null
        )
        account = accountRepository.save(account)
        trade = new TradeEntity(
                accountId: account.getId(),
                quantity: 2,
                price: 10.4,
                symbol: TradeEntity.Symbol.AAPL,
                side: TradeEntity.Side.BUY,
                status: TradeEntity.Status.SUBMITTED
        )

        when:
        trade = tradeRepository.save(trade)

        then: "trade should be retrievable by id"
        tradeRepository.findById(trade.id).isPresent()
    }

    def "Retrieves all trades from an account "() {

        given: "an account trade"
        account = new AccountEntity(
                email: "email3@email.com",
                username: "username2",
                address: null
        )
        account = accountRepository.save(account)
        trade = new TradeEntity(
                accountId: account.getId(),
                quantity: 2,
                price: 10.4,
                symbol: TradeEntity.Symbol.AAPL,
                side: TradeEntity.Side.BUY,
                status: TradeEntity.Status.SUBMITTED
        )

        when:
        trade = tradeRepository.save(trade)

        then: "trade should be retrievable by id"
        tradeRepository.getTradeEntitiesByAccountId(account.id).size() == 1
    }

    def "Should validate for missing trade field #field"() {

        given: "a trade with missing fields"
        account = new AccountEntity(
                email: "email3@email.com",
                username: "username2",
                address: null
        )
        account = accountRepository.save(account)
        trade = new TradeEntity(
                accountId: account.getId(),
                quantity: 2,
                price: 10.4,
                symbol: TradeEntity.Symbol.AAPL,
                side: TradeEntity.Side.BUY,
                status: TradeEntity.Status.SUBMITTED
        )
        trade[field] = null

        when:
        account = accountRepository.save(account)
        trade = tradeRepository.save(trade)
        entityManager.flush()

        then:
        thrown(PersistenceException)

        where:
        field << ["quantity", "price", "symbol", "side", "status"]
    }

    def "Should validate invalid account id"() {

        given: "a trade with missing fields"
        trade = new TradeEntity(
                accountId: UUID.randomUUID(),
                quantity: 2,
                price: 10.4,
                symbol: TradeEntity.Symbol.AAPL,
                side: TradeEntity.Side.BUY,
                status: TradeEntity.Status.SUBMITTED
        )

        when:
        account = accountRepository.save(account)
        trade = tradeRepository.save(trade)
        entityManager.flush()

        then:
        thrown(PersistenceException)

    }

}

