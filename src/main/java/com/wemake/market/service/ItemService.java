package com.wemake.market.service;

import com.wemake.market.domain.dto.ItemDeleteDto;
import com.wemake.market.domain.dto.ItemDto;
import com.wemake.market.domain.dto.ItemSearchTimeDto;
import com.wemake.market.domain.dto.ItemUpdateDto;
import com.wemake.market.exception.ItemDuplException;
import com.wemake.market.exception.NotAuthorityException;
import com.wemake.market.exception.NotFoundException;

import java.util.List;

public interface ItemService {
    ItemDto createItem(ItemDto itemDto) throws NotAuthorityException, ItemDuplException;
    ItemUpdateDto updateItem(ItemUpdateDto itemUpdateDto) throws NotAuthorityException, NotFoundException;
    void deleteItem(ItemDeleteDto itemDeleteDto) throws NotAuthorityException, NotFoundException;
    List<ItemDto> searchItemByTime(ItemSearchTimeDto itemSearchTimeDto) throws NotFoundException;
}
