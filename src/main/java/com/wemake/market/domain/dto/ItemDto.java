package com.wemake.market.domain.dto;

import com.wemake.market.domain.Item;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ItemDto {

    @NotBlank
    @Size(min = 1)
    private String name;
    @Min(value = 100)
    private int price;
    private LocalDateTime date;

    protected ItemDto() {

    }

    public ItemDto(Item item) {
        this.name = item.getName();
        this.price = item.getPrice();
        this.date = item.getDate();
    }
}
