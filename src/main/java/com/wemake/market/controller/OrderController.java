package com.wemake.market.controller;

import com.wemake.market.domain.dto.OrderDto;
import com.wemake.market.exception.CouponErrorException;
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
    public ResponseEntity<Integer> orderPrice(@RequestBody @Valid OrderDto orderDto) throws ItemNotFoundException, CouponErrorException {

        int orderPrice = orderService.getOrderPrice(orderDto);

        return ResponseEntity.ok(orderPrice);
    }


}
