package com.wemake.market.service;

import com.wemake.market.domain.Item;
import com.wemake.market.domain.Role;
import com.wemake.market.domain.dto.ItemDto;
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
        itemRepository.findByName(itemDto.getName()).orElseThrow(ItemDuplException::new);

        // 아이템 넣기
        Item item = itemRepository.save(new Item(itemDto, false));

        return new ItemDto(item);
    }

    @Override
    public ItemUpdateDto updateItem(ItemUpdateDto itemUpdateDto) throws NotAuthorityException, NotFoundException {

        if (itemUpdateDto.getRole().equals(Role.USER) ||
                itemUpdateDto.getPassword().equalsIgnoreCase(password)) {
            throw new NotAuthorityException("권한 없음 : 비밀번호 에러");
        }

        itemRepository.findByNameAndIsUpdate(itemUpdateDto.getName(), false)
                .orElseThrow(NotFoundException::new);

        Item updateItem = itemRepository.save(new Item(itemUpdateDto, true));

        return new ItemUpdateDto(updateItem);
    }
}
