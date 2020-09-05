package com.altruist.account;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AccountRepository extends CrudRepository<AccountEntity, UUID> {

    @Override
    @NotNull
    @EntityGraph(attributePaths = {"address"})
    Optional<AccountEntity> findById(@NotNull UUID uuid);

    @Override
    @NotNull
    @EntityGraph(attributePaths = {"address"})
    Iterable<AccountEntity> findAll();
}
