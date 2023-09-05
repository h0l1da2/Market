package com.wemake.market.service;

import com.wemake.market.domain.*;
import com.wemake.market.domain.dto.ItemDto;
import com.wemake.market.domain.dto.OrderDto;
import com.wemake.market.domain.dto.OrderItemDto;
import com.wemake.market.domain.dto.PayDto;
import com.wemake.market.exception.ItemDuplException;
import com.wemake.market.repository.ItemRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class OrderServiceImplTest {

    @Autowired
    private OrderService orderService;
    @Autowired
    private ItemRepository itemRepository;

    @Test
    @DisplayName("주문 금액 계산 성공 ! : 한개")
    void 주문금액계산_성공_한개() throws ItemDuplException {

        saveItem("감자", 3000);

        List<OrderItemDto> list = new ArrayList<>();
        OrderItemDto orderItemDto = new OrderItemDto("감자", 2);
        list.add(orderItemDto);
        OrderDto orderDto = new OrderDto(list, 1000);

        int orderPrice = orderService.getOrderPrice(orderDto);

        assertThat(orderPrice).isEqualTo(7000);

    }

    @Test
    @DisplayName("주문 금액 계산 성공 ! : 두 개")
    void 주문금액계산_성공_두개() throws ItemDuplException {

        Item item1 = saveItem("감자", 2000);
        Item item2 = saveItem("고구마", 4000);

        List<OrderItemDto> list = new ArrayList<>();
        OrderItemDto orderItemDtoA = new OrderItemDto(item1.getName(), 2);
        OrderItemDto orderItemDtoB = new OrderItemDto(item2.getName(), 4);
        list.add(orderItemDtoA);
        list.add(orderItemDtoB);
        OrderDto orderDto = new OrderDto(list, 3000);

        int orderPrice = orderService.getOrderPrice(orderDto);

        assertThat(orderPrice).isEqualTo(23000);

    }

    @Test
    @DisplayName("주문 금액 계산 성공 ! : 세 개")
    void 주문금액계산_성공_세개() throws ItemDuplException {

        Item item1 = saveItem("감자", 2000);
        Item item2 = saveItem("고구마", 4000);
        Item item3 = saveItem("치즈", 1500);

        List<OrderItemDto> list = new ArrayList<>();
        OrderItemDto orderItemDtoA = new OrderItemDto(item1.getName(), 2);
        OrderItemDto orderItemDtoB = new OrderItemDto(item2.getName(), 4);
        OrderItemDto orderItemDtoC = new OrderItemDto(item3.getName(), 1);
        list.add(orderItemDtoA);
        list.add(orderItemDtoB);
        list.add(orderItemDtoC);
        OrderDto orderDto = new OrderDto(list, 2500);

        int orderPrice = orderService.getOrderPrice(orderDto);

        assertThat(orderPrice).isEqualTo(24000);
    }

    @Test
    @DisplayName("주문 금액 계산 실패 : 중복 아이템")
    void 주문금액계산_실패_중복아이템() {

        Item item1 = saveItem("감자", 2000);
        Item item2 = saveItem("고구마", 4000);
        Item item3 = saveItem("치즈", 1500);

        List<OrderItemDto> list = new ArrayList<>();
        OrderItemDto orderItemDtoA = new OrderItemDto(item1.getName(), 2);
        OrderItemDto orderItemDtoB = new OrderItemDto(item2.getName(), 4);
        OrderItemDto orderItemDtoC = new OrderItemDto(item3.getName(), 1);
        list.add(orderItemDtoA);
        list.add(orderItemDtoA); // 중복
        list.add(orderItemDtoB);
        list.add(orderItemDtoC);
        OrderDto orderDto = new OrderDto(list, 2500);

        org.junit.jupiter.api.Assertions.assertThrows(ItemDuplException.class,
                () -> orderService.getOrderPrice(orderDto));
    }

    private Item saveItem(String name, int price) {
        ItemDto itemDto = new ItemDto(name, price, Role.MARKET);
        Item item = new Item(itemDto);
        itemRepository.save(item);

        return item;
    }

    @Test
    @DisplayName("결제 금액 게산 성공! : 아이템 하나, 쿠폰 사용 X")
    void 결제금액_성공_하나() throws ItemDuplException {
        saveItem("감자", 1000);

        List<OrderItemDto> list = new ArrayList<>();
        OrderItemDto orderItemDto = new OrderItemDto("감자", 10);
        list.add(orderItemDto);
        PayDto payDto = new PayDto(list, 1000, false);
        int payPrice = orderService.getPayPrice(payDto);

        assertThat(payPrice).isEqualTo(11000);
    }

    @Test
    @DisplayName("결제 금액 게산 쿠폰 성공! : 아이템 하나,고정값")
    void 결제금액_성공_하나_쿠폰_아이템_고정값() throws ItemDuplException {
        saveItem("감자", 1000);

        List<OrderItemDto> list = new ArrayList<>();
        OrderItemDto orderItemDto = new OrderItemDto("감자", 10);
        list.add(orderItemDto);
        Coupon coupon = new Coupon("감자", How.FIXED, Where.ITEM);
        coupon.setAmount(2000);
        PayDto payDto = new PayDto(list, 1000, true, coupon);
        int payPrice = orderService.getPayPrice(payDto);

        assertThat(payPrice).isEqualTo(9000);
    }

    @Test
    @DisplayName("결제 금액 게산 쿠폰 성공! : 아이템 하나,퍼센트")
    void 결제금액_성공_쿠폰_아이템_퍼센트_하나() throws ItemDuplException {
        saveItem("감자", 1000);

        List<OrderItemDto> list = new ArrayList<>();
        OrderItemDto orderItemDto = new OrderItemDto("감자", 10);
        list.add(orderItemDto);
        Coupon coupon = new Coupon("감자", How.PERCENTAGE, Where.ITEM);
        coupon.setRate(10);
        PayDto payDto = new PayDto(list, 1000, true, coupon);
        int payPrice = orderService.getPayPrice(payDto);

        assertThat(payPrice).isEqualTo(10000);
    }

    @Test
    @DisplayName("결제 금액 게산 쿠폰 성공! : 아이템 여러개,퍼센트")
    void 결제금액_성공_쿠폰_아이템_퍼센트_여러개() throws ItemDuplException {
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
        PayDto payDto = new PayDto(list, 3000, true, coupon);
        int payPrice = orderService.getPayPrice(payDto);

        assertThat(payPrice).isEqualTo(26600);
    }

    // 과연 아이템이 중복으로 들어갔다면 ???
    // 옵션이 생길 경우... -> 이름만으로 비교하면 나중에 고칠 게 많아지려나
    @Test
    @DisplayName("결제 금액 계산 쿠폰 실패 : 아이템 중복")
    void 결제금액_실패_여러개_쿠폰_아이템_퍼센트_여러개() {
        saveItem("감자", 1000);
        saveItem("고구마", 3000);
        saveItem("치즈", 2000);

        List<OrderItemDto> list = new ArrayList<>();
        OrderItemDto orderItemDto1 = new OrderItemDto("감자", 10);
        OrderItemDto orderItemDto2 = new OrderItemDto("고구마", 4);
        OrderItemDto orderItemDto3 = new OrderItemDto("치즈", 2);
        list.add(orderItemDto1);
        list.add(orderItemDto1); // 감자 중복
        list.add(orderItemDto2);
        list.add(orderItemDto3);
        Coupon coupon = new Coupon("고구마", How.PERCENTAGE, Where.ITEM);
        coupon.setRate(20);
        PayDto payDto = new PayDto(list, 3000, true, coupon);

        org.junit.jupiter.api.Assertions.assertThrows(ItemDuplException.class, () ->
                orderService.getPayPrice(payDto));
    }

    @Test
    @DisplayName("결제 금액 게산 쿠폰 성공! : 주문,퍼센트,여러개")
    void 결제금액_성공_하나_쿠폰_주문_퍼센트_여러개() throws ItemDuplException {
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
        PayDto payDto = new PayDto(list, 3000, true, coupon); // 3000
        int payPrice = orderService.getPayPrice(payDto);

        assertThat(payPrice).isEqualTo(23200);
    }

    @Test
    @DisplayName("결제 금액 게산 쿠폰 성공! : 주문,고정값,여러개")
    void 결제금액_성공_하나_쿠폰_주문_고정값_여러개() throws ItemDuplException {
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
        PayDto payDto = new PayDto(list, 3000, true, coupon); // 3000
        int payPrice = orderService.getPayPrice(payDto);

        assertThat(payPrice).isEqualTo(26000);
    }



}