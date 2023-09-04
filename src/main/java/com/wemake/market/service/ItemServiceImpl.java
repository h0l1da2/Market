package com.wemake.market.service;

import com.wemake.market.domain.Item;
import com.wemake.market.domain.Role;
import com.wemake.market.domain.dto.ItemDeleteDto;
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
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
        Item item = itemRepository.save(new Item(itemDto, false));

        return new ItemDto(item);
    }

    @Override
    public ItemUpdateDto updateItem(ItemUpdateDto itemUpdateDto) throws NotAuthorityException, NotFoundException {

        validRole(itemUpdateDto.getRole(), itemUpdateDto.getPassword());

        List<Item> byNameAndIsUpdate = itemRepository.findByNameAndIsUpdate(itemUpdateDto.getName(), false);

        if (byNameAndIsUpdate.size() == 0) {
            throw new NotFoundException("해당 아이템 찾을 수 없음");
        }

        Item updateItem = itemRepository.save(new Item(itemUpdateDto, true));

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

    private void validRole(Role role, String pwd) throws NotAuthorityException {
        if (role.equals(Role.USER) ||
                !pwd.equals(password)) {
            throw new NotAuthorityException("권한 없음 : 비밀번호 에러");
        }
    }
}
