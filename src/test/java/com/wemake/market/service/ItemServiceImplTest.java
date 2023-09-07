package com.wemake.market.service;

import com.wemake.market.domain.Item;
import com.wemake.market.domain.ItemPriceHistory;
import com.wemake.market.domain.Role;
import com.wemake.market.domain.dto.*;
import com.wemake.market.exception.DuplicateItemException;
import com.wemake.market.exception.NotAuthorityException;
import com.wemake.market.exception.ItemNotFoundException;
import com.wemake.market.exception.UnavailableDateTimeException;
import com.wemake.market.repository.ItemPriceHistoryRepository;
import com.wemake.market.repository.ItemRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.crossstore.ChangeSetPersister;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static java.time.LocalDateTime.*;
import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@PropertySource("classpath:application.yml")
class ItemServiceImplTest {

    @Autowired
    private ItemPriceHistoryRepository itemPriceHistoryRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private ItemService itemService;
    @Value("${market.password}")
    private String password;

    @BeforeEach
    void clean() {
        itemPriceHistoryRepository.deleteAll();
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
                .date(now())
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

        ItemUpdateDto dtoForItemUpdate = ItemUpdateDto.builder()
                .id(itemCreateDto.getId())
                .price(2000)
                .role(Role.MARKET)
                .password(password)
                .date(now())
                .build();

        // when
        ItemUpdateDto finishSaveItemUpdateDto = itemService.updateItem(dtoForItemUpdate);

        Item item = itemRepository.findById(finishSaveItemUpdateDto.getId()).orElseThrow(ItemNotFoundException::new);

        // then
        assertThat(finishSaveItemUpdateDto).isNotNull();
        assertThat(finishSaveItemUpdateDto.getDate()).isNotNull();
        assertThat(item.getName()).isEqualTo(itemCreateDto.getName());
        assertThat(finishSaveItemUpdateDto.getPrice()).isEqualTo(dtoForItemUpdate.getPrice());

        assertThat(item.getDate().getDayOfYear()).isEqualTo(finishSaveItemUpdateDto.getDate().getDayOfYear());
        assertThat(item.getDate().getMonth()).isEqualTo(finishSaveItemUpdateDto.getDate().getMonth());
        assertThat(item.getDate().getDayOfMonth()).isEqualTo(finishSaveItemUpdateDto.getDate().getDayOfMonth());
        assertThat(item.getDate().getHour()).isEqualTo(finishSaveItemUpdateDto.getDate().getHour());
        assertThat(item.getDate().getMinute()).isEqualTo(finishSaveItemUpdateDto.getDate().getMinute());
        assertThat(item.getDate().getSecond()).isEqualTo(finishSaveItemUpdateDto.getDate().getSecond());

    }

    @Test
    @DisplayName("아이템 수정 실패 : 권한 없음")
    void updateItem_실패_권한없음() {
        // given
        ItemCreateDto itemCreateDto = saveAndGetItemCreateDto("어묵", 1000, Role.MARKET);

        ItemUpdateDto itemUpdateDto = ItemUpdateDto.builder()
                .id(itemCreateDto.getId())
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
                .id(1L)
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
                .id(itemCreateDto.getId())
                .role(itemCreateDto.getRole())
                .password(password)
                .build();

        // when
        itemService.deleteItem(itemDeleteDto);

        Item deleteItem = itemRepository.findById(itemCreateDto.getId()).orElse(null);

        // then
        assertThat(deleteItem).isNull();
    }

    @Test
    @DisplayName("아이템 삭제 성공 : 여러 개")
    void deleteItem_성공_여러개() throws NotAuthorityException, ItemNotFoundException {
        // given
        ItemCreateDto itemCreateDto = saveAndGetItemCreateDto("포테토칩", 1000, Role.MARKET);
        Item saveItem = itemRepository.findById(itemCreateDto.getId()).orElse(null);

        ItemPriceHistory itemPriceHistory1 = ItemPriceHistory.builder()
                .item(saveItem)
                .date(saveItem.getDate())
                .price(itemCreateDto.getPrice())
                .build();

        ItemPriceHistory itemPriceHistory2 = ItemPriceHistory.builder()
                .item(saveItem)
                .date(saveItem.getDate())
                .price(itemCreateDto.getPrice())
                .build();
        itemPriceHistoryRepository.save(itemPriceHistory1);
        itemPriceHistoryRepository.save(itemPriceHistory2);

        ItemDeleteDto itemDeleteDto = ItemDeleteDto.builder()
                .id(itemCreateDto.getId())
                .role(itemCreateDto.getRole())
                .password(password)
                .build();
        // when
        itemService.deleteItem(itemDeleteDto);

        Item item = itemRepository.findById(itemDeleteDto.getId()).orElse(null);
        List<ItemPriceHistory> findPriceHistory = itemPriceHistoryRepository.findByItem(item);
        // then
        assertThat(item).isNull();
        assertThat(findPriceHistory.size()).isEqualTo(0);
    }

    @Test
    @DisplayName("아이템 삭제 실패 : 삭제 아이템 존재 안 함")
    void deleteItem_실패_존재없음() {
        // given
        ItemDeleteDto itemDeleteDto = ItemDeleteDto.builder()
                .id(1L)
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
                .id(itemCreateDto.getId())
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
        LocalDateTime createDate = LocalDateTime.of(
                2023, 1, 1, 12, 0, 0
        );

        String itemName = "짱구";

        ItemCreateDto itemCreateDto1 = ItemCreateDto.builder()
                .name(itemName)
                .price(3000)
                .date(createDate)
                .build();

        Item item1 = new Item(itemCreateDto1);
        item1 = itemRepository.save(item1);

        ItemPriceHistory itemPriceHistory1 = ItemPriceHistory.builder()
                .price(itemCreateDto1.getPrice())
                .item(item1)
                .date(createDate)
                .build();
        ItemPriceHistory priceHistory1 = itemPriceHistoryRepository.save(itemPriceHistory1);

        // 2023-04-01 12:00:00
        LocalDateTime item2CreateDate = createDate.plusMonths(3);
        ItemCreateDto itemCreateDto2 = ItemCreateDto.builder()
                .name(itemName)
                .price(3000)
                .date(item2CreateDate)
                .build();

        Item item2 = new Item(itemCreateDto2);
        item2 = itemRepository.save(item2);

        ItemPriceHistory itemPriceHistory2 = ItemPriceHistory.builder()
                .price(itemCreateDto2.getPrice())
                .item(item2)
                .date(item2CreateDate)
                .build();
        ItemPriceHistory priceHistory2 = itemPriceHistoryRepository.save(itemPriceHistory2);

        // 2023-09-01 12:00:00
        LocalDateTime item3CreateDate = item2CreateDate.plusMonths(5);
        ItemCreateDto itemCreateDto3 = ItemCreateDto.builder()
                .name(itemName)
                .price(3000)
                .date(item3CreateDate)
                .build();

        Item item3 = new Item(itemCreateDto3);
        item3 = itemRepository.save(item3);

        ItemPriceHistory itemPriceHistory3 = ItemPriceHistory.builder()
                .price(itemCreateDto3.getPrice())
                .item(item3)
                .date(item3CreateDate)
                .build();
        ItemPriceHistory priceHistory3 = itemPriceHistoryRepository.save(itemPriceHistory3);

        // when
        ItemDto itemDto = itemService.searchItemByTime(item1.getId(), "2023-09-06T12:00:00");

        // then
        assertThat(itemDto).isNotNull();
        assertThat(itemDto.getPrice()).isEqualTo(itemPriceHistory3.getPrice());

    }

    @Test
    @DisplayName("아이템 조회 성공 : 특정 시간2")
    void 아이템조회_성공2() throws ItemNotFoundException, UnavailableDateTimeException {

        // given
        // given
        LocalDateTime createDate = LocalDateTime.of(
                2023, 1, 1, 12, 0, 0
        );

        String itemName = "짱구";

        ItemCreateDto itemCreateDto1 = ItemCreateDto.builder()
                .name(itemName)
                .price(3000)
                .date(createDate)
                .build();

        Item item1 = new Item(itemCreateDto1);
        item1 = itemRepository.save(item1);

        ItemPriceHistory itemPriceHistory1 = ItemPriceHistory.builder()
                .price(itemCreateDto1.getPrice())
                .item(item1)
                .date(createDate)
                .build();
        ItemPriceHistory priceHistory1 = itemPriceHistoryRepository.save(itemPriceHistory1);

        // 2023-04-01 12:00:00
        LocalDateTime item2CreateDate = createDate.plusMonths(3);
        ItemCreateDto itemCreateDto2 = ItemCreateDto.builder()
                .name(itemName)
                .price(3000)
                .date(item2CreateDate)
                .build();

        Item item2 = new Item(itemCreateDto2);
        item2 = itemRepository.save(item2);

        ItemPriceHistory itemPriceHistory2 = ItemPriceHistory.builder()
                .price(itemCreateDto2.getPrice())
                .item(item2)
                .date(item2CreateDate)
                .build();
        ItemPriceHistory priceHistory2 = itemPriceHistoryRepository.save(itemPriceHistory2);

        // 2023-09-01 12:00:00
        LocalDateTime item3CreateDate = item2CreateDate.plusMonths(5);
        ItemCreateDto itemCreateDto3 = ItemCreateDto.builder()
                .name(itemName)
                .price(3000)
                .date(item3CreateDate)
                .build();

        Item item3 = new Item(itemCreateDto3);
        item3 = itemRepository.save(item3);

        ItemPriceHistory itemPriceHistory3 = ItemPriceHistory.builder()
                .price(itemCreateDto3.getPrice())
                .item(item3)
                .date(item3CreateDate)
                .build();
        ItemPriceHistory priceHistory3 = itemPriceHistoryRepository.save(itemPriceHistory3);


        // when
        ItemDto itemDto = itemService.searchItemByTime(item1.getId(), "2023-04-06T12:00:00");

        // then
        assertThat(itemDto).isNotNull();
        assertThat(itemDto.getPrice()).isEqualTo(itemCreateDto2.getPrice());

    }
    @Test
    @DisplayName("아이템 조회 성공 : 특정 시간")
    void 아이템조회_성공() throws ItemNotFoundException, UnavailableDateTimeException {
        // given
        LocalDateTime createDate = LocalDateTime.of(
                2023, 1, 1, 12, 0, 0
        );

        String itemName = "짱구";

        ItemCreateDto itemCreateDto1 = ItemCreateDto.builder()
                .name(itemName)
                .price(3000)
                .date(createDate)
                .build();

        Item item1 = new Item(itemCreateDto1);
        item1 = itemRepository.save(item1);

        ItemPriceHistory itemPriceHistory1 = ItemPriceHistory.builder()
                .price(itemCreateDto1.getPrice())
                .item(item1)
                .date(createDate)
                .build();
        ItemPriceHistory priceHistory1 = itemPriceHistoryRepository.save(itemPriceHistory1);

        // 2023-04-01 12:00:00
        LocalDateTime item2CreateDate = createDate.plusMonths(3);
        ItemCreateDto itemCreateDto2 = ItemCreateDto.builder()
                .name(itemName)
                .price(3000)
                .date(item2CreateDate)
                .build();

        Item item2 = new Item(itemCreateDto2);
        item2 = itemRepository.save(item2);

        ItemPriceHistory itemPriceHistory2 = ItemPriceHistory.builder()
                .price(itemCreateDto2.getPrice())
                .item(item2)
                .date(item2CreateDate)
                .build();
        ItemPriceHistory priceHistory2 = itemPriceHistoryRepository.save(itemPriceHistory2);

        // 2023-09-01 12:00:00
        LocalDateTime item3CreateDate = item2CreateDate.plusMonths(5);
        ItemCreateDto itemCreateDto3 = ItemCreateDto.builder()
                .name(itemName)
                .price(3000)
                .date(item3CreateDate)
                .build();

        Item item3 = new Item(itemCreateDto3);
        item3 = itemRepository.save(item3);

        ItemPriceHistory itemPriceHistory3 = ItemPriceHistory.builder()
                .price(itemCreateDto3.getPrice())
                .item(item3)
                .date(item3CreateDate)
                .build();
        ItemPriceHistory priceHistory3 = itemPriceHistoryRepository.save(itemPriceHistory3);

        // 2023-11-01 12:00:00
        LocalDateTime item4CreateDate = item3CreateDate.plusMonths(2);
        ItemCreateDto itemCreateDto4 = ItemCreateDto.builder()
                .name(itemName)
                .price(3000)
                .date(item3CreateDate)
                .build();

        Item item4 = new Item(itemCreateDto4);
        item4 = itemRepository.save(item4);

        ItemPriceHistory itemPriceHistory4 = ItemPriceHistory.builder()
                .price(itemCreateDto3.getPrice())
                .item(item4)
                .date(item4CreateDate)
                .build();
        ItemPriceHistory priceHistory4 = itemPriceHistoryRepository.save(itemPriceHistory3);

        // when
        ItemDto itemDto = itemService.searchItemByTime(item1.getId(), "2023-09-06T12:00:00");

        // then
        assertThat(itemDto).isNotNull();
        assertThat(itemDto.getPrice()).isEqualTo(priceHistory4.getPrice());

    }
    @Test
    @DisplayName("아이템 조회 성공 : 여러 개")
    void 아이템조회_성공_여러개() throws ItemNotFoundException, UnavailableDateTimeException {
        // given
        LocalDateTime createDate = LocalDateTime.of(
                2023, 1, 1, 12, 0, 0
        );

        String itemName = "짱구";

        ItemCreateDto itemCreateDto1 = ItemCreateDto.builder()
                .name(itemName)
                .price(3000)
                .date(createDate)
                .build();

        Item item1 = new Item(itemCreateDto1);
        item1 = itemRepository.save(item1);

        ItemPriceHistory itemPriceHistory1 = ItemPriceHistory.builder()
                .price(itemCreateDto1.getPrice())
                .item(item1)
                .date(createDate)
                .build();
        ItemPriceHistory priceHistory1 = itemPriceHistoryRepository.save(itemPriceHistory1);

        // 2023-04-01 12:00:00
        LocalDateTime item2CreateDate = createDate.plusMonths(3);
        ItemCreateDto itemCreateDto2 = ItemCreateDto.builder()
                .name(itemName)
                .price(3000)
                .date(item2CreateDate)
                .build();

        ItemPriceHistory itemPriceHistory2 = ItemPriceHistory.builder()
                .price(itemCreateDto2.getPrice())
                .item(item1)
                .date(item2CreateDate)
                .build();
        ItemPriceHistory priceHistory2 = itemPriceHistoryRepository.save(itemPriceHistory2);

        // 2023-09-01 12:00:00
        LocalDateTime item3CreateDate = item2CreateDate.plusMonths(5);
        ItemCreateDto itemCreateDto3 = ItemCreateDto.builder()
                .name(itemName)
                .price(3000)
                .date(item3CreateDate)
                .build();

        ItemPriceHistory itemPriceHistory3 = ItemPriceHistory.builder()
                .price(itemCreateDto3.getPrice())
                .item(item1)
                .date(item3CreateDate)
                .build();
        ItemPriceHistory priceHistory3 = itemPriceHistoryRepository.save(itemPriceHistory3);

        // when
        ItemDto itemDto = itemService.searchItemByTime(item1.getId(), "2023-03-06T12:00:00");

        // then
        assertThat(itemDto).isNotNull();
        assertThat(itemDto.getName()).isEqualTo(item1.getName());
        // 2000
        assertThat(itemDto.getPrice()).isEqualTo(priceHistory2.getPrice());

    }

    @Test
    @DisplayName("아이템 조회 실패 : 없는 아이템")
    void 아이템조회_실패_없는아이템() {
        // no given

        // when then
        Assertions.assertThrows(ItemNotFoundException.class, () ->
                itemService.searchItemByTime(1L, "2023-09-06T12:00:00"));

    }

    private ItemCreateDto saveAndGetItemCreateDto(String name, int price, Role role) {

        ItemCreateDto itemCreateDto = ItemCreateDto.builder()
                .name(name)
                .price(price)
                .role(role)
                .date(now())
                .build();

        Item item = Item.builder()
                .name(itemCreateDto.getName())
                .date(itemCreateDto.getDate())
                .build();

        Item saveItem = itemRepository.save(item);

        ItemPriceHistory itemPriceHistory = ItemPriceHistory.builder()
                .item(saveItem)
                .date(saveItem.getDate())
                .price(itemCreateDto.getPrice())
                .build();

        itemPriceHistoryRepository.save(itemPriceHistory);

        return ItemCreateDto.builder()
                .id(saveItem.getId())
                .role(itemCreateDto.getRole())
                .name(saveItem.getName())
                .date(saveItem.getDate())
                .price(itemPriceHistory.getPrice())
                .build();
    }
}