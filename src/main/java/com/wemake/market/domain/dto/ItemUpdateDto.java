package com.wemake.market.domain.dto;

import com.wemake.market.domain.Role;
import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static java.time.LocalDateTime.now;

@Data
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ItemUpdateDto {
    @NotNull
    @Min(value = 1)
    private Long id;
    @Min(value = 100)
    private int price;
    @NotNull
    private Role role;
    @NotBlank
    private String password;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime date;

    public void nowItemCreated() {
        this.date = now();
    }

}
