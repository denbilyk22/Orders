package com.project.orders.persistence.model;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "clients")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Client implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    private String name;

    private String email;

    private String address;

    @Builder.Default
    private Boolean active = true;

    private ZonedDateTime deactivationDate;

    @Builder.Default
    private ZonedDateTime createdDate = ZonedDateTime.now();

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @Builder.Default
    @OneToMany(mappedBy = "supplier")
    private final List<Order> suppliedOrders = new ArrayList<>();

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @Builder.Default
    @OneToMany(mappedBy = "consumer")
    private final List<Order> consumedOrders = new ArrayList<>();

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @Builder.Default
    @OneToMany(mappedBy = "client")
    private List<ClientBalanceChange> balanceChanges = new ArrayList<>();

}
