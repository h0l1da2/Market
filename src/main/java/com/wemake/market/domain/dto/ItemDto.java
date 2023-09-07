package com.wemake.market.domain.dto;

import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ItemDto {

    private Long id;
    private String name;
    private int price;
    private LocalDateTime date;

}
