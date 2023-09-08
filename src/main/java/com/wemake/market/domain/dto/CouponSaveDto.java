package com.wemake.market.domain.dto;

import com.wemake.market.domain.How;
import com.wemake.market.domain.Role;
import com.wemake.market.domain.Where;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@Builder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class CouponSaveDto {

    private Long itemId;
    @NotNull
    private How how;
    @NotNull
    private Where wheres;
    // 비율일 경우 -> 적용 비율
    private int rate;
    // 고정일 경우 -> 적용 금액
    private int amount;
    @NotBlank
    private String password;
    @NotNull
    private Role role;
}
