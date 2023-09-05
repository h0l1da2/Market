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
    public ItemDto createItem(ItemDto itemDto) throws NotAuthorityException, ItemDuplException {

        // 마켓 권한 검사
        if (itemDto.getRole().equals(Role.USER)) {
            throw new NotAuthorityException("마켓 권한 없음");
        }

        // 아이템 중복 검사
        List<Item> findItem = itemRepository.findByName(itemDto.getName());

        if (findItem.size() != 0) {
            throw new ItemDuplException("아이템 중복");
        }

        // 아이템 넣기
        Item item = itemRepository.save(new Item(itemDto));

        return new ItemDto(item);
    }

    @Override
    public ItemUpdateDto updateItem(ItemUpdateDto itemUpdateDto) throws NotAuthorityException, NotFoundException {

        validRole(itemUpdateDto.getRole(), itemUpdateDto.getPassword());

        List<Item> byName = itemRepository.findByName(itemUpdateDto.getName());

        if (byName.size() == 0) {
            throw new NotFoundException("해당 아이템 찾을 수 없음");
        }

        Item updateItem = itemRepository.save(new Item(itemUpdateDto));

        return new ItemUpdateDto(updateItem);
    }

    @Transactional
    @Override
    public void deleteItem(ItemDeleteDto itemDeleteDto) throws NotAuthorityException, NotFoundException {

        validRole(itemDeleteDto.getRole(), itemDeleteDto.getPassword());

        List<Item> itemList = itemRepository.findByName(itemDeleteDto.getName());
        if (itemList.size() == 0) {
            throw new NotFoundException();
        }

        itemRepository.deleteAllByName(itemDeleteDto.getName());

    }

    @Override
    public ItemDto searchItemByTime(ItemSearchTimeDto itemSearchTimeDto) throws NotFoundException, NotValidException {

        /**
         * 13 로 구한다면 -> 13 ~ 현재 시간까지 데이터를 구해서
         * -> 근데 이게 데이터가 없다면
         * 이전 ~ 12:59 으로 구해서 마지막 데이터
         */

        List<Item> items = itemRepository.findByName(itemSearchTimeDto.getName());

        if (items.size() == 0) {
            throw new NotFoundException();
        }

        Item item = items.get(items.size() - 1);
        LocalDateTime offset = itemSearchTimeDto.getDate();

        if (offset.isAfter(now()) && offset.isBefore(item.getDate())) {
            throw new NotValidException();
        }

        // 궁금한 시간 ~ 지금까지해서 한 개 조회
        LocalDateTime limitDate = now();

        PageRequest page = PageRequest.of(0, 1, Sort.by(ASC, "date"));

        Page<Item> itemPage = itemRepository.findByNameAndDate(item.getName(), offset, limitDate, page);
        items = itemPage.stream().toList();

        if (items.size() == 0) {

            limitDate = offset;
            // 완전 과거
            offset = item.getDate();

            // 한 개
            itemPage = itemRepository.findByNameAndDate(item.getName(), offset, limitDate, page);
            items = itemPage.stream().toList();
        }

        return new ItemDto(items.get(items.size() - 1));
    }

    private void validRole(Role role, String pwd) throws NotAuthorityException {
        if (role.equals(Role.USER) ||
                !pwd.equals(password)) {
            throw new NotAuthorityException("권한 없음 : 비밀번호 에러");
        }
    }
}
