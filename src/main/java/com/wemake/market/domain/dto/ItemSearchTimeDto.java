package com.wemake.market.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Data
public class ItemSearchTimeDto {

    @NotBlank
    private String name;
    @PastOrPresent
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime date;

    public ItemSearchTimeDto(String name, LocalDateTime date) {
        this.name = name;
        this.date = date;
    }
}
