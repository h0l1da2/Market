package com.wemake.market.domain.dto;

import com.wemake.market.domain.Role;
import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ItemCreateDto {
    private Long id;
    @NotBlank @Size(min = 1)
    private String name;
    @Min(value = 100)
    private int price;
    @NotNull
    private Role role;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime date;

}
