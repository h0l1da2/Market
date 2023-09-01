package com.wemake.market.domain.dto;

import com.wemake.market.domain.Item;
import com.wemake.market.domain.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Date;

@Data
public class ItemDto {
    @NotBlank @Size(min = 1)
    private String name;
    @Size(min = 100)
    private Integer price;
    @NotNull
    private Role role;
    private Date date;

    private ItemDto() {}

    public ItemDto(Item item) {
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
