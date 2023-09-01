package com.wemake.market.service;

import com.wemake.market.domain.dto.ItemDto;
import com.wemake.market.domain.dto.ItemUpdateDto;
import com.wemake.market.exception.ItemDuplException;
import com.wemake.market.exception.NotAuthorityException;
import com.wemake.market.exception.NotFoundException;

public interface ItemService {
    ItemDto createItem(ItemDto itemDto) throws NotAuthorityException, ItemDuplException;
    ItemUpdateDto updateItem(ItemUpdateDto itemUpdateDto) throws NotAuthorityException, NotFoundException;
}
