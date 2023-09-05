package com.wemake.market.service;

import com.wemake.market.domain.Item;
import com.wemake.market.domain.Role;
import com.wemake.market.domain.dto.*;
import com.wemake.market.exception.DuplicateItemException;
import com.wemake.market.exception.NotAuthorityException;
import com.wemake.market.exception.ItemNotFoundException;
import com.wemake.market.exception.NotValidException;
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
        ItemCreateDto itemCreateDto = new ItemCreateDto("name", 1000, Role.MARKET);

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
        ItemCreateDto itemCreateDto = new ItemCreateDto("name", 1000, Role.USER);

        // when then
        Assertions.assertThrows(NotAuthorityException.class,
                () -> itemService.createItem(itemCreateDto));

    }

    @Test
    @DisplayName("아이템 추가 실패 : 중복 아이템)")
    void 아이템추가_실패_중복아이템() {
        // given
        ItemCreateDto itemCreateDto = new ItemCreateDto("name", 1000, Role.MARKET);
        itemRepository.save(new Item(itemCreateDto));

        // when then
        Assertions.assertThrows(DuplicateItemException.class,
                () -> itemService.createItem(itemCreateDto));

    }

    @Test
    @DisplayName("아이템 수정 : 성공 !")
    void updateItem() throws NotAuthorityException, ItemNotFoundException {
        // given
        ItemCreateDto itemCreateDto = new ItemCreateDto("name", 1000, Role.MARKET);
        itemRepository.save(new Item(itemCreateDto));

        ItemUpdateDto itemUpdateDto = new ItemUpdateDto(itemCreateDto.getName(), 2000, Role.MARKET, password);

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
        ItemCreateDto itemCreateDto = new ItemCreateDto("name", 1000, Role.MARKET);
        itemRepository.save(new Item(itemCreateDto));

        ItemUpdateDto itemUpdateDto = new ItemUpdateDto(itemCreateDto.getName(), 2000, Role.USER, password);

        // when then
        Assertions.assertThrows(NotAuthorityException.class,
                () -> itemService.updateItem(itemUpdateDto));

    }

    @Test
    @DisplayName("아이템 수정 실패 : 없는 아이템")
    void updateItem_실패_없는아이템() {
        // given
        ItemCreateDto itemCreateDto = new ItemCreateDto("name", 1000, Role.MARKET);
        itemRepository.save(new Item(itemCreateDto));

        ItemUpdateDto itemUpdateDto = new ItemUpdateDto("ㅋㅋ", 2000, Role.MARKET, password);

        // when then
        Assertions.assertThrows(ItemNotFoundException.class,
                () -> itemService.updateItem(itemUpdateDto));

    }

    @Test
    @DisplayName("아이템 삭제 성공 : 한 개")
    void deleteItem_성공_한개() throws NotAuthorityException, ItemNotFoundException {
        // given
        ItemCreateDto itemCreateDto = new ItemCreateDto("name", 1000, Role.MARKET);
        itemRepository.save(new Item(itemCreateDto));

        ItemDeleteDto itemDeleteDto = new ItemDeleteDto(itemCreateDto.getName(), itemCreateDto.getRole(), password);

        // when
        itemService.deleteItem(itemDeleteDto);

        // then
        List<Item> itemList = itemRepository.findByName(itemDeleteDto.getName());

        assertThat(itemList.size()).isEqualTo(0);
    }

    @Test
    @DisplayName("아이템 삭제 성공 : 여러 개")
    void deleteItem_성공_여러개() throws NotAuthorityException, ItemNotFoundException {
        // given
        ItemCreateDto itemCreateDto = new ItemCreateDto("name", 1000, Role.MARKET);
        itemRepository.save(new Item(itemCreateDto));
        itemRepository.save(new Item(itemCreateDto));
        itemRepository.save(new Item(itemCreateDto));

        ItemDeleteDto itemDeleteDto = new ItemDeleteDto(itemCreateDto.getName(), itemCreateDto.getRole(), password);

        // when
        itemService.deleteItem(itemDeleteDto);

        // then
        List<Item> itemList = itemRepository.findByName(itemDeleteDto.getName());

        assertThat(itemList.size()).isEqualTo(0);
    }

    @Test
    @DisplayName("아이템 삭제 실패 : 삭제 아이템 존재 안 함")
    void deleteItem_실패_존재없음() {
        // given
        ItemDeleteDto itemDeleteDto = new ItemDeleteDto("없는 아이템", Role.MARKET, password);

        // when then
        Assertions.assertThrows(ItemNotFoundException.class, () -> itemService.deleteItem(itemDeleteDto));

    }

    @Test
    @DisplayName("아이템 삭제 실패 : 권한 없음")
    void deleteItem_실패_권한없음() {
        // given
        ItemCreateDto itemCreateDto = new ItemCreateDto("name", 1000, Role.MARKET);
        itemRepository.save(new Item(itemCreateDto));

        ItemDeleteDto itemDeleteDto = new ItemDeleteDto(itemCreateDto.getName(), Role.USER, password);

        // when then
        Assertions.assertThrows(NotAuthorityException.class, () -> itemService.deleteItem(itemDeleteDto));

    }

    @Test
    @DisplayName("아이템 조회 성공 : 특정 시간")
    void 아이템조회_성공() throws ItemNotFoundException, NotValidException {
        // given
        ItemCreateDto itemCreateDto = new ItemCreateDto("name", 1000, Role.MARKET);
        itemRepository.save(new Item(itemCreateDto));

        LocalDateTime localDateTime = of(
                now().getYear(),
                now().getMonth(),
                now().getDayOfMonth(),
                now().getHour(), 0);
        ItemSearchTimeDto itemSearchTimeDto = new ItemSearchTimeDto(itemCreateDto.getName(), localDateTime);

        // when
        ItemDto itemDto = itemService.searchItemByTime(itemSearchTimeDto);

        // then
        assertThat(itemDto).isNotNull();

        assertThat(itemDto.getDate()).isAfterOrEqualTo(localDateTime);
        assertThat(itemDto.getName()).isEqualTo(itemCreateDto.getName());
        assertThat(itemDto.getPrice()).isEqualTo(itemCreateDto.getPrice());

    }

    @Test
    @DisplayName("아이템 조회 성공 : 여러 개")
    void 아이템조회_성공_여러개() throws ItemNotFoundException, NotValidException {
        // given
        ItemCreateDto itemCreateDto = new ItemCreateDto("name", 1000, Role.MARKET);
        itemRepository.save(new Item(itemCreateDto));
        itemRepository.save(new Item(itemCreateDto));
        itemRepository.save(new Item(itemCreateDto));

        LocalDateTime localDateTime = of(
                now().getYear(),
                now().getMonth(),
                now().getDayOfMonth(),
                now().getHour(), 0);
        ItemSearchTimeDto itemSearchTimeDto = new ItemSearchTimeDto(itemCreateDto.getName(), localDateTime);

        // when
        ItemDto itemDto = itemService.searchItemByTime(itemSearchTimeDto);

        // then
        assertThat(itemDto).isNotNull();

        assertThat(itemDto.getDate()).isAfterOrEqualTo(localDateTime);
        assertThat(itemDto.getName()).isEqualTo(itemCreateDto.getName());
        assertThat(itemDto.getPrice()).isEqualTo(itemCreateDto.getPrice());

    }

    @Test
    @DisplayName("아이템 조회 실패 : 없는 아이템")
    void 아이템조회_실패_없는아이템() {
        // given
        ItemCreateDto itemCreateDto = new ItemCreateDto("name", 1000, Role.MARKET);

        LocalDateTime localDateTime = of(
                now().getYear(),
                now().getMonth(),
                now().getDayOfMonth(),
                now().getHour(), 0);
        ItemSearchTimeDto itemSearchTimeDto = new ItemSearchTimeDto(itemCreateDto.getName(), localDateTime);

        // when then
        Assertions.assertThrows(ItemNotFoundException.class, () -> itemService.searchItemByTime(itemSearchTimeDto));

    }

}