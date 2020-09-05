package com.altruist.trade;


import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import javax.validation.constraints.NotBlank;
import java.util.List;
import java.util.UUID;

@Repository
public interface TradeRepository extends CrudRepository<TradeEntity, UUID> {

    List<TradeEntity> getTradeEntitiesByAccountId(@NotBlank UUID accountId);
}
