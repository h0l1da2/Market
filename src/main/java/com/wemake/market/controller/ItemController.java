package com.wemake.market.controller;

import com.google.gson.JsonObject;
import com.wemake.market.domain.dto.ItemDto;
import com.wemake.market.exception.ItemDuplException;
import com.wemake.market.exception.NotAuthorityException;
import com.wemake.market.service.ItemService;
import com.wemake.market.service.WebService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

        } catch (ItemDuplException e) {
            jsonObject.addProperty("data", "ITEM_DUPL");
            return ResponseEntity.badRequest()
                    .body(jsonObject.toString());
        } catch (NotAuthorityException e) {
            jsonObject.addProperty("data", "NOT_AUTH");
            return ResponseEntity.badRequest()
                    .body(jsonObject.toString());
        }

        String itemJson = webService.objToJson(itemDto);

        jsonObject.addProperty("data", "OK");
        jsonObject.addProperty("item", itemJson);

        return ResponseEntity.ok(jsonObject.toString());

    }
}
