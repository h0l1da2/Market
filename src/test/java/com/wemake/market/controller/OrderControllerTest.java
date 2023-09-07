package com.wemake.market.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wemake.market.domain.*;
import com.wemake.market.domain.dto.ItemCreateDto;
import com.wemake.market.domain.dto.OrderItemDto;
import com.wemake.market.domain.dto.OrderDto;
import com.wemake.market.repository.ItemPriceHistoryRepository;
import com.wemake.market.repository.ItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
class OrderControllerTest {

    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private ItemPriceHistoryRepository itemPriceHistoryRepository;

    @BeforeEach
    void clean() {
        itemPriceHistoryRepository.deleteAll();
        itemRepository.deleteAll();
    }

    @Test
    @DisplayName("결제가격 성공 : 아이템 한개")
    void 결제가격_성공_아이템한개() throws Exception {
        List<OrderItemDto> list = itemSaveAndAddList();
        OrderDto orderDto = getOrderDto(list);

        mockMvc.perform(
                        post("/order")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(orderDto))
                ).andExpect(status().is2xxSuccessful())
                .andDo(print());

    }

    private List<OrderItemDto> itemSaveAndAddList() {
        Item item = saveItem("감자", 3000);

        List<OrderItemDto> list = new ArrayList<>();

        OrderItemDto orderItemDto = OrderItemDto.builder()
                .id(item.getId())
                .count(2)
                .build();

        list.add(orderItemDto);
        return list;
    }

    @Test
    @DisplayName("결제가격 성공 : 아이템 여러개")
    void 결제가격_성공_아이템여러개() throws Exception {

        List<OrderItemDto> list = getOrderItemDtos();

        OrderDto orderDto = getOrderDto(list);

        mockMvc.perform(
                        post("/order")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(orderDto))
                ).andExpect(status().is2xxSuccessful())
                .andDo(print());

    }

    @Test
    @DisplayName("결제가격 성공 : 아이템한개,아이템쿠폰(고정값)")
    void 결제가격_성공_아이템한개_쿠폰_고정값() throws Exception {

        List<OrderItemDto> list = itemSaveAndAddList();

        Coupon coupon = getFixedCoupon(3000);


        OrderDto orderDto = getOrderDtoUserCoupon(list, coupon);

        mockMvc.perform(
                        post("/order")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(orderDto))
                ).andExpect(status().is2xxSuccessful())
                .andDo(print());

    }

    @Test
    @DisplayName("결제가격 성공 : 아이템여러개,아이템쿠폰(고정값)")
    void 결제가격_성공_아이템여러개_쿠폰_고정값() throws Exception {

        List<OrderItemDto> list = getOrderItemDtos();

        Coupon coupon = getFixedCoupon(1000);

        OrderDto orderDto = getOrderDtoUserCoupon(list, coupon);

        mockMvc.perform(
                        post("/order")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(orderDto))
                ).andExpect(status().is2xxSuccessful())
                .andDo(print());

    }

    @Test
    @DisplayName("결제가격 성공 : 아이템한개,아이템쿠폰(비율)")
    void 결제가격_성공_아이템한개_쿠폰_비율() throws Exception {

        List<OrderItemDto> list = itemSaveAndAddList();

        Coupon coupon = getPercentageCoupon(20);

        OrderDto orderDto = getOrderDtoUserCoupon(list, coupon);

        mockMvc.perform(
                        post("/order")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(orderDto))
                ).andExpect(status().is2xxSuccessful())
                .andDo(print());

    }

    @Test
    @DisplayName("결제가격 성공 : 아이템여러개,아이템쿠폰(비율)")
    void 결제가격_성공_아이템여러개_쿠폰_비율() throws Exception {

        List<OrderItemDto> list = getOrderItemDtos();

        Coupon coupon = getPercentageCoupon(20);

        OrderDto orderDto = getOrderDtoUserCoupon(list, coupon);

        mockMvc.perform(
                        post("/order")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(orderDto))
                ).andExpect(status().is2xxSuccessful())
                .andDo(print());

    }

    @Test
    @DisplayName("결제가격 성공 : 아이템한개,주문쿠폰(고정값)")
    void 결제가격_성공_아이템한개_주문쿠폰_고정값() throws Exception {

        List<OrderItemDto> list = itemSaveAndAddList();

        Coupon coupon = getFixedCoupon(2000);

        OrderDto orderDto = getOrderDtoUserCoupon(list, coupon);

        mockMvc.perform(
                        post("/order")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(orderDto))
                ).andExpect(status().is2xxSuccessful())
                .andDo(print());

    }

    @Test
    @DisplayName("결제가격 성공 : 아이템여러개,주문쿠폰(고정값)")
    void 결제가격_성공_아이템여러개_주문쿠폰_고정값() throws Exception {

        List<OrderItemDto> list = getOrderItemDtos();

        Coupon coupon = getFixedCoupon(3000);

        OrderDto orderDto = getOrderDtoUserCoupon(list, coupon);

        mockMvc.perform(
                        post("/order")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(orderDto))
                ).andExpect(status().is2xxSuccessful())
                .andDo(print());

    }

    @Test
    @DisplayName("결제가격 성공 : 아이템한개,주문쿠폰(비율)")
    void 결제가격_성공_아이템한개_주문쿠폰_비율() throws Exception {

        List<OrderItemDto> list = itemSaveAndAddList();

        Coupon coupon = getPercentageCoupon(20);

        OrderDto orderDto = getOrderDtoUserCoupon(list, coupon);

        mockMvc.perform(
                        post("/order")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(orderDto))
                ).andExpect(status().is2xxSuccessful())
                .andDo(print());

    }

    @Test
    @DisplayName("결제가격 성공 : 아이템여러개,주문쿠폰(비율)")
    void 결제가격_성공_아이템여러개_주문쿠폰_비율() throws Exception {

        List<OrderItemDto> list = getOrderItemDtos();

        Coupon coupon = getPercentageCoupon(1000);

        OrderDto orderDto = getOrderDtoUserCoupon(list, coupon);

        mockMvc.perform(
                        post("/order")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(orderDto))
                ).andExpect(status().is2xxSuccessful())
                .andDo(print());

    }

    private static Coupon getPercentageCoupon(int rate) {
        Coupon coupon = new Coupon(How.PERCENTAGE, Where.ORDER);
        coupon.isPercentagePrice(rate);
        return coupon;
    }

    private Item saveItem(String name, int price) {

        ItemCreateDto itemCreateDto = ItemCreateDto.builder()
                .name(name)
                .price(price)
                .role(Role.MARKET)
                .build();

        Item item = Item.builder()
                .name(itemCreateDto.getName())
                .build();
        Item saveItem = itemRepository.save(item);

        ItemPriceHistory itemPriceHistory = ItemPriceHistory.builder()
                .item(saveItem)
                .date(itemCreateDto.getDate())
                .price(itemCreateDto.getPrice())
                .build();
        itemPriceHistoryRepository.save(itemPriceHistory);

        return item;
    }

    private Coupon getFixedCoupon(int amount) {
        Coupon coupon = new Coupon(How.FIXED, Where.ORDER);
        coupon.isFixedPrice(amount);
        return coupon;
    }

    private OrderDto getOrderDto(List<OrderItemDto> list) {
        OrderDto orderDto = OrderDto.builder()
                .items(list)
                .deliveryPrice(1000)
                .useCoupon(false)
                .build();
        return orderDto;
    }

    private List<OrderItemDto> getOrderItemDtos() {
        Item item1 = saveItem("감자", 3000);
        Item item2 = saveItem("고구마", 1000);
        Item item3 = saveItem("사과", 2000);

        List<OrderItemDto> list = new ArrayList<>();

        OrderItemDto orderItemDto1 = OrderItemDto.builder()
                .id(item1.getId())
                .count(2)
                .build();

        OrderItemDto orderItemDto2 = OrderItemDto.builder()
                .id(item2.getId())
                .count(2)
                .build();

        OrderItemDto orderItemDto3 = OrderItemDto.builder()
                .id(item3.getId())
                .count(2)
                .build();

        list.add(orderItemDto1);
        list.add(orderItemDto2);
        list.add(orderItemDto3);

        return list;
    }

    private OrderDto getOrderDtoUserCoupon(List<OrderItemDto> list, Coupon coupon) {

        OrderDto orderDto = OrderDto.builder()
                .items(list)
                .deliveryPrice(1000)
                .useCoupon(true)
                .coupon(coupon)
                .build();

        return orderDto;
    }
}