package com.wemake.market.service;

import com.wemake.market.domain.Role;
import com.wemake.market.domain.dto.ItemDto;
import com.wemake.market.exception.ItemDuplException;
import com.wemake.market.exception.NotAuthorityException;
import com.wemake.market.repository.ItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
class ItemServiceImplTest {

    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private ItemService itemService;

    @BeforeEach
    void clean() {
        itemRepository.deleteAll();
    }

    @Test
    @DisplayName("아이템 추가 : 성공 !")
    void createItem() throws NotAuthorityException, ItemDuplException {
        // given
        ItemDto itemDto = new ItemDto("name", 1000, Role.MARKET);

        // when
        ItemDto item = itemService.createItem(itemDto);

        // then
        assertThat(item).isNotNull();
        assertThat(item.getDate()).isNotNull();
        assertThat(item.getName()).isEqualTo(itemDto.getName());
        assertThat(item.getPrice()).isEqualTo(itemDto.getPrice());
    }
}