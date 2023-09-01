package com.wemake.market.domain;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
public class Coupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated(value = EnumType.STRING)
    private How how;
    @Enumerated(value = EnumType.STRING)
    private Where wheres;
    // 비율일 경우 -> 적용 비율
    private Integer rate;
    // 고정일 경우 -> 적용 금액
    private Integer amount;
}
