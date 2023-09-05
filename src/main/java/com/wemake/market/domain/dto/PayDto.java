package com.wemake.market.domain.dto;

import com.wemake.market.domain.Coupon;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class PayDto {
    @Size(min = 1)
    private List<OrderItemDto> items;
    @Min(value = 0)
    private int deliveryPrice;
    private boolean useCoupon;
    private Coupon coupon;

    protected PayDto() {}

    public PayDto(List<OrderItemDto> items, int deliveryPrice, boolean useCoupon, Coupon coupon) {
        this.items = items;
        this.deliveryPrice = deliveryPrice;
        this.useCoupon = useCoupon;
        this.coupon = coupon;
    }

    public PayDto(List<OrderItemDto> items, int deliveryPrice, boolean useCoupon) {
        this.items = items;
        this.deliveryPrice = deliveryPrice;
        this.useCoupon = useCoupon;
    }
}
