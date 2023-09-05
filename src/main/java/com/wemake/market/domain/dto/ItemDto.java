package com.wemake.market.domain.dto;

import com.wemake.market.domain.Item;
import com.wemake.market.domain.Role;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ItemDto {
    @NotBlank @Size(min = 1)
    private String name;
    @Min(value = 100)
    private int price;
    @NotNull
    private Role role;
    private LocalDateTime date;

    private ItemDto() {}

    public ItemDto(Item item) {
        this.name = item.getName();
        this.price = item.getPrice();
        this.date = item.getDate();
    }

    public ItemDto(ItemUpdateDto item) {
        this.name = item.getName();
        this.price = item.getPrice();
        this.date = item.getDate();
    }

    public ItemDto(String name, Integer price, Role role) {
        this.name = name;
        this.price = price;
        this.role = role;
    }
}
