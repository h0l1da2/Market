package com.wemake.market.controller;

import com.google.gson.JsonObject;
import com.wemake.market.domain.Code;
import com.wemake.market.domain.dto.ItemDeleteDto;
import com.wemake.market.domain.dto.ItemDto;
import com.wemake.market.domain.dto.ItemSearchTimeDto;
import com.wemake.market.domain.dto.ItemUpdateDto;
import com.wemake.market.exception.ItemDuplException;
import com.wemake.market.exception.NotAuthorityException;
import com.wemake.market.exception.NotFoundException;
import com.wemake.market.service.ItemService;
import com.wemake.market.service.WebService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/item")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;
    private final WebService webService;

    @GetMapping
    public ResponseEntity<String> searchTime(@RequestBody @Valid ItemSearchTimeDto itemSearchTimeDto) {

        JsonObject jsonObject = new JsonObject();

        try {

            List<ItemDto> itemDto = itemService.searchItemByTime(itemSearchTimeDto);
            String itemJson = webService.objToJson(itemDto);
            jsonObject.addProperty("item", itemJson);

        } catch (NotFoundException e) {

            log.error("아이템 찾을 수 없음 = {}", itemSearchTimeDto.getName());
            jsonObject.addProperty("data", Code.NOT_FOUND.name());

            return ResponseEntity.badRequest()
                    .body(jsonObject.toString());
        }

        jsonObject.addProperty("data", Code.OK.name());

        return ResponseEntity.ok(jsonObject.toString());
    }

    @PostMapping
    public ResponseEntity<String> create(@RequestBody @Valid ItemDto itemDto) {

        JsonObject jsonObject = new JsonObject();
        try {

            itemDto = itemService.createItem(itemDto);

        } catch (NotAuthorityException e) {

            log.error("마켓 권한 없음");
            jsonObject.addProperty("data", Code.AUTH_ERR.name());
            return ResponseEntity.badRequest()

                    .body(jsonObject.toString());
        } catch (ItemDuplException e) {

            log.error("같은 이름 아이템 존재 = {}", itemDto.getName());
            jsonObject.addProperty("data", Code.DUPL_ITEM.name());
            return ResponseEntity.badRequest()
                    .body(jsonObject.toString());

        }

        String itemJson = webService.objToJson(itemDto);

        jsonObject.addProperty("data", Code.OK.name());
        jsonObject.addProperty("item", itemJson);

        return ResponseEntity.ok(jsonObject.toString());

    }

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

        } catch (NotFoundException e) {

            log.error("수정할 아이템이 존재하지 않음 = {}", itemUpdateDto.getName());
            jsonObject.addProperty("data", Code.NOT_FOUND.name());
            return ResponseEntity.badRequest()
                    .body(jsonObject.toString());

        }

        String itemJson = webService.objToJson(new ItemDto(itemUpdateDto));
        jsonObject.addProperty("data", Code.OK.name());
        jsonObject.addProperty("item", itemJson);

        return ResponseEntity.ok(jsonObject.toString());
    }

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

        } catch (NotFoundException e) {

            log.error("삭제할 아이템이 존재하지 않음 = {}", itemDeleteDto.getName());
            jsonObject.addProperty("data", Code.NOT_FOUND.name());
            return ResponseEntity.badRequest()
                    .body(jsonObject.toString());

        }

        jsonObject.addProperty("data", Code.OK.name());
        return ResponseEntity.ok(jsonObject.toString());
    }
}
