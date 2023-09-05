package com.wemake.market.domain.dto;

import com.wemake.market.domain.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ItemDeleteDto {

    @NotBlank
    private String name;
    @NotNull
    private Role role;
    @NotBlank
    private String password;

    protected ItemDeleteDto() {}

    public ItemDeleteDto(String name, @NotNull Role role, String password) {
        this.name = name;
        this.role = role;
        this.password = password;
    }
}
