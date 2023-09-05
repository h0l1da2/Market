package com.wemake.market.domain.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class OrderDto {

    @Size(min = 1)
    private List<OrderItemDto> items;
    @Min(value = 0)
    private int deliveryPrice;

    public OrderDto(List<OrderItemDto> items, int deliveryPrice) {
        this.items = items;
        this.deliveryPrice = deliveryPrice;
    }
}
