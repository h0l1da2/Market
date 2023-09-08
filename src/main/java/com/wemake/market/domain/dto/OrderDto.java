package com.wemake.market.domain.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class OrderDto {
    @Size(min = 1)
    private List<OrderItemDto> items;
    @Min(value = 0)
    private int deliveryPrice;
    private boolean useCoupon;
    private Long couponId;

}
