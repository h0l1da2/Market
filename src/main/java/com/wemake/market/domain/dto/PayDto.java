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
}
