package com.wemake.market.domain;

import com.wemake.market.domain.dto.ItemDto;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Getter
@NoArgsConstructor
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private Integer price;
    private Date date;
    private Boolean isUpdate;

    public Item(ItemDto itemDto, boolean isUpdate) {
        this.name = itemDto.getName();
        this.price = itemDto.getPrice();
        this.date = new Date();
        this.isUpdate = isUpdate;
    }
}
