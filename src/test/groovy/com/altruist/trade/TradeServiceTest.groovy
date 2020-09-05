package com.altruist.trade


import spock.lang.Specification

class TradeServiceTest extends Specification {

    TradeRepository mockTradeRepository = Mock()
    TradeService service = new TradeService(mockTradeRepository)


    def "Should save trade"() {
        given: "a trade"
        UUID uuid = UUID.randomUUID()
        TradeDto trade = new TradeDto(
                id: uuid,
                accountId: uuid,
                quantity: 2,
                price: 2,
                symbol: TradeEntity.Symbol.MSFT,
                side: TradeEntity.Side.BUY,
                status: null
        )
        UUID expectedTradeId = UUID.randomUUID()

        when:
        UUID tradeId = service.create(trade)

        then: "the trade is saved"
        1 * mockTradeRepository.save(_) >> { TradeEntity arg ->
            with(arg) {
                id == null // uuid should not be set
                accountId == trade.accountId
                quantity == trade.quantity
                price == trade.price
                symbol == trade.symbol
                side == trade.side
                status == TradeEntity.Status.SUBMITTED // on create state
            }

            arg.id = expectedTradeId
            arg
        }

        and:
        tradeId == expectedTradeId
    }

    def "Should cancel submitted trade"() {
        given: "a already existing trade in SUBMITTED status"
        UUID expectedTradeId = UUID.randomUUID()
        TradeEntity trade = new TradeEntity(
                id: expectedTradeId,
                accountId: UUID.randomUUID(),
                quantity: 2,
                price: 2,
                symbol: TradeEntity.Symbol.MSFT,
                side: TradeEntity.Side.BUY,
                status: TradeEntity.Status.SUBMITTED
        )


        when:
        Boolean operation = service.cancel(expectedTradeId)

        then: "the trade is updated"
        1 * mockTradeRepository.findById(expectedTradeId) >> Optional.of(trade)
        1 * mockTradeRepository.save(_) >> { TradeEntity arg ->
            with(arg) {
                id == expectedTradeId // uuid should not be set
                accountId == trade.accountId
                quantity == trade.quantity
                price == trade.price
                symbol == trade.symbol
                side == trade.side
                status == TradeEntity.Status.CANCELLED // on create state
            }

            arg.id = expectedTradeId
            arg
        }

        and:
        operation
    }

    def "Should not be able to cancel a trade that is not in SUBMITTED status"() {
        given: "a already existing trade not in SUBMITTED status"
        UUID expectedTradeId = UUID.randomUUID()
        TradeEntity trade = new TradeEntity(
                id: expectedTradeId,
                accountId: UUID.randomUUID(),
                quantity: 2,
                price: 2,
                symbol: TradeEntity.Symbol.MSFT,
                side: TradeEntity.Side.BUY,
                status: TradeEntity.Status.COMPLETED
        )


        when:
        Boolean operation = service.cancel(expectedTradeId)

        then: "the trade is not updated"
        1 * mockTradeRepository.findById(expectedTradeId) >> Optional.of(trade)
        0 * mockTradeRepository.save(_)
        and:
        !operation
    }

    def "Should not be able to cancel a non existing trade"() {
        given: "a already non existing trade"
        UUID expectedTradeId = UUID.randomUUID()
        1 * mockTradeRepository.findById(expectedTradeId) >> Optional.empty()

        when:
        Boolean operation = service.cancel(expectedTradeId)

        then: "the trade is not updated"
        !operation
    }
}
