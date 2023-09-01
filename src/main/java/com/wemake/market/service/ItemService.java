package com.wemake.market.service;

import com.wemake.market.domain.dto.ItemDto;
import com.wemake.market.exception.ItemDuplException;
import com.wemake.market.exception.NotAuthorityException;

public interface ItemService {
    ItemDto createItem(ItemDto itemDto) throws NotAuthorityException, ItemDuplException;
}
