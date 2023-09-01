package com.wemake.market.service;

import com.wemake.market.domain.Item;
import com.wemake.market.domain.Role;
import com.wemake.market.domain.dto.ItemDto;
import com.wemake.market.exception.ItemDuplException;
import com.wemake.market.exception.NotAuthorityException;
import com.wemake.market.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    @Override
    public ItemDto createItem(ItemDto itemDto) throws NotAuthorityException, ItemDuplException {

        // 마켓 권한 검사
        if (itemDto.getRole().equals(Role.USER)) {
            log.error("마켓 권한 없음");
            throw new NotAuthorityException("마켓 권한 없음");
        }

        // 아이템 중복 검사
        Item findItem = itemRepository.findByName(itemDto.getName()).orElse(null);

        if (findItem != null) {
            log.error("이미 같은 이름으로 아이템이 있음 = {}", findItem.getName());
            throw new ItemDuplException("아이템 중복");
        }

        // 아이템 넣기
        Item item = itemRepository.save(new Item(itemDto, false));

        return new ItemDto(item);
    }
}
