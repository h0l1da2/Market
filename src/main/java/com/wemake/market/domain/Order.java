package com.wemake.market.domain;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    private Item item;
    private Integer count;
    private Integer deliveryPrice;
}
