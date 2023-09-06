package com.wemake.market.service;

import com.wemake.market.domain.Item;
import com.wemake.market.domain.Role;
import com.wemake.market.domain.dto.*;
import com.wemake.market.exception.DuplicateItemException;
import com.wemake.market.exception.NotAuthorityException;
import com.wemake.market.exception.ItemNotFoundException;
import com.wemake.market.exception.UnavailableDateTimeException;
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
import java.util.List;

import static java.time.LocalDateTime.*;
import static org.springframework.data.domain.Sort.Direction.*;

@Slf4j
@Service
@RequiredArgsConstructor
@PropertySource("classpath:application.yml")
public class ItemServiceImpl implements ItemService {

    @Value("${market.password}")
    private String password;
    private final ItemRepository itemRepository;

    @Override
    public ItemCreateDto createItem(ItemCreateDto itemCreateDto) throws NotAuthorityException, DuplicateItemException {

        // 마켓 권한 검사
        if (itemCreateDto.getRole().equals(Role.USER)) {
            throw new NotAuthorityException("마켓 권한 없음");
        }

        // 아이템 중복 검사
        List<Item> findItem = itemRepository.findByName(itemCreateDto.getName());

        if (findItem.size() != 0) {
            throw new DuplicateItemException("아이템 중복");
        }

        // 아이템 넣기
        Item item = itemRepository.save(new Item(itemCreateDto));

        return new ItemCreateDto(item);

    }

    @Override
    public ItemUpdateDto updateItem(ItemUpdateDto itemUpdateDto) throws NotAuthorityException, ItemNotFoundException {

        checkMarketRole(itemUpdateDto.getRole(), itemUpdateDto.getPassword());

        List<Item> findItem = itemRepository.findByName(itemUpdateDto.getName());

        if (findItem.size() == 0) {
            throw new ItemNotFoundException("해당 아이템 찾을 수 없음");
        }

        Item updateItem = itemRepository.save(new Item(itemUpdateDto));

        return new ItemUpdateDto(updateItem);
    }

    @Transactional
    @Override
    public void deleteItem(ItemDeleteDto itemDeleteDto) throws NotAuthorityException, ItemNotFoundException {

        checkMarketRole(itemDeleteDto.getRole(), itemDeleteDto.getPassword());

        List<Item> deleteItemList = itemRepository.findByName(itemDeleteDto.getName());
        if (deleteItemList.size() == 0) {
            throw new ItemNotFoundException();
        }

        itemRepository.deleteAllByName(itemDeleteDto.getName());

    }

    @Override
    public ItemDto searchItemByTime(ItemSearchTimeDto itemSearchTimeDto) throws ItemNotFoundException, UnavailableDateTimeException {

        /**
         * 13 로 구한다면 -> 13 ~ 현재 시간까지 데이터를 구해서
         * -> 근데 이게 데이터가 없다면
         * 이전 ~ 12:59 으로 구해서 마지막 데이터
         */

        List<Item> findItems = itemRepository.findByName(itemSearchTimeDto.getName());

        if (findItems.size() == 0) {
            throw new ItemNotFoundException();
        }

        Item findItem = findItems.get(0);

        LocalDateTime userWantPriceDate = itemSearchTimeDto.getDate();

        if (userWantPriceDate.isBefore(findItem.getDate())) {
            throw new UnavailableDateTimeException();
        }

        // 궁금한 시간 ~ 지금까지해서 한 개 조회
        LocalDateTime offsetDateTime = LocalDateTime.of(
                findItem.getDate().getYear(),
                findItem.getDate().getMonth(),
                findItem.getDate().getDayOfMonth(),
                findItem.getDate().getHour(),
                findItem.getDate().getMinute(),
                findItem.getDate().getSecond() - 1
        );
        LocalDateTime limitDateTime = userWantPriceDate;

        PageRequest page = PageRequest.of(0, 1, Sort.by(DESC, "date"));

        Page<Item> resultItemPage = itemRepository.findByNameAndDate(findItem.getName(), offsetDateTime, limitDateTime, page);
        List<Item> resultItemList = resultItemPage.stream().toList();

        if (resultItemList.size() == 0) {

            limitDateTime = offsetDateTime;
            // 완전 과거
            offsetDateTime = findItem.getDate();

            // 한 개
            resultItemPage = itemRepository.findByNameAndDate(findItem.getName(), offsetDateTime, limitDateTime, page);
            resultItemList = resultItemPage.stream().toList();
        }

        Item resultItem = resultItemList.get(resultItemList.size() - 1);
        return new ItemDto(resultItem);
    }

    private void checkMarketRole(Role role, String pwd) throws NotAuthorityException {
        if (role.equals(Role.USER) ||
                !pwd.equals(password)) {
            throw new NotAuthorityException("권한 없음 : 비밀번호 에러");
        }
    }
}
