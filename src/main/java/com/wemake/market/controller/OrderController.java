package com.wemake.market.controller;

import com.google.gson.JsonObject;
import com.wemake.market.domain.Code;
import com.wemake.market.domain.dto.OrderDto;
import com.wemake.market.exception.ItemNotFoundException;
import com.wemake.market.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/order")
public class OrderController {

    private final OrderService orderService;

    /**
     * 주문 총 금액 계산
     * (각 주문 목록 상품 가격 * 개수) + 배달비
     * 쿠폰이 있다면(useCoupon) 쿠폰 할인 금액 반영해서 계산
     */
    @PostMapping
    public ResponseEntity<String> orderPrice(@RequestBody @Valid OrderDto orderDto) {

        JsonObject jsonObject = new JsonObject();

        try {

            int orderPrice = orderService.getOrderPrice(orderDto);
            jsonObject.addProperty("price", orderPrice);

        } catch (ItemNotFoundException e) {
            log.error("없는 아이템을 주문함");
            jsonObject.addProperty("data", Code.NOT_FOUND.name());
            return ResponseEntity.badRequest()
                    .body(jsonObject.toString());
        }

        jsonObject.addProperty("data", Code.OK.name());

        return ResponseEntity.ok(jsonObject.toString());
    }


}
