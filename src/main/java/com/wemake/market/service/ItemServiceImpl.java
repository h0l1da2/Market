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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.springframework.data.domain.Sort.Direction.*;

@Slf4j
@Service
@RequiredArgsConstructor
@PropertySource("classpath:application.yml")
public class ItemServiceImpl implements ItemService {

    @Value("${market.password}")
    private String password;
    private final ItemRepository itemRepository;
    private final ItemPriceHistoryRepository itemPriceHistoryRepository;

    @Transactional
    @Override
    public ItemCreateDto createItem(ItemCreateDto itemCreateDto) throws NotAuthorityException, DuplicateItemException {

        // 마켓 권한 검사
        if (!itemCreateDto.getRole().equals(Role.MARKET)) {
            throw new NotAuthorityException("마켓 권한 없음");
        }

        // 아이템 중복 검사
        List<Item> findItem = itemRepository.findByName(itemCreateDto.getName());

        if (findItem.size() != 0) {
            throw new DuplicateItemException("아이템 중복");
        }

        // 아이템 넣기
        Item item = Item.builder()
                .name(itemCreateDto.getName())
                .build();
        Item saveItem = itemRepository.save(item);

        ItemPriceHistory itemPriceHistory = ItemPriceHistory.builder()
                .item(saveItem)
                .date(itemCreateDto.getDate())
                .price(itemCreateDto.getPrice())
                .build();

        itemPriceHistoryRepository.save(itemPriceHistory);

        return ItemCreateDto.builder()
                .name(item.getName())
                .price(itemCreateDto.getPrice())
                .date(itemPriceHistory.getDate())
                .build();

    }

    @Override
    public ItemUpdateDto updateItem(ItemUpdateDto itemUpdateDto) throws NotAuthorityException, ItemNotFoundException {

        checkMarketRole(itemUpdateDto.getRole(), itemUpdateDto.getPassword());

        Item item = itemRepository.findById(itemUpdateDto.getId())
                .orElseThrow(ItemNotFoundException::new);

        ItemPriceHistory itemPriceHistory = ItemPriceHistory
                .builder()
                .item(item)
                .price(itemUpdateDto.getPrice())
                .date(itemUpdateDto.getDate())
                .build();

        ItemPriceHistory newItemPriceHistory = itemPriceHistoryRepository.save(itemPriceHistory);

        return ItemUpdateDto.builder()
                .price(newItemPriceHistory.getPrice())
                .date(newItemPriceHistory.getDate())
                .build();
    }

    @Transactional
    @Override
    public void deleteItem(ItemDeleteDto itemDeleteDto) throws NotAuthorityException, ItemNotFoundException {

        checkMarketRole(itemDeleteDto.getRole(), itemDeleteDto.getPassword());

        Item item = itemRepository.findById(itemDeleteDto.getId()).orElseThrow(ItemNotFoundException::new);

        itemPriceHistoryRepository.deleteAllByItem(item);
        itemRepository.deleteById(itemDeleteDto.getId());

    }

    @Override
    public ItemDto searchItemByTime(Long id, String date) throws ItemNotFoundException, UnavailableDateTimeException {

        /**
         * 13 로 구한다면 -> 13 ~ 현재 시간까지 데이터를 구해서
         * -> 근데 이게 데이터가 없다면
         * 이전 ~ 12:59 으로 구해서 마지막 데이터
         */

        DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        LocalDateTime dateForItemPrice = LocalDateTime.parse(date, dateTimeFormat);

        Item findItem = itemRepository.findById(id).orElseThrow(ItemNotFoundException::new);

        // 궁금한 시간 ~ 지금까지해서 한 개 조회
        LocalDateTime offsetDateTime = LocalDateTime.of(
                dateForItemPrice.getYear(),
                dateForItemPrice.getMonth(),
                dateForItemPrice.getDayOfMonth(),
                dateForItemPrice.getHour(),
                dateForItemPrice.getMinute(),
                dateForItemPrice.getSecond()
        );

        LocalDateTime limitDateTime = dateForItemPrice;

        PageRequest page = PageRequest.of(0, 1, Sort.by(DESC, "date"));

        Page<ItemPriceHistory> resultItemPage = itemPriceHistoryRepository.findByItemAndDate(findItem, offsetDateTime, limitDateTime, page);
        List<ItemPriceHistory> resultItemList = resultItemPage.stream().toList();

        if (resultItemList.size() == 0) {

            limitDateTime = offsetDateTime;
            // 완전 과거
            offsetDateTime = findItem.getDate();

            // 한 개
            resultItemPage = itemPriceHistoryRepository.findByItemAndDate(findItem, offsetDateTime, limitDateTime, page);
            resultItemList = resultItemPage.stream().toList();
        }

        ItemPriceHistory resultItemPriceHistory = resultItemList.get(resultItemList.size() - 1);

        ItemDto resultItemDto = ItemDto.builder()
                .id(findItem.getId())
                .name(findItem.getName())
                .price(resultItemPriceHistory.getPrice())
                .date(resultItemPriceHistory.getDate())
                .build();

        return resultItemDto;
    }

    private void checkMarketRole(Role role, String pwd) throws NotAuthorityException {
        if (!role.equals(Role.MARKET) ||
                !pwd.equals(password)) {
            throw new NotAuthorityException("권한 없음 : 비밀번호 에러");
        }
    }
}
