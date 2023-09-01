package com.wemake.market.domain.dto;

import com.wemake.market.domain.Item;
import com.wemake.market.domain.Role;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Date;

@Data
public class ItemUpdateDto {
    @NotBlank
    @Size(min = 1)
    private String name;
    @Min(value = 100)
    private Integer price;
    @NotNull
    private Role role;
    @NotBlank
    private String password;
    private Date date;

    private ItemUpdateDto() {}

    public ItemUpdateDto(Item item) {
        this.name = item.getName();
        this.price = item.getPrice();
        this.date = item.getDate();
    }
}
