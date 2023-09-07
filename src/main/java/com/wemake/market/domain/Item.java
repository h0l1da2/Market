package com.wemake.market.domain;

import com.wemake.market.domain.dto.ItemCreateDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime date;

    public Item(ItemCreateDto itemCreateDto) {
        this.id = itemCreateDto.getId();
        this.name = itemCreateDto.getName();
        this.date = itemCreateDto.getDate();
    }
}
