package com.wemake.market.domain.dto;

import com.wemake.market.domain.How;
import com.wemake.market.domain.Role;
import com.wemake.market.domain.Where;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@Builder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class CouponSaveDto {

    @NotNull @Min(value = 1)
    private Long itemId;
    @NotNull
    private Role role;
    @NotBlank
    private String password;
    private How how;
    private Where wheres;
    // 비율일 경우 -> 적용 비율
    @Min(value = 1)
    private int rate;
    // 고정일 경우 -> 적용 금액
    @Min(value = 100)
    private int amount;
}
