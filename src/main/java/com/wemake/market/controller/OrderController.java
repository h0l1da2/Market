package com.wemake.market.controller;

import com.google.gson.JsonObject;
import com.wemake.market.domain.Code;
import com.wemake.market.domain.dto.PayDto;
import com.wemake.market.domain.dto.OrderDto;
import com.wemake.market.exception.ItemDuplException;
import com.wemake.market.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/order")
public class OrderController {

    private final OrderService orderService;

    /**
     * 주문 총 금액 계산
     * (각 주문 목록 상품 가격 * 개수) + 배달비
     */
    @GetMapping
    public ResponseEntity<String> orderPrice(@RequestBody @Valid OrderDto orderDto) {

        JsonObject jsonObject = new JsonObject();

        try {

            int orderPrice = orderService.getOrderPrice(orderDto);
            jsonObject.addProperty("price", orderPrice);

        } catch (ItemDuplException e) {

            log.error("아이템이 중복으로 장바구니에 들어감.");
            jsonObject.addProperty("data", Code.DUPL_ITEM.name());

            return ResponseEntity.badRequest()
                    .body(jsonObject.toString());
        }

        jsonObject.addProperty("data", Code.OK.name());

        return ResponseEntity.ok(jsonObject.toString());
    }

    /**
     * 주문 필요 결제 금액
     * 쿠폰이 있다면 쿠폰 할인 금액 반영해서 계산
     */
    @GetMapping("/pay")
    public ResponseEntity<String> pay(@RequestBody @Valid PayDto payDto) {

        JsonObject jsonObject = new JsonObject();

        try {
            int payPrice = orderService.getPayPrice(payDto);
            jsonObject.addProperty("pay", payPrice);

        } catch (ItemDuplException e) {

            log.error("아이템이 중복으로 장바구니에 들어감.");
            jsonObject.addProperty("data", Code.DUPL_ITEM.name());

            return ResponseEntity.badRequest()
                    .body(jsonObject.toString());
        }

        jsonObject.addProperty("data", Code.OK.name());

        return ResponseEntity.ok(jsonObject.toString());
    }

}
