package com.wemake.market.service;

import com.wemake.market.domain.Item;
import com.wemake.market.domain.Role;
import com.wemake.market.domain.dto.ItemDeleteDto;
import com.wemake.market.domain.dto.ItemDto;
import com.wemake.market.domain.dto.ItemSearchTimeDto;
import com.wemake.market.domain.dto.ItemUpdateDto;
import com.wemake.market.exception.ItemDuplException;
import com.wemake.market.exception.NotAuthorityException;
import com.wemake.market.exception.NotFoundException;
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

    @Test
    @DisplayName("아이템 추가 실패 : 유저 요청")
    void 아이템추가_실패_유저요청() {
        // given
        ItemDto itemDto = new ItemDto("name", 1000, Role.USER);

        // when then
        Assertions.assertThrows(NotAuthorityException.class,
                () -> itemService.createItem(itemDto));

    }

    @Test
    @DisplayName("아이템 추가 실패 : 중복 아이템)")
    void 아이템추가_실패_중복아이템() {
        // given
        ItemDto itemDto = new ItemDto("name", 1000, Role.MARKET);
        itemRepository.save(new Item(itemDto));

        // when then
        Assertions.assertThrows(ItemDuplException.class,
                () -> itemService.createItem(itemDto));

    }

    @Test
    @DisplayName("아이템 수정 : 성공 !")
    void updateItem() throws NotAuthorityException, NotFoundException {
        // given
        ItemDto itemDto = new ItemDto("name", 1000, Role.MARKET);
        itemRepository.save(new Item(itemDto));

        ItemUpdateDto itemUpdateDto = new ItemUpdateDto(itemDto.getName(), 2000, Role.MARKET, password);

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
        ItemDto itemDto = new ItemDto("name", 1000, Role.MARKET);
        itemRepository.save(new Item(itemDto));

        ItemUpdateDto itemUpdateDto = new ItemUpdateDto(itemDto.getName(), 2000, Role.USER, password);

        // when then
        Assertions.assertThrows(NotAuthorityException.class,
                () -> itemService.updateItem(itemUpdateDto));

    }

    @Test
    @DisplayName("아이템 수정 실패 : 없는 아이템")
    void updateItem_실패_없는아이템() {
        // given
        ItemDto itemDto = new ItemDto("name", 1000, Role.MARKET);
        itemRepository.save(new Item(itemDto));

        ItemUpdateDto itemUpdateDto = new ItemUpdateDto("ㅋㅋ", 2000, Role.MARKET, password);

        // when then
        Assertions.assertThrows(NotFoundException.class,
                () -> itemService.updateItem(itemUpdateDto));

    }

    @Test
    @DisplayName("아이템 삭제 성공 : 한 개")
    void deleteItem_성공_한개() throws NotAuthorityException, NotFoundException {
        // given
        ItemDto itemDto = new ItemDto("name", 1000, Role.MARKET);
        itemRepository.save(new Item(itemDto));

        ItemDeleteDto itemDeleteDto = new ItemDeleteDto(itemDto.getName(), itemDto.getRole(), password);

        // when
        itemService.deleteItem(itemDeleteDto);

        // then
        List<Item> itemList = itemRepository.findByName(itemDeleteDto.getName());

        assertThat(itemList.size()).isEqualTo(0);
    }

    @Test
    @DisplayName("아이템 삭제 성공 : 여러 개")
    void deleteItem_성공_여러개() throws NotAuthorityException, NotFoundException {
        // given
        ItemDto itemDto = new ItemDto("name", 1000, Role.MARKET);
        itemRepository.save(new Item(itemDto));
        itemRepository.save(new Item(itemDto));
        itemRepository.save(new Item(itemDto));

        ItemDeleteDto itemDeleteDto = new ItemDeleteDto(itemDto.getName(), itemDto.getRole(), password);

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
        Assertions.assertThrows(NotFoundException.class, () -> itemService.deleteItem(itemDeleteDto));

    }

    @Test
    @DisplayName("아이템 삭제 실패 : 권한 없음")
    void deleteItem_실패_권한없음() {
        // given
        ItemDto itemDto = new ItemDto("name", 1000, Role.MARKET);
        itemRepository.save(new Item(itemDto));

        ItemDeleteDto itemDeleteDto = new ItemDeleteDto(itemDto.getName(), Role.USER, password);

        // when then
        Assertions.assertThrows(NotAuthorityException.class, () -> itemService.deleteItem(itemDeleteDto));

    }

    @Test
    @DisplayName("아이템 조회 성공 : 특정 시간")
    void 아이템조회_성공() throws NotFoundException, NotValidException {
        // given
        ItemDto itemDto = new ItemDto("name", 1000, Role.MARKET);
        itemRepository.save(new Item(itemDto));

        LocalDateTime localDateTime = of(
                now().getYear(),
                now().getMonth(),
                now().getDayOfMonth(),
                now().getHour(), 0);
        ItemSearchTimeDto itemSearchTimeDto = new ItemSearchTimeDto(itemDto.getName(), localDateTime);

        // when
        ItemDto item = itemService.searchItemByTime(itemSearchTimeDto);

        // then
        assertThat(item).isNotNull();

        assertThat(item.getDate()).isAfterOrEqualTo(localDateTime);
        assertThat(item.getName()).isEqualTo(itemDto.getName());
        assertThat(item.getPrice()).isEqualTo(itemDto.getPrice());

    }

    @Test
    @DisplayName("아이템 조회 성공 : 여러 개")
    void 아이템조회_성공_여러개() throws NotFoundException, NotValidException {
        // given
        ItemDto itemDto = new ItemDto("name", 1000, Role.MARKET);
        itemRepository.save(new Item(itemDto));
        itemRepository.save(new Item(itemDto));
        itemRepository.save(new Item(itemDto));

        LocalDateTime localDateTime = of(
                now().getYear(),
                now().getMonth(),
                now().getDayOfMonth(),
                now().getHour(), 0);
        ItemSearchTimeDto itemSearchTimeDto = new ItemSearchTimeDto(itemDto.getName(), localDateTime);

        // when
        ItemDto item = itemService.searchItemByTime(itemSearchTimeDto);

        // then
        assertThat(item).isNotNull();

        assertThat(item.getDate()).isAfterOrEqualTo(localDateTime);
        assertThat(item.getName()).isEqualTo(itemDto.getName());
        assertThat(item.getPrice()).isEqualTo(itemDto.getPrice());

    }

    @Test
    @DisplayName("아이템 조회 실패 : 없는 아이템")
    void 아이템조회_실패_없는아이템() {
        // given
        ItemDto itemDto = new ItemDto("name", 1000, Role.MARKET);

        LocalDateTime localDateTime = of(
                now().getYear(),
                now().getMonth(),
                now().getDayOfMonth(),
                now().getHour(), 0);
        ItemSearchTimeDto itemSearchTimeDto = new ItemSearchTimeDto(itemDto.getName(), localDateTime);

        // when then
        Assertions.assertThrows(NotFoundException.class, () -> itemService.searchItemByTime(itemSearchTimeDto));

    }

}