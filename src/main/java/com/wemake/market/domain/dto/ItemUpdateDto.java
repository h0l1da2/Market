package com.wemake.market.domain.dto;

import com.wemake.market.domain.Item;
import com.wemake.market.domain.Role;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ItemUpdateDto {
    @NotBlank
    @Size(min = 1)
    private String name;
    @Min(value = 100)
    private int price;
    @NotNull
    private Role role;
    @NotBlank
    private String password;
    private LocalDateTime date;

    private ItemUpdateDto() {}

    public ItemUpdateDto(Item item) {
        this.name = item.getName();
        this.price = item.getPrice();
        this.date = item.getDate();
    }

    public ItemUpdateDto(String name, Integer price, @NotNull Role role, String password) {
        this.name = name;
        this.price = price;
        this.role = role;
        this.password = password;
    }
}
