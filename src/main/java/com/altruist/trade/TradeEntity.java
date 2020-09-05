package com.altruist.trade;

import com.altruist.base.BaseEntity;
import com.altruist.common.PostgreSQLEnumType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.UUID;


@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity(name = "Trade")
@Table(name = "trade")
@TypeDef(name = "pgsql_enum", typeClass = PostgreSQLEnumType.class)
@SuppressWarnings("unused")
public class TradeEntity extends BaseEntity {

    @Min(0)
    @Column(name = "quantity", nullable = false, updatable = false)
    private Integer quantity;

    @Min(0)
    @Column(name = "price", nullable = false, updatable = false)
    private BigDecimal price;

    @Column(name = "symbol", nullable = false, updatable = false)
    @Enumerated(EnumType.STRING)
    @Type(type = "pgsql_enum")
    private Symbol symbol;

    @Column(name = "side", nullable = false, updatable = false)
    @Enumerated(EnumType.STRING)
    @Type(type = "pgsql_enum")
    private Side side;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    @Type(type = "pgsql_enum")
    private Status status;

    @NotNull
    @Column(name = "account_id", nullable = false, updatable = false)
    private UUID accountId;

    public TradeEntity(
            UUID accountId,
            Integer quantity,
            BigDecimal price,
            Symbol symbol,
            Side side) {
        this.accountId = accountId;
        this.quantity = quantity;
        this.price = price;
        this.symbol = symbol;
        this.side = side;
        this.status = Status.SUBMITTED;
    }


    public enum Symbol {
        AAPL, MSFT
    }

    public enum Side {
        BUY, SELL
    }

    public enum Status {
        SUBMITTED, CANCELLED, COMPLETED, FAILED
    }

}
