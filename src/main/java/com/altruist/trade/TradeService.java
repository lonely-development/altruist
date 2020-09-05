package com.altruist.trade;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class TradeService {

    private final TradeRepository tradeRepository;

    public Optional<TradeDto> getById(UUID id) {
        return tradeRepository.findById(id).map(TradeDto::new);
    }

    public List<TradeDto> getAll() {
        return StreamSupport
                .stream(tradeRepository.findAll().spliterator(), false)
                .map(TradeDto::new)
                .collect(Collectors.toList());
    }

    public List<TradeDto> getByAccountId(UUID accountId) {
        return tradeRepository.getTradeEntitiesByAccountId(accountId)
                              .stream()
                              .map(TradeDto::new)
                              .collect(Collectors.toList());
    }

    public UUID create(TradeDto tradeDto) {

        return tradeRepository.save(new TradeEntity(
                tradeDto.getAccountId(),
                tradeDto.getQuantity(),
                tradeDto.getPrice(),
                tradeDto.getSymbol(),
                tradeDto.getSide()
        )).getId();
    }


    public boolean cancel(UUID tradeId) {

        Optional<TradeEntity> tradeEntity = tradeRepository.findById(tradeId);

        if (tradeEntity.isEmpty()) {
            return false;
        }

        if (!tradeEntity.get().getStatus()
                        .equals(TradeEntity.Status.SUBMITTED)) {
            return false;
        }

        tradeEntity.get().setStatus(TradeEntity.Status.CANCELLED);

        tradeRepository.save(tradeEntity.get());

        return true;
    }
}
