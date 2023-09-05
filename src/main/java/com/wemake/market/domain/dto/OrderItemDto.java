package com.wemake.market.domain.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class OrderItemDto {

    @NotBlank
    private String name;
    @Min(value = 1)
    private int count;

    protected OrderItemDto() {}

    public OrderItemDto(String name, int count) {
        this.name = name;
        this.count = count;
    }
}
