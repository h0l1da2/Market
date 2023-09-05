package com.wemake.market.domain;

import com.wemake.market.domain.dto.ItemDto;
import com.wemake.market.domain.dto.ItemUpdateDto;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private int price;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime date;
    private boolean isUpdate;

    public Item(ItemDto itemDto, boolean isUpdate) {
        this.name = itemDto.getName();
        this.price = itemDto.getPrice();
        this.date = LocalDateTime.now();
        this.isUpdate = isUpdate;
    }

    public Item(ItemUpdateDto itemDto, boolean isUpdate) {
        this.name = itemDto.getName();
        this.price = itemDto.getPrice();
        this.date = LocalDateTime.now();
        this.isUpdate = isUpdate;
    }
}
