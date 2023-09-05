package com.wemake.market.domain;

import lombok.Data;

@Data
public class Coupon {

    private String name;
    private How how;
    private Where wheres;
    // 비율일 경우 -> 적용 비율
    private int rate;
    // 고정일 경우 -> 적용 금액
    private int amount;

    protected Coupon() {

    }

    public Coupon(String name, How how, Where wheres) {
        this.name = name;
        this.how = how;
        this.wheres = wheres;
    }

    public Coupon(How how, Where wheres) {
        this.how = how;
        this.wheres = wheres;
    }
}
