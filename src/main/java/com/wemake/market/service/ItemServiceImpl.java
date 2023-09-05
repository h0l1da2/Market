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
import com.wemake.market.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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
    public List<ItemDto> searchItemByTime(ItemSearchTimeDto itemSearchTimeDto) throws NotFoundException {

        // 해당 시간으로 사이 시간 구하기 -- > 3:00:00 이면 3:00:00 과 3:59:00 사이 시간
        LocalDateTime searchTime = itemSearchTimeDto.getDate();
        LocalDateTime offset =
                LocalDateTime.of(
                        searchTime.getYear(), searchTime.getMonth(), searchTime.getDayOfMonth(),
                        searchTime.getHour(), 0);

        LocalDateTime limitDate = offset.plusMinutes(59);

        List<Item> items = itemRepository.findByNameAndDate(itemSearchTimeDto.getName(), offset, limitDate);

        if (items.size() == 0) {
            throw new NotFoundException();
        }

        List<ItemDto> list = new ArrayList<>();
        items.forEach(i -> {
            list.add(new ItemDto(i));
        });

        return list;
    }

    private void validRole(Role role, String pwd) throws NotAuthorityException {
        if (role.equals(Role.USER) ||
                !pwd.equals(password)) {
            throw new NotAuthorityException("권한 없음 : 비밀번호 에러");
        }
    }
}
