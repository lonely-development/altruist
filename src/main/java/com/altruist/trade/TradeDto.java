package com.altruist.trade;

import com.altruist.common.View;
import com.altruist.trade.TradeEntity.Side;
import com.altruist.trade.TradeEntity.Status;
import com.altruist.trade.TradeEntity.Symbol;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TradeDto {

    private UUID id;

    @JsonView(View.Create.class)
    private UUID accountId;

    @NotNull
    @Min(1)
    @JsonView(View.Create.class)
    private Integer quantity;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = false)
    @JsonView(View.Create.class)
    private BigDecimal price;

    @NotNull
    @JsonView(View.Create.class)
    private Symbol symbol;

    @NotNull
    @JsonView(View.Create.class)
    private Side side;

    private Status status;

    public TradeDto(TradeEntity trade) {
        this.id = trade.getId();
        this.accountId = trade.getAccountId();
        this.quantity = trade.getQuantity();
        this.price = trade.getPrice();
        this.symbol = trade.getSymbol();
        this.side = trade.getSide();
        this.status = trade.getStatus();
    }

}
