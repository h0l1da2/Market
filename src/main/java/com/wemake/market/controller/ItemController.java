package com.wemake.market.controller;

import com.google.gson.JsonObject;
import com.wemake.market.domain.dto.ItemDto;
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

@Slf4j
@RestController
@RequestMapping("/item")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;
    private final WebService webService;

    @PostMapping
    public ResponseEntity<String> create(@RequestBody @Valid ItemDto itemDto) {

        JsonObject jsonObject = new JsonObject();
        try {

            itemDto = itemService.createItem(itemDto);

        } catch (NotAuthorityException e) {

            log.error("마켓 권한 없음");
            jsonObject.addProperty("data", "NOT_AUTH");
            return ResponseEntity.badRequest()

                    .body(jsonObject.toString());
        } catch (ItemDuplException e) {

            log.error("같은 이름 아이템 존재 = {}", itemDto.getName());
            jsonObject.addProperty("data", "ITEM_DUPL");
            return ResponseEntity.badRequest()
                    .body(jsonObject.toString());

        }

        String itemJson = webService.objToJson(itemDto);

        jsonObject.addProperty("data", "OK");
        jsonObject.addProperty("item", itemJson);

        return ResponseEntity.ok(jsonObject.toString());

    }

    @PutMapping
    public ResponseEntity<String> update(@RequestBody @Valid ItemUpdateDto itemUpdateDto) {

        JsonObject jsonObject = new JsonObject();

        try {

            itemService.updateItem(itemUpdateDto);

        } catch (NotAuthorityException e) {

            log.error("마켓 권한 없음 : 비밀번호 에러 = {}", itemUpdateDto.getPassword());
            jsonObject.addProperty("data", "PWD_ERR");
            return ResponseEntity.badRequest()
                    .body(jsonObject.toString());

        } catch (NotFoundException e) {

            log.error("수정할 아이템이 존재하지 않음 = {}", itemUpdateDto.getName());
            jsonObject.addProperty("data", "NOT_FOUND");
            return ResponseEntity.badRequest()
                    .body(jsonObject.toString());

        }

        String itemJson = webService.objToJson(itemUpdateDto);
        jsonObject.addProperty("data", "OK");
        jsonObject.addProperty("item", itemJson);

        return ResponseEntity.ok(jsonObject.toString());
    }
}
