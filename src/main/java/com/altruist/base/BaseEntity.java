package com.altruist.base;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.validation.constraints.FutureOrPresent;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@MappedSuperclass
public abstract class BaseEntity {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "created_by", updatable = false, nullable = false)
    private String createdBy;

    @FutureOrPresent
    @Column(name = "created_date", updatable = false, nullable = false)
    private LocalDateTime createdDate;

    @Column(name = "updated_by", updatable = false, nullable = false)
    private String updatedBy;

    @FutureOrPresent
    @Column(name = "updated_date", updatable = false, nullable = false)
    private LocalDateTime updatedDate;

}
