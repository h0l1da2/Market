package com.wemake.market.domain.dto;

import com.wemake.market.domain.Item;
import com.wemake.market.domain.Role;
import jakarta.validation.constraints.*;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

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
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime date;

    protected ItemUpdateDto() {}

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
