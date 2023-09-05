package com.wemake.market.service;

import com.wemake.market.domain.dto.*;
import com.wemake.market.exception.DuplicateItemException;
import com.wemake.market.exception.NotAuthorityException;
import com.wemake.market.exception.ItemNotFoundException;
import com.wemake.market.exception.NotValidException;

public interface ItemService {
    ItemCreateDto createItem(ItemCreateDto itemCreateDto) throws NotAuthorityException, DuplicateItemException;
    ItemUpdateDto updateItem(ItemUpdateDto itemUpdateDto) throws NotAuthorityException, ItemNotFoundException;
    void deleteItem(ItemDeleteDto itemDeleteDto) throws NotAuthorityException, ItemNotFoundException;
    ItemDto searchItemByTime(ItemSearchTimeDto itemSearchTimeDto) throws ItemNotFoundException, NotValidException;
}
