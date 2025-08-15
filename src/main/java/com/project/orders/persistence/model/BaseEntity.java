package com.project.orders.persistence.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.UUID;

@MappedSuperclass
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public abstract class BaseEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    private ZonedDateTime createdDate;

    @PrePersist
    public void prePersist() {
        if (Objects.isNull(this.createdDate)) {
            this.createdDate = ZonedDateTime.now();
        }
    }

}
