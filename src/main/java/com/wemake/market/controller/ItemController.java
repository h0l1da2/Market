package com.wemake.market.controller;

import com.wemake.market.domain.dto.*;
import com.wemake.market.exception.DuplicateItemException;
import com.wemake.market.exception.NotAuthorityException;
import com.wemake.market.exception.ItemNotFoundException;
import com.wemake.market.exception.UnavailableDateTimeException;
import com.wemake.market.service.ItemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/item")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    /**
     * (상품 이름 + 특정 시간)으로 주문을 검색할 수 있음.
     */
    @GetMapping
    public ResponseEntity<ItemDto> searchTime(@RequestBody @Valid ItemSearchTimeDto itemSearchTimeDto) throws ItemNotFoundException, UnavailableDateTimeException {

        ItemDto itemDto = itemService.searchItemByTime(itemSearchTimeDto);

        return ResponseEntity.ok(itemDto);
    }

    /**
     * 상품 추가
     * 마켓 운영자만 상품을 추가할 수 있도록 Role 외에 password 를 추가.
     * 상품 이름은 중복 불가
     */
    @PostMapping
    public ResponseEntity<ItemCreateDto> create(@RequestBody @Valid ItemCreateDto itemCreateDto) throws DuplicateItemException, NotAuthorityException {

        itemCreateDto = itemService.createItem(itemCreateDto);

        return ResponseEntity.ok(itemCreateDto);

    }

    /**
     * 상품 수정
     * 가격 수정이 가능하며 권한, 패스워드가 있을 경우에만 수정 가능
     */
    @PutMapping
    public ResponseEntity<ItemUpdateDto> update(@RequestBody @Valid ItemUpdateDto itemUpdateDto) throws ItemNotFoundException, NotAuthorityException {

        itemUpdateDto = itemService.updateItem(itemUpdateDto);

        return ResponseEntity.ok(itemUpdateDto);
    }

    /**
     * 상품 삭제
     * 권한이 있을 경우 해당 이름을 가진 상품 전부 삭제
     */
    @DeleteMapping
    public ResponseEntity<String> deleteItem(@RequestBody @Valid ItemDeleteDto itemDeleteDto) throws ItemNotFoundException, NotAuthorityException {

        itemService.deleteItem(itemDeleteDto);

        return ResponseEntity.ok("삭제 완료.");
    }
}
