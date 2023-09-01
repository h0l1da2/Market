package com.wemake.market.controller;

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

        try {

            itemDto = itemService.createItem(itemDto);

        } catch (ItemDuplException e) {
            return ResponseEntity.badRequest()
                    .body("ITEM_DUPL");
        } catch (NotAuthorityException e) {
            return ResponseEntity.badRequest()
                    .body("NOT_AUTH");
        }

        String itemJson = webService.objToJson(itemDto);

        return ResponseEntity.ok(itemJson);

    }
}
