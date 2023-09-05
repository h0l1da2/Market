package com.wemake.market.controller;

import com.google.gson.JsonObject;
import com.wemake.market.domain.Code;
import com.wemake.market.domain.dto.*;
import com.wemake.market.exception.DuplicateItemException;
import com.wemake.market.exception.NotAuthorityException;
import com.wemake.market.exception.ItemNotFoundException;
import com.wemake.market.exception.NotValidException;
import com.wemake.market.service.ItemService;
import com.wemake.market.service.WebService;
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
    private final WebService webService;

    /**
     * (상품 이름 + 특정 시간)으로 주문을 검색할 수 있음.
     */
    @GetMapping
    public ResponseEntity<String> searchTime(@RequestBody @Valid ItemSearchTimeDto itemSearchTimeDto) {

        JsonObject jsonObject = new JsonObject();

        try {

            ItemDto itemDto = itemService.searchItemByTime(itemSearchTimeDto);
            String itemJson = webService.objectToJson(itemDto);
            jsonObject.addProperty("item", itemJson);

        } catch (ItemNotFoundException e) {

            log.error("아이템 찾을 수 없음 = {}", itemSearchTimeDto.getName());
            jsonObject.addProperty("data", Code.NOT_FOUND.name());

            return ResponseEntity.badRequest()
                    .body(jsonObject.toString());
        } catch (NotValidException e) {

            log.error("시간을 다시 확인하세요 = {}", itemSearchTimeDto.getDate());
            jsonObject.addProperty("data", Code.NOT_VALID.name());

            return ResponseEntity.badRequest()
                    .body(jsonObject.toString());

        }

        jsonObject.addProperty("data", Code.OK.name());

        return ResponseEntity.ok(jsonObject.toString());
    }

    /**
     * 상품 추가
     * 마켓 운영자만 상품을 추가할 수 있도록 Role 외에 password 를 추가.
     * 상품 이름은 중복 불가
     */
    @PostMapping
    public ResponseEntity<String> create(@RequestBody @Valid ItemCreateDto itemCreateDto) {

        JsonObject jsonObject = new JsonObject();
        try {

            itemCreateDto = itemService.createItem(itemCreateDto);

        } catch (NotAuthorityException e) {

            log.error("마켓 권한 없음");
            jsonObject.addProperty("data", Code.AUTH_ERR.name());
            return ResponseEntity.badRequest()
                    .body(jsonObject.toString());

        } catch (DuplicateItemException e) {

            log.error("같은 이름 아이템 존재 = {}", itemCreateDto.getName());
            jsonObject.addProperty("data", Code.DUPL_ITEM.name());
            return ResponseEntity.badRequest()
                    .body(jsonObject.toString());

        }

        String itemJson = webService.objectToJson(itemCreateDto);

        jsonObject.addProperty("data", Code.OK.name());
        jsonObject.addProperty("item", itemJson);

        return ResponseEntity.ok(jsonObject.toString());

    }

    /**
     * 상품 수정
     * 가격 수정이 가능하며 권한, 패스워드가 있을 경우에만 수정 가능
     */
    @PutMapping
    public ResponseEntity<String> update(@RequestBody @Valid ItemUpdateDto itemUpdateDto) {

        JsonObject jsonObject = new JsonObject();

        try {

            itemUpdateDto = itemService.updateItem(itemUpdateDto);

        } catch (NotAuthorityException e) {

            log.error("마켓 권한 없음 : 비밀번호 = {}", itemUpdateDto.getPassword());
            jsonObject.addProperty("data", Code.AUTH_ERR.name());
            return ResponseEntity.badRequest()
                    .body(jsonObject.toString());

        } catch (ItemNotFoundException e) {

            log.error("수정할 아이템이 존재하지 않음 = {}", itemUpdateDto.getName());
            jsonObject.addProperty("data", Code.NOT_FOUND.name());
            return ResponseEntity.badRequest()
                    .body(jsonObject.toString());

        }

        String itemJson = webService.objectToJson(new ItemCreateDto(itemUpdateDto));
        jsonObject.addProperty("data", Code.OK.name());
        jsonObject.addProperty("item", itemJson);

        return ResponseEntity.ok(jsonObject.toString());
    }

    /**
     * 상품 삭제
     * 권한이 있을 경우 해당 이름을 가진 상품 전부 삭제
     */
    @DeleteMapping
    public ResponseEntity<String> deleteItem(@RequestBody @Valid ItemDeleteDto itemDeleteDto) {

        JsonObject jsonObject = new JsonObject();
        try {

            itemService.deleteItem(itemDeleteDto);

        } catch (NotAuthorityException e) {

            log.error("마켓 권한 없음 : 비밀번호 = {}", itemDeleteDto.getPassword());
            jsonObject.addProperty("data", Code.AUTH_ERR.name());
            return ResponseEntity.badRequest()
                    .body(jsonObject.toString());

        } catch (ItemNotFoundException e) {

            log.error("삭제할 아이템이 존재하지 않음 = {}", itemDeleteDto.getName());
            jsonObject.addProperty("data", Code.NOT_FOUND.name());
            return ResponseEntity.badRequest()
                    .body(jsonObject.toString());

        }

        jsonObject.addProperty("data", Code.OK.name());
        return ResponseEntity.ok(jsonObject.toString());
    }
}
