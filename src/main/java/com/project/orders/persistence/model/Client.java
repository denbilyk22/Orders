package com.project.orders.persistence.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "clients")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class Client extends BaseEntity {

    private String name;

    private String email;

    private String address;

    @Builder.Default
    private Boolean active = true;

    private ZonedDateTime deactivationDate;

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
