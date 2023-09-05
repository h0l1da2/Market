package com.wemake.market.domain.dto;

import com.wemake.market.domain.Item;
import com.wemake.market.domain.Role;
import jakarta.validation.constraints.*;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Data
public class ItemCreateDto {
    @NotBlank @Size(min = 1)
    private String name;
    @Min(value = 100)
    private int price;
    @NotNull
    private Role role;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime date;

    private ItemCreateDto() {}

    public ItemCreateDto(Item item) {
        this.name = item.getName();
        this.price = item.getPrice();
        this.date = item.getDate();
    }

    public ItemCreateDto(ItemUpdateDto item) {
        this.name = item.getName();
        this.price = item.getPrice();
        this.date = item.getDate();
    }

    public ItemCreateDto(String name, Integer price, Role role) {
        this.name = name;
        this.price = price;
        this.role = role;
    }
}
