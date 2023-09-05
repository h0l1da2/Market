package com.wemake.market.service;

import com.wemake.market.domain.*;
import com.wemake.market.domain.dto.ItemDto;
import com.wemake.market.domain.dto.OrderItemDto;
import com.wemake.market.domain.dto.OrderDto;
import com.wemake.market.exception.NotFoundException;
import com.wemake.market.repository.ItemRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
class OrderServiceImplTest {

    @Autowired
    private OrderService orderService;
    @Autowired
    private ItemRepository itemRepository;

    private Item saveItem(String name, int price) {
        ItemDto itemDto = new ItemDto(name, price, Role.MARKET);
        Item item = new Item(itemDto);
        itemRepository.save(item);

        return item;
    }

    @Test
    @DisplayName("결제 금액 계산 성공! : 아이템 하나, 쿠폰 사용 X")
    void 결제금액_성공_하나() throws NotFoundException {
        saveItem("감자", 1000);

        List<OrderItemDto> list = new ArrayList<>();
        OrderItemDto orderItemDto = new OrderItemDto("감자", 10);
        list.add(orderItemDto);
        OrderDto orderDto = new OrderDto(list, 1000, false);
        int payPrice = orderService.getOrderPrice(orderDto);

        assertThat(payPrice).isEqualTo(11000);
    }

    @Test
    @DisplayName("결제 금액 계산 쿠폰 성공! : 아이템 하나,고정값")
    void 결제금액_성공_하나_쿠폰_아이템_고정값() throws NotFoundException {
        saveItem("감자", 1000);

        List<OrderItemDto> list = new ArrayList<>();
        OrderItemDto orderItemDto = new OrderItemDto("감자", 10);
        list.add(orderItemDto);
        Coupon coupon = new Coupon("감자", How.FIXED, Where.ITEM);
        coupon.setAmount(2000);
        OrderDto orderDto = new OrderDto(list, 1000, true, coupon);
        int payPrice = orderService.getOrderPrice(orderDto);

        assertThat(payPrice).isEqualTo(9000);
    }

    @Test
    @DisplayName("결제 금액 계산 쿠폰 성공! : 아이템 하나,무료")
    void 결제금액_성공_하나_쿠폰_아이템_고정값_무료() throws NotFoundException {
        saveItem("감자", 1000);

        List<OrderItemDto> list = new ArrayList<>();
        OrderItemDto orderItemDto = new OrderItemDto("감자", 10);
        list.add(orderItemDto);
        Coupon coupon = new Coupon("감자", How.FIXED, Where.ITEM);
        coupon.setAmount(200000);
        OrderDto orderDto = new OrderDto(list, 1000, true, coupon);
        int payPrice = orderService.getOrderPrice(orderDto);

        assertThat(payPrice).isEqualTo(0);
    }

    @Test
    @DisplayName("결제 금액 계산 쿠폰 성공! : 아이템 하나,퍼센트")
    void 결제금액_성공_쿠폰_아이템_퍼센트_하나() throws NotFoundException {
        saveItem("감자", 1000);

        List<OrderItemDto> list = new ArrayList<>();
        OrderItemDto orderItemDto = new OrderItemDto("감자", 10);
        list.add(orderItemDto);
        Coupon coupon = new Coupon("감자", How.PERCENTAGE, Where.ITEM);
        coupon.setRate(10);
        OrderDto orderDto = new OrderDto(list, 1000, true, coupon);
        int payPrice = orderService.getOrderPrice(orderDto);

        assertThat(payPrice).isEqualTo(10000);
    }

    @Test
    @DisplayName("결제 금액 계산 쿠폰 성공! : 아이템 하나,백퍼센트")
    void 결제금액_성공_쿠폰_아이템_퍼센트_하나_무료() throws NotFoundException {
        saveItem("감자", 1000);

        List<OrderItemDto> list = new ArrayList<>();
        OrderItemDto orderItemDto = new OrderItemDto("감자", 10);
        list.add(orderItemDto);
        Coupon coupon = new Coupon("감자", How.PERCENTAGE, Where.ITEM);
        coupon.setRate(100);
        OrderDto orderDto = new OrderDto(list, 1000, true, coupon);
        int payPrice = orderService.getOrderPrice(orderDto);

        assertThat(payPrice).isEqualTo(1000);
    }

    @Test
    @DisplayName("결제 금액 계산 쿠폰 성공! : 아이템 여러개,퍼센트")
    void 결제금액_성공_쿠폰_아이템_퍼센트_여러개() throws NotFoundException {
        saveItem("감자", 1000);
        saveItem("고구마", 3000);
        saveItem("치즈", 2000);

        List<OrderItemDto> list = new ArrayList<>();
        OrderItemDto orderItemDto1 = new OrderItemDto("감자", 10);
        OrderItemDto orderItemDto2 = new OrderItemDto("고구마", 4);
        OrderItemDto orderItemDto3 = new OrderItemDto("치즈", 2);
        list.add(orderItemDto1);
        list.add(orderItemDto2);
        list.add(orderItemDto3);
        Coupon coupon = new Coupon("고구마", How.PERCENTAGE, Where.ITEM);
        coupon.setRate(20);
        OrderDto orderDto = new OrderDto(list, 3000, true, coupon);
        int payPrice = orderService.getOrderPrice(orderDto);

        assertThat(payPrice).isEqualTo(26600);
    }

    // 과연 아이템이 중복으로 들어갔다면 ???
    // 옵션이 생길 경우... -> 이름만으로 비교하면 나중에 고칠 게 많아지려나

    @Test
    @DisplayName("결제 금액 계산 쿠폰 성공! : 주문,퍼센트,여러개")
    void 결제금액_성공_하나_쿠폰_주문_퍼센트_여러개() throws NotFoundException {
        saveItem("감자", 1000);
        saveItem("고구마", 3000);
        saveItem("치즈", 2000);

        List<OrderItemDto> list = new ArrayList<>();
        OrderItemDto orderItemDto1 = new OrderItemDto("감자", 10); // 10000
        OrderItemDto orderItemDto2 = new OrderItemDto("고구마", 4); // 12000
        OrderItemDto orderItemDto3 = new OrderItemDto("치즈", 2); // 4000
        list.add(orderItemDto1);
        list.add(orderItemDto2);
        list.add(orderItemDto3);
        Coupon coupon = new Coupon(How.PERCENTAGE, Where.ORDER);
        coupon.setRate(20);
        OrderDto orderDto = new OrderDto(list, 3000, true, coupon); // 3000
        int payPrice = orderService.getOrderPrice(orderDto);

        assertThat(payPrice).isEqualTo(23200);
    }

    @Test
    @DisplayName("결제 금액 계산 쿠폰 성공! : 주문,퍼센트,여러개,100퍼")
    void 결제금액_성공_하나_쿠폰_주문_퍼센트_여러개_백퍼() throws NotFoundException {
        saveItem("감자", 1000);
        saveItem("고구마", 3000);
        saveItem("치즈", 2000);

        List<OrderItemDto> list = new ArrayList<>();
        OrderItemDto orderItemDto1 = new OrderItemDto("감자", 10); // 10000
        OrderItemDto orderItemDto2 = new OrderItemDto("고구마", 4); // 12000
        OrderItemDto orderItemDto3 = new OrderItemDto("치즈", 2); // 4000
        list.add(orderItemDto1);
        list.add(orderItemDto2);
        list.add(orderItemDto3);
        Coupon coupon = new Coupon(How.PERCENTAGE, Where.ORDER);
        coupon.setRate(100);
        OrderDto orderDto = new OrderDto(list, 3000, true, coupon); // 3000
        int payPrice = orderService.getOrderPrice(orderDto);

        assertThat(payPrice).isEqualTo(0);
    }

    @Test
    @DisplayName("결제 금액 계산 쿠폰 성공! : 주문,고정값,여러개")
    void 결제금액_성공_하나_쿠폰_주문_고정값_여러개() throws NotFoundException {
        saveItem("감자", 1000);
        saveItem("고구마", 3000);
        saveItem("치즈", 2000);

        List<OrderItemDto> list = new ArrayList<>();
        OrderItemDto orderItemDto1 = new OrderItemDto("감자", 10); // 10000
        OrderItemDto orderItemDto2 = new OrderItemDto("고구마", 4); // 12000
        OrderItemDto orderItemDto3 = new OrderItemDto("치즈", 2); // 4000
        list.add(orderItemDto1);
        list.add(orderItemDto2);
        list.add(orderItemDto3);
        Coupon coupon = new Coupon(How.FIXED, Where.ORDER);
        coupon.setAmount(3000);
        OrderDto orderDto = new OrderDto(list, 3000, true, coupon); // 3000
        int payPrice = orderService.getOrderPrice(orderDto);

        assertThat(payPrice).isEqualTo(26000);
    }

    @Test
    @DisplayName("결제 금액 계산 쿠폰 성공! : 주문,고정값,여러개, 무료")
    void 결제금액_성공_하나_쿠폰_주문_고정값_여러개_무료() throws NotFoundException {
        saveItem("감자", 1000);
        saveItem("고구마", 3000);
        saveItem("치즈", 2000);

        List<OrderItemDto> list = new ArrayList<>();
        OrderItemDto orderItemDto1 = new OrderItemDto("감자", 10); // 10000
        OrderItemDto orderItemDto2 = new OrderItemDto("고구마", 4); // 12000
        OrderItemDto orderItemDto3 = new OrderItemDto("치즈", 2); // 4000
        list.add(orderItemDto1);
        list.add(orderItemDto2);
        list.add(orderItemDto3);
        Coupon coupon = new Coupon(How.FIXED, Where.ORDER);
        coupon.setAmount(3000000);
        OrderDto orderDto = new OrderDto(list, 3000, true, coupon); // 3000
        int payPrice = orderService.getOrderPrice(orderDto);

        assertThat(payPrice).isEqualTo(0);
    }



}