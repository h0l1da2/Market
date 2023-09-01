package com.wemake.market.controller;

import com.wemake.market.domain.dto.ItemDto;
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

        ItemDto item = itemService.createItem(itemDto);
        String itemJson = webService.objToJson(item);

        return ResponseEntity.ok(itemJson);

    }
}
