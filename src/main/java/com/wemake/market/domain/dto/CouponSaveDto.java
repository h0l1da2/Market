package com.wemake.market.domain.dto;

import com.wemake.market.domain.How;
import com.wemake.market.domain.Where;
import lombok.*;

@Data
@Builder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class CouponSaveDto {

    private Long itemId;
    private How how;
    private Where wheres;
    // 비율일 경우 -> 적용 비율
    private int rate;
    // 고정일 경우 -> 적용 금액
    private int amount;
}
