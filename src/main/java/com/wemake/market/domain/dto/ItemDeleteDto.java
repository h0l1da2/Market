package com.wemake.market.domain.dto;

import com.wemake.market.domain.Role;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ItemDeleteDto {

    @NotNull
    @Min(value = 1)
    private Long id;
    @NotNull
    private Role role;
    @NotBlank
    private String password;

}
