package com.wemake.market.service;

import com.wemake.market.domain.Item;
import com.wemake.market.domain.Role;
import com.wemake.market.domain.dto.*;
import com.wemake.market.exception.DuplicateItemException;
import com.wemake.market.exception.NotAuthorityException;
import com.wemake.market.exception.ItemNotFoundException;
import com.wemake.market.exception.UnavailableDateTimeException;
import com.wemake.market.repository.ItemRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.PropertySource;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static java.time.LocalDateTime.*;
import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@PropertySource("classpath:application.yml")
class ItemServiceImplTest {

    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private ItemService itemService;
    @Value("${market.password}")
    private String password;

    @BeforeEach
    void clean() {
        itemRepository.deleteAll();
    }

    @Test
    @DisplayName("아이템 추가 : 성공 !")
    void createItem() throws NotAuthorityException, DuplicateItemException {
        // given
        ItemCreateDto itemCreateDto = ItemCreateDto.builder()
                .name("감자")
                .price(20000)
                .role(Role.MARKET)
                .build();

        // when
        ItemCreateDto item = itemService.createItem(itemCreateDto);

        // then
        assertThat(item).isNotNull();
        assertThat(item.getDate()).isNotNull();
        assertThat(item.getName()).isEqualTo(itemCreateDto.getName());
        assertThat(item.getPrice()).isEqualTo(itemCreateDto.getPrice());
    }

    @Test
    @DisplayName("아이템 추가 실패 : 유저 요청")
    void 아이템추가_실패_유저요청() {
        // given
        ItemCreateDto itemCreateDto = saveAndGetItemCreateDto("감자", 1000, Role.USER);

        // when then
        Assertions.assertThrows(NotAuthorityException.class,
                () -> itemService.createItem(itemCreateDto));

    }

    @Test
    @DisplayName("아이템 추가 실패 : 중복 아이템)")
    void 아이템추가_실패_중복아이템() {
        // given
        ItemCreateDto itemCreateDto = saveAndGetItemCreateDto("딸기", 1000, Role.MARKET);

        // when then
        Assertions.assertThrows(DuplicateItemException.class,
                () -> itemService.createItem(itemCreateDto));

    }

    @Test
    @DisplayName("아이템 수정 : 성공 !")
    void updateItem() throws NotAuthorityException, ItemNotFoundException {
        // given
        ItemCreateDto itemCreateDto = saveAndGetItemCreateDto("도넛", 30000, Role.MARKET);

        ItemUpdateDto itemUpdateDto = ItemUpdateDto.builder()
                .name(itemCreateDto.getName())
                .price(2000)
                .role(Role.MARKET)
                .password(password)
                .build();

        // when
        ItemUpdateDto updateItem = itemService.updateItem(itemUpdateDto);

        List<Item> item = itemRepository.findByName(itemUpdateDto.getName());

        // then
        assertThat(updateItem).isNotNull();
        assertThat(updateItem.getDate()).isNotNull();
        assertThat(updateItem.getName()).isEqualTo(itemUpdateDto.getName());
        assertThat(updateItem.getPrice()).isEqualTo(itemUpdateDto.getPrice());
        assertThat(item.get(item.size()-1).getDate()).isEqualTo(updateItem.getDate());

    }

    @Test
    @DisplayName("아이템 수정 실패 : 권한 없음")
    void updateItem_실패_권한없음() {
        // given
        ItemCreateDto itemCreateDto = saveAndGetItemCreateDto("어묵", 1000, Role.MARKET);

        ItemUpdateDto itemUpdateDto = ItemUpdateDto.builder()
                .name(itemCreateDto.getName())
                .price(2000)
                .role(Role.USER)
                .password(password)
                .build();

        // when then
        Assertions.assertThrows(NotAuthorityException.class,
                () -> itemService.updateItem(itemUpdateDto));

    }

    @Test
    @DisplayName("아이템 수정 실패 : 없는 아이템")
    void updateItem_실패_없는아이템() {
        // given
        ItemUpdateDto itemUpdateDto = ItemUpdateDto.builder()
                .name("힝")
                .price(2000)
                .role(Role.MARKET)
                .password(password)
                .build();

        // when then
        Assertions.assertThrows(ItemNotFoundException.class,
                () -> itemService.updateItem(itemUpdateDto));

    }

    @Test
    @DisplayName("아이템 삭제 성공 : 한 개")
    void deleteItem_성공_한개() throws NotAuthorityException, ItemNotFoundException {
        // given
        ItemCreateDto itemCreateDto = saveAndGetItemCreateDto("당근", 3000, Role.MARKET);

        ItemDeleteDto itemDeleteDto = ItemDeleteDto.builder()
                .name(itemCreateDto.getName())
                .role(itemCreateDto.getRole())
                .password(password)
                .build();

        // when
        itemService.deleteItem(itemDeleteDto);

        List<Item> itemList = itemRepository.findByName(itemDeleteDto.getName());

        // then
        assertThat(itemList.size()).isEqualTo(0);
    }

    @Test
    @DisplayName("아이템 삭제 성공 : 여러 개")
    void deleteItem_성공_여러개() throws NotAuthorityException, ItemNotFoundException {
        // given
        ItemCreateDto itemCreateDto = saveAndGetItemCreateDto("포테토칩", 1000, Role.MARKET);
        itemRepository.save(new Item(itemCreateDto));
        itemRepository.save(new Item(itemCreateDto));

        ItemDeleteDto itemDeleteDto = ItemDeleteDto.builder()
                .name(itemCreateDto.getName())
                .role(itemCreateDto.getRole())
                .password(password)
                .build();
        // when
        itemService.deleteItem(itemDeleteDto);

        List<Item> itemList = itemRepository.findByName(itemDeleteDto.getName());
        // then
        assertThat(itemList.size()).isEqualTo(0);
    }

    @Test
    @DisplayName("아이템 삭제 실패 : 삭제 아이템 존재 안 함")
    void deleteItem_실패_존재없음() {
        // given
        ItemDeleteDto itemDeleteDto = ItemDeleteDto.builder()
                .name("치약")
                .role(Role.MARKET)
                .password(password)
                .build();

        // when then
        Assertions.assertThrows(ItemNotFoundException.class, () ->
                itemService.deleteItem(itemDeleteDto));

    }

    @Test
    @DisplayName("아이템 삭제 실패 : 권한 없음")
    void deleteItem_실패_권한없음() {
        // given
        ItemCreateDto itemCreateDto = saveAndGetItemCreateDto("칫솔", 1000, Role.MARKET);

        ItemDeleteDto itemDeleteDto = ItemDeleteDto.builder()
                .name(itemCreateDto.getName())
                .role(Role.USER)
                .password(password)
                .build();

        // when then
        Assertions.assertThrows(NotAuthorityException.class, () ->
                itemService.deleteItem(itemDeleteDto));

    }

    @Test
    @DisplayName("아이템 조회 성공 : 특정 시간1")
    void 아이템조회_성공1() throws ItemNotFoundException, UnavailableDateTimeException {
        // given
        List<Item> itemList =
                saveAndGetItemList("짱구", 30000, 2000, 15000, 2, 3);

        Item item1 = itemList.get(0);
        Item item3 = itemList.get(2);

        // when
        ItemDto itemDto = itemService.searchItemByTime(item1.getName(), "2023-09-06T12:00:00");

        // then
        assertThat(itemDto).isNotNull();
        assertThat(itemDto.getPrice()).isEqualTo(item3.getPrice());

    }

    @Test
    @DisplayName("아이템 조회 성공 : 특정 시간2")
    void 아이템조회_성공2() throws ItemNotFoundException, UnavailableDateTimeException {

        // given
        List<Item> itemList =
                saveAndGetItemList("포카칩", 3000, 4000, 1000, 2, 3);

        Item item1 = itemList.get(0);
        Item item2 = itemList.get(1);

        // when
        ItemDto itemDto = itemService.searchItemByTime(item1.getName(), "2023-04-06T12:00:00");

        // then
        assertThat(itemDto).isNotNull();
        assertThat(itemDto.getPrice()).isEqualTo(item2.getPrice());

    }
    @Test
    @DisplayName("아이템 조회 성공 : 특정 시간")
    void 아이템조회_성공() throws ItemNotFoundException, UnavailableDateTimeException {
        // given
        List<Item> itemList =
                saveAndGetItemList("감자튀김", 1000, 2000, 3000, 2, 3);

        LocalDateTime searchDate = of(
                2023,
                9,
                5,
                12, 52, 0);

        Item item1 = itemList.get(0);
        Item item4 = itemList.get(2);

        // when
        ItemDto itemDto = itemService.searchItemByTime(item1.getName(), "2023-09-06T12:00:00");

        // then
        assertThat(itemDto).isNotNull();
        assertThat(itemDto.getPrice()).isEqualTo(item4.getPrice());

    }
    @Test
    @DisplayName("아이템 조회 성공 : 여러 개")
    void 아이템조회_성공_여러개() throws ItemNotFoundException, UnavailableDateTimeException {
        // given
        List<Item> itemList =
                saveAndGetItemList("왕뚜껑", 1000, 2000, 3000, 2, 3);

        Item item2 = itemList.get(1);

        // when
        ItemDto itemDto = itemService.searchItemByTime(item2.getName(), "2023-03-06T12:00:00");

        // then
        assertThat(itemDto).isNotNull();
        assertThat(itemDto.getName()).isEqualTo(item2.getName());
        // 2000
        assertThat(itemDto.getPrice()).isEqualTo(item2.getPrice());

    }

    @Test
    @DisplayName("아이템 조회 실패 : 없는 아이템")
    void 아이템조회_실패_없는아이템() {
        // no given

        // when then
        Assertions.assertThrows(ItemNotFoundException.class, () ->
                itemService.searchItemByTime("바보", "2023-09-06T12:00:00"));

    }


    private List<Item> saveAndGetItemList(String itemName, int priceA, int priceB, int priceC, int plusMonthsA, int plusMonthsB) {
        LocalDateTime createDate = LocalDateTime.of(
                2023, 1, 1, 12, 20, 0
        );

        Item item1 = new Item(itemName, priceA, createDate);
        itemRepository.save(item1);

        // 2023-09-05 13:25:00
        LocalDateTime item2CreateDate = createDate.plusMonths(plusMonthsA);
        Item item2 = new Item(item1.getName(), priceB, item2CreateDate);
        itemRepository.save(item2);

        // 2023-09-05 13:28:00
        LocalDateTime item3CreateDate = item2CreateDate.plusMonths(plusMonthsB);
        Item item3 = new Item(item1.getName(), priceC, item3CreateDate);
        itemRepository.save(item3);

        List<Item> itemList = new ArrayList<>();
        itemList.add(item1);
        itemList.add(item2);
        itemList.add(item3);

        return itemList;
    }

    private ItemCreateDto saveAndGetItemCreateDto(String name, int price, Role role) {

        ItemCreateDto itemCreateDto = ItemCreateDto.builder()
                .name(name)
                .price(price)
                .role(role)
                .build();

        itemRepository.save(new Item(itemCreateDto));

        return itemCreateDto;
    }
}